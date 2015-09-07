package com.kinnack.dgmt2.option;

/**
 * Created by prodrive555 on 4/12/15.
 */
public class Left<L, R> extends Either<L, R> {
    private L value;
    public Left(L left) {
       value = left;
    }

    @Override
    public Either<L, R> flatmap(Function1<R, Either<L, R>> s) {
        return this;
    }

    @Override
    public <B> Either<L, B> map(Function1<R, B> f) {
        return new Left(this.value);
    }

    @Override
    public <B> B fold(Function1<L, B> f, Function1<? super R, B> s) {
        return f.apply(value);
    }


    @Override
    public boolean isRight() {
        return false;
    }
}
