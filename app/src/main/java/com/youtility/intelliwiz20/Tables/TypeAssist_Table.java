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

public class TypeAssist_Table implements BaseColumns {

    public static final String TABLE_NAME= "TypeAssist";

    public static final String TYPE_ASSIST_ID="taid";
    public static final String TYPE_ASSIST_CODE="tacode";
    public static final String TYPE_ASSIST_NAME="taname";

    public static final String TYPE_ASSIST_TYPE="tatype";
    public static final String TYPE_ASSIST_CUSER="cuser";

    public static final String TYPE_ASSIST_CDTZ="cdtz";
    public static final String TYPE_ASSIST_MUSER="muser";
    public static final String TYPE_ASSIST_MDTZ="mdtz";
    //public static final String TYPE_ASSIST_ISDELETED="isdeleted";
    public static final String TYPE_ASSIST_PARENT="parent";
    public static final String TYPE_ASSIST_BUID="buid";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                             +" "+   ID_TYPE     +   ","+
            TYPE_ASSIST_ID                  +" "+   INT_TYPE    +   ","+
            TYPE_ASSIST_CODE                +" "+   TEXT_TYPE    +   ","+
            TYPE_ASSIST_NAME                +" "+   TEXT_TYPE    +   ","+
            TYPE_ASSIST_TYPE                +" "+   TEXT_TYPE    +   ","+
            TYPE_ASSIST_CUSER               +" "+   INT_TYPE    +   ","+
            TYPE_ASSIST_CDTZ                +" "+   TEXT_TYPE    +   ","+
            TYPE_ASSIST_MUSER               +" "+   INT_TYPE    +   ","+
            TYPE_ASSIST_BUID                +" "+   INT_TYPE    +   ","+
            TYPE_ASSIST_MDTZ                +" "+   TEXT_TYPE   +   ","+
            TYPE_ASSIST_PARENT              +" "+   INT_TYPE   +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("TypeAssist Table Created");
    }

}
