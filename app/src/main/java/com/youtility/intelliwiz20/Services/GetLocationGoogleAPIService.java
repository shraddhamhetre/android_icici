package com.youtility.intelliwiz20.Services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.youtility.intelliwiz20.AsyncTask.DeviceEventLogAsyntask;
import com.youtility.intelliwiz20.Interfaces.IUploadDeviceEventLogDataListener;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.EventLogInsertion;

public class GetLocationGoogleAPIService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, IUploadDeviceEventLogDataListener {
    private GoogleApiClient mGoogleApiClient;
    private static final String ACTION_RESCHEDULE ="service.intent.action.SERVICE_RESCHEDULE";
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 1 * 60 * 1000;  /* 1 min */
    private long FASTEST_INTERVAL = 1 * 60 * 1000; /* 30000 30 sec */

    private LocationManager locationManager;

    private SharedPreferences deviceRelatedPref;
    private SharedPreferences sharedPreferences;
    private EventLogInsertion eventLogInsertion;
    private SharedPreferences loginPref;

    private CheckNetwork checkNetwork;
    private AlarmManager alarmManager;
    private PendingIntent pi;

    public GetLocationGoogleAPIService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.print("GetLocationGoogleAPIService started..........................");

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(GetLocationGoogleAPIService.this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);
        eventLogInsertion=new EventLogInsertion(GetLocationGoogleAPIService.this);
        checkNetwork=new CheckNetwork(GetLocationGoogleAPIService.this);

        checkLocation();

        //call service on time interval
        Intent i = new Intent();
        i.setClassName(getApplicationContext(), "GetLocationGoogleAPIService");
        i.setAction(ACTION_RESCHEDULE);
        pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //int interval=(1*60*1000  );
        long interval=Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_LOCATION_FREQ,"10"))*(60*1000);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pi);

        return Service.START_STICKY;
    }

    private boolean checkLocation() {
        if(!isLocationEnabled()) {
            //showAlert();
            showNotification();
        }
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showNotification()
    {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.gps_disconnected)
                        .setSound(alarmSound)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setContentTitle("Enable Location")
                        .setContentText("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                                "use this app");
        int NOTIFICATION_ID = 12345;

        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(GetLocationGoogleAPIService.this);

        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("Google play connected .............");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if(mLocation == null){
            startLocationUpdates();
        }

        if (mLocation != null) {

            if(loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CAPTURE_DEVICEEVENT,false)) {
                if (checkNetwork.isNetworkConnectionAvailable()) {
                    DeviceEventLogAsyntask deviceEventLogAsyntask = new DeviceEventLogAsyntask(GetLocationGoogleAPIService.this, this);
                    deviceEventLogAsyntask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                eventLogInsertion.addDeviceEvent("TRACKING", "Tracking", "Event Type");
            }
            //Toast.makeText(GetLocationGoogleAPIService.this,mLocation.getProvider(),Toast.LENGTH_SHORT).show();

            System.out.println("mLocation.getProvider(): "+mLocation.getProvider());
            System.out.println("mLocation.getSpeed(): "+mLocation.getSpeed());

            deviceRelatedPref.edit().putString(Constants.DEVICE_LATITUDE, String.valueOf(mLocation.getLatitude())).apply();
            deviceRelatedPref.edit().putString(Constants.DEVICE_LONGITUDE, String.valueOf(mLocation.getLongitude())).apply();
            deviceRelatedPref.edit().putString(Constants.DEVICE_ALTITUDE, String.valueOf(mLocation.getAltitude())).apply();
            deviceRelatedPref.edit().putString(Constants.DEVICE_ACCURACY, String.valueOf(mLocation.getAccuracy())).apply();



            //Toast.makeText(GetLocationGoogleAPIService.this, mLocation.getLatitude()+" : "+mLocation.getLongitude(),Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

        Log.d("reque", "--->>>>");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        //mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        /*deviceRelatedPref.edit().putString(Constants.DEVICE_LATITUDE, String.valueOf(location.getLatitude())).apply();
        deviceRelatedPref.edit().putString(Constants.DEVICE_LONGITUDE, String.valueOf(location.getLongitude())).apply();
        deviceRelatedPref.edit().putString(Constants.DEVICE_ALTITUDE, String.valueOf(location.getAltitude())).apply();
        deviceRelatedPref.edit().putString(Constants.DEVICE_ACCURACY, String.valueOf(location.getAccuracy())).apply();

        eventLogInsertion.addDeviceEvent("TRACKING","Tracking","Event Type");*/

        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //Toast.makeText(GetLocationGoogleAPIService.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishAllDeviceEventLogUpload() {

    }

    @Override
    public void finishDeviceEventLogUpload(int status) {

    }
}
