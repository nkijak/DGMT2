package com.kinnack.dgmt2.event;

public class SampleStoredLocally {
    private long when;
    private String tags;
    private boolean synced;
    public SampleStoredLocally(long when, String tags, boolean synced) {
        this.when = when;
        this.tags = tags;
        this.synced = synced;
    }

    public long getWhen() {
        return when;
    }

    public String getTags() {
        return tags;
    }

    public boolean isSynced() {
        return synced;
    }
}
