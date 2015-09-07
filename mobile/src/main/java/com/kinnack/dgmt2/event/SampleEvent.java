package com.kinnack.dgmt2.event;

import com.kinnack.dgmt2.model.Sample;
import com.kinnack.dgmt2.option.Function1;

//TODO this is really Either<IOException, Sample> (or is it Either<SampleFailed, SampleRecorded>?)
public interface SampleEvent extends Iterable<SampleSucceed> {
    Sample getSample();
    <B> B catamorph(Function1<SampleFailed, B> onFailure, Function1<SampleSucceed, B> onSuccess);
}
