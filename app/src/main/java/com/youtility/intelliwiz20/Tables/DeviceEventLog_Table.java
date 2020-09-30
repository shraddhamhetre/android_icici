package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * Device event log table
 *
 */

//deviceid, eventvalue, gpslocation, accuracy, altitude, batterylevel, signalstrength, availextmemory, availintmemory,
// cdtz, mdtz, isdeleted, cuser, eventtype, muser, peoplecode, signalbandwidth
public class DeviceEventLog_Table implements BaseColumns {

    public static final String TABLE_NAME= "DeviceEventLog";

    public static final String DEVICEEVENT_LOGID="deviceeventlogid";
    public static final String DEVICEEVENT_IMEI="deviceid";
    public static final String DEVICEEVENT_EVENTVALUE="eventvalue";
    public static final String DEVICEEVENT_GPS_LOCATION="gpslocation";
    public static final String DEVICEEVENT_ACCURACY="accuracy";
    public static final String DEVICEEVENT_ALTITUDE="altitude";
    public static final String DEVICEEVENT_BATTERYLEVEL="batterylevel";
    public static final String DEVICEEVENT_SIGNALSTRENGTH="signalstrength";
    public static final String DEVICEEVENT_AVAILEXTERNALMEMORY="availextmemory";
    public static final String DEVICEEVENT_AVAILINTERNALMEMORY="availintmemory";
    public static final String DEVICEEVENT_EVENTTYPE="eventtype";
    public static final String DEVICEEVENT_PEOPLEID="peopleid";

    public static final String DEVICEEVENT_CUSER="cuser";
    public static final String DEVICEEVENT_MUSER="muser";
    public static final String DEVICEEVENT_CDTZ="cdtz";
    public static final String DEVICEEVENT_MDTZ="mdtz";

    public static final String DEVICEEVENT_BADNWIDTHSIGNAL="signalbandwidth";
    public static final String DEVICEEVENT_SYNCSTATUS="syncStatus";
    //public static final String DEVICEEVENT_ISDELETED="isdeleted";
    public static final String DEVICEEVENT_BUID="buid";

    public static final String DEVICEEVENT_ANDROID_VERSION="androidosversion";
    public static final String DEVICEEVENT_APPLICATION_VERSION="applicationversion";
    public static final String DEVICEEVENT_MODEL_NAME="modelname";
    public static final String DEVICEEVENT_INSTALLED_APPS="installedapps";

    public static final String DEVICEEVENT_SIM_NUMBER="simserialnumber";
    public static final String DEVICEEVENT_LINE_NUMBER="linenumber";
    public static final String DEVICEEVENT_NETWORK_PROVIDER_NAME="networkprovidername";

    public static final String DEVICEEVENT_STEP_COUNT="stepCount";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="integer";
    private static final String REAL_TYPE = "Real";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    /*private static final String DATABASE_ALTER_JOBNEED_MFACTOR1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + JOBNEED_MFACTOR1 + " real;";*/

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                                 +" "+   ID_TYPE     +   ","+
            DEVICEEVENT_LOGID                   +" "+   INT_TYPE     +   ","+
            DEVICEEVENT_PEOPLEID                +" "+   INT_TYPE    +   ","+
            DEVICEEVENT_IMEI                    +" "+   INT_TYPE    +   ","+
            DEVICEEVENT_EVENTTYPE               +" "+   INT_TYPE    +   ","+
            DEVICEEVENT_EVENTVALUE              +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_GPS_LOCATION            +" "+   TEXT_TYPE   +   ","+
            DEVICEEVENT_ACCURACY                +" "+   REAL_TYPE    +   ","+
            DEVICEEVENT_ALTITUDE                +" "+   REAL_TYPE   +   ","+
            DEVICEEVENT_BATTERYLEVEL            +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_SIGNALSTRENGTH          +" "+   TEXT_TYPE   +   ","+
            DEVICEEVENT_AVAILEXTERNALMEMORY     +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_AVAILINTERNALMEMORY     +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_BADNWIDTHSIGNAL         +" "+   TEXT_TYPE   +   ","+
            DEVICEEVENT_CUSER                   +" "+   INT_TYPE    +   ","+
            DEVICEEVENT_MUSER                   +" "+   INT_TYPE   +   ","+
            DEVICEEVENT_CDTZ                    +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_SYNCSTATUS              +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_BUID                    +" "+   INT_TYPE    +   ","+
            DEVICEEVENT_ANDROID_VERSION         +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_APPLICATION_VERSION     +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_MODEL_NAME              +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_INSTALLED_APPS          +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_SIM_NUMBER              +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_LINE_NUMBER             +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_NETWORK_PROVIDER_NAME   +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_STEP_COUNT              +" "+   TEXT_TYPE    +   ","+
            DEVICEEVENT_MDTZ                    +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("DeviceEventLog Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {
        /*db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        OnCreate(db);*/

        /*if(newv>oldv)
            db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR1);*/
    }

}
