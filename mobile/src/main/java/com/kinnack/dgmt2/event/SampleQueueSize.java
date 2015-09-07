package com.kinnack.dgmt2.event;

/**
 * Created by nicolas on 4/22/15.
 */
public class SampleQueueSize {
    private int mSize;
    public SampleQueueSize(int size) {
       mSize = size;
    }

    public int getSize() { return mSize; }
}
