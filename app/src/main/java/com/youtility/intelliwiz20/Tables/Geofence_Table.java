package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * Geofence master table
 */
// g.gfid, g.gfcode, g.gfname, g.geofence, g.enable, j.aaatop as peopleid, j.fromdt, j.uptodt, j.identifier, j.starttime, j.endtime,

public class Geofence_Table implements BaseColumns {

    public static final String TABLE_NAME= "Geofence";

    public static final String GEOFENCE_ID="gfid";
    public static final String GEOFENCE_CODE="gfcode";
    public static final String GEOFENCE_NAME="gfname";
    public static final String GEOFENCE_GEOFENCE_POINTS="geofence";
    public static final String GEOFENCE_ENABLE="enable";

    public static final String GEOFENCE_PEOPLEID="peopleid";
    public static final String GEOFENCE_FROMDATE="fromdt";

    public static final String GEOFENCE_UPTODATE="uptodt";
    public static final String GEOFENCE_IDENTIFIER="identifier";
    public static final String GEOFENCE_STARTTIME="starttime";
    public static final String GEOFENCE_ENDTIME="endtime";
    public static final String GEOFENCE_BUID="buid";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                             +" "+   ID_TYPE     +   ","+
            GEOFENCE_ID                     +" "+   INT_TYPE     +   ","+
            GEOFENCE_CODE                   +" "+   TEXT_TYPE    +   ","+
            GEOFENCE_NAME                   +" "+   TEXT_TYPE    +   ","+
            GEOFENCE_GEOFENCE_POINTS        +" "+   TEXT_TYPE    +   ","+
            GEOFENCE_PEOPLEID               +" "+   INT_TYPE    +   ","+
            GEOFENCE_FROMDATE               +" "+   TEXT_TYPE    +   ","+
            GEOFENCE_UPTODATE               +" "+   TEXT_TYPE    +   ","+
            GEOFENCE_ENABLE                 +" "+   TEXT_TYPE   +   ","+
            GEOFENCE_IDENTIFIER             +" "+   INT_TYPE    +   ","+
            GEOFENCE_STARTTIME              +" "+   TEXT_TYPE    +   ","+
            GEOFENCE_BUID                   +" "+   INT_TYPE    +   ","+
            GEOFENCE_ENDTIME                +" "+   TEXT_TYPE   +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("Geofence Table Created");
    }

}
