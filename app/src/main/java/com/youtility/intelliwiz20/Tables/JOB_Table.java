package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * not in used
 */

public class JOB_Table implements BaseColumns {

    public static final String TABLE_NAME= "JOB";

    public static final String JOB_CODE="jobcode";
    public static final String JOB_DESC="JobDesc";

    public static final String JOB_FROM="from";
    public static final String JOB_UPTO="upto";

    public static final String JOB_CRON="cron";
    public static final String JOB_ASSETCODE="assetcode";
    public static final String JOB_QSETNAME="Qsetname";
    public static final String JOB_AATOP="aatop";
    public static final String JOB_AATOG="aatog";

    public static final String JOB_AAATOP="aaatop";
    public static final String JOB_AAATOG="aaatog";
    public static final String JOB_FREQUENCY="frequency";
    public static final String JOB_PRIORITY="priority";
    public static final String JOB_PLANDURATION="planduration";

    public static final String JOB_EXPIRYTIME="expirytime";
    public static final String JOB_GRACETIME="gracetime";

    public static final String JOB_CUSER="cuser";
    public static final String JOB_MUSER="muser";
    public static final String JOB_CDTZ="cdtz";
    public static final String JOB_MDTZ="mdtz";

    private static final String TEXT_TYPE="text";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                     +" "+   ID_TYPE     +   ","+
            JOB_CODE                +" "+   TEXT_TYPE    +   ","+
            JOB_DESC                +" "+   TEXT_TYPE    +   ","+
            JOB_FROM                +" "+   TEXT_TYPE    +   ","+
            JOB_UPTO                +" "+   TEXT_TYPE    +   ","+
            JOB_CRON                +" "+   TEXT_TYPE    +   ","+
            JOB_ASSETCODE           +" "+   TEXT_TYPE    +   ","+
            JOB_QSETNAME            +" "+   TEXT_TYPE   +   ","+
            JOB_AATOP               +" "+   TEXT_TYPE    +   ","+
            JOB_AATOG               +" "+   TEXT_TYPE   +   ","+
            JOB_AAATOP              +" "+   TEXT_TYPE    +   ","+
            JOB_AAATOG              +" "+   TEXT_TYPE   +   ","+
            JOB_FREQUENCY           +" "+   TEXT_TYPE    +   ","+
            JOB_PRIORITY            +" "+   TEXT_TYPE    +   ","+
            JOB_PLANDURATION        +" "+   TEXT_TYPE   +   ","+
            JOB_EXPIRYTIME          +" "+   TEXT_TYPE    +   ","+
            JOB_GRACETIME           +" "+   TEXT_TYPE    +   ","+
            JOB_CUSER               +" "+   TEXT_TYPE    +   ","+
            JOB_MUSER               +" "+   TEXT_TYPE   +   ","+
            JOB_CDTZ                +" "+   TEXT_TYPE    +   ","+
            JOB_MDTZ                +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("JOB Table Created");
    }

}
