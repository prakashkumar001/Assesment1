package com.altimetrik.assesment1.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.altimetrik.assesment1.R;
import com.altimetrik.assesment1.realm.LocationDBObject;
import com.altimetrik.assesment1.realm.LocationModel;
import com.altimetrik.assesment1.service.LocationUpdateService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 01;

    public static final String MESSANGER_FROM_JOB_SERVICE = "job_service_key";

    // Handler for incoming messages from the service.
    private IncomingMessageHandler mHandler;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Button showLocationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showLocationList = (Button) findViewById(R.id.locationList);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);


        mHandler = new IncomingMessageHandler();

        showLocationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LocationListActivity.class);
                startActivityForResult(i, 100);
            }
        });


        requestPermissions();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        LatLng coordinate = new LatLng(13.0113, 80.2227); //Store these lat lng values somewhere. These should be constant.
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 15);
        mMap.animateCamera(location);
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting the position for the marker
        markerOptions.position(coordinate);
        // Setting the title for the marker.
        markerOptions.title(coordinate.latitude + " : " + coordinate.longitude);
        mMap.addMarker(markerOptions);


    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Start service and provide it a way to communicate with this class.
                Intent jobService = new Intent(this, LocationUpdateService.class);
                Messenger messengerIncoming = new Messenger(mHandler);
                jobService.putExtra(MESSANGER_FROM_JOB_SERVICE, messengerIncoming);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this, jobService);
                } else {
                    startService(jobService);
                }
            } else {
                // Permission denied.
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage..." + msg.toString());

            super.handleMessage(msg);

            switch (msg.what) {
                case LocationUpdateService.LOCATION_MESSAGE:
                    Location obj = (Location) msg.obj;
                    //Get Location details from Handler
                    setRealmData(new LocationModel(String.valueOf(obj.getLatitude()), String.valueOf(obj.getLongitude())));
                    break;
            }
        }
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            // Request permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                mMap.clear();

                for (LocationDBObject location : getDataFromRealm()) {
                    mLocationOnMap(location);
                }

            }
        }
    }


    List<LocationDBObject> getDataFromRealm() {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        List<LocationDBObject> realmResults = realm.where(LocationDBObject.class).findAll();

        ArrayList<LocationDBObject> arrayList = new ArrayList<LocationDBObject>();

        for (LocationDBObject results : realmResults) {

            LocationDBObject locationObject = new LocationDBObject();
            locationObject.lastUpdatedTime = results.lastUpdatedTime;
            locationObject.latitude = results.latitude;
            locationObject.longitude = results.longitude;

            arrayList.add(locationObject);
        }

        return arrayList;
    }


    void mLocationOnMap(LocationDBObject locationDBObject) {
        LatLng coordinate = new LatLng(Double.parseDouble(locationDBObject.latitude), Double.parseDouble(locationDBObject.longitude)); //Store these lat lng values somewhere. These should be constant.
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 15);
// Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(coordinate);

        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title(coordinate.latitude + " : " + coordinate.longitude);
        mMap.addMarker(markerOptions);
    }

    private void setRealmData(LocationModel location) {

        Realm realm = Realm.getDefaultInstance();

        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }

        Number currentIdNum = realm.where(LocationDBObject.class).max("id");
        int nextId = 0;
        if (currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }

        LocationDBObject locationData = realm.createObject(LocationDBObject.class, nextId);
        locationData.latitude = location.getLatitude().toString();
        locationData.longitude = location.getLongitude().toString();
        locationData.lastUpdatedTime = DateFormat.getDateTimeInstance().format(new Date());

        realm.commitTransaction();
    }
}
