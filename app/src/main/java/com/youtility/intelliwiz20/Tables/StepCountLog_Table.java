package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * login user Steps Count table
 */


public class StepCountLog_Table implements BaseColumns {

    public static final String TABLE_NAME= "StepCountLog";

    public static final String STEPCOUNT_TIMESTAMP="stepCountTimestamp";
    public static final String STEPCOUNT_STEPS="stepCountSteps";
    public static final String STEPCOUNT_TOTALSTEPS="stepCountTotalSteps";
    public static final String STEPCOUNT_STEPS_TAKEN="stepCountStepsTaken";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                             +" "+   ID_TYPE     +   ","+
            STEPCOUNT_TIMESTAMP             +" "+   INT_TYPE    +   ","+
            STEPCOUNT_STEPS                 +" "+   TEXT_TYPE    +   ","+
            STEPCOUNT_STEPS_TAKEN           +" "+   TEXT_TYPE    +   ","+
            STEPCOUNT_TOTALSTEPS            +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("StepCountLog Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {

    }

}
