package com.youtility.intelliwiz20.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ActivityRecognitionIntentService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_RESCHEDULE ="service.intent.action.SERVICE_RESCHEDULE";
    GoogleApiClient mApiClient;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(ActivityRecognitionIntentService.this);
        callActivityRecService();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void callActivityRecService()
    {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        mApiClient.connect();

        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }

        Intent i = new Intent(getApplicationContext(), ActivityRecognitionIntentService.class);
        //i.setClassName(getApplicationContext(), "com.youtility.guard_tour.services.ActivityRecognitionIntentService");
        PendingIntent pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //int interval=(10*60*1000);
        long interval=Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_ACTIVITY_REQ_FREQ,"10"))*(60*1000);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pi);

        return START_STICKY;
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities)
    {
        for( DetectedActivity activity : probableActivities )
        {
            switch( activity.getType() )
            {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    break;
                }


            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent i = new Intent(getApplicationContext(), ActivityRecognitionIntentService.class);
        //i.setClassName(getApplicationContext(), "com.youtility.guard_tour.services.ActivityRecognitionIntentService");
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT );
        long interval=Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_ACTIVITY_REQ_FREQ,"10"))*(60*1000);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, (interval*60*1000), pendingIntent );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
