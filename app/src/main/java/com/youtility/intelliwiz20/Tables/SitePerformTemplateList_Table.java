package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * login user assigned sites master table
 */

public class SitePerformTemplateList_Table implements BaseColumns {

    public static final String TABLE_NAME= "SitePerformTemplateList";

    public static final String TEMPLATE_ID="templateId";
    public static final String TEMPLATE_PEOPLE="templatePeople";
    public static final String TEMPLATE_SITEID="templateSiteid";

    public static final String TEMPLATE_SITENAME="templatesitename";
    public static final String TEMPLATE_CHECKIN_TIMESTAMP="templatecheckintimestamp";

    public static final String TEMPLATE_QSETID="templateqsetid";
    public static final String TEMPLATE_QSETNAME="templateqsetname";


    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                             +" "+   ID_TYPE     +   ","+
            TEMPLATE_ID                     +" "+   INT_TYPE    +   ","+
            TEMPLATE_PEOPLE                 +" "+   INT_TYPE    +   ","+
            TEMPLATE_SITEID                 +" "+   INT_TYPE    +   ","+
            TEMPLATE_SITENAME               +" "+   TEXT_TYPE    +   ","+
            TEMPLATE_CHECKIN_TIMESTAMP      +" "+   INT_TYPE    +   ","+
            TEMPLATE_QSETID                 +" "+   INT_TYPE    +   ","+
            TEMPLATE_QSETNAME               +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("SitePerformTemplateList Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {

    }

}
