package com.kinnack.dgmt2.service;

import android.content.Context;


public class RecordRepository {
    public static void recordEntry(Context context, String tags, String type, int count, long when) {
        LocationProviderService.produceLocation(context, when, type);
        RecordHandlerService.recordRecord(context, type, count, when);
    }
}
