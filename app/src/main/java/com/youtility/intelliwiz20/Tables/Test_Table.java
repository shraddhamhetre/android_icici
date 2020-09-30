package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * Typeassist master table
 *
 */

//tacode,taname,tatype,cuser,cdtz,muser,mdtz,isdeleted,parent

public class Test_Table implements BaseColumns {

    public static final String TABLE_NAME= "Test";

    public static final String TYPE_ASSIST_ID="taid";
    public static final String TYPE_ASSIST_CODE="tacode";
    public static final String TYPE_ASSIST_NAME="taname";

    //public static final String TYPE_ASSIST_TEST="tatest";



    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                             +" "+   ID_TYPE     +   ","+
            TYPE_ASSIST_ID                  +" "+   INT_TYPE    +   ","+
            TYPE_ASSIST_CODE                +" "+   TEXT_TYPE    +   ","+
            TYPE_ASSIST_NAME                +" "+   TEXT_TYPE    + /*","+
            TYPE_ASSIST_TEST                +" "+   TEXT_TYPE     +*/
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("Test Table Created");
    }

}
