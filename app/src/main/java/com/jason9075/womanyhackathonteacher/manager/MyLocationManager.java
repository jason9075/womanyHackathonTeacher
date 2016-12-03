package com.jason9075.womanyhackathonteacher.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

/**
 * Created by jason9075 on 2016/12/3.
 */

public class MyLocationManager implements GoogleApiClient.ConnectionCallbacks, LocationListener {

    private final Context applicationContext;

    private static Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private final LocationManager locationManager;

    @Inject
    public MyLocationManager(Context context) {
        this.applicationContext = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mGoogleApiClient = new GoogleApiClient.Builder(applicationContext)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        start();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLastLocation == null) {
            RetrofitManager.INSTANCE.requestAddress(location, true);
        }
        mLastLocation = location;
    }

    public void start() {
        if (mGoogleApiClient.isConnected()) {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(0);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(applicationContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(applicationContext,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /* Public Methods */

    @Nullable
    public static Location tryGetLastLocation() {
        return mLastLocation ;
    }

    public boolean isLocationServiceEnable() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
