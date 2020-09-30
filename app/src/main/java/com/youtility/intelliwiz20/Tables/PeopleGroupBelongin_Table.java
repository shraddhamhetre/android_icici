package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * people group mapping master table
 *
 */
//@pgbid@isgrouplead@cdtz@mdtz@isdeleted@cuser@groupcode@muser@peoplecode 9
public class PeopleGroupBelongin_Table implements BaseColumns {

    public static final String TABLE_NAME= "PeopleGroupBelonging";

    public static final String PGB_ID="pgbid";
    public static final String PGB_GROUPID="groupid";
    public static final String PGB_PEOPLEID="peopleid";
    public static final String PGB_ISGROUPLEAD="isgrouplead";
    public static final String PGB_CUSER="cuser";
    public static final String PGB_CDTZ="cdtz";
    public static final String PGB_MUSER="muser";
    public static final String PGB_MDTZ="mdtz";
    //public static final String PGB_ISDELETED="isdeleted";
    public static final String PGB_BUID="buid";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";

    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                     +" "+   ID_TYPE     +   ","+
            PGB_ID                  +" "+   INT_TYPE    +   ","+
            PGB_GROUPID             +" "+   INT_TYPE    +   ","+
            PGB_PEOPLEID             +" "+   INT_TYPE    + ","+
            PGB_ISGROUPLEAD         +" "+   TEXT_TYPE    + ","+
            PGB_CUSER               +" "+   INT_TYPE    + ","+
            PGB_CDTZ                +" "+   TEXT_TYPE    + ","+
            PGB_MUSER               +" "+   INT_TYPE    + ","+
            PGB_BUID                +" "+   INT_TYPE    + ","+
            PGB_MDTZ                +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("PeopleGroupBelonging Table Created");
    }

}
