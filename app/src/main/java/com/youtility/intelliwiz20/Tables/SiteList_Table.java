package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * login user assigned sites master table
 */

//buid,bucode, buname, butype, butypename, enable, incharge

public class SiteList_Table implements BaseColumns {

    public static final String TABLE_NAME= "Sites";

    public static final String SITE_PEOPLE_BUILD="buid";
    public static final String BU_CODE="bucode";
    public static final String BU_NAME="buname";

    public static final String BU_TYPE="butype";
    public static final String BU_TYPENAME="butypename";
    public static final String SITE_ENABLE="enable";
    public static final String SITE_INCHARGE="incharge";

    public static final String SITE_PEOPLE_CUSER="cuser";
    public static final String SITE_PEOPLE_MUSER="muser";
    public static final String SITE_PEOPLE_CDTZ="cdtz";
    public static final String SITE_PEOPLE_MDTZ="mdtz";

    /*public static final String SITE_PEOPLE_ID="sitepeopleid";
    public static final String SITE_PEOPLE_FROMDATE="fromdt";
    public static final String SITE_PEOPLE_UPTODATE="uptodt";
    public static final String SITE_PEOPLE_SITEOWNER="siteowner";
    public static final String SITE_PEOPLE_PEOPLEID="peopleid";
    public static final String SITE_PEOPLE_REPORTTO="reportto";
    public static final String SITE_PEOPLE_SHIFT="shift";
    public static final String SITE_PEOPLE_SLNO="slno";
    public static final String SITE_PEOPLE_POSTINGREV="postingrev";
    public static final String SITE_PEOPLE_CONTRACTID="contractid";
    public static final String SITE_PEOPLE_WORKTYPE="worktype";
    public static final String SITE_REPORT_ID="reportids";
    public static final String SITE_REPORT_NAME="reportnames";*/

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    /*private static final String DATABASE_ALTER_JOBNEED_MFACTOR1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + JOBNEED_MFACTOR1 + " real;";*/

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                             +" "+   ID_TYPE     +   ","+
            SITE_PEOPLE_BUILD               +" "+   INT_TYPE    +   ","+
            SITE_PEOPLE_CUSER               +" "+   INT_TYPE    +   ","+
            SITE_PEOPLE_MUSER               +" "+   INT_TYPE    +   ","+
            SITE_PEOPLE_CDTZ                +" "+   TEXT_TYPE    +   ","+
            SITE_PEOPLE_MDTZ                +" "+   TEXT_TYPE    +   ","+
            SITE_ENABLE                     +" "+   TEXT_TYPE    +   ","+
            BU_TYPE                         +" "+   INT_TYPE    +   ","+
            BU_TYPENAME                     +" "+   TEXT_TYPE    +   ","+
            SITE_INCHARGE                   +" "+   TEXT_TYPE    +   ","+
            BU_CODE                         +" "+   TEXT_TYPE    +   ","+
            BU_NAME                         +" "+   TEXT_TYPE   +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("Sites Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {
        /*db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        OnCreate(db);*/

        /*if(newv>oldv)
            db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR1);*/
    }

}
