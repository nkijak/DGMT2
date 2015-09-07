package com.kinnack.dgmt2.event;

import com.kinnack.dgmt2.model.Sample;
import com.kinnack.dgmt2.option.Function1;

import java.util.Iterator;

import static com.kinnack.dgmt2.option.Option.Some;

public class SampleTagsUpdated implements SampleEvent {
    private Sample sample;

    public Sample getSample() {
        return sample;
    }

    @Override
    public <B> B catamorph(Function1<SampleFailed, B> onFailure, Function1<SampleSucceed, B> onSuccess) {
        return onSuccess.apply((SampleSucceed)this);
    }


    public SampleTagsUpdated(Sample sample) {
        this.sample = sample;
    }

    public String toString() {
        return "SampleTagsUpdated: {"+sample.toString()+"}";
    }

    public static final Function1<Sample, SampleEvent> ToSampleTagsUpdated =  new Function1<Sample, SampleEvent>() {
        @Override
        public SampleTagsUpdated apply(Sample sample) {
            return new SampleTagsUpdated(sample);
        }
    };

    @Override
    public Iterator<SampleSucceed> iterator() {
        return Some((SampleSucceed)this).iterator();
    }
}
