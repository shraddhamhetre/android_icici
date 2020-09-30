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

public class CaptchaConfig_Table implements BaseColumns {

    public static final String TABLE_NAME= "CaptchaConfig";

    public static final String CAPTCHA_CONFIG_ID="id";
    public static final String CAPTCHA_CONFIG_ENABLE="enablesleepingguard";
    public static final String CAPTCHA_CONFIG_STARTTIME="starttime";
    public static final String CAPTCHA_CONFIG_ENDTIME="endtime";
    public static final String CAPTCHA_CONFIG_FREQUENCY="captchafreq";


    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String BOOLEAN_TYPE="boolean";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            CAPTCHA_CONFIG_ID                       +" "+   ID_TYPE         +   ","+
            CAPTCHA_CONFIG_ENABLE                   +" "+   BOOLEAN_TYPE    +   ","+
            CAPTCHA_CONFIG_STARTTIME                +" "+   TEXT_TYPE       +   ","+
            CAPTCHA_CONFIG_ENDTIME                  +" "+   TEXT_TYPE       +   ","+
            CAPTCHA_CONFIG_FREQUENCY                +" "+   INT_TYPE        +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("CaptchaConfig_Table Created");
    }

}
