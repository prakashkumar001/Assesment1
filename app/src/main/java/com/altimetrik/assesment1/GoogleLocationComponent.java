package com.altimetrik.assesment1;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


/**
 * stand alone component for location updates
 */
public class GoogleLocationComponent {

    private static final String TAG = GoogleLocationComponent.class.getSimpleName();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5 * 1000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            2000;

    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    /**
     * The current location.
     */
    private Location mLocation;

    public ILocationProvider iLocationProvider;

    Context context;

    public GoogleLocationComponent(ILocationProvider iLocationProvider, Context context) {
        this.iLocationProvider = iLocationProvider;
        this.context = context;
    }

    /**
     * create first time to initialize the location components
     *
     * @param context
     */
    public void onCreate(Context context) {
        Log.i(TAG, "created...............");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG, "onCreate...onLocationResult...............loc " + locationResult.getLastLocation());

                onNewLocation(locationResult.getLastLocation());
            }
        };
        // create location request
        createLocationRequest();
        // get last known location
        getLastLocation();
    }

    /**
     * start location updates
     */
    public void onStart() {
        Log.i(TAG, "onStart ");
        //hey request for location updates
        requestLocationUpdates();
    }

    /**
     * remove location updates
     */
    public void onStop() {
        Log.i(TAG, "onStop....");
        removeLocationUpdates();
    }


    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.getMainLooper());
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }


    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//            Utils.setRequestingLocationUpdates(this, false);
//            stopSelf();
        } catch (SecurityException unlikely) {
//            Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    /**
     * get last location
     */
    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                                Log.i(TAG, "getLastLocation " + mLocation);
//                                Toast.makeText(getApplicationContext(), "" + mLocation, Toast.LENGTH_SHORT).show();
                                onNewLocation(mLocation);
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
      //  Toast.makeText(context, String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
        Log.i(TAG, "New location: " + location);
//        Toast.makeText(getApplicationContext(), "onNewLocation " + location, Toast.LENGTH_LONG).show();

        mLocation = location;
        if (this.iLocationProvider != null) {
            this.iLocationProvider.onLocationUpdate(mLocation);
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * implements this interface to get call back of location changes
     */
    public interface ILocationProvider {
        void onLocationUpdate(Location location);
    }
}