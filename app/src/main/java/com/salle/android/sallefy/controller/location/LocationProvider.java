package com.salle.android.sallefy.controller.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.Session;

public class LocationProvider {

    private static LocationProvider location;
    private final Context context;

    public static LocationProvider getInstance(Context c){
        if(location == null){
            location = new LocationProvider(c);
        }
        return location;
    }

    public LocationProvider(Context c){
        this.context = c;
    }

    public void getLatitude(){

    }

    public void getLongitude(){

    }

    public void requestLocationPermissions(Activity activity) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSIONS.LOCATION
            );
        }else{
            Session.getInstance(context).setLocationEnabled(true);
        }
    }
/*

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.PERMISSIONS.LOCATION);
            } else {

                android.location.Location locationGPS = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (locationGPS != null) {
                    double lat = locationGPS.getLatitude();
                    double longi = locationGPS.getLongitude();
                    String latitude = String.valueOf(lat);
                    String longitude = String.valueOf(longi);
                    Log.d(TAG, "getLocation:Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
                } else {
                    Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/
}
