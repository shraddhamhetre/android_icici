package com.youtility.intelliwiz20.Services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.youtility.intelliwiz20.Activities.DialogActivity;
import com.youtility.intelliwiz20.AsyncTask.PeopleEventLogAsyntask;
import com.youtility.intelliwiz20.DataAccessObject.GeofenceDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadPeopleEventDataListener;
import com.youtility.intelliwiz20.Model.Geofence;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.Model.Point;
import com.youtility.intelliwiz20.Model.Polygon;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.EventLogInsertion;

import java.util.ArrayList;
import java.util.Calendar;

public class UserGeofenceBreachService extends Service implements IUploadPeopleEventDataListener,LocationListener {

    private static final String ACTION_RESCHEDULE = "service.intent.action.SERVICE_RESCHEDULE";
    private LocationManager locationMgr;
    public static final String BROADCAST_ACTION = "com.youtility.guard_tour.activities.DashBoard_Activity";
    private final Handler handler = new Handler();
    private Intent intent;
    private SharedPreferences fenceBreachPref;
    private SharedPreferences loginPref;
    private SharedPreferences deviceRelatedPref;
    private long currentTimestamp = -1;
    private boolean isActive = true;
    private GeofenceDAO geofenceDAO;
    private TypeAssistDAO typeAssistDAO;
    private PeopleEventLogDAO peopleEventLogDAO;
    private ArrayList<Polygon> userRouteList;
    private ArrayList<String> userRouteIdList;
    private ArrayList<Geofence> geofenceArrayList;

    private EventLogInsertion eventLogInsertion;
    private long workSlotStartTimestamp;
    private long workSlotEndTimestamp;
    private long gfValidFromTimestamp;
    private long gfValidUptoTimestamp;
    public UserGeofenceBreachService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Geofence service oncreate");

    }

    @Override
    public void onStart(Intent intent, int startId) {
        //super.onStart(intent, startId);
        handler.removeCallbacks(sendUpdateToUI);
        System.out.println("Geofence service onstart");
    }

    private Runnable sendUpdateToUI = new Runnable() {

        @Override
        public void run() {
            displayGeoFenceStatus();
            handler.postDelayed(this, 1000);
        }
    };

    private void displayGeoFenceStatus() {

        System.out.println("displayGeoFenceStatus");
        intent.putExtra("status", fenceBreachPref.getBoolean(Constants.GEOFENCE_IS_IN_OUT, false));
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        System.out.println("Geofence service onstartcommond");
        CommonFunctions.UploadLog("\n <Geofence service onstartcommond> \n");


        intent = new Intent(BROADCAST_ACTION);
        geofenceDAO = new GeofenceDAO(UserGeofenceBreachService.this);
        typeAssistDAO=new TypeAssistDAO(UserGeofenceBreachService.this);
        peopleEventLogDAO=new PeopleEventLogDAO(UserGeofenceBreachService.this);

        fenceBreachPref = getSharedPreferences(Constants.GEOFENCE_BREACH_PREF, MODE_PRIVATE);
        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);

        Geofence geoMapping;
        geofenceArrayList = new ArrayList<Geofence>();
        userRouteList = new ArrayList<Polygon>();
        userRouteIdList = new ArrayList<String>();
        geofenceArrayList = geofenceDAO.getGeofenceList(loginPref.getLong(Constants.LOGIN_SITE_ID,-1));

        System.out.println("geofenceArrayList=="+geofenceArrayList.size());
        CommonFunctions.UploadLog("\n <geofenceArrayList==> \n"+geofenceArrayList.size());


        if (geofenceArrayList != null && geofenceArrayList.size() > 0) {
            for (int i = 0; i < geofenceArrayList.size(); i++) {
                System.out.println("geofenceArrayList value: "+geofenceArrayList.get(i).getGeofence());
                if(geofenceArrayList.get(i).getGfcode()!=null && !geofenceArrayList.get(i).getGfcode().equalsIgnoreCase("None") && geofenceArrayList.get(i).getGfcode().toString().trim().length()>0) {
                    String[] geoCode = geofenceArrayList.get(i).getGeofence().trim().split("~");
                    Log.d("Dashboard", "geoCode.length: " + geoCode.length);
                    CommonFunctions.UploadLog("\n <geoCode.length: > \n"+geoCode.length);

                    Polygon.Builder builder = Polygon.Builder();
                    for (int j = 0; j < geoCode.length; j++) {
                        //Log.d("Dashboard", "geoCode: " + geoCode[j]);
                        if (geoCode[j] != null && !geoCode[j].equalsIgnoreCase("None")) {
                            String[] points = geoCode[j].split(",");
                            System.out.println(" points: "+(points[0]) +" "+(points[1]));

                            System.out.println("Float points: "+ Float.valueOf(points[0]) +" "+Float.valueOf(points[1]));
                            builder = builder.addVertex(new Point(Float.valueOf(points[0]), Float.valueOf(points[1])));
                        }
                    }

                    Polygon polygon = builder.build();
                    userRouteList.add(polygon);
                    System.out.println("@@@@@@" + String.valueOf(geofenceArrayList.get(i).getGfid()));
                    String geofenceId= String.valueOf(geofenceArrayList.get(i).getGfid());
                    System.out.println("@@@@@@ geofenceId: " + geofenceId);
                    userRouteIdList.add(geofenceId.toString());
                    //userRouteIdList.add(String.valueOf(geofenceArrayList.get(i).getGfid()));
                }
            }
        }
        else
            System.out.println("Geofence not active");

        currentTimestamp = System.currentTimeMillis();

        locationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);

        updateLocation();
        Intent i = new Intent();
        i.setClassName(getApplicationContext(), "UserGeofenceBreachService");
        i.setAction(ACTION_RESCHEDULE);
        PendingIntent pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = (15 * 1000);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, pi);
        if(!loginPref.getBoolean(Constants.IS_LOGIN_DONE,false))
            stopSelf(startId);

        return Service.START_NOT_STICKY;
    }

    private void updateLocation() {
        if (locationMgr != null) {
            if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 50, this);
                }
            }
            if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 100, this);
                }
            }

        }else
        {
            Log.i("TrackmyLocation GT", "Location Manager is null");
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null)
        {
			/*if((isActive) && (currentTimestamp >= workSlotStartTimestamp && currentTimestamp <= workSlotEndTimestamp))
				checkPointIntersect(location);*/

            checkPointIntersect(location);
        }
    }
