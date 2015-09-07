package com.kinnack.dgmt2.service;

import android.util.Log;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.kinnack.dgmt2.model.Location;
import com.kinnack.dgmt2.model.Record;
import com.kinnack.dgmt2.option.Option;
import com.snappydb.DB;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

public class SnappyRepo {
    private DB snappy;

    public SnappyRepo(DB snappy) {
       this.snappy = snappy;
    }

    public boolean store(Record record) {
        try {
            return insertRecord(record);
        } catch (SnappydbException sde) {
            Log.e("SnappyRepo#store", "could not store record", sde);
            return false;
        }
    }

    public boolean store(String key, Location location) {
        try {
            return insertLocation(key, location);
        } catch (SnappydbException sde) {
            Log.e("SnpapyRepo#store", "could not store location", sde);
            return false;
        }
    }

    public Collection<Record> query(String type, Date start, Date end) {
        try {
            List<String> keys = Arrays.asList(snappy.findKeysBetween(type + ":" + start.getTime(), type + ":" + end.getTime()));
            Collection<String> primaries = filter(keys, new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    return input.matches("(\\w+:\\w){1}");
                }
            });

            return flatMap(primaries, new Function<String, Option<Record>>() {
                @Override
                public Option<Record> apply(String input) {
                    String[] parts = input.split(":");
                    try {
                        Location location = new Location(
                                snappy.getDouble(input + ":latitude"),
                                snappy.getDouble(input + ":longitude"),
                                snappy.getFloat(input + ":speed"),
                                snappy.getFloat(input + ":heading"),
                                snappy.getFloat(input + ":accuracy"),
                                snappy.getLong(input + ":locationtime"));
                        return Option.Some(
                                new Record(
                                        Long.parseLong(parts[1]),
                                        snappy.getInt(input),
                                        parts[0],
                                        location));
                    } catch (SnappydbException sde) {
                        Log.w("SnappyRepo#query", "Exception gathering parts to make record", sde);
                        return Option.None();
                    } catch (NumberFormatException nfe) {
                        Log.w("SnappyRepo#query", "Could not parse " + parts[1] + " to long");
                        return Option.None();
                    }
                }
            });
        } catch (SnappydbException sde) {
            Log.e("SnappyRepo#query", "could query records", sde);
            return new ArrayList<Record>();
        }
    }

    private <I,E> Collection<E> flatMap(Collection<I> input, Function<I, Option<E>> flatmap) {
        return transform(filter(transform(input, flatmap), new Predicate<Option<E>>() {
                @Override
                public boolean apply(Option<E> input) {
                    return input.canIterate();
                }
            }), new Function<Option<E>, E>() {
                @Override
                public E apply(Option<E> input) {
                    return input.get();
                }
            });
    }

    protected boolean insertLocation(String key, Location location) throws SnappydbException {
        snappy.putDouble(key + ":latitude",     location.getLatitude());
        snappy.putDouble(key + ":longitude",    location.getLongitude());
        snappy.putFloat(key  + ":speed",        location.getSpeed());
        snappy.putFloat(key  + ":heading",      location.getBearing());
        snappy.putFloat(key  + ":accuracy",     location.getAccuracy());
        snappy.putLong(key   + ":locationtime", location.getTime());
        return true;
    }

    protected boolean insertRecord(Record record) throws SnappydbException {
        String primaryKey = record.getType() + ":" + record.getWhen();
        snappy.putInt(primaryKey, record.getCount());
        return record.getLocation() == null ? true : insertLocation(primaryKey, record.getLocation());
    }

}
