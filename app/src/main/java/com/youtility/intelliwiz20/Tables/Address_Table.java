package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * Address table
 */


public class Address_Table implements BaseColumns {

    public static final String TABLE_NAME= "Address";

    public static final String ADDRESS_ID="addressid";
    public static final String ADDRESS_ADDRESS="address";
    public static final String ADDRESS_LANDMARK="landmark";
    public static final String ADDRESS_POSTALCODE="postalcode";
    public static final String ADDRESS_MOBILENO="mobileNo";
    public static final String ADDRESS_PHONENO="phoneNo";
    public static final String ADDRESS_FAXNO="faxNo";
    public static final String ADDRESS_WEBSITE="website";
    public static final String ADDRESS_EMAIL="email";
    public static final String ADDRESS_GPSLOCATION="gpslocation";
    public static final String ADDRESS_TYPE="addressType";
    public static final String ADDRESS_CITY="city";
    public static final String ADDRESS_COUNTRY="country";
    public static final String ADDRESS_PEOPLEID="peopleid";
    public static final String ADDRESS_SITEID="siteid";
    public static final String ADDRESS_STATE="state";
    public static final String ADDRESS_ASSETID="assetid";
    public static final String ADDRESS_BUID="buid";

    public static final String ADDRESS_CUSER="cuser";
    public static final String ADDRESS_MUSER="muser";
    public static final String ADDRESS_CDTZ="cdtz";
    public static final String ADDRESS_MDTZ="mdtz";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                     +" "+   ID_TYPE      +   ","+
            ADDRESS_ID              +" "+   INT_TYPE     +   ","+
            ADDRESS_PEOPLEID        +" "+   INT_TYPE     +   ","+
            ADDRESS_SITEID          +" "+   INT_TYPE     +   ","+
            ADDRESS_TYPE            +" "+   INT_TYPE     +   ","+
            ADDRESS_ADDRESS         +" "+   TEXT_TYPE    +   ","+
            ADDRESS_LANDMARK        +" "+   TEXT_TYPE    +   ","+
            ADDRESS_COUNTRY         +" "+   INT_TYPE     +   ","+
            ADDRESS_STATE           +" "+   INT_TYPE     +   ","+
            ADDRESS_CITY            +" "+   INT_TYPE     +   ","+
            ADDRESS_POSTALCODE      +" "+   TEXT_TYPE    +   ","+
            ADDRESS_PHONENO         +" "+   TEXT_TYPE    +   ","+
            ADDRESS_FAXNO           +" "+   TEXT_TYPE    +   ","+
            ADDRESS_MOBILENO        +" "+   TEXT_TYPE    +   ","+
            ADDRESS_WEBSITE         +" "+   TEXT_TYPE    +   ","+
            ADDRESS_EMAIL           +" "+   TEXT_TYPE    +   ","+
            ADDRESS_GPSLOCATION     +" "+   TEXT_TYPE    +   ","+
            ADDRESS_CUSER           +" "+   INT_TYPE     +   ","+
            ADDRESS_MUSER           +" "+   INT_TYPE     +   ","+
            ADDRESS_CDTZ            +" "+   TEXT_TYPE    +   ","+
            ADDRESS_MDTZ            +" "+   TEXT_TYPE    +   ","+
            ADDRESS_ASSETID         +" "+   TEXT_TYPE    +   ","+
            ADDRESS_BUID            +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("Address Table Created");
    }

}
