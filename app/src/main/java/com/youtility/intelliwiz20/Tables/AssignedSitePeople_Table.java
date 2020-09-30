package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * People master table
 */
//peoplecode,loginid,password,peoplename,gender,mobileno,email,department,designation,peopletype,salt,fromdt,uptodt,opening,
// closing,enable,dob,doj,gpslocation,cuser,cdtz,muser,mdtz,isdeleted,reportto

public class AssignedSitePeople_Table implements BaseColumns {

    public static final String TABLE_NAME= "AssignedSitePeople";

    public static final String PEOPLE_ID="peopleid";
    public static final String PEOPLE_CODE="peoplecode";
    public static final String PEOPLE_FULLNAME="peoplename";
    public static final String PEOPLE_MOBILENO="mobileno";
    public static final String PEOPLE_EMAIL="email";
    public static final String PEOPLE_DESGINATION="designationname";
    public static final String PEOPLE_SITES="sites";


    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                           +" "+   ID_TYPE     +   ","+
            PEOPLE_ID                     +" "+   INT_TYPE     +   ","+
            PEOPLE_CODE                   +" "+   TEXT_TYPE    +   ","+
            PEOPLE_FULLNAME               +" "+   TEXT_TYPE    +   ","+
            PEOPLE_MOBILENO               +" "+   TEXT_TYPE    +   ","+
            PEOPLE_EMAIL                  +" "+   TEXT_TYPE   +   ","+
            PEOPLE_DESGINATION            +" "+   TEXT_TYPE   +   ","+
            PEOPLE_SITES                  +" "+  TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("AssignedSitePeople Table Created");
    }

    public static void onUpgrade(SQLiteDatabase db, int oldv, int newv)
    {
        System.out.println("AssignedSitePeople Table updated"+ oldv+" : "+newv);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        OnCreate(db);
    }

}
