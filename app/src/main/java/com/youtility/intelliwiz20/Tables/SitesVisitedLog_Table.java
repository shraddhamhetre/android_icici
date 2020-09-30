package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * login user assigned sites master table
 */

//buid, bucode, buname, punchtime, punchstatus;


public class SitesVisitedLog_Table implements BaseColumns {

    public static final String TABLE_NAME= "SiteVisitedLog";

    public static final String SITEVISITEDLOG_PUNCHSTATUS="punchstatus";
    public static final String SITEVISITEDLOG_PUNCHTIME="punchtime";
    public static final String SITEVISITEDLOG_BUID="buid";
    public static final String SITEVISITEDLOG_BUCODE="bucode";
    public static final String SITEVISITEDLOG_BUNAME="buname";
    public static final String SITEVISITEDLOG_OTHERSITE="remarks";
    public static final String SITEVISITEDLOG_FROM="from";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    /*private static final String DATABASE_ALTER_JOBNEED_MFACTOR1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + JOBNEED_MFACTOR1 + " real;";*/

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                             +" "+   ID_TYPE     +   ","+
            SITEVISITEDLOG_PUNCHSTATUS      +" "+   TEXT_TYPE    +   ","+
            SITEVISITEDLOG_PUNCHTIME        +" "+   TEXT_TYPE    +   ","+
            SITEVISITEDLOG_BUID             +" "+   INT_TYPE    +   ","+
            SITEVISITEDLOG_BUCODE           +" "+   TEXT_TYPE    +   ","+
            SITEVISITEDLOG_OTHERSITE        +" "+   TEXT_TYPE    +   ","+
            SITEVISITEDLOG_BUNAME           +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("SiteVisitedLog Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {
        /*db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        OnCreate(db);*/

        /*if(newv>oldv)
            db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR1);*/
    }

}
