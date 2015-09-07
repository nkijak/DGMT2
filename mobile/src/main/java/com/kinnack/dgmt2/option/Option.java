package com.kinnack.dgmt2.option;

import java.util.Iterator;

/**
 * Created by prodrive555 on 4/18/15.
 */
public abstract class Option<T> implements Iterable<T> {
    protected T mValue;

    public <B> Option<B> map(Function1<T,B> f) {
        return apply(f.apply(mValue));
    }
    public <B> Option<B> flatmap(Function1<T, Option<B>> f) {
       return f.apply(mValue);
    }
    public Option<T> filter(Function1<T, Boolean> p) {
        return p.apply(mValue) ? this : None();
    }

    public T get() { return mValue; }

    public T getOrElse(T that) {
        T retval = get();
        return retval == null ? that: retval;
    }

    public static <A> Option<A> apply(A thing) {
        if (thing == null) return new Zero();
        return new One(thing);
    }


    public boolean canIterate() { return true; }

    public <B> B fold(B notThere, Function1<T,B> there) { return there.apply(mValue); }
    public <B> B catamorph(B notThere, Function1<T,B> there) { return fold(notThere, there); }
    public Option<T> or(Option<T> other) { return this;}

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private boolean hasNext = canIterate();
            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public T next() {
                hasNext = false;
                return mValue;
            }

            @Override
            public void remove() {
                //wat?
            }
        };
    }


    public boolean toBoolean() { return true; }

    static class One<T> extends Option<T>{
        public One(T value) {
            mValue = value;
        }

    }

    static class Zero<T> extends Option<T> {

        @Override
        public boolean canIterate() { return false; }

        @Override
        public boolean toBoolean() { return false; }

        @Override
        public T get() { return null; }

        @Override
        public <B> Option<B> map(Function1<T, B> f) {
            return new Zero<B>();
        }
        @Override
        public <B> Option<B> flatmap(Function1<T, Option<B>> f) {
            return new Zero<B>();
        }
        @Override
        public <B> B fold(B notThere, Function1<T,B> there) { return notThere; }
        @Override
        public Option<T> or(Option<T> other) { return other;}
        @Override
        public Option<T> filter(Function1<T, Boolean> p) { return this; }
    }

    public static <T> Option<T> Some(T value) { return new One(value); }
    public static Option None() { return new Zero(); }
}
