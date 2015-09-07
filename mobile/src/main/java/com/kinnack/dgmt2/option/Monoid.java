package com.kinnack.dgmt2.option;

/**
 * Created by prodrive555 on 4/18/15.
 */
public interface Monoid<T> {
    T identity();
    Monoid<T> associate(Monoid<T> other);

    //HACK
    T get();
}
