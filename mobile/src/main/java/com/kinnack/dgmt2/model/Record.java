package com.kinnack.dgmt2.model;

import java.util.Date;

public class Record {
    private long when;
    private int count;
    private String type;
    private Location location;

    public Record(long when, int count, String type, Location location) {
        this.when = when;
        this.count = count;
        this.type = type;
        this.location = location;
    }
    public Record(long when, int count, String type) {
        this(when,count, type, null);
    }

    public long getWhen() {
        return when;
    }

    public int getCount() {
        return count;
    }

    public String getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }
}
