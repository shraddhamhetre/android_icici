package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * JOB Need transaction table
 */

//jobneedid,jobdesc,frequency,plandatetime,expirydatetime,gracetime,jobtype,jobstatus,scantype,receivedonserver,priority,
// starttime,endtime,gpslocation,remarks,cuser,cdtz,muser,mdtz,isdeleted,assetcode,aatog,aatop,jobcode,performedby,qsetcode,jobidentifier, jnpid


public class JOBNeed_Table implements BaseColumns {

    public static final String TABLE_NAME= "JOBNEED";

    public static final String JOBNEED_ID="jobneedid";
    public static final String JOBNEED_DESC="jobdesc";
    public static final String JOBNEED_PLANDATETIME="PlanDateTime";
    public static final String JOBNEED_EXPIRYDATETIME="ExpiryDatetime";
    public static final String JOBNEED_GRACETIME="GraceTime";
    public static final String JOBNEED_RECEIVEDONSERVER="ReceivedonServer";
    public static final String JOBNEED_JOBSTARTTIME="JobStartTime";
    public static final String JOBNEED_JOBENDTIME="JobEndTime";
    public static final String JOBNEED_GPSLOCATION="gpslocation";
    public static final String JOBNEED_REMARK="remarks";
    public static final String JOBNEED_AATOP="aatop";
    public static final String JOBNEED_ASSETID="assetid";
    public static final String JOBNEED_FREQUENCY="frequency";
    public static final String JOBNEED_JOBID="Jobid";
    public static final String JOBNEED_JOBSTATUS="JobStatus";
    public static final String JOBNEED_JOBTYPE="JobType";
    public static final String JOBNEED_PERFORMEDBY="PerformedBy";
    public static final String JOBNEED_PRIORITY="priority";
    public static final String JOBNEED_QSETID="questionsetid";
    public static final String JOBNEED_SCANTYPE="ScanType";
    public static final String JOBNEED_PEOPLEID="peopleid";
    public static final String JOBNEED_GROUPID="groupid";
    public static final String JOBNEED_IDENTIFIER="identifier";
    public static final String JOBNEED_PARENT="parent";
    //public static final String JOBNEED_ISDELETED="isDeleted";
    public static final String JOBNEED_CUSER="cuser";
    public static final String JOBNEED_MUSER="muser";
    public static final String JOBNEED_CDTZ="cdtz";
    public static final String JOBNEED_MDTZ="mdtz";
    public static final String JOBNEED_TICKETNO="ticketno";
    public static final String JOBNEED_BUID="buid";
    public static final String JOBNEED_SEQNO="seqno";
    public static final String JOBNEED_TICKETCATEGORY="ticketcategory";
    public static final String JOBNEED_CDTZOFFSET="ctzoffset";
    public static final String JOBNEED_OTHERSITE="othersite";
    public static final String JOBNEED_MFACTOR="multiplicationfactor";

    public static final String JOBNEED_MFACTOR1="multiplicationfactor1";
    public static final String JOBNEED_MFACTOR2="multiplicationfactor2";

    public static final String JOBNEED_ATTACHMENTCOUNT="attachmentcount";
    public static final String JOBNEED_SYNC_STATUS="syncStatus";

    public static final String JOBNEED_DEVIATION="deviation";




    private static final String DATABASE_ALTER_JOBNEED_MFACTOR1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + JOBNEED_MFACTOR1 + " real;";

    private static final String DATABASE_ALTER_JOBNEED_MFACTOR2 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + JOBNEED_MFACTOR2 + " real;";


    public static final String TEXT_TYPE="text";
    public static final String INT_TYPE="Integer";
    public static final String REAL_TYPE="real";
    private static final String BOOLEAN_TYPE="boolean";

