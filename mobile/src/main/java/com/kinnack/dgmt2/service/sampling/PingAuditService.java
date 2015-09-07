package com.kinnack.dgmt2.service.sampling;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kinnack.dgmt2.DGMT2;
import com.kinnack.dgmt2.event.SampleLookupById;
import com.kinnack.dgmt2.event.SampleStoredLocally;
import com.kinnack.dgmt2.event.UserNameChanged;
import com.kinnack.dgmt2.event.UserPinged;
import com.kinnack.dgmt2.model.CsvTags;
import com.kinnack.dgmt2.model.Sample;
import com.kinnack.dgmt2.option.Option;
import com.google.common.base.Joiner;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.provider.BaseColumns._ID;
import static com.kinnack.dgmt2.QueryBuilder.select;
import static com.kinnack.dgmt2.QueryBuilder.using;
import static com.kinnack.dgmt2.option.Option.None;
import static com.kinnack.dgmt2.option.Option.Some;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.*;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.SampleEntry.COLUMN_ACCURACY;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.SampleEntry.COLUMN_BEARING;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.SampleEntry.COLUMN_LAT;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.SampleEntry.COLUMN_LONG;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.SampleEntry.COLUMN_SPEED;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.SampleEntry.COLUMN_SYNCED;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.SampleEntry.COLUMN_TAGS;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.SampleEntry.COLUMN_TIME;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.SampleEntry.STAR;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.SampleEntry.TABLE_NAME;
import static com.kinnack.dgmt2.service.sampling.SampleStorageDbHelper.toReadableDb;
import static com.kinnack.dgmt2.service.sampling.SampleStorageDbHelper.toWritableDb;

public class PingAuditService extends IntentService {
    private static final String TAG = "PingAuditService";

    private static final String ACTION_NOTE = "com.kinnack.dgmt2.service.sampling.action.NOTE_PING";
    private static final String ACTION_SYNC = "com.kinnack.dgmt2.service.sampling.action.SYNC_SAMPLE";
    private static final String ACTION_EDIT = "com.kinnack.dgmt2.service.sampling.action.EDIT_SAMPLE";
    private static final String ACTION_FIND = "com.kinnack.dgmt2.service.sampling.action.FIND_SAMPLE";

    private static final String EXTRA_WHEN = "com.kinnack.dgmt2.service.sampling.extra.WHEN";
    private static final String EXTRA_TAGS = "com.kinnack.dgmt2.service.sampling.extra.TAGS";

    private Bus bus;
    private String username;

    public static void pingUser(Context context, long when) {
        Intent intent = new Intent(context, PingAuditService.class);
        intent.setAction(ACTION_NOTE);
        intent.putExtra(EXTRA_WHEN, when);
        context.startService(intent);
    }

    public static void editSample(Context context, Sample sample) {
        Intent intent = new Intent(context, PingAuditService.class);
        intent.setAction(ACTION_EDIT);
        intent.putExtra(EXTRA_WHEN, sample.getWhen());
        intent.putExtra(EXTRA_TAGS, Joiner.on(",").join(sample.getTags()));
        context.startService(intent);
    }

    public static void sampleSync(Context context, Sample sample) {
        Intent intent = new Intent(context, PingAuditService.class);
        intent.setAction(ACTION_SYNC);
        intent.putExtra(EXTRA_WHEN, sample.getWhen());
        intent.putExtra(EXTRA_TAGS, Joiner.on(",").join(sample.getTags()));
        context.startService(intent);
    }

    public static void findSample(Context context, long id) {
        Intent intent = new Intent(context, PingAuditService.class);
        intent.setAction(ACTION_FIND);
        intent.putExtra(EXTRA_WHEN, id);
        context.startService(intent);
    }

    private Option<SampleStorageDbHelper> dbHelper;

    public PingAuditService() {
        super("PingAuditService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DGMT2 dgmt = ((DGMT2) getApplication());

        bus = dgmt.getBus();
        bus.register(this);

        dbHelper = Some(dgmt.getSampleStorageDbHelper());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final long when = intent.getLongExtra(EXTRA_WHEN, new Date().getTime());
            if (ACTION_SYNC.equals(action)) {
                final String tags = intent.getStringExtra(EXTRA_TAGS);
                sampleRecorded(tags, when);
            } else if (ACTION_EDIT.equals(action)) {
                final String tags = intent.getStringExtra(EXTRA_TAGS);
                sampleEdited(tags, when);
            } else if (ACTION_NOTE.equals(action)) {
                pinged(when);
            } else if (ACTION_FIND.equals(action)) {
                find(when);
            }
        }
    }

