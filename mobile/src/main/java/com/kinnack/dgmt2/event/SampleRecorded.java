package com.kinnack.dgmt2.event;

import com.kinnack.dgmt2.model.Sample;
import com.kinnack.dgmt2.option.Function1;

import java.util.Iterator;

import static com.kinnack.dgmt2.option.Option.Some;

public class SampleRecorded implements SampleEvent, SampleSucceed {
    private Sample sample;

    public Sample getSample() {
        return sample;
    }


    public SampleRecorded(Sample sample) {
        this.sample = sample;
    }

    public String toString() {
        return "SampleRecored: {"+sample.toString()+"}";
    }

    public static final Function1<Sample, SampleEvent> ToSampleRecorded =  new Function1<Sample, SampleEvent>() {
        @Override
        public SampleEvent apply(Sample sample) {
            return new SampleRecorded(sample);
        }
    };

    @Override
    public Iterator<SampleSucceed> iterator() {
        return Some((SampleSucceed)this).iterator();
    }

    @Override
    public <B> B catamorph(Function1<SampleFailed, B> onFailure, Function1<SampleSucceed, B> onSuccess) {
        return onSuccess.apply(this);
    }

}
