package com.example.weatherapp1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class LocationService extends Service implements LocationListener {
    protected LocationManager locationManager;
    Location location;

    private static final long min_distUpdate = 10;
    private static final long min_TimeUpdate = 1000 * 60 * 2;
    private static  final int REQUEST_LOCATION = 1;

    Context mContext;
    public LocationService(Context context) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        mContext = context;
    }

    // Programmatically writing PERMISSIONS
    public Location getLocation(String provider) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {
            locationManager.requestLocationUpdates(provider, min_TimeUpdate, min_distUpdate, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(provider);
                return location;
            }
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}