    private void sampleRecorded(String tags, long when) {
        updateSample(tags, when, true);
    }

    private void sampleEdited(String tags, long when) {
        for(SQLiteDatabase db : dbHelper.map(toWritableDb)) {
            updateSample(tags, when, false, db);
            updateTagCounts(tags, when, db);
        }
    }

    private void updateSample(String tags, long when, boolean synced) {
        for(SQLiteDatabase db : dbHelper.map(toWritableDb)) {
            updateSample(tags, when, synced, db);
        }
    }
    private int updateSample(String tags, long when, boolean synced, SQLiteDatabase db) {
        ContentValues values = sampleValues(tags, when, ACTION_SYNC);
        values.put(COLUMN_SYNCED, synced);
        int count = db.update(
                TABLE_NAME,
                values,
                _ID +"= ?",
                new String[] { String.valueOf(when) }
        );
        Log.i(TAG, "updated record count at " + when + " (should always be 1): " + count);
        if (count == 1) { bus.post(new SampleStoredLocally(when, tags, synced)); }
        return count;
    }

    private void updateTagCounts(String tags, long when, SQLiteDatabase db) {
        //clean up
        db.delete(TagCountEntry.TABLE_NAME, TagCountEntry.COLUMN_SAMPLE_ID + " = ?", new String[]{String.valueOf(when)});

        //replace|add
        for (String tag : tags.split(",")) {
            ContentValues values = new ContentValues();
            values.put(TagCountEntry.COLUMN_TAG, tag);
            values.put(TagCountEntry.COLUMN_SAMPLE_ID, when);
            db.insert(TagCountEntry.TABLE_NAME, null, values);
        }
    }

    private void pinged(long when) {
        for(SQLiteDatabase db : dbHelper.map(toWritableDb)) {
            ContentValues values = sampleValues(null, when, ACTION_NOTE);
            long id = db.insert(TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select count(*) from samples where tags is null", null);
            int pending = 1;
            if (cursor.moveToFirst()) { pending = cursor.getInt(0); cursor.close(); }

            PingBroadcast.userPinged(this, new UserPinged(when, pending));
        }
    }

    private void find(long id) {
        for (SQLiteDatabase db : dbHelper.map(toReadableDb)) {
            Cursor cursor = using(db, select(STAR).from(TABLE_NAME).where(_ID + " = ?").replacedBy(id+""));
            Option<Sample> sample;

            if (cursor.moveToNext()) {
                String tags = cursor.getString(cursor.getColumnIndex(COLUMN_TAGS));
                Boolean synced = cursor.getInt(1) == 1;
                Sample internal = Sample.apply(username, id, new CsvTags(tags), synced);
                for (Map<String, Object> location : makeLocationMap(cursor)) {
                    internal.addExtras("location", location);
                }
                sample = Some(internal);
            } else {
                sample = None();
            }

            bus.post(new SampleLookupById(id).resolve(sample));
        }
    }

    private Option<Map<String, Object>> makeLocationMap(Cursor cursor) {
        Map<String, Object> map = new HashMap<>();
        map.put("latitude", cursor.getDouble(cursor.getColumnIndex(COLUMN_LAT)));
        Log.d("PingAuditService", "LAT: " + map.get("latitude"));
        map.put("longitude", cursor.getDouble(cursor.getColumnIndex(COLUMN_LONG)));
        Log.d("PingAuditService", "LONG: " + map.get("longitude"));
        map.put("bearing", cursor.getDouble(cursor.getColumnIndex(COLUMN_BEARING)));
        Log.d("PingAuditService", "BEARING: " + map.get("bearing"));
        map.put("speed", cursor.getDouble(cursor.getColumnIndex(COLUMN_SPEED)));
        map.put("time", cursor.getLong(cursor.getColumnIndex(COLUMN_TIME)));
        Log.d("PingAuditService", "TIME: " + map.get("time"));
        map.put("accuracy",cursor.getDouble(cursor.getColumnIndex(COLUMN_ACCURACY)));
        return ((long)map.get("time")) == 0 ? None() : Some(map);
    }

    private ContentValues sampleValues(String tags, long when, String action) {
        ContentValues values = new ContentValues();
        values.put(_ID, when);
        values.put(COLUMN_TAGS, tags);
        return values;
    }

    @Subscribe public void usernameChanged(UserNameChanged event) {
        username = event.getUsername().getOrElse("???");
    }
}
