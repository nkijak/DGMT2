package com.kinnack.dgmt2.model;

import com.kinnack.dgmt2.option.Function1;

/**
 * Created by prodrive555 on 4/19/15.
 */

public class Gen<T> {
    public T generate() {return null;}

    public <S> Gen<S> map(final Function1<T,S> f) {
        return new Gen<S>() {
            @Override
            public S generate() {
                return f.apply(Gen.this.generate());
            }
        };
    }

    public <S> Gen<S> flatmap(final Function1<T,Gen<S>> f) {
        return new Gen<S>() {
            @Override
            public S generate() {
                return f.apply(Gen.this.generate()).generate();
            }
        };
    }
}