package com.youtility.intelliwiz20.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.PeopleEventLog;

/**
 * Created by PrashantD on 5/10/17.
 */

public class PeopleEventLogInsertion {
    private Context context;
    private PeopleEventLog peopleEventLog;
    private PeopleEventLogDAO peopleEventLogDAO;
    private SharedPreferences deviceRelatedPref;
    private MemoryInfo memoryInfo;
    private SharedPreferences loginPref;

    private TypeAssistDAO typeAssistDAO;
    private PeopleDAO peopleDAO;

    public PeopleEventLogInsertion(Context context)
    {
        this.context=context;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF,Context.MODE_PRIVATE);
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
    }

    public void addConveyanceEvent(String punchType, String gpsLocation,long refValue, String remark, String transportMode, double expamout, int distance, int duration,long pelogid)
    {
        peopleEventLogDAO=new PeopleEventLogDAO(context);
        typeAssistDAO=new TypeAssistDAO(context);

        System.out.println("Login code: "+loginPref.getString(Constants.LOGIN_PEOPLE_CODE,""));

        peopleEventLog = new PeopleEventLog();
        peopleEventLog.setAccuracy(Float.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ACCURACY,"-1")));
        //peopleEventLog.setDeviceid(deviceRelatedPref.getLong(Constants.DEVICE_IMEI,-1));
        peopleEventLog.setDeviceid(deviceRelatedPref.getString(Constants.DEVICE_IMEI,"-1"));

        peopleEventLog.setDatetime(String.valueOf(pelogid));
        peopleEventLog.setGpslocation(gpsLocation);
        peopleEventLog.setPhotorecognitionthreshold(-1);
        peopleEventLog.setPhotorecognitionscore(-1);
        peopleEventLog.setPhotorecognitiontimestamp("");
        peopleEventLog.setPhotorecognitionserviceresponse(null);
        peopleEventLog.setFacerecognition("false");
        peopleEventLog.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        peopleEventLog.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        peopleEventLog.setCuser(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        peopleEventLog.setMuser(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        peopleEventLog.setPeopleid(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        peopleEventLog.setPeventtype(typeAssistDAO.getEventTypeID(Constants.EVENT_TYPE_CONVEYANCE, Constants.EVENT_TYPE));
        peopleEventLog.setPunchstatus(typeAssistDAO.getEventTypeID(punchType, Constants.IDENTIFIER_PUNCHSTATUS));
        peopleEventLog.setVerifiedby(-1);
        peopleEventLog.setScanPeopleCode("");
        peopleEventLog.setGfid(-1);
        peopleEventLog.setBuid(loginPref.getLong(Constants.LOGIN_SITE_ID,-1));
        peopleEventLog.setDistance(distance);
        peopleEventLog.setDuration(duration);
        peopleEventLog.setExpamt(expamout);
        peopleEventLog.setReference(String.valueOf(refValue));
        peopleEventLog.setRemarks(remark);
        peopleEventLog.setTransportmode(typeAssistDAO.getEventTypeID(transportMode, Constants.TRANSPORT_MODE));
        peopleEventLog.setOtherlocation("");
        peopleEventLogDAO.insertRecord(peopleEventLog);

    }

    public void insertPeopleEventLogRecord(String punchStatus, String empCode, long inoutTimestamp, long siteID, long peopleId, String attendanceType )
    {
        peopleDAO =new PeopleDAO(context);

        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        long scannedPeopleId=peopleDAO.getPeopleId(empCode);

        PeopleEventLog peopleEventLog=new PeopleEventLog();
        peopleEventLog.setAccuracy(-1);
        peopleEventLog.setDeviceid("-1");
        peopleEventLog.setDatetime(String.valueOf(inoutTimestamp));
        peopleEventLog.setGpslocation(gpsLocation);
        peopleEventLog.setPhotorecognitionthreshold(-1);
        peopleEventLog.setPhotorecognitionscore(-1);
        peopleEventLog.setPhotorecognitiontimestamp(null);
        peopleEventLog.setPhotorecognitionserviceresponse(null);
        peopleEventLog.setFacerecognition("false");
        peopleEventLog.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        peopleEventLog.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        //peopleEventLog.setIsdeleted("false");
        peopleEventLog.setCuser(peopleId);
        peopleEventLog.setMuser(peopleId);
        peopleEventLog.setPeopleid(scannedPeopleId);
        peopleEventLog.setPeventtype(typeAssistDAO.getEventTypeID(attendanceType));
        peopleEventLog.setPunchstatus(typeAssistDAO.getEventTypeID(punchStatus));
        peopleEventLog.setVerifiedby(-1);
        peopleEventLog.setScanPeopleCode(empCode);
        peopleEventLog.setBuid(siteID);
        peopleEventLog.setGfid(-1);
        peopleEventLog.setDistance(0);
        peopleEventLog.setDuration(0);
        peopleEventLog.setExpamt(0.0);
        peopleEventLog.setReference("");
        peopleEventLog.setRemarks("");
        peopleEventLog.setTransportmode(-1);
        peopleEventLogDAO.insertRecord(peopleEventLog);

    }


}
