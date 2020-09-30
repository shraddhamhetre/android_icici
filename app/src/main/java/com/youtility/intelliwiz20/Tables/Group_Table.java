package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * People group master table
 */

public class Group_Table implements BaseColumns {

    public static final String TABLE_NAME= "GroupTable";

    public static final String GROUP_ID="groupid";
    public static final String GROUP_NAME="groupname";
    public static final String GROUP_ENABLE="enable";
    public static final String GROUP_CUSER="cuser";
    public static final String GROUP_CDTZ="cdtz";
   // public static final String GROUP_ISDELETED="isdeleted";
    public static final String GROUP_MUSER="muser";
    public static final String GROUP_MDTZ="mdtz";
    public static final String GROUP_BUID="buid";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                     +" "+   ID_TYPE     +   ","+
            GROUP_ID                +" "+   INT_TYPE    +   ","+
            GROUP_NAME              +" "+   TEXT_TYPE    +   ","+
            GROUP_ENABLE            +" "+   TEXT_TYPE    + ","+
            GROUP_CUSER             +" "+   INT_TYPE    + ","+
            GROUP_CDTZ              +" "+   TEXT_TYPE    + ","+
            GROUP_MUSER             +" "+   INT_TYPE    + ","+
            GROUP_MDTZ              +" "+   TEXT_TYPE    +   ","+
            GROUP_BUID              +" "+   INT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("Group Table Created");
    }

}
