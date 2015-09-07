package com.kinnack.dgmt2.option;

/**
 * Created by prodrive555 on 4/12/15.
 */
public class Right<R> extends Either<Object, R> {
    private R value;
    public Right(R right) {
        value = right;
    }

    @Override
    public Either<Object, R> flatmap(Function1<R, Either<Object, R>> s) {
        return s.apply(value);
    }

    @Override
    public <B> Either<Object, B> map(Function1<R, B> f) {
        return new Right(f.apply(value));
    }

    @Override
    public <B> B fold(Function1<Object, B> f, Function1<? super R, B> s) {
        return s.apply(value);
    }

    //HACK
    public R get() { return value; }
}

