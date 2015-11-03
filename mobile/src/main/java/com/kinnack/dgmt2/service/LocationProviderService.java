package com.kinnack.dgmt2.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.kinnack.dgmt2.DGMT2;
import com.kinnack.dgmt2.event.LocationStoredLocally;
import com.kinnack.dgmt2.event.LocationUpdated;
import com.kinnack.dgmt2.option.Function1;
import com.kinnack.dgmt2.option.Option;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import java.util.Date;

import static com.kinnack.dgmt2.option.Option.None;

public class LocationProviderService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String ACTION_PRODUCE_LOCATION = "com.kinnack.dgmt2.service.action.PRODUCT_LOCATION";
    private static final String EXTRA_WHEN              = "com.kinnack.dgmt2.service.extra.WHEN";
    private static final String EXTRA_TYPE              = "com.kinnack.dgmt2.service.extra.TYPE";
    private static final int LOCATION_INTERVAL = 5000;
    private static final int LOCATION_FAST_INTERVAL = 1000;

    private Bus bus;
    private GoogleApiClient googleApiClient;
    private Option<SnappyRepo> dbHelper = None();

    public static void produceLocation(Context context, long forWhen, String type) {
        Intent intent = new Intent(context, LocationProviderService.class);
        intent.setAction(ACTION_PRODUCE_LOCATION);
        intent.putExtra(EXTRA_WHEN, forWhen);
        intent.putExtra(EXTRA_TYPE, type);
        context.startService(intent);
    }

    public LocationProviderService() {
        super("LocationProviderService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DGMT2 dgmt2 = ((DGMT2) getApplication());
        bus = dgmt2.getBus();

        dbHelper = dgmt2.getRecordRepo();

        Log.d("LocationProviderService", "Building client");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PRODUCE_LOCATION.equals(action)) {
                handleLocationProduction(intent.getLongExtra(EXTRA_WHEN, new Date().getTime()), intent.getStringExtra(EXTRA_TYPE));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void handleLocationProduction(final long when, final String type) {
        Log.d("LocationProviderService", "Location Requested");
        try {
            while (lastLocation == null || (when - lastLocation.getTime()) > LOCATION_INTERVAL) {
                Thread.sleep(LOCATION_FAST_INTERVAL);
                if (lastLocation != null) Log.d("LocationProviderService", "Waiting for " + (when - lastLocation.getTime()) + " to be > " + LOCATION_INTERVAL + " " + ((when - lastLocation.getTime()) > LOCATION_INTERVAL));
            }
        } catch (InterruptedException ie) {
            Log.i("LocationProviderService", "Thread interupted while waiting for location", ie);
        }
        for(Location loc: dbHelper.map(new Function1<SnappyRepo, Location>() {
            @Override
            public Location apply(SnappyRepo repo) {
                repo.store(type + ":" + when, new com.kinnack.dgmt2.model.Location(lastLocation));
                return lastLocation;
            }
        })){ bus.post(new LocationStoredLocally(when, loc)); }
    }

    private Location lastLocation;
    @Override
    public void onConnected(Bundle bundle)  {
        Log.d("LocationProviderService", "Connected");
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        Log.d("LocationProviderService", "Posting: " + lastLocation);
        bus.post(lastKnownLocation());
        startLocationUpdates();
    }


    @Override
    public void onLocationChanged(Location location) {
       Log.d("LocationProviderService", "LocationChanged");
        lastLocation = location;
        for (LocationUpdated loc : lastKnownLocation()) { bus.post(loc); }
    }

    @Produce public Option<LocationUpdated> lastKnownLocation() {
        Log.d("LocationProviderService", "Last know requested :" + lastLocation);
        return lastLocation == null ? Option.None() : Option.Some(new LocationUpdated(lastLocation));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("LocationProviderService", "Connection Failed: " + connectionResult.toString());
    }


    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_FAST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

}
