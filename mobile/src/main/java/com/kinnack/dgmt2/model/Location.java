package com.kinnack.dgmt2.model;

public class Location {
    private double latitude;
    private double longitude;
    private float speed;
    private float bearing;
    private float accuracy;
    private long time;

    public Location(android.location.Location location) {
        this(location.getLatitude(), location.getLongitude(), location.getSpeed(), location.getBearing(), location.getAccuracy(), location.getTime());
    }
    public Location(double latitude, double longitude, float speed, float bearing, float accuracy, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.bearing = bearing;
        this.accuracy = accuracy;
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public float getBearing() {
        return bearing;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public long getTime() {
        return time;
    }
}
