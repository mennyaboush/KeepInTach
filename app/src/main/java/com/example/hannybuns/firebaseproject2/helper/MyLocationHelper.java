package com.example.hannybuns.firebaseproject2.helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.hannybuns.firebaseproject2.KeepInTouchLogic;
import com.example.hannybuns.firebaseproject2.Keys;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseUser;

//import com.example.hannybuns.firebaseproject2.KeepInTouchLogic;

public class MyLocationHelper extends LocationCallback implements LocationListener {

    private android.location.Location currentLocation;
    private boolean didAlreadyRequestLocationPermission;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private LocationManager locationManager;
    MyfirebaseHelper databaseReference;
    Context context;
    FusedLocationProviderClient mFusedLocationClient;

    public MyLocationHelper(Context context) {
        if(context ==null)return;
        didAlreadyRequestLocationPermission = false;
        this.context = context;
        this.databaseReference = KeepInTouchLogic.getInstance().getMyfirebaseHelperRef();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        getCurrentLocation(context);
        saveUserLocation();

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        databaseReference.removeUserLocation(currentLocation.getLongitude(), currentLocation.getLatitude());
        currentLocation = location;
        saveUserLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }

    public void setCurrentLocation() {
        getCurrentLocation(context);
    }

    // get current location by permission
    public void getCurrentLocation(Context context) {
        boolean isAccessGranted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
            String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
            if (context.checkSelfPermission(fineLocationPermission) != PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(coarseLocationPermission) != PackageManager.PERMISSION_GRANTED) {
                // The user blocked the location services of THIS app / not yet approved
                isAccessGranted = false;
                if (!didAlreadyRequestLocationPermission) {
                    didAlreadyRequestLocationPermission = true;
                    String[] permissionsToAsk = new String[]{fineLocationPermission, coarseLocationPermission};
                    ((FragmentActivity) context).requestPermissions(permissionsToAsk, LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                isAccessGranted = true;
            }

            if (isAccessGranted) {
                float metersToUpdate = 1;
                long intervalMilliseconds = 1000;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, intervalMilliseconds, metersToUpdate, this);
                if (currentLocation == null) {
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        }
    }

    public android.location.Location getCurrentLocation() {
        return currentLocation;
    }

    public void removeUpdates() {
        locationManager.removeUpdates(this);
    }

    private void saveUserLocation() {
        FirebaseUser user = KeepInTouchLogic.getInstance().getMyfirebaseHelperRef().getUser();
        if(currentLocation!=null && user!=null) {
            double userLongitude = currentLocation.getLongitude();
            double userLatitude = currentLocation.getLatitude();
            databaseReference.addToFirebase(Keys.USERS + "/" + user.getUid() + "/" +
                    Keys.LOCATION + "/" + Keys.LONGITUDE, String.valueOf(userLongitude));
            databaseReference.addToFirebase(Keys.USERS + "/" +user.getUid() + "/" +
                    Keys.LOCATION + "/" + Keys.LATITUDE, String.valueOf(userLatitude));
            databaseReference.addUserLocation(userLongitude, userLatitude);
        }
    }
}

