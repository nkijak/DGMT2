package com.kinnack.dgmt2.event;

import com.kinnack.dgmt2.model.Sample;
import com.kinnack.dgmt2.option.Function1;

import java.io.IOException;
import java.util.Iterator;

import static com.kinnack.dgmt2.option.Option.None;

/**
 * Created by nicolas on 4/19/15.
 */
public class SampleFailed implements SampleEvent {


    private Sample mSample;
    private Throwable mFailure;

    public SampleFailed(Sample sample, Throwable failure) {
        mSample = sample;
        mFailure = failure;
    }

    @Override
    public Sample getSample() {
        return mSample;
    }

    public static final Function1<IOException, SampleEvent> ToSampleFailed(final Sample sample) {

        return new Function1<IOException, SampleEvent>() {
            @Override
            public SampleFailed apply(IOException ioe) {
                return new SampleFailed(sample, ioe);
            }
        };
    }

    @Override
    public <B> B catamorph(Function1<SampleFailed, B> onFailure, Function1<SampleSucceed, B> onSuccess) {
        return onFailure.apply(this);
    }

    @Override
    public Iterator<SampleSucceed> iterator() {
        return None().iterator();
    }
}
