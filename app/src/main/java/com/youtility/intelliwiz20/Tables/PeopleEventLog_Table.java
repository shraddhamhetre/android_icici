package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * People event log transation table
 */
//accuracy, deviceid, datetime, gpslocation, photorecognitionthreshold,photorecognitionscore, " +
//"photorecognitiontimestamp, photorecognitionserviceresponse,facerecognition, cdtz, mdtz, isdeleted, cuser, muser, peoplecode, peventtype, punchstatus, verifiedby
public class PeopleEventLog_Table implements BaseColumns {

    public static final String TABLE_NAME= "PeopleEventLog";

    public static final String PE_LOGID="pelogid";

    public static final String PE_ACCURACY="accuracy";
    public static final String PE_DEVICEID="deviceid";
    public static final String PE_DATETIME="datetime";
    public static final String PE_GPSLOCATION="gpslocation";
    public static final String PE_PR_THRESHOLD="photorecognitionthreshold";
    public static final String PE_PR_SCORE="photorecognitionscore";
    public static final String PE_PR_TIMESTAMP="photorecognitiontimestamp";
    public static final String PE_PR_RESPONSE="photorecognitionserviceresponse";
    public static final String PE_FACEREGONITION="facerecognition";

    public static final String PE_GFID="gfid";
    //public static final String PE_ISDELETED="isdeleted";
    public static final String PE_PEOPLEID="peopleid";
    public static final String PE_TYPE="peventtype";
    public static final String PE_PUNCHSTATUS="punchstatus";
    public static final String PE_VARIFIEDBY="verifiedby";

    public static final String PE_CUSER="cuser";
    public static final String PE_MUSER="muser";
    public static final String PE_CDTZ="cdtz";
    public static final String PE_MDTZ="mdtz";
    public static final String PE_BUID="buid";
    public static final String PE_SYNCSTATUS="syncStatus";
    public static final String PE_SCAN_PEOPLECODE="scanPeopleCode";

    public static final String PE_TRANSPORTMODE="transportmode";
    public static final String PE_EXPENCES="expamt";
    public static final String PE_DURATION="duration";
    public static final String PE_DISTANCE="distance";
    public static final String PE_REFERENCE="reference";
    public static final String PE_REMARKS="remarks";

    public static final String PE_OTHERLOCATION="otherlocation";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String REAL_TYPE="Real";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    /*private static final String DATABASE_ALTER_JOBNEED_MFACTOR1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + JOBNEED_MFACTOR1 + " real;";*/

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                     +" "+   ID_TYPE     +   ","+
            PE_LOGID                +" "+   INT_TYPE    +   ","+
            PE_ACCURACY             +" "+   REAL_TYPE    +   ","+
            PE_DEVICEID             +" "+   INT_TYPE    +   ","+
            PE_DATETIME             +" "+   TEXT_TYPE    +   ","+
            PE_GPSLOCATION          +" "+   TEXT_TYPE    +   ","+
            PE_PR_THRESHOLD         +" "+   INT_TYPE    +   ","+
            PE_PR_SCORE             +" "+   TEXT_TYPE    +   ","+
            PE_PR_TIMESTAMP         +" "+   TEXT_TYPE    +   ","+
            PE_PR_RESPONSE          +" "+   TEXT_TYPE    +   ","+
            PE_FACEREGONITION       +" "+   TEXT_TYPE    +   ","+
            PE_PEOPLEID             +" "+   INT_TYPE    +   ","+
            PE_TYPE                 +" "+   TEXT_TYPE    +   ","+
            PE_PUNCHSTATUS          +" "+   TEXT_TYPE    +   ","+
            PE_VARIFIEDBY           +" "+   INT_TYPE    +   ","+
            PE_SYNCSTATUS           +" "+   TEXT_TYPE    +   ","+
            PE_BUID                 +" "+   INT_TYPE    +   ","+
            PE_CUSER                +" "+   INT_TYPE    +   ","+
            PE_MUSER                +" "+   INT_TYPE    +   ","+
            PE_MDTZ                 +" "+   TEXT_TYPE    +   ","+
            PE_GFID                 +" "+   INT_TYPE    +   ","+
            PE_SCAN_PEOPLECODE      +" "+   TEXT_TYPE    +   ","+
            PE_TRANSPORTMODE        +" "+   INT_TYPE    +   ","+
            PE_EXPENCES             +" "+   REAL_TYPE    +   ","+
            PE_DURATION             +" "+   INT_TYPE    +   ","+
            PE_DISTANCE             +" "+   INT_TYPE    +   ","+
            PE_REFERENCE            +" "+   TEXT_TYPE    +   ","+
            PE_REMARKS              +" "+   TEXT_TYPE    +   ","+
            PE_OTHERLOCATION        +" "+   TEXT_TYPE    +   ","+
            PE_CDTZ                 +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("PeopleEventLog Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {
        /*db.execSQL("DROP TABLE IF EXISTS PeopleEventLog");
        OnCreate(db);*/

        /*if(newv>oldv)
            db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR1);*/
    }

}
