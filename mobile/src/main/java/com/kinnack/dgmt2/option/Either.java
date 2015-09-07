package com.kinnack.dgmt2.option;

public abstract class Either<L,R> {
    //TODO return values should be R or super(R) and L or Super(L)
    public abstract Either<L, R> flatmap(final Function1<R, Either<L, R>> s);
    public abstract<B> Either<L, B> map(final Function1<R, B> f);
    public abstract <B> B fold(final Function1<L, B> f, Function1<? super R, B> s);
    public void foreach(final Function1<R, Void> f) {
        flatmap(new Function1<R, Either<L, R>>() {
            @Override
            public Either<L, R> apply(R element) {
                f.apply(element);
                return new Right(element);
            }
        });
    }

    public boolean isRight() { return true; }

}
