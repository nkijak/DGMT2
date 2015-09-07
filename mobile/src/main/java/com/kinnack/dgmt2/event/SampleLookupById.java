package com.kinnack.dgmt2.event;

import com.kinnack.dgmt2.model.Sample;
import com.kinnack.dgmt2.option.Function1;
import com.kinnack.dgmt2.option.Option;

import static com.kinnack.dgmt2.option.Option.None;

public class SampleLookupById {

    private long id;
    protected State state;

    public SampleLookupById(long id) {
        this.id = id;
        this.state = new Pending(0);
    }

    private SampleLookupById(long id, State state) {
        this.id = id;
        this.state = state;
    }

    public SampleLookupById resolve(final Option<Sample> sample) {
        this.state = state.resolve(sample);
        return this;
    }

    public SampleLookupById deny() {
        this.state = new Resolved(None(), Integer.MAX_VALUE, this.state.completedCb);
        return this;
    }

    public void onComplete(Function1<Option<Sample>, Void> cb) { this.state.onComplete(cb); }


    public long getId() {
        return id;
    }

    public Option<Sample> getSample() {
        return state.sample;
    }

    public int getAttempts()  { return state.attempts; }


    private static abstract class State {
        Option<Function1<Option<Sample>, Void>> completedCb = None();
        int attempts;
        Option<Sample> sample = None();
        protected State resolve(final Option<Sample> sample) {
            completedCb.map(new Function1<Function1<Option<Sample>,Void>, Void>() {
                @Override
                public Void apply(Function1<Option<Sample>, Void> cb) {
                    cb.apply(sample);
                    return null;
                }
            });
            return new Resolved(sample, attempts + 1, completedCb);
        }

        public void onComplete(Function1<Option<Sample>, Void> cb) {
            this.completedCb = Option.apply(cb);
        }
    }

    private static class Resolved extends State {
        protected Resolved(Option<Sample> sample, int attempts, Option<Function1<Option<Sample>, Void>> completedCb) {
            this.sample = sample;
            this.attempts = attempts;
            this.completedCb = completedCb;
        }
        @Override
        public State resolve(Option<Sample> sample) {
            throw new IllegalStateException("has already been resolved!");
        }

        @Override
        public void onComplete(Function1<Option<Sample>, Void> cb) {
            this.completedCb = Option.apply(cb);
            resolve(this.sample); //discard the result
        }
    }

    private static class Pending extends State {
        protected Pending(int attempts) {
            this.attempts = attempts;
        }
    }
}
