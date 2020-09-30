package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * Attachment table
 */

public class Attachment_Table implements BaseColumns {

    public static final String TABLE_NAME= "Attachment";

    public static final String ATTACHMENT_ID="attachmentid";
    public static final String ATTACHMENT_FILEPATH="FilePath";
    public static final String ATTACHMENT_FILENAME="FileName";
    public static final String ATTACHMENT_NARRATION="Narration";
    public static final String ATTACHMENT_GPSLOCATION="gpslocation";
    public static final String ATTACHMENT_DATETIME="datetime";
    public static final String ATTACHMENT_TYPE="AttachmentType";
    public static final String ATTACHMENT_CUSER="cuser";
    public static final String ATTACHMENT_MUSER="muser";
    public static final String ATTACHMENT_CDTZ="cdtz";
    public static final String ATTACHMENT_MDTZ="mdtz";
    //public static final String ATTACHMENT_ISDELETED ="isdeleted";
    public static final String ATTACHMENT_OWNERNAME ="ownername";
    public static final String ATTACHMENT_OWNERID ="ownerid";
    public static final String ATTACHMENT_SERVERPATH ="serverpath";
    public static final String ATTACHMENT_BUID="buid";


    /*public static final String ATTACHMENT_PEOPLECODE="peoplecode";
    public static final String ATTACHMENT_PAID="paid";
    public static final String ATTACHMENT_JOBNEEDID="jobneedid";
    public static final String ATTACHMENT_JOBNEEDDESCID="jndid";
    public static final String ATTACHMENT_PELOGID="pelogid";
    public static final String ATTACHMENT_ACCESTCODE="acode";
    public static final String ATTACHMENT_QUESTSETCODE="qscode";*/

    public static final String ATTACHMENT_SYNC_STATUS="syncStatus";
    public static final String ATTACHMENT_CATEGORY="attachmentCategory";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                         +" "+   ID_TYPE     +   ","+
            ATTACHMENT_ID               +" "+   INT_TYPE    +   ","+
            ATTACHMENT_OWNERNAME        +" "+   TEXT_TYPE    +   ","+
            ATTACHMENT_OWNERID          +" "+   INT_TYPE    +   ","+
            ATTACHMENT_TYPE             +" "+   INT_TYPE    +   ","+
            ATTACHMENT_FILEPATH         +" "+   TEXT_TYPE   +   ","+
            ATTACHMENT_FILENAME         +" "+   TEXT_TYPE    +   ","+
            ATTACHMENT_NARRATION        +" "+   TEXT_TYPE   +   ","+
            ATTACHMENT_GPSLOCATION      +" "+   TEXT_TYPE    +   ","+
            ATTACHMENT_DATETIME         +" "+   TEXT_TYPE    +   ","+
            ATTACHMENT_CUSER            +" "+   INT_TYPE   +   ","+
            ATTACHMENT_MUSER            +" "+   INT_TYPE    +   ","+
            ATTACHMENT_CDTZ             +" "+   TEXT_TYPE    +   ","+
            ATTACHMENT_SYNC_STATUS      +" "+   TEXT_TYPE    +   ","+
            ATTACHMENT_SERVERPATH       +" "+   TEXT_TYPE    +   ","+
            ATTACHMENT_BUID              +" "+   INT_TYPE    +   ","+
            ATTACHMENT_CATEGORY         +" "+   INT_TYPE    +   ","+
            ATTACHMENT_MDTZ             +" "+   TEXT_TYPE   +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("ATTACHMENT Table Created");
    }

}
