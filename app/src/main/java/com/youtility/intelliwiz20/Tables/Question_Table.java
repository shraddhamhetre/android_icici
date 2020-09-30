package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * Question master table
 */

//"$ questioncode $ questionname $ option $ min $ max $ alerton $ cdtz $ mdtz $ isdeleted $ cuser $ muser $ type $ unit" 13
public class Question_Table implements BaseColumns {

    public static final String TABLE_NAME= "Question";

    public static final String QUESTION_ID="questionid";
    public static final String QUESTION_NAME="questionname";
    public static final String QUESTION_TYPE="type";
    public static final String QUESTION_OPTIONS="options";
    public static final String QUESTION_UNIT="unit";
    public static final String QUESTION_MIN="min";
    public static final String QUESTION_MAX="max";
    public static final String QUESTION_ALERTON="alertOn";
    public static final String QUESTION_CUSER="cuser";
    public static final String QUESTION_CDTZ="cdtz";
    public static final String QUESTION_MUSER="muser";
    public static final String QUESTION_MDTZ="mdtz";
    //public static final String QUESTION_ISDELETED="isDeleted";
    public static final String QUESTION_BUID="buid";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="integer";
    private static final String REAL_TYPE="REAL";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                     +" "+   ID_TYPE     +   ","+
            QUESTION_ID           +" "+   INT_TYPE    +   ","+
            QUESTION_NAME           +" "+   TEXT_TYPE    +   ","+
            QUESTION_TYPE           +" "+   INT_TYPE    +   ","+
            QUESTION_OPTIONS        +" "+   TEXT_TYPE    +   ","+
            QUESTION_UNIT           +" "+   INT_TYPE    +   ","+
            QUESTION_MIN            +" "+   REAL_TYPE    +   ","+
            QUESTION_MAX            +" "+   REAL_TYPE   +   ","+
            QUESTION_ALERTON        +" "+   TEXT_TYPE    +   ","+
            QUESTION_CUSER          +" "+   INT_TYPE   +   ","+
            QUESTION_CDTZ           +" "+   TEXT_TYPE    +   ","+
            QUESTION_MUSER          +" "+   INT_TYPE   +   ","+
            QUESTION_BUID          +" "+   INT_TYPE   +   ","+
            QUESTION_MDTZ           +" "+   TEXT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        //db.execSQL("CREATE INDEX idx1 ON "+ TABLE_NAME+"(questionid)");
        System.out.println("Question Table Created");
    }

}
