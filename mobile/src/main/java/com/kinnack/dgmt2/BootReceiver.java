package com.kinnack.dgmt2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d("BootReceiver", "Received boot intent");
            //FIXME how do I get the date of the last alarm? DB? Settings?
            ((DGMT2) context.getApplicationContext()).getPingGenerator().setNextAlarm(context, new Date());
        }
    }
}
