package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * not in used
 */

public class PeopleAvailability_Table implements BaseColumns {

    public static final String TABLE_NAME= "PeopleAvailability";

    public static final String PA_ID="paid";
    public static final String PA_AVAILABLEDATE="availabledate";
    public static final String PA_SHIFTID="shiftid";
    public static final String PA_PEOPLEID="peopleid";
    //public static final String PA_ISDELETED="isdeleted";
    public static final String PA_CUSER="cuser";
    public static final String PA_CDTZ="cdtz";
    public static final String PA_MUSER="muser";
    public static final String PA_MDTZ="mdtz";
    public static final String PA_BUID="buid";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                     +" "+   ID_TYPE     +   ","+
            PA_ID                   +" "+   INT_TYPE    +   ","+
            PA_AVAILABLEDATE        +" "+   TEXT_TYPE    +   ","+
            PA_SHIFTID              +" "+   INT_TYPE    + ","+
            PA_PEOPLEID             +" "+   INT_TYPE    +   ","+
            PA_CUSER                +" "+   INT_TYPE    + ","+
            PA_CDTZ                 +" "+   TEXT_TYPE    + ","+
            PA_MUSER                +" "+   INT_TYPE    + ","+
            PA_BUID                +" "+   INT_TYPE    + ","+
            PA_MDTZ                 +" "+   TEXT_TYPE    +   ","+
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("PeopleAvailability Table Created");
    }

}
