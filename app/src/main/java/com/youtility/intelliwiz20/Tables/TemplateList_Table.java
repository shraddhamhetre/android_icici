package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * login user assigned Templates master table
 */

//questionsetid, qsetname, sites

public class TemplateList_Table implements BaseColumns {

    public static final String TABLE_NAME= "Templates";

    public static final String TEMPLATE_QSETID="questionsetid";
    public static final String TEMPLATE_QSETNAME="qsetname";
    public static final String TEMPLATE_SITES="sites";

    public static final String TEMPLATE_CUSER="cuser";
    public static final String TEMPLATE_MUSER="muser";
    public static final String TEMPLATE_CDTZ="cdtz";
    public static final String TEMPLATE_MDTZ="mdtz";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                             +" "+   ID_TYPE     +   ","+
            TEMPLATE_QSETID                 +" "+   INT_TYPE    +   ","+
            TEMPLATE_CUSER                  +" "+   INT_TYPE    +   ","+
            TEMPLATE_MUSER                  +" "+   INT_TYPE    +   ","+
            TEMPLATE_CDTZ                   +" "+   TEXT_TYPE    +   ","+
            TEMPLATE_MDTZ                   +" "+   TEXT_TYPE    +   ","+
            TEMPLATE_QSETNAME               +" "+   TEXT_TYPE    +   ","+
            TEMPLATE_SITES                  +" "+   TEXT_TYPE   +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
            System.out.println("Template Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {

    }

}
