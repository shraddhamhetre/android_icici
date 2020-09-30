package com.youtility.intelliwiz20.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.BatteryManager;
import android.text.format.Formatter;

import com.youtility.intelliwiz20.DataAccessObject.DeviceEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.DeviceEventLog;
import com.youtility.intelliwiz20.Tables.DeviceEventLog_Table;

/**
 * Created by PrashantD on 5/10/17.
 */

public class EventLogInsertion {
    private Context context;
    private DeviceEventLog deviceEventLog;
    private DeviceEventLogDAO deviceEventLogDAO;
    private SharedPreferences deviceRelatedPref;
    private MemoryInfo memoryInfo;
    private SharedPreferences loginPref;
    private SharedPreferences stepCountPref;
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private Cursor c = null;

    private TypeAssistDAO typeAssistDAO;

    public EventLogInsertion(Context context)
    {
        this.context=context;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF,Context.MODE_PRIVATE);
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        stepCountPref=context.getSharedPreferences(Constants.STEP_COUNTER_PREF, Context.MODE_PRIVATE);
        deviceEventLogDAO=new DeviceEventLogDAO(context);
        typeAssistDAO=new TypeAssistDAO(context);
        memoryInfo = new MemoryInfo(context);
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
        System.out.println("EventLogInsertion--------------------------------------------------------");
    }

    public void addDeviceEvent(String eventValue, String eventMessage, String eventType)
    {
        System.out.println("addDeviceEvent");
        CommonFunctions.UploadLog("\n <getLocation servicestarted----> \n");

        int bLeval=0;
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if(batteryIntent!=null)
            bLeval = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        long eventId=typeAssistDAO.getEventTypeID(eventValue,eventType);
        //System.out.println("Device Event Log event id: "+eventId);

        if(eventId!=-1) {

            deviceEventLog = new DeviceEventLog();
            //deviceEventLog.setDeviceid(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1));
            deviceEventLog.setDeviceid(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1"));
            deviceEventLog.setEventvalue(eventMessage);
            deviceEventLog.setEventtype(eventId);
            deviceEventLog.setGpslocation(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE, "0.0") + "," + deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
            deviceEventLog.setAccuracy(Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ACCURACY, "0")));
            deviceEventLog.setAltitude(Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ALTITUDE, "0")));
            deviceEventLog.setBatterylevel(String.valueOf(bLeval + "%"));
            deviceEventLog.setSignalstrength("High");
            deviceEventLog.setAvailextmemory(Formatter.formatFileSize(context, memoryInfo.getAvailableExternalMemory()));
            deviceEventLog.setAvailintmemory(Formatter.formatFileSize(context, memoryInfo.getAvailableInternalMemory()));
            deviceEventLog.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            deviceEventLog.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            //deviceEventLog.setIsdeleted("false");
            deviceEventLog.setCuser((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setMuser((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setPeopleid((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setSignalbandwidth("High");
            deviceEventLog.setBuid(loginPref.getLong(Constants.LOGIN_SITE_ID, -1));
            deviceEventLog.setAndroidosversion(CommonFunctions.getOSVerName(CommonFunctions.getAndroidVersion(context)));
            deviceEventLog.setApplicationversion(CommonFunctions.getApplicationVersion(context));
            deviceEventLog.setModelname(CommonFunctions.getDeviceInformation());
            deviceEventLog.setSimserialnumber("");
            deviceEventLog.setLinenumber("");
            deviceEventLog.setNetworkprovidername(deviceRelatedPref.getString(Constants.DEVICE_LOC_PROVIDER,""));
            if(eventValue.equalsIgnoreCase(Constants.EVENT_TYPE_INSTALLED_APPLICATIONS))
                deviceEventLog.setInstalledapps(CommonFunctions.getInstalledAppList(context));
            else
                deviceEventLog.setInstalledapps("");
            deviceEventLog.setStepCount("0~0");
            System.out.println("DeviceEventLog Insert1: "+eventMessage);
            deviceEventLogDAO.insertRecord(deviceEventLog,"0");

            System.out.println("addDeviceEvent--------------------------------------------------------");

        }
    }

    public void addStepCountEvent(String eventValue, String eventMessage, String eventType)
    {
        //System.out.println("Login code: "+loginPref.getString(Constants.LOGIN_PEOPLE_CODE,""));

        int bLeval=0;
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if(batteryIntent!=null)
            bLeval = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        long eventId=typeAssistDAO.getEventTypeID(eventValue,eventType);
        //System.out.println("Device Event Log event id: "+eventId);

        if(eventId!=-1) {

            deviceEventLog = new DeviceEventLog();
            deviceEventLog.setDeviceid(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1"));
            deviceEventLog.setEventvalue(eventMessage);
            deviceEventLog.setEventtype(eventId);
            deviceEventLog.setGpslocation(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE, "0.0") + "," + deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
            deviceEventLog.setAccuracy(Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ACCURACY, "0")));
            deviceEventLog.setAltitude(Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ALTITUDE, "0")));
            deviceEventLog.setBatterylevel(String.valueOf(bLeval + "%"));
            deviceEventLog.setSignalstrength("High");
            deviceEventLog.setAvailextmemory(Formatter.formatFileSize(context, memoryInfo.getAvailableExternalMemory()));
            deviceEventLog.setAvailintmemory(Formatter.formatFileSize(context, memoryInfo.getAvailableInternalMemory()));
            deviceEventLog.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            deviceEventLog.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            //deviceEventLog.setIsdeleted("false");
            deviceEventLog.setCuser((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setMuser((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setPeopleid((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setSignalbandwidth("High");
            deviceEventLog.setBuid(loginPref.getLong(Constants.LOGIN_SITE_ID, -1));
            deviceEventLog.setAndroidosversion(CommonFunctions.getOSVerName(CommonFunctions.getAndroidVersion(context)));
            deviceEventLog.setApplicationversion(CommonFunctions.getApplicationVersion(context));
            deviceEventLog.setModelname(CommonFunctions.getDeviceInformation());
            deviceEventLog.setSimserialnumber("");
            deviceEventLog.setLinenumber("");
            deviceEventLog.setNetworkprovidername("");
            if(eventValue.equalsIgnoreCase(Constants.EVENT_TYPE_INSTALLED_APPLICATIONS))
                deviceEventLog.setInstalledapps(CommonFunctions.getInstalledAppList(context));
            else
                deviceEventLog.setInstalledapps("");
            deviceEventLog.setStepCount(stepCountPref.getString(Constants.STEP_COUNTER_TIME,"0")+"~"+stepCountPref.getLong(Constants.STEP_COUNTER_COUNT,0));
            System.out.println("DeviceEventLog Insert2: "+eventMessage);
            deviceEventLogDAO.insertRecord(deviceEventLog,"0");
        }
    }

    public int editBuzzerStepCountEvent(long deviceEventLogId,String captchaValue)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DeviceEventLog_Table.DEVICEEVENT_STEP_COUNT, captchaValue);
            values.put(DeviceEventLog_Table.DEVICEEVENT_SYNCSTATUS,"0");
            return db.update(DeviceEventLog_Table.TABLE_NAME, values, "deviceeventlogid=?", new String[] {String.valueOf(deviceEventLogId)});
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {

        }
    }

    public void addBuzzerStepCountEvent(String eventValue, String eventMessage, String eventType, String capchaString, long deviceEventLogId)
    {
        //System.out.println("Login code: "+loginPref.getString(Constants.LOGIN_PEOPLE_CODE,""));

        int bLeval=0;
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if(batteryIntent!=null)
            bLeval = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        long eventId=typeAssistDAO.getEventTypeID(eventValue,eventType);
        //System.out.println("Device Event Log event id: "+eventId);

        if(eventId!=-1) {

            deviceEventLog = new DeviceEventLog();
            deviceEventLog.setDeviceeventlogid(deviceEventLogId);
            deviceEventLog.setDeviceid(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1"));
            deviceEventLog.setEventvalue(eventMessage);
            deviceEventLog.setEventtype(eventId);
            deviceEventLog.setGpslocation(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE, "0.0") + "," + deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
            deviceEventLog.setAccuracy(Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ACCURACY, "0")));
            deviceEventLog.setAltitude(Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ALTITUDE, "0")));
            deviceEventLog.setBatterylevel(String.valueOf(bLeval + "%"));
            deviceEventLog.setSignalstrength("High");
            deviceEventLog.setAvailextmemory(Formatter.formatFileSize(context, memoryInfo.getAvailableExternalMemory()));
            deviceEventLog.setAvailintmemory(Formatter.formatFileSize(context, memoryInfo.getAvailableInternalMemory()));
            deviceEventLog.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            deviceEventLog.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            //deviceEventLog.setIsdeleted("false");
            deviceEventLog.setCuser((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setMuser((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setPeopleid((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setSignalbandwidth("High");
            deviceEventLog.setBuid(loginPref.getLong(Constants.LOGIN_SITE_ID, -1));
            deviceEventLog.setAndroidosversion(CommonFunctions.getOSVerName(CommonFunctions.getAndroidVersion(context)));
            deviceEventLog.setApplicationversion(CommonFunctions.getApplicationVersion(context));
            deviceEventLog.setModelname(CommonFunctions.getDeviceInformation());
            deviceEventLog.setSimserialnumber("");
            deviceEventLog.setLinenumber("");
            deviceEventLog.setNetworkprovidername("");
            if(eventValue.equalsIgnoreCase(Constants.EVENT_TYPE_INSTALLED_APPLICATIONS))
                deviceEventLog.setInstalledapps(CommonFunctions.getInstalledAppList(context));
            else
                deviceEventLog.setInstalledapps("");
            deviceEventLog.setStepCount(capchaString);
            System.out.println("DeviceEventLog Insert3: "+eventMessage);
            deviceEventLogDAO.insertRecord(deviceEventLog,"-1");
        }
    }

    public void addNetworkInfo(String eventValue, String eventMessage, String eventType, String simSerialNumber, String lineNumber, String providerName)
    {
        //System.out.println("Login code: "+loginPref.getString(Constants.LOGIN_PEOPLE_CODE,""));

        int bLeval=0;
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if(batteryIntent!=null)
            bLeval = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        long eventId=typeAssistDAO.getEventTypeID(eventValue,eventType);
        //System.out.println("Device Event Log event id: "+eventId);

        if(eventId!=-1) {

            deviceEventLog = new DeviceEventLog();
            deviceEventLog.setDeviceid(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1"));
            deviceEventLog.setEventvalue(eventMessage);
            deviceEventLog.setEventtype(eventId);
            deviceEventLog.setGpslocation(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE, "0.0") + "," + deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
            deviceEventLog.setAccuracy(Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ACCURACY, "0")));
            deviceEventLog.setAltitude(Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ALTITUDE, "0")));
            deviceEventLog.setBatterylevel(String.valueOf(bLeval + "%"));
            deviceEventLog.setSignalstrength("High");
            deviceEventLog.setAvailextmemory(Formatter.formatFileSize(context, memoryInfo.getAvailableExternalMemory()));
            deviceEventLog.setAvailintmemory(Formatter.formatFileSize(context, memoryInfo.getAvailableInternalMemory()));
            deviceEventLog.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            deviceEventLog.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            //deviceEventLog.setIsdeleted("false");
            deviceEventLog.setCuser((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setMuser((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setPeopleid((loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1)));
            deviceEventLog.setSignalbandwidth("High");
            deviceEventLog.setBuid(loginPref.getLong(Constants.LOGIN_SITE_ID, -1));
            deviceEventLog.setAndroidosversion(CommonFunctions.getOSVerName(CommonFunctions.getAndroidVersion(context)));
            deviceEventLog.setApplicationversion(CommonFunctions.getApplicationVersion(context));
            deviceEventLog.setModelname(CommonFunctions.getDeviceInformation());
            deviceEventLog.setSimserialnumber(simSerialNumber);
            deviceEventLog.setLinenumber(lineNumber);
            deviceEventLog.setNetworkprovidername(providerName);
            if(eventValue.equalsIgnoreCase(Constants.EVENT_TYPE_INSTALLED_APPLICATIONS))
                deviceEventLog.setInstalledapps(CommonFunctions.getInstalledAppList(context));
            else
                deviceEventLog.setInstalledapps("");
            deviceEventLog.setStepCount("0~0");
            System.out.println("DeviceEventLog Insert4: "+eventMessage);
            deviceEventLogDAO.insertRecord(deviceEventLog,"0");
        }
    }
}
