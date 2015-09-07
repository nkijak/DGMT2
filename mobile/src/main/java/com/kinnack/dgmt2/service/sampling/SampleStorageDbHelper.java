package com.kinnack.dgmt2.service.sampling;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kinnack.dgmt2.option.Function1;

import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.*;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.MIGRATE_0001_CREATE_SAMPLES;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.MIGRATE_0002_CREATE_TAG_COUNTS;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.MIGRATE_0003_ADD_LONGITUDE;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.MIGRATE_0004_ADD_LATITUDE;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.MIGRATE_0005_ADD_SPEED;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.MIGRATE_0006_ADD_BEARING;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.MIGRATE_0007_ADD_TIME;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.MIGRATE_0008_ADD_ACCURACY;

public class SampleStorageDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "samples.db";
    public static final String[] MIGRATIONS = new String[]{
            MIGRATE_0001_CREATE_SAMPLES,
            MIGRATE_0002_CREATE_TAG_COUNTS,
            MIGRATE_0003_ADD_LONGITUDE,
            MIGRATE_0004_ADD_LATITUDE,
            MIGRATE_0005_ADD_SPEED,
            MIGRATE_0006_ADD_BEARING,
            MIGRATE_0007_ADD_TIME,
            MIGRATE_0008_ADD_ACCURACY,
            MIGRATE_0009_ADD_EXERCISE_COUNT,
            MIGRATE_0010_ADD_EXERCISE_TYPE
    };

    public SampleStorageDbHelper(Context context) {
        super(context, DB_NAME, null, MIGRATIONS.length);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, MIGRATIONS.length);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("SampleStorageDbHelper", String.format("Migrating from %d to %d", oldVersion, newVersion));
        for (int i = oldVersion; i < newVersion; i++) {
            Log.d("SampleStorageDbHelper", MIGRATIONS[i]);
           db.execSQL(MIGRATIONS[i]);
        }
    }

    public static Function1<SampleStorageDbHelper, SQLiteDatabase> toWritableDb = new Function1<SampleStorageDbHelper, SQLiteDatabase>() {
        @Override
        public SQLiteDatabase apply(SampleStorageDbHelper helper) {
            return helper.getWritableDatabase();
        }
    };

    public static Function1<SampleStorageDbHelper, SQLiteDatabase> toReadableDb = new Function1<SampleStorageDbHelper, SQLiteDatabase>() {
        @Override
        public SQLiteDatabase apply(SampleStorageDbHelper helper) {
            return helper.getReadableDatabase();
        }
    };
}
