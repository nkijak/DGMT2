package com.kinnack.dgmt2.service.sampling;

import android.provider.BaseColumns;

import java.util.Arrays;

public class SampleStorageContract {
    public SampleStorageContract(){}

    public static abstract class SampleEntry implements BaseColumns {
        public static final String TABLE_NAME = "samples";
        public static final String COLUMN_TAGS = "tags";
        public static final String COLUMN_SYNCED = "synced";
        public static final String COLUMN_LAT = "location_latitude";
        public static final String COLUMN_LONG = "location_longitude";
        public static final String COLUMN_SPEED = "location_speed";
        public static final String COLUMN_BEARING = "location_bearing";
        public static final String COLUMN_TIME = "location_time";
        public static final String COLUMN_ACCURACY = "location_accuracy";
        public static final String COLUMN_EXERCISE_COUNT = "exercise_count";
        public static final String COLUMN_EXERCISE_TYPE = "exercise_pushup";
        public static final String[] STAR = new String[] {
            _ID, COLUMN_TAGS, COLUMN_SYNCED,
            COLUMN_LAT, COLUMN_LONG, COLUMN_SPEED, COLUMN_BEARING, COLUMN_TIME, COLUMN_ACCURACY, COLUMN_EXERCISE_COUNT, COLUMN_EXERCISE_TYPE
        };
    }

    public static abstract class TagCountEntry {
        public static final String TABLE_NAME = "tag_counts";
        public static final String COLUMN_TAG = "tag";
        public static final String COLUMN_SAMPLE_ID = "sample_id";
    }
    public static final String SQL_CREATE_TABLE = "CREATE TABLE %s (%s)";
    public static final String SQL_ADD_COLUMN = "ALTER TABLE %s ADD %s %s";

    public static final String MIGRATE_0001_CREATE_SAMPLES =
            String.format(
                    SQL_CREATE_TABLE,
                    SampleEntry.TABLE_NAME,
                    SampleEntry._ID + " INTEGER PRIMARY KEY, " + SampleEntry.COLUMN_TAGS + " TEXT, " + SampleEntry.COLUMN_SYNCED + " INTEGER"
                    );

    public static final String MIGRATE_0002_CREATE_TAG_COUNTS =
            String.format(
                    SQL_CREATE_TABLE,
                    TagCountEntry.TABLE_NAME,
                    TagCountEntry.COLUMN_TAG + " TEXT, " + TagCountEntry.COLUMN_SAMPLE_ID + " INTEGER"
            );

    public static final String MIGRATE_0003_ADD_LONGITUDE =
            String.format(
                    SQL_ADD_COLUMN,
                    SampleEntry.TABLE_NAME,
                    SampleEntry.COLUMN_LONG,
                    "REAL");

    public static final String MIGRATE_0004_ADD_LATITUDE =
            String.format(
                    SQL_ADD_COLUMN,
                    SampleEntry.TABLE_NAME,
                    SampleEntry.COLUMN_LAT,
                    "REAL");

    public static final String MIGRATE_0005_ADD_SPEED =
            String.format(
                    SQL_ADD_COLUMN,
                    SampleEntry.TABLE_NAME,
                    SampleEntry.COLUMN_SPEED,
                    "REAL");

    public static final String MIGRATE_0006_ADD_BEARING =
            String.format(
                    SQL_ADD_COLUMN,
                    SampleEntry.TABLE_NAME,
                    SampleEntry.COLUMN_BEARING,
                    "REAL");

    public static final String MIGRATE_0007_ADD_TIME =
            String.format(
                    SQL_ADD_COLUMN,
                    SampleEntry.TABLE_NAME,
                    SampleEntry.COLUMN_TIME,
                    "INTEGER");

    public static final String MIGRATE_0008_ADD_ACCURACY =
            String.format(
                    SQL_ADD_COLUMN,
                    SampleEntry.TABLE_NAME,
                    SampleEntry.COLUMN_ACCURACY,
                    "REAL");

    public static final String MIGRATE_0009_ADD_EXERCISE_COUNT =
            String.format(
                    SQL_ADD_COLUMN,
                    SampleEntry.TABLE_NAME,
                    SampleEntry.COLUMN_EXERCISE_COUNT,
                    "INTEGER");

    public static final String MIGRATE_0010_ADD_EXERCISE_TYPE =
            String.format(
                    SQL_ADD_COLUMN,
                    SampleEntry.TABLE_NAME,
                    SampleEntry.COLUMN_EXERCISE_TYPE,
                    "TEXT");
}
