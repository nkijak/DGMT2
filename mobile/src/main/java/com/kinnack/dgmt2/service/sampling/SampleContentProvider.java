package com.kinnack.dgmt2.service.sampling;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.kinnack.dgmt2.DGMT2;

import static com.kinnack.dgmt2.QueryBuilder.select;
import static com.kinnack.dgmt2.QueryBuilder.using;
import static com.kinnack.dgmt2.service.sampling.SampleStorageContract.SampleEntry.TABLE_NAME;

public class SampleContentProvider extends ContentProvider {
    private SampleStorageDbHelper helper;

    public SampleContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        this.helper = ((DGMT2)getContext().getApplicationContext()).getSampleStorageDbHelper();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        return using(db, select(projection).from(TABLE_NAME).replacedBy(selectionArgs).orderBy(sortOrder));
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
