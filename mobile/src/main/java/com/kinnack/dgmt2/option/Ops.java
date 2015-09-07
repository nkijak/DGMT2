package com.kinnack.dgmt2.option;

/**
 * Created by nicolas on 4/22/15.
 */
public class Ops {
    public static <L, R> Option<R> eitherToOption(Either<L, R> either) {
        return either
        .fold(new Function1<L, Option<R>>() {
            @Override
            public Option<R> apply(L left) {
                return Option.None();
            }
        }, new Function1<R, Option<R>>() {
            @Override
            public Option<R> apply(R right) {
                return Option.Some(right);
            }
        });
    }
}
