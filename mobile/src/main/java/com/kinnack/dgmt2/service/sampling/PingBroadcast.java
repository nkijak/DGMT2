package com.kinnack.dgmt2.service.sampling;

import android.content.Context;

import com.kinnack.dgmt2.SampleRequestNotification;
import com.kinnack.dgmt2.event.UserPinged;
import com.kinnack.dgmt2.service.LocationProviderService;

public class PingBroadcast {

    private static final String TAG = "PingBroadcast";
    private PingBroadcast() {}

    public static void userPinged(Context context, UserPinged pinged) {
        //LocationProviderService.produceLocation(context, pinged.getWhen());
        SampleRequestNotification.notify(context, pinged.getWhen(), pinged.getOutstandingPings());
    }


}

