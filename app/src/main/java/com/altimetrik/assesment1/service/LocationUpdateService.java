package com.altimetrik.assesment1.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.location.Location;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.altimetrik.assesment1.GoogleLocationComponent;

import static com.altimetrik.assesment1.ui.MainActivity.MESSANGER_FROM_JOB_SERVICE;


/**
 * location update service continues to running and getting location information
 */
public class LocationUpdateService extends JobService implements GoogleLocationComponent.ILocationProvider {

    private static final String TAG = LocationUpdateService.class.getSimpleName();
    public static final int LOCATION_MESSAGE = 9999;

    private Messenger mActivityMessenger;

    private GoogleLocationComponent locationUpdatesComponent;

    public LocationUpdateService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob....");
//        Utils.scheduleJob(getApplicationContext(), LocationUpdateService.class);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob....");

        locationUpdatesComponent.onStop();

        return false;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "created...............");

        locationUpdatesComponent = new GoogleLocationComponent(this,this);

        locationUpdatesComponent.onCreate(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand Service started");
        if (intent != null) {
            mActivityMessenger = intent.getParcelableExtra(MESSANGER_FROM_JOB_SERVICE);
        }
        //hey request for location updates
        locationUpdatesComponent.onStart();


        return START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy....");
    }

    /**
     * send message by using messenger
     *
     * @param messageID
     */
    private void sendMessage(int messageID, Location location) {
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the MainActivity calls startService() with the callback in the Intent.
        if (mActivityMessenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.");
            return;
        }
        Message m = Message.obtain();
        m.what = messageID;
        m.obj = location;
        try {
            mActivityMessenger.send(m);
        } catch (RemoteException e) {
            Log.e(TAG, "Error passing service object back to activity.");
        }
    }

    @Override
    public void onLocationUpdate(Location location) {
        sendMessage(LOCATION_MESSAGE, location);
    }
}