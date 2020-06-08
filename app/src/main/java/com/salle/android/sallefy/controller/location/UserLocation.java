package com.salle.android.sallefy.controller.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.salle.android.sallefy.model.LatLong;
import com.salle.android.sallefy.utils.Constants;
import com.salle.android.sallefy.utils.Session;

public class UserLocation {
    public static final String TAG = UserLocation.class.getName();

    private static UserLocation location;
    private final Context context;

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    Location lastLocation;

    public static UserLocation getInstance(Context c){
        if(location == null){
            location = new UserLocation(c);
        }
        return location;
    }

    public UserLocation(Context c){
        this.context = c;
        lastLocation = null;
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

    public LatLong getLocation() {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_MEDIUM);
            crit.setPowerRequirement(Criteria.POWER_MEDIUM);

            String provider = locationManager.getBestProvider(crit, true);
            
            if (provider != null && locationManager.isProviderEnabled(provider)) {

                @SuppressLint("MissingPermission") android.location.Location locationGPS = locationManager.getLastKnownLocation(provider);
                if (locationGPS != null) {
                    //Si la nueva posicion es mala, usa la anterior.
                    if (!isBetterLocation(locationGPS, lastLocation)) {
                        locationGPS = lastLocation;
                    }

                    lastLocation = locationGPS;

                    //Log.d(TAG, "getLocation:Your Location: " + "\n" + "Latitude: " + locationGPS.getLatitude() + "\n" + "Longitude: " +  locationGPS.getLongitude());
                    return new LatLong(locationGPS.getLatitude(), locationGPS.getLongitude());
                }
            }
            //No data obtained. Return last postion obtained.

            Log.d(TAG, "getLocation: USING LAST LOCATION. WE CANT GET NEW DATA.");
            if (lastLocation == null) return new LatLong();
            return new LatLong(lastLocation.getLatitude(), lastLocation.getLongitude());
        }catch (Exception e){
            if (lastLocation == null) return new LatLong();
            return new LatLong(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
