package com.kinnack.dgmt2.service;

import com.kinnack.dgmt2.event.SampleLookupById;
import com.kinnack.dgmt2.model.Sample;

public interface SampleStorage {
    boolean recordSample(Sample sample);
    boolean updateTags(Sample sample);
    SampleLookupById find(SampleLookupById lookup);
}

