package com.kinnack.dgmt2.service.sampling;

import android.content.Context;

import com.kinnack.dgmt2.event.SampleLookupById;
import com.kinnack.dgmt2.event.SampleRecorded;
import com.kinnack.dgmt2.event.SampleTagsUpdated;
import com.kinnack.dgmt2.model.Sample;
import com.kinnack.dgmt2.option.Option;
import com.kinnack.dgmt2.service.SampleStorage;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.WeakHashMap;

public class LocalSampleServiceDecorator implements SampleStorage {
    private Context context;
    private SampleStorage storage;
    private WeakHashMap<Long, SampleLookupById> lookups;

    public LocalSampleServiceDecorator(Context context, SampleStorage storage, Bus bus) {
        this.context = context;
        this.storage = storage;
        this.lookups = new WeakHashMap<Long, SampleLookupById>();
        bus.register(this);
    }

    @Override
    public boolean recordSample(Sample sample) {
        PingAuditService.editSample(context, sample);
        storage.recordSample(sample);
        return true;
    }

    @Override
    public boolean updateTags(Sample sample) {
        PingAuditService.editSample(context, sample);
        storage.updateTags(sample);
        return true;
    }

    @Override
    public SampleLookupById find(SampleLookupById lookup) {
        return add(lookup); //Option.apply(lookups.get(lookup.getId())).getOrElse(add(lookup));
    }

    private SampleLookupById add(SampleLookupById lookup) {
        lookups.put(lookup.getId(), lookup);
        PingAuditService.findSample(context, lookup.getId());
        return lookup;
    }

    @Subscribe public void sampleRecorded(SampleRecorded event) {
        PingAuditService.sampleSync(context, event.getSample());
    }

    @Subscribe public void sampleTagsUpdated(SampleTagsUpdated event) {
        PingAuditService.sampleSync(context, event.getSample());
    }

    @Subscribe public void sampleLookupById(SampleLookupById event) {
        for(SampleLookupById lookup : Option.apply(lookups.get(event.getId()))) {
           lookup.resolve(event.getSample());
        }
        lookups.remove(event.getId());
    }
}
