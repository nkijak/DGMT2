package com.kinnack.dgmt2.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinnack.dgmt2.DGMT2;
import com.kinnack.dgmt2.IntentUtils;
import com.kinnack.dgmt2.service.sampling.PingAuditService;

import java.util.Date;

public class PingReceiver extends BroadcastReceiver {
    public PingReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        PingAuditService.pingUser(context, new Date().getTime());

        cancelCurrentAlarm(context);
        ((DGMT2) context.getApplicationContext()).getPingGenerator().setNextAlarm(context, new Date());
    }

    private void cancelCurrentAlarm(Context context) {
        IntentUtils utils = new IntentUtils(context);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (PendingIntent intent : utils.pendingAlarm()) {
            intent.cancel();
            manager.cancel(intent);
        }
    }
}
