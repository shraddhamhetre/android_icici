package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * question and question set mapping master table
 *
 */
//"$qsbid$ismandatory$seqno$cdtz$mdtz$isdeleted$cuser$muser$qsetcode$questioncode"

public class QuestionSetBelonging_Table implements BaseColumns {

    public static final String TABLE_NAME= "QsetBelonging";

    public static final String QUESTIONSETBELONGING_ID="qsbid";
    public static final String QUESTIONSETBELONGING_ISMANDATORY="ismandatory";
    public static final String QUESTIONSETBELONGING_SEQNO="seqno";
    public static final String QUESTIONSETBELONGING_QUESTIONSETID="questionsetid";
    public static final String QUESTIONSETBELONGING_QUESTIONID="questionid";
    public static final String QUESTIONSETBELONGING_CDTZ="cdtz";
    public static final String QUESTIONSETBELONGING_MDTZ="mdtz";
    //public static final String QUESTIONSETBELONGING_ISDELETED="isdeleted";
    public static final String QUESTIONSETBELONGING_CUSER="cuser";
    public static final String QUESTIONSETBELONGING_MUSER="muser";
    public static final String QUESTIONSETBELONGING_MIN="min";
    public static final String QUESTIONSETBELONGING_MAX="max";
    public static final String QUESTIONSETBELONGING_ALERTON="alerton";
    public static final String QUESTIONSETBELONGING_OPTION="option";
    public static final String QUESTIONSETBELONGING_BUID="buid";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String REAL_TYPE="REAL";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                                     +" "+   ID_TYPE     +   ","+
            QUESTIONSETBELONGING_ID                 +" "+   INT_TYPE    +   ","+
            QUESTIONSETBELONGING_ISMANDATORY        +" "+   TEXT_TYPE    +   ","+
            QUESTIONSETBELONGING_SEQNO              +" "+   INT_TYPE    +   ","+
            QUESTIONSETBELONGING_CDTZ               +" "+   TEXT_TYPE    +   ","+
            QUESTIONSETBELONGING_MDTZ               +" "+   TEXT_TYPE    +   ","+
            QUESTIONSETBELONGING_CUSER              +" "+   INT_TYPE   +   ","+
            QUESTIONSETBELONGING_QUESTIONSETID      +" "+   INT_TYPE   +   ","+
            QUESTIONSETBELONGING_QUESTIONID         +" "+   INT_TYPE   +   ","+
            QUESTIONSETBELONGING_BUID               +" "+   INT_TYPE   +   ","+
            QUESTIONSETBELONGING_MIN                +" "+   REAL_TYPE    +   ","+
            QUESTIONSETBELONGING_MAX                +" "+   REAL_TYPE    +   ","+
            QUESTIONSETBELONGING_ALERTON            +" "+   TEXT_TYPE   +   ","+
            QUESTIONSETBELONGING_OPTION             +" "+   TEXT_TYPE   +   ","+
            QUESTIONSETBELONGING_MUSER              +" "+   INT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        //db.execSQL("CREATE INDEX idx2 ON "+ TABLE_NAME+"(qsbid, seqno,questionsetid,questionid,alerton,option)");
        System.out.println("QsetBelonging Table Created");
    }

}
