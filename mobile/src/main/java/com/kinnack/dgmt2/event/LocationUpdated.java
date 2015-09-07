package com.kinnack.dgmt2.event;

import android.location.Location;

import java.util.Date;

public class LocationUpdated {
    Location location;
    long asOf;

    public LocationUpdated(Location location) {
        this.location = location;
        this.asOf = new Date().getTime();
    }

    public Location getLocation() {
        return location;
    }

    public long getAsOf() {
        return asOf;
    }
}
