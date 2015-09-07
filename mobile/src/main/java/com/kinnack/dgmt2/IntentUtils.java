package com.kinnack.dgmt2;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.kinnack.dgmt2.option.Option;

public class IntentUtils {
    private Context mContext;
    private Intent mIntent;

    public IntentUtils(Context context) {
        mContext = context;
        mIntent = new Intent(context, PingReceiver.class);
    }

    public Option<PendingIntent> pendingAlarm() {
        return Option.apply(PendingIntent.getBroadcast(mContext, 0, mIntent, PendingIntent.FLAG_NO_CREATE));
    }

    public PendingIntent newPendingAlarm() {
        return PendingIntent.getBroadcast(mContext, 0, mIntent, 0);
    }

}
