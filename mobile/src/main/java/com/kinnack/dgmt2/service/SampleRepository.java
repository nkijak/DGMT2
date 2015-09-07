package com.kinnack.dgmt2.service;

import android.util.Log;

import com.kinnack.dgmt2.event.SampleFailed;
import com.kinnack.dgmt2.event.SampleLookupById;
import com.kinnack.dgmt2.event.SampleRecorded;
import com.kinnack.dgmt2.model.Sample;
import com.kinnack.dgmt2.option.Function1;
import com.kinnack.dgmt2.option.Option;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;

public class SampleRepository {
    private Bus bus;
    private SampleStorage storage;

    public SampleRepository(Bus bus, SampleStorage storage) {
       this.bus = bus;
       register(this);
       this.storage = storage;
    }

    public void register(Object object) {
        bus.register(object);
    }

    public boolean recordSample(final Sample sample) {
        Log.d("SampleRepo", String.format("Received sample: %s, %s", sample.getUserId(), new Date(sample.getWhen())));

        return storage.recordSample(sample);
    }

    public boolean updateTags(final Sample sample) {
        Log.d("SampleRepo", String.format("UPdating sample: $s", sample.toString()));
        return storage.updateTags(sample);
    }

    public void find(long id, Function1<Option<Sample>, Void> cb) {
        storage.find(new SampleLookupById(id)).onComplete(cb);
    }

    @Subscribe public void sampleRecorded(SampleRecorded event) {
        Log.d("SampleRepo", "Treating event as truth>> " + event.toString());
    }

    @Subscribe public void sampleFailed(SampleFailed event) {
        Log.i("SampleRepo", "Sample recording failure. Mark for retry");
    }

    public void unregister(Object object) {
        bus.unregister(object);
    }


}
