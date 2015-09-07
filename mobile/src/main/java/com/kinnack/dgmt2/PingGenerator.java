package com.kinnack.dgmt2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.kinnack.dgmt2.model.PingCalculator;

import java.util.Date;

public class PingGenerator {

    private PingCalculator pingCalculator;

    public PingGenerator(PingCalculator calc) {
        pingCalculator = calc;
    }

    public long setNextAlarm(final Context context, final Date previous) {
        IntentUtils intentUtils = new IntentUtils(context);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (!intentUtils.pendingAlarm().canIterate()) {
            PendingIntent newPendingIntent = intentUtils.newPendingAlarm();
            long millisFromNow = pingCalculator.nextPingInMillis(previous);
            manager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + millisFromNow,
                    newPendingIntent
            );
            Log.d("PingGenerator", String.format("Setting the next alarm for %sms from now", millisFromNow));

            return millisFromNow;
        } else {
            Log.d("PingGenerator", "Alarm already set. Not touching.. ");
            return 0;
        }
    }

//    public PendingIntent nextAlarm(final Context context, AlarmManager manager) {
//        select().from(manager).where(IntentClause.of(PingReceiver.class)).map(new Function1<PendingIntent, Option<PendingIntent>>() {
//            @Override
//            public Option<PendingIntent> apply(PendingIntent element) {
//                return Option.apply(element);
//            }
//        }).generate()
//        .getOrElse(
//                insert().from(manager).where(IntentClause.of(PingReceiver.class))
//                .generate()
//        );
//    }
//
//
//    public PendingIntentQuery select() {
//        return new PendingIntentQuery() {
//            @Override
//            public PendingIntent generate() {
//                return mClause.map(new Function1<Intent, Object>() {
//                    @Override
//                    public Object apply(Intent intent) {
//                        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
//                    }
//                });
//            }
//        };
//    }
//    public PendingIntentQuery insert() {
//        return new PendingIntentQuery() {
//            @Override
//            public PendingIntent generate() {
//                return mClause.map(new Function1<Intent, Object>() {
//                    @Override
//                    public Object apply(Intent intent) {
//                        return PendingIntent.getBroadcast(context, 0, intent, 0);
//                    }
//                });
//            }
//        };
//    }
//
//
//    abstract class PendingIntentQuery extends Gen<PendingIntent> {
//        protected IntentClause mClause = null;
//        protected AlarmManager mAlarmManager = null;
//
//        public PendingIntentQuery from(final AlarmManager alarmManager) {
//            mAlarmManager = alarmManager;
//            return this;
//        }
//
//        public PendingIntentQuery where(final IntentClause clause) {
//           mClause = clause;
//            return this;
//        }
//    }
//
//
//    static class IntentClause extends Gen<Intent> {
//        public static IntentClause of(final Class intentClass) {
//            return new IntentClause(){
//                @Override
//                public Intent generate() {
//                    return new Intent(context, PingReceiver.class);
//                }
//            }
//        }
//    }
//
//
//    static final Gen<Integer> noCreateFlagGen = new Gen<Integer>() {
//        @Override
//        public Integer generate() {
//            return PendingIntent.FLAG_NO_CREATE;
//        }
//    };



    protected void setPingCalculator(PingCalculator calc) { pingCalculator = calc; }
}
