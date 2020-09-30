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

public class People_Table implements BaseColumns {

    public static final String TABLE_NAME= "People";

    public static final String PEOPLE_ID="peopleid";
    public static final String PEOPLE_CODE="peoplecode";
    public static final String PEOPLE_FULLNAME="peoplename";
    public static final String PEOPLE_LOGINID="loginid";
    public static final String PEOPLE_PASSWORD="password";
    public static final String PEOPLE_SALT="salt";
    public static final String PEOPLE_GENDER="gender";
    public static final String PEOPLE_MOBILENO="mobileno";
    public static final String PEOPLE_EMAIL="email";
    public static final String PEOPLE_DOB="dob";
    public static final String PEOPLE_DOJ="doj";
    public static final String PEOPLE_DOR="dor";
    public static final String PEOPLE_ENABLE="enable";
    public static final String PEOPLE_LOCATIONTRACKING="locationtracking";
    public static final String PEOPLE_DEPARTMENT="department";
    public static final String PEOPLE_DESGINATION="designation";
    public static final String PEOPLE_TYPE="peopletype";
    public static final String PEOPLE_REPORTTO="reportto";
    public static final String PEOPLE_CUSER="cuser";
    public static final String PEOPLE_CDTZ="cdtz";
    public static final String PEOPLE_MUSER="muser";
    public static final String PEOPLE_MDTZ="mdtz";
    public static final String PEOPLE_BUID="buid";
    public static final String PEOPLE_CAPTURE_M_LOG="capturemlog";
    public static final String PEOPLE_M_LOG_SEND_TO="mlogsendto";



    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    /*private static final String DATABASE_ALTER_JOBNEED_MFACTOR1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + JOBNEED_MFACTOR1 + " real;";*/

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                           +" "+   ID_TYPE     +   ","+
            PEOPLE_ID                     +" "+   INT_TYPE     +   ","+
            PEOPLE_CODE                   +" "+   TEXT_TYPE    +   ","+
            PEOPLE_LOGINID                +" "+   TEXT_TYPE    +   ","+
            PEOPLE_PASSWORD               +" "+   TEXT_TYPE    +   ","+
            PEOPLE_FULLNAME               +" "+   TEXT_TYPE    +   ","+
            PEOPLE_GENDER                 +" "+   TEXT_TYPE    +   ","+
            PEOPLE_MOBILENO               +" "+   TEXT_TYPE    +   ","+
            PEOPLE_EMAIL                  +" "+   TEXT_TYPE   +   ","+
            PEOPLE_DEPARTMENT             +" "+   INT_TYPE    +   ","+
            PEOPLE_DESGINATION            +" "+   INT_TYPE   +   ","+
            PEOPLE_TYPE                   +" "+   INT_TYPE    +   ","+
            PEOPLE_SALT                   +" "+   TEXT_TYPE   +   ","+
            PEOPLE_ENABLE                 +" "+   TEXT_TYPE    + ","+
            PEOPLE_DOB                    +" "+   TEXT_TYPE    + ","+
            PEOPLE_DOJ                    +" "+   TEXT_TYPE    + ","+
            PEOPLE_DOR                    +" "+   TEXT_TYPE    + ","+
            PEOPLE_REPORTTO               +" "+   INT_TYPE    +   ","+
            PEOPLE_CUSER                  +" "+   INT_TYPE    + ","+
            PEOPLE_CDTZ                   +" "+   TEXT_TYPE    + ","+
            PEOPLE_MUSER                  +" "+   INT_TYPE    + ","+
            PEOPLE_BUID                   +" "+   INT_TYPE    + ","+
            PEOPLE_MDTZ                   +" "+   TEXT_TYPE    +   ","+
            PEOPLE_CAPTURE_M_LOG          +" "+   INT_TYPE    + ","+
            PEOPLE_M_LOG_SEND_TO          +" "+   TEXT_TYPE    +   ","+
            PEOPLE_LOCATIONTRACKING       +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("People Table Created");
    }

    public static void onUpgrade(SQLiteDatabase db, int oldv, int newv)
    {
        /*System.out.println("People Table updated"+ oldv+" : "+newv);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        OnCreate(db);*/

        /*if(newv>oldv)
            db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR1);*/
    }

}
