package com.kinnack.dgmt2.event;

import android.location.Location;

public class LocationStoredLocally {
    private long when;
    private Location location;
    public LocationStoredLocally(long when, Location location)  { this.when = when; this.location = location; }

    public long getWhen() {
        return when;
    }

    public Location getLocation() {
        return location;
    }
}