//accuracy-, datetime-, gpslocation-, photorecognitionthreshold-, photorecognitionscore-, photorecognitiontimestamp-, photorecognitionserviceresponse-,
  //  facerecognition-, peopleid-, peventtype-, punchstatus-, verifiedby-, siteid, cuser-, muser-, cdtz-, mdtz-, isdeleted-, gfid-, deviceid-
    private void insertPeopleEventLogRecord(String eventtype, long gfid, String gpsLocation)
    {
        CommonFunctions.UploadLog("\n <up peloglog--> \n");

        System.out.println("GPS location: "+gpsLocation);

        PeopleEventLog peopleEventLog=new PeopleEventLog();
        peopleEventLog.setAccuracy(Float.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ACCURACY,"-1")));
        //peopleEventLog.setDeviceid(deviceRelatedPref.getLong(Constants.DEVICE_IMEI,-1));
        peopleEventLog.setDeviceid(deviceRelatedPref.getString(Constants.DEVICE_IMEI,"-1"));
        peopleEventLog.setDatetime(String.valueOf(System.currentTimeMillis()));
        peopleEventLog.setGpslocation(gpsLocation);
        peopleEventLog.setPhotorecognitionthreshold(-1);
        peopleEventLog.setPhotorecognitionscore(-1);
        peopleEventLog.setPhotorecognitiontimestamp(null);
        peopleEventLog.setPhotorecognitionserviceresponse(null);
        peopleEventLog.setFacerecognition("false");
        peopleEventLog.setCdtz(CommonFunctions.getTimezoneDate((System.currentTimeMillis())));
        peopleEventLog.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        //peopleEventLog.setIsdeleted("false");
        peopleEventLog.setCuser(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        peopleEventLog.setMuser(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        peopleEventLog.setPeopleid(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        peopleEventLog.setPeventtype(typeAssistDAO.getEventTypeID("GEOFENCE", Constants.IDENTIFIER_JOBNEED));
        peopleEventLog.setPunchstatus(typeAssistDAO.getEventTypeID(eventtype, Constants.IDENTIFIER_PUNCHSTATUS));
        peopleEventLog.setVerifiedby(-1);
        peopleEventLog.setScanPeopleCode("");
        peopleEventLog.setGfid(gfid);
        peopleEventLog.setBuid(loginPref.getLong(Constants.LOGIN_SITE_ID,-1));
        peopleEventLog.setDistance(0);
        peopleEventLog.setDuration(0);
        peopleEventLog.setExpamt(0.0);
        peopleEventLog.setReference("");
        peopleEventLog.setRemarks("");
        peopleEventLog.setTransportmode(-1);
        peopleEventLog.setOtherlocation("");

        System.out.println("People Event logid: "+peopleEventLog.getPeventtype());
        System.out.println("People Event gfid: "+peopleEventLog.getGfid());

        peopleEventLogDAO.insertRecord(peopleEventLog);
        uploaddata();

    }

    public void uploaddata(){
        System.out.println("updatacalled");
        PeopleEventLogAsyntask peopleEventLogAsyntask=new PeopleEventLogAsyntask(UserGeofenceBreachService.this, this);;
        peopleEventLogAsyntask.execute();
    }

    /*private void insertDeviceEventLogRecord(String eventvalue)
    {
        eventLogInsertion=new EventLogInsertion(this);
        eventLogInsertion.addDeviceEvent(eventvalue,eventvalue,"");
    }*/

    private long prepareWorkSlotTime(String gfTime)
    {
        long workingTime=-1;
        String[]wTime=gfTime.split(":");
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(wTime[0]));
        calendar.set(Calendar.MINUTE,Integer.parseInt(wTime[1]));
        calendar.set(Calendar.SECOND,Integer.parseInt(wTime[2]));
        calendar.set(Calendar.MILLISECOND,0);
        workingTime=calendar.getTimeInMillis();
        return workingTime;
    }

    private void checkPointIntersect(Location location){
        CommonFunctions.UploadLog("\n <checkPointIntersect() start: > \n");
        CommonFunctions.UploadLog("\n <Geofence: >"+fenceBreachPref.getString(Constants.GEOFENCE,"") );
        System.out.println("checkPointIntersect() start: " + CommonFunctions.getFormatedDate(System.currentTimeMillis()));
        boolean uploadGeoefence= false;
        System.out.println("GEOFENCE A: " + fenceBreachPref.getString(Constants.GEOFENCE,""));
        if(fenceBreachPref.getString(Constants.GEOFENCE, "").equals("")){
            System.out.println("GEOFENCE @@@@@@@@@@ IN");
            if(userRouteList!=null && userRouteList.size()>0) {
                System.out.println("userList==" + userRouteList);
                for (int i = 0; i < (int) userRouteList.size(); i++) {
                    Polygon polygon=userRouteList.get(i);
                    String geofenceId= userRouteIdList.get(i);
                    System.out.println("START TIME: "+geofenceArrayList.get(i).getStarttime() +" END TIME: "+ geofenceArrayList.get(i).getEndtime());

                    workSlotStartTimestamp=prepareWorkSlotTime(geofenceArrayList.get(i).getStarttime());
                    workSlotEndTimestamp=prepareWorkSlotTime(geofenceArrayList.get(i).getEndtime());
                    gfValidFromTimestamp=CommonFunctions.getParseDate(geofenceArrayList.get(i).getFromdt());
                    gfValidUptoTimestamp=CommonFunctions.getParseDate(geofenceArrayList.get(i).getUptodt());
                    /*if((isActive) && (currentTimestamp >= workSlotStartTimestamp && currentTimestamp <= workSlotEndTimestamp))
                    {*/
                        if(polygon.contains(new Point((float)location.getLatitude(), (float)location.getLongitude()))==true)
                        {
                            uploadGeoefence= true;
                            fenceBreachPref.edit().putString(Constants.GEOFENCE, geofenceId + "-IN").apply();
                            System.out.println("GEOFENCE IN: " + geofenceId);
                            break;
                        }
                    //}
                }
            }
        }else if(!fenceBreachPref.getString(Constants.GEOFENCE, "").equals("")){
            System.out.println("GEOFENCE @@@@@@@@@@ OUT");

            String geofenceId = fenceBreachPref.getString(Constants.GEOFENCE,"").split("-")[0];
            if(geofenceId != ""){
                for (int i=0; i< userRouteIdList.size(); i++){
                    if(geofenceId.startsWith(userRouteIdList.get(i))){
                        Polygon polygon=userRouteList.get(i);
                        System.out.println("polygonIndex1: "+geofenceId);

                        if(polygon.contains(new Point((float)location.getLatitude(), (float)location.getLongitude()))==false)
                        {
                            uploadGeoefence= true;
                            fenceBreachPref.edit().putString(Constants.GEOFENCE, geofenceId + "-OUT").apply();
                            System.out.println("GEOFENCE OUT: " + geofenceId);
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("GEOFENCE B: " + fenceBreachPref.getString(Constants.GEOFENCE,""));
        CommonFunctions.UploadLog("\n <uploadGeoefence: >"+uploadGeoefence );

        System.out.println("uploadGeoefence: "+uploadGeoefence);
        if (uploadGeoefence==true)
        {
            String geofenceIdStatus[]= fenceBreachPref.getString(Constants.GEOFENCE,"").split("-");
            String geofenceId = geofenceIdStatus[0];
            String geofenceStatus= geofenceIdStatus[1];
            System.out.println("geofenceId: "+geofenceId +  ", geofenceStatus: " + geofenceStatus);
            for (int i=0; i< userRouteIdList.size(); i++) {
                if (geofenceId.startsWith(userRouteIdList.get(i))) {
                    insertPeopleEventLogRecord(geofenceStatus, geofenceArrayList.get(i).getGfid(), location.getLatitude() + "," + location.getLongitude());
                    if(geofenceStatus.equals("OUT")) {
                        fenceBreachPref.edit().putString(Constants.GEOFENCE, "").apply();
                    }
                    break;
                }
            }
        }
        System.out.println("Geofence: "+fenceBreachPref.getString(Constants.GEOFENCE,""));
        System.out.println("checkPointIntersect() end");
        CommonFunctions.UploadLog("\n <Geofence: >"+fenceBreachPref.getString(Constants.GEOFENCE,"") );
        CommonFunctions.UploadLog("\n <checkPointIntersect() end: > \n");

    }
    /*private void checkPointIntersect(Location location)
    {
        System.out.println("Location chek intersect point");
        CommonFunctions.UploadLog("\n <Location chek intersect point---> \n");

        if(userRouteList!=null && userRouteList.size()>0)
        {
            System.out.println("userList=="+userRouteList);
            for(int i=0;i<(int)userRouteList.size();i++)
            {
                System.out.println("userList: "+userRouteList.get(i));
                Polygon polygon=userRouteList.get(i);

                workSlotStartTimestamp=prepareWorkSlotTime(geofenceArrayList.get(i).getStarttime());
                workSlotEndTimestamp=prepareWorkSlotTime(geofenceArrayList.get(i).getEndtime());
                gfValidFromTimestamp=CommonFunctions.getParseDate(geofenceArrayList.get(i).getFromdt());
                gfValidUptoTimestamp=CommonFunctions.getParseDate(geofenceArrayList.get(i).getUptodt());

                    if((isActive) && (currentTimestamp >= workSlotStartTimestamp && currentTimestamp <= workSlotEndTimestamp))
                    {
                        //System.out.println("GF Id: "+geofenceArrayList.get(i).getGfid()+" : "+geofenceArrayList.get(i).getGfcode());
                        if(polygon.contains(new Point((float)location.getLatitude(), (float)location.getLongitude()))==true)
                        {
                            CommonFunctions.UploadLog("\n <IN RECORD geofence in(sharedpref=========>"+geofenceArrayList.get(i).isEntered()+fenceBreachPref.getBoolean(Constants.GEOFENCE_Is_In, false));

                            System.out.println("in  geofence"+geofenceArrayList.get(i).isEntered());

                            if(!geofenceArrayList.get(i).isEntered()) {
                                if(!fenceBreachPref.getBoolean(Constants.GEOFENCE_Is_In, false)) {


                                    String msg = loginPref.getString(Constants.LOGIN_USER_ID, "") + " " + getResources().getString(R.string.geofence_entered, geofenceArrayList.get(i).getGfname());
                                    //Toast.makeText(UserGeofenceBreachService.this, msg, Toast.LENGTH_LONG).show();
                                    geofenceArrayList.get(i).setEntered(true);
                                    fenceBreachPref.edit().putInt(Constants.GEOFENCE_STATUS, 0).apply();
                                    //Toast.makeText(UserGeofenceBreachService.this, "Entered into geofence "+geofenceArrayList.get(i).getGfname(), Toast.LENGTH_LONG).show();
                                    //insertDeviceEventLogRecord("IN");
                                    insertPeopleEventLogRecord("IN", geofenceArrayList.get(i).getGfid(), location.getLatitude() + "," + location.getLongitude());
                                    showAlertForUser(i, location, msg, 1);
                                    //fenceBreachPref.edit().putBoolean(String.valueOf(Constants.GEOFENCE_Is_In), true).apply();
                                    fenceBreachPref.edit().putBoolean(Constants.GEOFENCE_Is_In, true).apply();
                                    CommonFunctions.UploadLog("\n <IN RECORD geofence in=========>"+geofenceArrayList.get(i).isEntered()+fenceBreachPref.getBoolean(String.valueOf(Constants.GEOFENCE_Is_In), false));

                                }else {
                                }
                            }
                            else
                            {
                                System.out.println("perform out");
                            }
                        }
                        else
                        {
                            CommonFunctions.UploadLog("\n <OUT RECORD sharedpref=========>"+geofenceArrayList.get(i).isEntered()+fenceBreachPref.getBoolean(Constants.GEOFENCE_Is_In, false));

                            if(geofenceArrayList.get(i).isEntered()) {
                                if(fenceBreachPref.getBoolean(Constants.GEOFENCE_Is_In, false)) {
                                    String msg = loginPref.getString(Constants.LOGIN_USER_ID, "") + " " + getResources().getString(R.string.geofence_breached, geofenceArrayList.get(i).getGfname());
                                    //Toast.makeText(UserGeofenceBreachService.this, msg, Toast.LENGTH_LONG).show();
                                    geofenceArrayList.get(i).setEntered(false);
                                    fenceBreachPref.edit().putInt(Constants.GEOFENCE_STATUS, 1).apply();
                                    //Toast.makeText(UserGeofenceBreachService.this, "Breached from geofence "+geofenceArrayList.get(i).getGfname(), Toast.LENGTH_LONG).show();
                                    //insertDeviceEventLogRecord("OUT");
                                    insertPeopleEventLogRecord("OUT", geofenceArrayList.get(i).getGfid(), location.getLatitude() + "," + location.getLongitude());
                                    showAlertForUser(i, location, msg, 0);
                                    fenceBreachPref.edit().putBoolean(Constants.GEOFENCE_Is_In, false).apply();
                                    CommonFunctions.UploadLog("\n <OUT RECORD out=========>"+geofenceArrayList.get(i).isEntered()+fenceBreachPref.getBoolean(Constants.GEOFENCE_Is_In, false));

                                }else{
                                }
                            }
                            else {
                                System.out.println("perform is out");
                                //insertPeopleEventLogRecord("OUT",geofenceArrayList.get(i).getGfid(),location.getLatitude()+","+location.getLongitude());
                           }
                        }
                    }
            }
        }

        System.out.println("userRouteList--"+ userRouteList.size());

    }*/

    private void showAlertForUser(int i, Location location,String msg, int type)
    {
        /*Intent ii = new Intent();
        ii.setClassName("com.youtility.intelliwiz20", "DialogActivity");
        ii.putExtra("Message", msg+" "+geofenceArrayList.get(i).getGfname());
        ii.putExtra("GeoFenceID", geofenceArrayList.get(i).getGfid());
        ii.putExtra("GeoFenceName", geofenceArrayList.get(i).getGfname());
        ii.putExtra("GeoFenceLat", location.getLatitude());
        ii.putExtra("GeoFenceLon", location.getLongitude());
        ii.putExtra("GeoFenceAlertTo", geofenceArrayList.get(i).getAlerttopeople());
        ii.putExtra("Allow", 0);
        ii.putExtra("geoFenceType", type);
        ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(ii);*/


        StringBuilder msgStr = new StringBuilder();

        msgStr.append(msg+"~");
        msgStr.append(CommonFunctions.getFormatedDate(System.currentTimeMillis()));

        Intent ii = new Intent(this, DialogActivity.class);
        ii.putExtra("EventName", msgStr.toString());
        ii.putExtra("Allow", 0);
        ii.putExtra("Activity", "GEOFENCE");
        ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(ii);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdateToUI);
        super.onDestroy();

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
    public void finishAllPeopleEventLogUpload() {

    }

    @Override
    public void finishPeopleEventLogUpload(int status) {

    }
}
