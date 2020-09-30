package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * Attendance History
 *
 */
//pelogid, peoplecode, datetime, isdeleted , punchstatus

public class AttendanceHistoy_Table implements BaseColumns {

    public static final String TABLE_NAME= "AttendanceHistoyTable";

    public static final String ATTENDANCEHISOTY_PELOGID="pelogid";
    public static final String ATTENDANCEHISOTY_PEOPLECODE="peopleid";
    public static final String ATTENDANCEHISOTY_DATETIME="datetime";
    public static final String ATTENDANCEHISOTY_CUSER="cuser";
    public static final String ATTENDANCEHISOTY_PUNCHSTATUS="punchstatus";
    public static final String ATTENDANCEHISOTY_PUNCHTYPE="punchtype";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                     +" "+   ID_TYPE     +   ","+
            ATTENDANCEHISOTY_PELOGID              +" "+   INT_TYPE    +   ","+
            ATTENDANCEHISOTY_PEOPLECODE              +" "+   TEXT_TYPE    +   ","+
            ATTENDANCEHISOTY_DATETIME            +" "+   TEXT_TYPE    + ","+
            ATTENDANCEHISOTY_CUSER             +" "+   INT_TYPE    + ","+
            ATTENDANCEHISOTY_PUNCHTYPE             +" "+   INT_TYPE    + ","+
            ATTENDANCEHISOTY_PUNCHSTATUS              +" "+   INT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("AttendanceHistoy Table Created");
    }

}
