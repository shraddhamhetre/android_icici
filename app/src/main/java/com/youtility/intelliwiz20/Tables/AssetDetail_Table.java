package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * Asset Table
 */

public class AssetDetail_Table implements BaseColumns {

    public static final String TABLE_NAME= "AssetDetails";

    public static final String ASSET_ID="assetid";
    public static final String ASSET_CODE="assetcode";
    public static final String ASSET_NAME="assetname";
    public static final String ASSET_ENABLE="enable";
    public static final String ASSET_IS_CRITICAL="iscritical";
    public static final String ASSET_GPS_LOCATION="gpslocation";
    public static final String ASSET_PARENT="parent";
    public static final String ASSET_IDENTIFIER="identifier";
    public static final String ASSET_RUNNING_STATUS="runningstatus";

    public static final String ASSET_CUSER="cuser";
    public static final String ASSET_MUSER="muser";
    public static final String ASSET_CDTZ="cdtz";
    public static final String ASSET_IS_DELETED="isdeleted";
    public static final String ASSET_MDTZ="mdtz";
    public static final String ASSET_SYNC_STATUS="syncStatus";
    public static final String ASSET_BUID="buid";
    public static final String ASSET_LOCATION_CODE="loccode";
    public static final String ASSET_LOCATION_NAME="locname";

    public static final String ASSET_TYPE="type";
    public static final String ASSET_CATEGORY="category";
    public static final String ASSET_SUBCATEGORY="subcategory";
    public static final String ASSET_BRAND="brand";
    public static final String ASSET_MODEL="model";
    public static final String ASSET_SUPPLIER="supplier";
    public static final String ASSET_CAPACITY="capacity";
    public static final String ASSET_UNIT="unit";
    public static final String ASSET_YOM="yom";
    public static final String ASSET_MSN="msn";
    public static final String ASSET_BILLDATE="bdate";
    public static final String ASSET_PURCHACEDATE="pdate";
    public static final String ASSET_INSTALLATIONDATE="isdate";
    public static final String ASSET_BILLVALUE="billval";
    public static final String ASSET_SERVICE="service";
    public static final String ASSET_SERVICEPROVIDER="servprov";
    public static final String ASSET_SERVICEPROVIDER_NAME="servprovname";
    public static final String ASSET_SERVICEFROMDATE="sfdate";
    public static final String ASSET_SERVICETODATE="stdate";
    public static final String ASSET_METER="meter";
    public static final String ASSET_QSETIDS="qsetids";
    public static final String ASSET_QSETNAME="qsetname";
    public static final String ASSET_TEMPCODE="tempcode";
    public static final String ASSET_MFACTOR="multiplicationfactor";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="integer";
    private static final String REAL_TYPE="real";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    /*private static final String DATABASE_ALTER_JOBNEED_MFACTOR1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + JOBNEED_MFACTOR1 + " real;";*/
    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                     +" "+   ID_TYPE     +   ","+
            ASSET_ID                +" "+   INT_TYPE    +   ","+
            ASSET_ENABLE            +" "+   TEXT_TYPE    +   ","+
            ASSET_PARENT            +" "+   INT_TYPE    +   ","+
            ASSET_CUSER             +" "+   INT_TYPE    +   ","+
            ASSET_MUSER             +" "+   INT_TYPE    +   ","+
            ASSET_CDTZ              +" "+   TEXT_TYPE    +   ","+
            ASSET_IS_DELETED        +" "+   TEXT_TYPE    +   ","+
            ASSET_CODE              +" "+   TEXT_TYPE   +   ","+
            ASSET_MDTZ              +" "+   TEXT_TYPE    +   ","+
            ASSET_NAME              +" "+   TEXT_TYPE   +   ","+
            ASSET_IS_CRITICAL       +" "+   TEXT_TYPE    +   ","+
            ASSET_GPS_LOCATION      +" "+   TEXT_TYPE   +   ","+
            ASSET_IDENTIFIER        +" "+   INT_TYPE    +   ","+
            ASSET_SYNC_STATUS       +" "+   TEXT_TYPE    +   ","+
            ASSET_BUID              +" "+   INT_TYPE    +   ","+
            ASSET_RUNNING_STATUS    +" "+   INT_TYPE    +   ","+
            ASSET_LOCATION_CODE     +" "+   TEXT_TYPE    +   ","+
            ASSET_TYPE              +" "+   INT_TYPE    +   ","+
            ASSET_CATEGORY          +" "+   INT_TYPE    +   ","+
            ASSET_SUBCATEGORY       +" "+   INT_TYPE    +   ","+
            ASSET_BRAND             +" "+   INT_TYPE    +   ","+
            ASSET_MODEL             +" "+   INT_TYPE    +   ","+
            ASSET_SUPPLIER          +" "+   TEXT_TYPE    +   ","+
            ASSET_CAPACITY          +" "+   REAL_TYPE    +   ","+
            ASSET_UNIT              +" "+   INT_TYPE    +   ","+
            ASSET_YOM               +" "+   TEXT_TYPE    +   ","+
            ASSET_MSN               +" "+   TEXT_TYPE    +   ","+
            ASSET_BILLDATE          +" "+   TEXT_TYPE    +   ","+
            ASSET_PURCHACEDATE      +" "+   TEXT_TYPE    +   ","+
            ASSET_INSTALLATIONDATE  +" "+   TEXT_TYPE    +   ","+
            ASSET_BILLVALUE         +" "+   REAL_TYPE    +   ","+
            ASSET_SERVICEPROVIDER   +" "+   INT_TYPE    +   ","+
            ASSET_SERVICEPROVIDER_NAME   +" "+   TEXT_TYPE    +   ","+
            ASSET_SERVICE           +" "+   INT_TYPE    +   ","+
            ASSET_SERVICEFROMDATE   +" "+   TEXT_TYPE    +   ","+
            ASSET_SERVICETODATE     +" "+   TEXT_TYPE    +   ","+
            ASSET_METER             +" "+   INT_TYPE    +   ","+
            ASSET_QSETIDS           +" "+   TEXT_TYPE    +   ","+
            ASSET_QSETNAME          +" "+   TEXT_TYPE    +   ","+
            ASSET_TEMPCODE          +" "+   TEXT_TYPE    +   ","+
            ASSET_MFACTOR           +" "+   REAL_TYPE   +   ","+
            ASSET_LOCATION_NAME     +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("Asset Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {
        /*db.execSQL("DROP TABLE IF EXISTS AssetDetails");
        OnCreate(db);*/

        /*if(newv>oldv)
            db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR1);*/
    }

}
