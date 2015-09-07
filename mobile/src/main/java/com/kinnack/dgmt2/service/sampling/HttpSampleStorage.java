package com.kinnack.dgmt2.service.sampling;

import android.util.Log;

import com.kinnack.dgmt2.event.SampleEvent;
import com.kinnack.dgmt2.event.SampleFailed;
import com.kinnack.dgmt2.event.SampleLookupById;
import com.kinnack.dgmt2.event.SampleRecorded;
import com.kinnack.dgmt2.event.SampleTagsUpdated;
import com.kinnack.dgmt2.model.Sample;
import com.kinnack.dgmt2.option.Function1;
import com.kinnack.dgmt2.service.SampleHttpService;
import com.kinnack.dgmt2.service.SampleStorage;
import com.squareup.otto.Bus;

import java.io.IOException;

public class HttpSampleStorage implements SampleStorage {
    private SampleHttpService sampleHttpService;
    private Bus bus;

    public HttpSampleStorage(SampleHttpService httpService, Bus bus)  {
        this.bus = bus;
        sampleHttpService = httpService;
    }


    @Override
    public boolean recordSample(final Sample sample) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bus.post(sampleHttpService.recordSample(sample)
                        .fold(new Function1<IOException, SampleEvent>() {
                            @Override
                            public SampleEvent apply(IOException ioe) {
                                Log.w("SampleHttpSrvsAdapter", "Failed to record sample: ", ioe);
                                return new SampleFailed(sample, ioe);
                            }
                        }, SampleRecorded.ToSampleRecorded));
            }
        }).start();
        return true;
    }

    @Override
    public boolean updateTags(final Sample sample) {
        bus.post(sampleHttpService.updateTags(sample).fold(new Function1<IOException, SampleEvent>() {
            @Override
            public SampleEvent apply(IOException ioe) {
                Log.w("SampleHttpSrvsAdapter", "Failed to update tags: ", ioe);
                return new SampleFailed(sample, ioe);
            }
        }, SampleTagsUpdated.ToSampleTagsUpdated));
        return true;
    }

    @Override
    public SampleLookupById find(SampleLookupById lookup) {
        return lookup.deny();
    }
}
