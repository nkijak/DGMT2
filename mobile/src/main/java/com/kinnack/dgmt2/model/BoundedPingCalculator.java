package com.kinnack.dgmt2.model;

import com.kinnack.dgmt2.option.Function1;

import java.util.Date;

/**
 * Created by prodrive555 on 4/19/15.
 */
public class BoundedPingCalculator implements PingCalculator {
    private int mMinMin;
    private int mMaxMin;
    private Gen<Long> nextPingInMillis = null;

    public BoundedPingCalculator(int minMin, int maxMin) {
        mMinMin = minMin;
        mMaxMin = maxMin;
        nextPingInMillis = rand.map(new Function1<Double, Double>() {
            @Override
            public Double apply(Double random) {
                return (random * (mMaxMin - mMinMin)) + mMinMin;
            }
        }).map(new Function1<Double, Long>() {
            @Override
            public Long apply(Double value) {
                return Math.round(value * 60 * 1000);
            }
        });
    }

    // I want this to be the idempotent. Given an instance of this calculator + a date the result should
    // always be the same.  At the same time I want to avoid patterns in the resulting value.
    // C+Dn=Pms !=> C+Dn+1=Pms+(Xms+1)
    @Override
    public long nextPingInMillis(Date previous) {
        return nextPingInMillis.generate();
    }

    private static Gen<Double> rand = new Gen<Double>() {
        @Override
        public Double generate() {
            return Math.random();
        }
    };



}
