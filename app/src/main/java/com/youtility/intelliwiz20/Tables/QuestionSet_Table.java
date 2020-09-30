package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * question set master table
 *
 */

//"$qsetcode$qsetname$enable$seqno$cdtz$mdtz$isdeleted$cuser$muser$parent$type" 11
public class QuestionSet_Table implements BaseColumns {

    public static final String TABLE_NAME= "QuestionSet";

    public static final String QUESTION_SET_ID="questionsetid";
    public static final String QUESTION_SET_ASSETID="assetid";
    public static final String QUESTION_SET_NAME="qsetname";
    public static final String QUESTION_SET_SEQNO="seqno";
    public static final String QUESTION_SET_ENABLE="enable";
    public static final String QUESTION_SET_CUSER="cuser";
    public static final String QUESTION_SET_CDTZ="cdtz";
    public static final String QUESTION_SET_MUSER="muser";
    public static final String QUESTION_SET_MDTZ="mdtz";
    //public static final String QUESTION_SET_ISDELETED="isDeleted";
    public static final String QUESTION_SET_PARENT="parent";
    public static final String QUESTION_SET_TYPE="type";
    public static final String QUESTION_SET_BUID="buid";
    public static final String QUESTION_SET_URL="url";

    public static final String QUESTION_SET_ASSETINCLUDES="assetincludes";
    public static final String QUESTION_SET_BUINCLUDES="buincludes";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    /*private static final String DATABASE_ALTER_JOBNEED_MFACTOR1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + JOBNEED_MFACTOR1 + " real;";*/

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                     +" "+   ID_TYPE     +   ","+
            QUESTION_SET_ID           +" "+   INT_TYPE    +   ","+
            QUESTION_SET_ASSETID           +" "+   INT_TYPE    +   ","+
            QUESTION_SET_NAME           +" "+   TEXT_TYPE    +   ","+
            QUESTION_SET_ENABLE           +" "+   TEXT_TYPE    +   ","+
            QUESTION_SET_CUSER        +" "+   INT_TYPE    +   ","+
            QUESTION_SET_CDTZ           +" "+   TEXT_TYPE    +   ","+
            QUESTION_SET_MUSER            +" "+   INT_TYPE    +   ","+
            QUESTION_SET_MDTZ            +" "+   TEXT_TYPE   +   ","+
            QUESTION_SET_SEQNO            +" "+   INT_TYPE   +   ","+
            QUESTION_SET_PARENT            +" "+   INT_TYPE   +   ","+
            QUESTION_SET_TYPE            +" "+   INT_TYPE   +   ","+
            QUESTION_SET_URL            +" "+   TEXT_TYPE   +   ","+
            QUESTION_SET_BUID            +" "+   INT_TYPE   +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("QuestionSet Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {
        /*db.execSQL("DROP TABLE IF EXISTS QuestionSet");
        OnCreate(db);*/

        /*if(newv>oldv)
            db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR1);*/

    }

}
