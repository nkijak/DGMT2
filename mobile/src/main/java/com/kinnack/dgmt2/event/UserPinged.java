package com.kinnack.dgmt2.event;

public class UserPinged {
    private long when;
    private int outstandingPings;

    public UserPinged(long when, int outstandingPings) { this.when = when; this.outstandingPings = outstandingPings; }

    public long getWhen() {
        return when;
    }

    public int getOutstandingPings() {
        return outstandingPings;
    }
}
