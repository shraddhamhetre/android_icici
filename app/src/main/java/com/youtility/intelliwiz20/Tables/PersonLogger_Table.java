package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 08/06/2018
 *
 * Person Logger Table
 */

/*personloggerid,identifier,peopleid,visitoridno,firstname,middlename,lastname,mobileno,idprooftype,photoidno,belongings,meetingpurpose,
    scheduledintime,scheduledouttime,actualintime,actualouttime,referenceid,dob,localaddress,nativeaddress,qualification,english,currentemployement,lengthofservice,heightincms,
    weightinkgs,waist,ishandicapped,identificationmark,physicalcondition,religion,caste,maritalstatus,gender,areacode,enable,cuser,muser,cdtz,mdtz,buid,clientid*/

public class PersonLogger_Table implements BaseColumns {

    public static final String TABLE_NAME= "PersonLogger";

    public static final String PLOGGER_ID="personloggerid";
    public static final String PLOGGER_IDENTIFIER="identifier";
    public static final String PLOGGER_PEOPLEID="peopleid";
    public static final String PLOGGER_VISITORIDNO="visitoridno";
    public static final String PLOGGER_FIRSTNAME="firstname";
    public static final String PLOGGER_MIDDLENAME="middlename";
    public static final String PLOGGER_LASTNAME="lastname";
    public static final String PLOGGER_MOBILENO="mobileno";
    public static final String PLOGGER_IDPROOFTYPE="idprooftype";
    public static final String PLOGGER_CUSER="cuser";
    public static final String PLOGGER_MUSER="muser";
    public static final String PLOGGER_CDTZ="cdtz";
    public static final String PLOGGER_MDTZ="mdtz";
    public static final String PLOGGER_SYNC_STATUS="syncStatus";
    public static final String PLOGGER_BUID="buid";
    public static final String PLOGGER_PHOTOIDNO="photoidno";
    public static final String PLOGGER_BELONGINGS="belongings";
    public static final String PLOGGER_MEETINGPURPOSE="meetingpurpose";
    public static final String PLOGGER_SCHEDULE_IN_TIME="scheduledintime";
    public static final String PLOGGER_SCHEDULE_OUT_TIME="scheduledouttime";
    public static final String PLOGGER_ACTUAL_IN_TIME="actualintime";
    public static final String PLOGGER_ACTUAL_OUT_TIME="actualouttime";
    public static final String PLOGGER_REFERENCEID="referenceid";
    public static final String PLOGGER_DOB="dob";
    public static final String PLOGGER_LOCALADDRESS="localaddress";
    public static final String PLOGGER_NATIVEADDRESS="nativeaddress";
    public static final String PLOGGER_QUALIFICATION="qualification";
    public static final String PLOGGER_ENGLISH="english";
    public static final String PLOGGER_CURRENTEMPLOYEMENT="currentemployement";
    public static final String PLOGGER_LENGTHOFSERVICE="lengthofservice";
    public static final String PLOGGER_HEIGHT="heightincms";
    public static final String PLOGGER_WEIGHT="weightinkgs";
    public static final String PLOGGER_WAIST="waist";
    public static final String PLOGGER_ISHANDICAPPED="ishandicapped";
    public static final String PLOGGER_IDENTIFICATIONMARK="identificationmark";
    public static final String PLOGGER_PHYSICALCONDITION="physicalcondition";
    public static final String PLOGGER_RELIGION="religion";
    public static final String PLOGGER_CASTE="caste";
    public static final String PLOGGER_MARITALSTATUS="maritalstatus";

    public static final String PLOGGER_GENDER="gender";
    public static final String PLOGGER_L_AREACODE="lareacode";
    public static final String PLOGGER_ENABLE="enable";
    public static final String PLOGGER_CLIENTID="clientid";

