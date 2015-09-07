package com.kinnack.dgmt2.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.kinnack.dgmt2.DGMT2;
import com.kinnack.dgmt2.SampleRequestNotification;
import com.kinnack.dgmt2.event.UserNameChanged;
import com.kinnack.dgmt2.model.CsvTags;
import com.kinnack.dgmt2.model.Record;
import com.kinnack.dgmt2.model.Sample;
import com.kinnack.dgmt2.option.Function1;
import com.kinnack.dgmt2.option.Option;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static com.kinnack.dgmt2.option.Option.None;
import static com.kinnack.dgmt2.option.Option.Some;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RecordHandlerService extends IntentService {
    private static final String ACTION_SUBMIT = "com.kinnack.dgmt2.service.action.SUBMIT_RECORD";
    private static final String ACTION_UPDATE = "com.kinnack.dgmt2.service.action.UPDATE_RECORD";
    //private static final String ACTION_FETCH_NOW = "com.github.davelnewton.dagny.service.action.FETCH_NOW";

    private static final String EXTRA_TAGS = "com.kinnack.dgmt2.service.extra.TAGS";
    private static final String EXTRA_TYPE = "com.kinnack.dgmt2.service.extra.TYPE";
    private static final String EXTRA_COUNT = "com.kinnack.dgmt2.service.extra.COUNT";
    private static final String EXTRA_VOICE_COUNT = "com.kinnack.dgmt2.service.extra.VOICE_COUNT";
    private static final String EXTRA_SAMPLE_TIME = "com.kinnack.dgmt2.service.extra.SAMPLE_TIME";
    private static final String EXTRA_SAMPLE_ID = "com.kinnack.dgmt2.service.extra.SAMPLE_ID";

    private static Option<SnappyRepo> repo = None();
    private Option<String> username = None();

    private Option<Map<String, Object>> locationData = None();

    @Override
    public void onCreate() {
        super.onCreate();
        DGMT2 dgmt2 = ((DGMT2) getApplication());
        repo = dgmt2.getRecordRepo();

        dgmt2.getBus().register(this);
    }

    @Subscribe public void usernameChanged(UserNameChanged event) {
        username = event.getUsername();
    }

    public static void recordRecord(Context context, String type, int count, long when) {
        Intent intent = new Intent(context, RecordHandlerService.class);
        intent.setAction(ACTION_SUBMIT);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_COUNT, count);
        intent.putExtra(EXTRA_SAMPLE_TIME, when);
        context.startService(intent);
    }

    public RecordHandlerService() {
        super("RecordHandlerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SUBMIT.equals(action)) {
                final String type = intent.getStringExtra(EXTRA_TYPE);
                final int count = intent.getIntExtra(EXTRA_COUNT, 0);
                handleActionSubmit(type, count, intent.getLongExtra(EXTRA_SAMPLE_TIME, new Date().getTime()));
            }
        }
    }

    private void handleActionSubmit(final String type, final int count, final Long when) {
        //SIDE EFFECTS BAD! Bad! bad!
        String outcomeDescription =
            username.flatmap(new Function1<String, Option<Boolean>>() {
                @Override
                public Option<Boolean> apply(final String user) {
                    return repo.map(new Function1<SnappyRepo, Boolean>() {
                        @Override
                        public Boolean apply(final SnappyRepo r) {
                            return r.store(new Record(when, count, type));
                        }
                    });
                }
            }).catamorph(
                    "Username or Repo not available, couldn't submit record",
                    new Function1<Boolean, String>() {
                        @Override
                        public String apply(Boolean wasSuccess) {
                            return "Record submitted? " + wasSuccess;
                        }
                    });
        Log.d("RecordHandlerService", outcomeDescription);
    }

    private static Function1<String, String> tagCleaner = new Function1<String, String>() {
        @Override
        public String apply(String tag) {
            String mod = tag.replaceAll(" ", "").toLowerCase();
            Log.d("Sampling", "adding tag " + mod);
            return mod;
        }
    };

    private String getVoiceReplyFromIntent(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(SampleRequestNotification.EXTRA_VOICE_REPLY).toString();
        }
        return null;
    }

    private static final Bundler bundler = new Bundler();

    interface Associative<T> {
        T associate(T... these);
    }

    static class Bundler implements Associative<Option<Bundle>> {
        private Bundle mBundle;

        @Override
        public Option<Bundle> associate(Option<Bundle>... these) {
            Bundle retval= these[0].getOrElse(new Bundle());
            for (int i = 1; i < these.length; i++) {
                retval.putAll(these[i].getOrElse(new Bundle()));
            }
            Log.d("Bundler", "retval: " + retval);
            return retval.size() == 0? None(): Some(retval);
        }
    }
}
