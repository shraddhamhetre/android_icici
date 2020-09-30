package com.youtility.intelliwiz20.Services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.youtility.intelliwiz20.AsyncTask.DeviceEventLogAsyntask;
import com.youtility.intelliwiz20.Interfaces.IUploadDeviceEventLogDataListener;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.EventLogInsertion;

/**
 * Created by youtility4 on 3/10/17.
 */

public class GetLocationService extends Service implements LocationListener, IUploadDeviceEventLogDataListener {

    private static final String ACTION_RESCHEDULE = "service.intent.action.SERVICE_RESCHEDULE";

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude = 0.0; // latitude
    double longitude = 0.0; // longitude
    double altitude = 0.0;
    float accuracy = 0;
    String locProvider=null;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 30000;

    // Declaring a Location Manager
    protected LocationManager locationManager;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences loginPref;
    private EventLogInsertion eventLogInsertion;
    private SharedPreferences sharedPreferences;
    private CheckNetwork checkNetwork;
    private AlarmManager alarmManager;
    private PendingIntent pi;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("startes service");
        //return super.onStartCommand(intent, flags, startId);
        deviceRelatedPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(GetLocationService.this);
        checkNetwork = new CheckNetwork(GetLocationService.this);
        //call service on time interval
        Intent i = new Intent();
        i.setClassName(getApplicationContext(), "GetLocationService");
        i.setAction(ACTION_RESCHEDULE);
        pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //int interval=(60*1000);
        long interval = 15000;
        //long interval1 = Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_LOCATION_FREQ, "5")) * (60 * 1000);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, pi);
        CommonFunctions.UploadLog("\n <Location service called after 30sec----> \n");


        getLocation();
        return Service.START_NOT_STICKY;

    }

    public Location getLocation() {
        System.out.println("getLocation servicestarted----");
        try {
            if (loginPref.getBoolean(Constants.IS_LOGIN_DONE, false)) {

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


                Criteria criteria = new Criteria();

                //System.out.println("Best Location provider: " + locationManager.getBestProvider(criteria, false));
                // getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if(locationManager.getBestProvider(criteria,false).equalsIgnoreCase("gps"))
                {
                    //Toast.makeText(GetLocationService.this,"GPS",Toast.LENGTH_SHORT).show();
                    if (isGPSEnabled)
                    {
                        //Log.d("GPS Enabled", "GPS Enabled");
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                altitude = location.getAltitude();
                                accuracy = location.getAccuracy();
                                locProvider="GPS";
                                //Toast.makeText(GetLocationService.this, "Location not null",Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {
                        latitude = 0.0;
                        longitude = 0.0;
                        altitude = 0.0;
                        accuracy = 0.0f;

                    }

                    //Toast.makeText(GetLocationService.this, latitude+" : "+longitude,Toast.LENGTH_LONG).show();
                }
                else if(locationManager.getBestProvider(criteria,false).equalsIgnoreCase("network"))
                {
                    //Toast.makeText(GetLocationService.this,"NETWORK",Toast.LENGTH_SHORT).show();
                    if (isNetworkEnabled) {
                        //Log.d("Network", "Network");
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        }
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    altitude = location.getAltitude();
                                    accuracy = location.getAccuracy();
                                    locProvider="NETWORK";
                                    //Toast.makeText(GetLocationService.this, "Location not null",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } else {
                        latitude = 0.0;
                        longitude = 0.0;
                        altitude = 0.0;
                        accuracy = 0.0f;
                    }

                    //Toast.makeText(GetLocationService.this, latitude+" : "+longitude,Toast.LENGTH_LONG).show();
                }
                System.out.println("---------------------------------------------"+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CAPTURE_DEVICEEVENT,false));
                long currentTimestamp= System.currentTimeMillis();
                long previousTimestamp= deviceRelatedPref.getLong(Constants.DEVICE_LOG_TIME, currentTimestamp);
                int diffCaptureActivity= CommonFunctions.getDateDifferenceInSec(currentTimestamp, previousTimestamp );

                System.out.println("diff===="+ diffCaptureActivity);
                CommonFunctions.UploadLog("\n@@"+ CommonFunctions.getFormatedDate(System.currentTimeMillis())+" | " + latitude + "," + longitude );
                /*if (diffCaptureActivity >= 30){*/
                    CommonFunctions.UploadLog("\n  @@15"+ CommonFunctions.getFormatedDate(System.currentTimeMillis())+" | " + + latitude + "," + longitude );
                    System.out.println("===="+ loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CAPTURE_DEVICEEVENT,false));

                    deviceRelatedPref.edit().putLong(Constants.DEVICE_LOG_TIME, currentTimestamp).apply();
                    if(loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CAPTURE_DEVICEEVENT,false)) {
                        if (checkNetwork.isNetworkConnectionAvailable()) {
                            DeviceEventLogAsyntask deviceEventLogAsyntask = new DeviceEventLogAsyntask(GetLocationService.this, this);
                            deviceEventLogAsyntask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        eventLogInsertion=new EventLogInsertion(GetLocationService.this);
                        eventLogInsertion.addDeviceEvent("TRACKING", "Tracking", "Event Type");
                    }
                /*} else if (diffCaptureActivity == 0){
                    System.out.println("diff===="+ diffCaptureActivity);

                    deviceRelatedPref.edit().putLong(Constants.DEVICE_LOG_TIME, currentTimestamp).apply();

                }*/

                /*Location temp = new Location(locationManager.getBestProvider(criteria,false));
                temp.setLatitude(Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")));
                temp.setLongitude(Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0")));
                float distance = location.distanceTo(temp)/1000;


                String result = String.format("%.4f", distance);
                String result1 = String.format("%.4f", (distance*3280.8));

                System.out.println("distance in meter: "+result);
                System.out.println("distance in feet: "+result1);*/

                float[] results = new float[1];
                Location.distanceBetween(Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")),
                                        Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0")),latitude,longitude,results);
                //System.out.println("results[0]: "+results[0]/1000);

                Constants.travelledDist=Constants.travelledDist+(results[0]/1000);

                deviceRelatedPref.edit().putString(Constants.DEVICE_LATITUDE, String.valueOf(latitude)).apply();
                deviceRelatedPref.edit().putString(Constants.DEVICE_LONGITUDE, String.valueOf(longitude)).apply();
                deviceRelatedPref.edit().putString(Constants.DEVICE_ALTITUDE, String.valueOf(altitude)).apply();
                deviceRelatedPref.edit().putString(Constants.DEVICE_ACCURACY, String.valueOf(accuracy)).apply();
                deviceRelatedPref.edit().putString(Constants.DEVICE_LOC_PROVIDER,locProvider).apply();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Location="+location+"--lat"+ latitude +"==long"+ longitude );
        return location;
    }


    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GetLocationService.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GetLocationService.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location!=null)
        {
            System.out.println("Onlocation changed......"+ CommonFunctions.getFormatedDate(System.currentTimeMillis()));
            getLocation();
        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void finishAllDeviceEventLogUpload() {

    }

    @Override
    public void finishDeviceEventLogUpload(int status) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
