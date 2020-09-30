package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 * JOB need details transaction table
 *
 */
//jndid,seqno,questionname,type,answer,option,min,max,alerton,
// ismandatory,cdtz,mdtz,isdeleted,cuser,jobneedid,muser

public class JOBNeedDetails_Table implements BaseColumns {

    public static final String TABLE_NAME= "JOBNeedDetails";

    public static final String JOBNEEDDETAILS_ID="jndid";
    public static final String JOBNEEDDETAILS_JOBNEEDID="jobneedid";
    public static final String JOBNEEDDETAILS_SEQNO="seqno";
    public static final String JOBNEEDDETAILS_QUESTIONID="questionid";
    public static final String JOBNEEDDETAILS_ANSWER="Answer";
    public static final String JOBNEEDDETAILS_MIN="Min";
    public static final String JOBNEEDDETAILS_MAX="Max";
    public static final String JOBNEEDDETAILS_OPTION="option";
    public static final String JOBNEEDDETAILS_ALERTON="alerton";
    public static final String JOBNEEDDETAILS_TYPE="type";
    public static final String JOBNEEDDETAILS_CDTZ="cdtz";
    public static final String JOBNEEDDETAILS_MDTZ="mdtz";
    public static final String JOBNEEDDETAILS_CUSER="cuser";
    public static final String JOBNEEDDETAILS_MUSER="muser";
    public static final String JOBNEEDDETAILS_ISMANATORY="ismandatory";


    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String REAL_TYPE="Real";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                                     +" "+   ID_TYPE     +   ","+
            JOBNEEDDETAILS_ID                       +" "+   INT_TYPE    +   ","+
            JOBNEEDDETAILS_JOBNEEDID                +" "+   INT_TYPE    +   ","+
            JOBNEEDDETAILS_SEQNO                    +" "+   INT_TYPE    +   ","+
            JOBNEEDDETAILS_QUESTIONID               +" "+   INT_TYPE    +   ","+
            JOBNEEDDETAILS_ANSWER                   +" "+   TEXT_TYPE    +   ","+
            JOBNEEDDETAILS_MIN                      +" "+   REAL_TYPE    +   ","+
            JOBNEEDDETAILS_MAX                      +" "+   REAL_TYPE   +   ","+
            JOBNEEDDETAILS_OPTION                   +" "+   TEXT_TYPE    +   ","+
            JOBNEEDDETAILS_TYPE                     +" "+   INT_TYPE    +   ","+
            JOBNEEDDETAILS_CDTZ                     +" "+   TEXT_TYPE    +   ","+
            JOBNEEDDETAILS_MDTZ                     +" "+   TEXT_TYPE    +   ","+
            JOBNEEDDETAILS_CUSER                    +" "+   INT_TYPE    +   ","+
            JOBNEEDDETAILS_MUSER                    +" "+   INT_TYPE    +   ","+
            JOBNEEDDETAILS_ISMANATORY               +" "+   TEXT_TYPE    +   ","+
            JOBNEEDDETAILS_ALERTON                  +" "+   TEXT_TYPE   +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("JOB Need Details Table Created");
    }

}