    public static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                             +" "+   ID_TYPE      +   ","+
            JOBNEED_ID                      +" "+   INT_TYPE    +   ","+
            JOBNEED_ASSETID                 +" "+   INT_TYPE    +   ","+
            JOBNEED_DESC                    +" "+   TEXT_TYPE    +   ","+
            JOBNEED_FREQUENCY               +" "+   INT_TYPE    +   ","+
            JOBNEED_PLANDATETIME            +" "+   TEXT_TYPE    +   ","+
            JOBNEED_EXPIRYDATETIME          +" "+   TEXT_TYPE    +   ","+
            JOBNEED_GRACETIME               +" "+   INT_TYPE    +   ","+
            JOBNEED_JOBID                   +" "+   INT_TYPE    +   ","+
            JOBNEED_QSETID                  +" "+   INT_TYPE    +   ","+
            JOBNEED_AATOP                   +" "+   INT_TYPE    +   ","+
            JOBNEED_JOBSTATUS               +" "+   INT_TYPE    +   ","+
            JOBNEED_JOBTYPE                 +" "+   INT_TYPE    +   ","+
            JOBNEED_SCANTYPE                +" "+   INT_TYPE    +   ","+
            JOBNEED_RECEIVEDONSERVER        +" "+   TEXT_TYPE    +   ","+
            JOBNEED_PRIORITY                +" "+   INT_TYPE    +   ","+
            JOBNEED_JOBSTARTTIME            +" "+   TEXT_TYPE    +   ","+
            JOBNEED_JOBENDTIME              +" "+   TEXT_TYPE    +   ","+
            JOBNEED_PERFORMEDBY             +" "+   INT_TYPE    +   ","+
            JOBNEED_GPSLOCATION             +" "+   TEXT_TYPE    +   ","+
            JOBNEED_REMARK                  +" "+   TEXT_TYPE    +   ","+
            JOBNEED_CUSER                   +" "+   INT_TYPE    +   ","+
            JOBNEED_MUSER                   +" "+   INT_TYPE    +   ","+
            JOBNEED_CDTZ                    +" "+   TEXT_TYPE    +   ","+
            JOBNEED_MDTZ                    +" "+   TEXT_TYPE    +   ","+
            JOBNEED_ATTACHMENTCOUNT         +" "+   TEXT_TYPE    +   ","+
            JOBNEED_PEOPLEID                +" "+   INT_TYPE    +   ","+
            JOBNEED_GROUPID                 +" "+   INT_TYPE    +   ","+
            JOBNEED_SYNC_STATUS             +" "+   TEXT_TYPE    +   ","+
            JOBNEED_IDENTIFIER              +" "+   INT_TYPE    +   ","+
            JOBNEED_PARENT                  +" "+   INT_TYPE    +   ","+
            JOBNEED_TICKETNO                +" "+   INT_TYPE    +   ","+
            JOBNEED_BUID                    +" "+   INT_TYPE    +   ","+
            JOBNEED_SEQNO                   +" "+   INT_TYPE    +   ","+
            JOBNEED_CDTZOFFSET              +" "+   INT_TYPE    +   ","+
            JOBNEED_OTHERSITE               +" "+   TEXT_TYPE    +   ","+
            JOBNEED_MFACTOR                 +" "+   REAL_TYPE   +   ","+
            JOBNEED_MFACTOR1                +" "+   REAL_TYPE   +   ","+
            JOBNEED_MFACTOR2                +" "+   REAL_TYPE   +   ","+
            JOBNEED_TICKETCATEGORY          +" "+   INT_TYPE    +   ","+
            JOBNEED_DEVIATION               +" "+   BOOLEAN_TYPE    +

            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("JOB NEED Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {
        /*db.execSQL("DROP TABLE IF EXISTS JobNeed");
        OnCreate(db);*/
        System.out.println("Db Version: "+oldv+" : "+newv);

        switch (oldv)
        {
            case 7:
                System.out.println("JOB NEED Table Altered");
                System.out.println("factor1 added");
                db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR1);
                break;
            case 8:
                System.out.println("JOB NEED Table Altered");
                System.out.println("factor2 added");
                db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR2);
                break;
        }

        /*if(oldv<7) {
            System.out.println("JOB NEED Table Altered");
            System.out.println("factor1 added");
            db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR1);
        }*/

        /*if(oldv<8) {
            System.out.println("JOB NEED Table Altered");
            System.out.println("factor2 added");
            db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR2);
        }*/
    }

}