    public static final String PLOGGER_N_AREACODE="nareacode";
    public static final String PLOGGER_L_CITY="lcity";
    public static final String PLOGGER_L_STATE="lstate";
    public static final String PLOGGER_N_CITY="ncity";
    public static final String PLOGGER_N_STATE="nstate";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="integer";
    private static final String REAL_TYPE="real";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    /*private static final String DATABASE_ALTER_JOBNEED_MFACTOR1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + JOBNEED_MFACTOR1 + " real;";*/

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                             +" "+   ID_TYPE     +   ","+
            PLOGGER_ID                      +" "+   INT_TYPE    +   ","+
            PLOGGER_IDENTIFIER              +" "+   INT_TYPE    +   ","+
            PLOGGER_PEOPLEID                +" "+   INT_TYPE    +   ","+
            PLOGGER_VISITORIDNO             +" "+   TEXT_TYPE    +   ","+
            PLOGGER_FIRSTNAME               +" "+   TEXT_TYPE    +   ","+
            PLOGGER_MIDDLENAME              +" "+   TEXT_TYPE    +   ","+
            PLOGGER_LASTNAME                +" "+   TEXT_TYPE    +   ","+
            PLOGGER_MOBILENO                +" "+   TEXT_TYPE   +   ","+
            PLOGGER_IDPROOFTYPE             +" "+   INT_TYPE    +   ","+
            PLOGGER_CUSER                   +" "+   INT_TYPE   +   ","+
            PLOGGER_MUSER                   +" "+   INT_TYPE    +   ","+
            PLOGGER_CDTZ                    +" "+   TEXT_TYPE   +   ","+
            PLOGGER_MDTZ                    +" "+   TEXT_TYPE    +   ","+
            PLOGGER_SYNC_STATUS             +" "+   INT_TYPE    +   ","+
            PLOGGER_BUID                    +" "+   INT_TYPE    +   ","+
            PLOGGER_PHOTOIDNO               +" "+   INT_TYPE    +   ","+
            PLOGGER_BELONGINGS              +" "+   TEXT_TYPE    +   ","+
            PLOGGER_MEETINGPURPOSE          +" "+   TEXT_TYPE    +   ","+
            PLOGGER_SCHEDULE_IN_TIME        +" "+   TEXT_TYPE    +   ","+
            PLOGGER_SCHEDULE_OUT_TIME       +" "+   TEXT_TYPE    +   ","+
            PLOGGER_ACTUAL_IN_TIME          +" "+   TEXT_TYPE    +   ","+
            PLOGGER_ACTUAL_OUT_TIME         +" "+   TEXT_TYPE    +   ","+
            PLOGGER_REFERENCEID             +" "+   TEXT_TYPE    +   ","+
            PLOGGER_DOB                     +" "+   TEXT_TYPE    +   ","+
            PLOGGER_LOCALADDRESS            +" "+   TEXT_TYPE    +   ","+
            PLOGGER_NATIVEADDRESS           +" "+   TEXT_TYPE    +   ","+
            PLOGGER_QUALIFICATION           +" "+   TEXT_TYPE    +   ","+
            PLOGGER_ENGLISH                 +" "+   TEXT_TYPE    +   ","+
            PLOGGER_CURRENTEMPLOYEMENT      +" "+   TEXT_TYPE    +   ","+
            PLOGGER_LENGTHOFSERVICE         +" "+   REAL_TYPE    +   ","+
            PLOGGER_HEIGHT                  +" "+   REAL_TYPE    +   ","+
            PLOGGER_WEIGHT                  +" "+   REAL_TYPE    +   ","+
            PLOGGER_WAIST                   +" "+   REAL_TYPE    +   ","+
            PLOGGER_ISHANDICAPPED           +" "+   INT_TYPE    +   ","+
            PLOGGER_IDENTIFICATIONMARK      +" "+   TEXT_TYPE    +   ","+
            PLOGGER_PHYSICALCONDITION       +" "+   TEXT_TYPE    +   ","+
            PLOGGER_RELIGION                +" "+   TEXT_TYPE    +   ","+
            PLOGGER_CASTE                   +" "+   TEXT_TYPE    +   ","+
            PLOGGER_MARITALSTATUS           +" "+   TEXT_TYPE    +   ","+
            PLOGGER_L_AREACODE              +" "+   TEXT_TYPE    +   ","+
            PLOGGER_N_AREACODE              +" "+   TEXT_TYPE    +   ","+
            PLOGGER_L_CITY                  +" "+   TEXT_TYPE    +   ","+
            PLOGGER_L_STATE                 +" "+   INT_TYPE    +   ","+
            PLOGGER_N_CITY                  +" "+   TEXT_TYPE    +   ","+
            PLOGGER_N_STATE                 +" "+   INT_TYPE    +   ","+
            PLOGGER_ENABLE                  +" "+   TEXT_TYPE    +   ","+
            PLOGGER_CLIENTID                +" "+   INT_TYPE    +   ","+
            PLOGGER_GENDER                  +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("PersonLogger_Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {

        /*db.execSQL("DROP TABLE IF EXISTS PersonLogger");
        OnCreate(db);*/

        /*if(newv>oldv)
            db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR1);*/

    }

}
