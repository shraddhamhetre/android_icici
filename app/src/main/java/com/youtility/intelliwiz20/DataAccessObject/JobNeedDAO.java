package com.youtility.intelliwiz20.DataAccessObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Tables.JOBNeed_Table;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by PrashantD on 5/9/17.
 *
 * job need related data access object
 *
 */

public class JobNeedDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceDetailPref;
    private long peopleID;
    private Cursor c=null;



    public JobNeedDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
        loginDetailPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        deviceDetailPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);


    }

//jobdesc, frequency, plandatetime, expirydatetime, gracetime, jobtype, jobstatus,  scantype, receivedonserver, priority,starttime, endtime, gpslocation,
// remarks, cuser,  cdtz, muser,mdtz, isdeleted, assetcode, aatog, aatop, jobcode, performedby,  qsetcode
    public void insertRecord(JobNeed regRecord, String syncStatus)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(JOBNeed_Table.JOBNEED_SYNC_STATUS, syncStatus);
            values.put(JOBNeed_Table.JOBNEED_ID, regRecord.getJobneedid());
            values.put(JOBNeed_Table.JOBNEED_DESC, regRecord.getJobdesc());
            values.put(JOBNeed_Table.JOBNEED_FREQUENCY, regRecord.getFrequency());
            values.put(JOBNeed_Table.JOBNEED_PLANDATETIME, regRecord.getPlandatetime());
            values.put(JOBNeed_Table.JOBNEED_EXPIRYDATETIME, regRecord.getExpirydatetime());
            values.put(JOBNeed_Table.JOBNEED_GRACETIME, regRecord.getGracetime());
            values.put(JOBNeed_Table.JOBNEED_JOBTYPE, regRecord.getJobtype());
            values.put(JOBNeed_Table.JOBNEED_JOBSTATUS, regRecord.getJobstatus());
            values.put(JOBNeed_Table.JOBNEED_SCANTYPE, regRecord.getScantype());
            values.put(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER, regRecord.getReceivedonserver());
            values.put(JOBNeed_Table.JOBNEED_PRIORITY, regRecord.getPriority());
            values.put(JOBNeed_Table.JOBNEED_JOBSTARTTIME, regRecord.getStarttime());
            values.put(JOBNeed_Table.JOBNEED_JOBENDTIME, regRecord.getEndtime());
            values.put(JOBNeed_Table.JOBNEED_GPSLOCATION, regRecord.getGpslocation());
            values.put(JOBNeed_Table.JOBNEED_REMARK, regRecord.getRemarks());
            values.put(JOBNeed_Table.JOBNEED_CUSER, regRecord.getCuser());
            values.put(JOBNeed_Table.JOBNEED_CDTZ, regRecord.getCdtz());
            values.put(JOBNeed_Table.JOBNEED_MUSER, regRecord.getMuser());
            values.put(JOBNeed_Table.JOBNEED_MDTZ, regRecord.getMdtz());
            //values.put(JOBNeed_Table.JOBNEED_ISDELETED, regRecord.getIsdeleted());
            values.put(JOBNeed_Table.JOBNEED_ASSETID, regRecord.getAssetid());
            values.put(JOBNeed_Table.JOBNEED_GROUPID, regRecord.getGroupid());
            values.put(JOBNeed_Table.JOBNEED_PEOPLEID, regRecord.getPeopleid());
            values.put(JOBNeed_Table.JOBNEED_AATOP, regRecord.getAatop());
            values.put(JOBNeed_Table.JOBNEED_JOBID, regRecord.getJobid());
            values.put(JOBNeed_Table.JOBNEED_PERFORMEDBY, regRecord.getPerformedby());
            values.put(JOBNeed_Table.JOBNEED_QSETID, regRecord.getQuestionsetid());
            values.put(JOBNeed_Table.JOBNEED_IDENTIFIER, regRecord.getIdentifier());
            values.put(JOBNeed_Table.JOBNEED_PARENT, regRecord.getParent());
            values.put(JOBNeed_Table.JOBNEED_SEQNO, regRecord.getSeqno());
            values.put(JOBNeed_Table.JOBNEED_TICKETCATEGORY, regRecord.getTicketcategory());
            values.put(JOBNeed_Table.JOBNEED_BUID, regRecord.getBuid());
            values.put(JOBNeed_Table.JOBNEED_CDTZOFFSET, regRecord.getCtzoffset());
            values.put(JOBNeed_Table.JOBNEED_OTHERSITE, regRecord.getOthersite());
            long val= db.insert(JOBNeed_Table.TABLE_NAME, "", values);
            System.out.println("Common Data val: "+val);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void insertOrUpdateRecord(JobNeed regRecord, String syncStatus)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(JOBNeed_Table.JOBNEED_SYNC_STATUS, syncStatus);
            values.put(JOBNeed_Table.JOBNEED_ID, regRecord.getJobneedid());
            values.put(JOBNeed_Table.JOBNEED_DESC, regRecord.getJobdesc());
            values.put(JOBNeed_Table.JOBNEED_FREQUENCY, regRecord.getFrequency());
            values.put(JOBNeed_Table.JOBNEED_PLANDATETIME, regRecord.getPlandatetime());
            values.put(JOBNeed_Table.JOBNEED_EXPIRYDATETIME, regRecord.getExpirydatetime());
            values.put(JOBNeed_Table.JOBNEED_GRACETIME, regRecord.getGracetime());
            values.put(JOBNeed_Table.JOBNEED_JOBTYPE, regRecord.getJobtype());
            values.put(JOBNeed_Table.JOBNEED_JOBSTATUS, regRecord.getJobstatus());
            values.put(JOBNeed_Table.JOBNEED_SCANTYPE, regRecord.getScantype());
            values.put(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER, regRecord.getReceivedonserver());
            values.put(JOBNeed_Table.JOBNEED_PRIORITY, regRecord.getPriority());
            values.put(JOBNeed_Table.JOBNEED_JOBSTARTTIME, regRecord.getStarttime());
            values.put(JOBNeed_Table.JOBNEED_JOBENDTIME, regRecord.getEndtime());
            values.put(JOBNeed_Table.JOBNEED_GPSLOCATION, regRecord.getGpslocation());
            values.put(JOBNeed_Table.JOBNEED_REMARK, regRecord.getRemarks());
            values.put(JOBNeed_Table.JOBNEED_CUSER, regRecord.getCuser());
            values.put(JOBNeed_Table.JOBNEED_CDTZ, regRecord.getCdtz());
            values.put(JOBNeed_Table.JOBNEED_MUSER, regRecord.getMuser());
            values.put(JOBNeed_Table.JOBNEED_MDTZ, regRecord.getMdtz());
            //values.put(JOBNeed_Table.JOBNEED_ISDELETED, regRecord.getIsdeleted());
            values.put(JOBNeed_Table.JOBNEED_ASSETID, regRecord.getAssetid());
            values.put(JOBNeed_Table.JOBNEED_GROUPID, regRecord.getGroupid());
            values.put(JOBNeed_Table.JOBNEED_PEOPLEID, regRecord.getPeopleid());
            values.put(JOBNeed_Table.JOBNEED_AATOP, regRecord.getAatop());
            values.put(JOBNeed_Table.JOBNEED_JOBID, regRecord.getJobid());
            values.put(JOBNeed_Table.JOBNEED_PERFORMEDBY, regRecord.getPerformedby());
            values.put(JOBNeed_Table.JOBNEED_QSETID, regRecord.getQuestionsetid());
            values.put(JOBNeed_Table.JOBNEED_IDENTIFIER, regRecord.getIdentifier());
            values.put(JOBNeed_Table.JOBNEED_PARENT, regRecord.getParent());
            values.put(JOBNeed_Table.JOBNEED_SEQNO, regRecord.getSeqno());
            values.put(JOBNeed_Table.JOBNEED_TICKETCATEGORY, regRecord.getTicketcategory());
            values.put(JOBNeed_Table.JOBNEED_BUID, regRecord.getBuid());
            values.put(JOBNeed_Table.JOBNEED_OTHERSITE, regRecord.getOthersite());

            String where="jobneedid = ? AND parent = ? AND questionsetid = ?";
            String[] args=new String[]{String.valueOf(regRecord.getJobneedid()), String.valueOf(regRecord.getParent()), String.valueOf(regRecord.getQuestionsetid())};
            db.delete(JOBNeed_Table.TABLE_NAME,where,args);

            long val= db.insert(JOBNeed_Table.TABLE_NAME, "", values);

            /*if(isJobNeedParentPresent(regRecord.getJobneedid(), regRecord.getParent(), regRecord.getQuestionsetid()))
            {
                String where="jobneedid = ? AND parent = ? AND questionsetid = ?";
                String[] args=new String[]{String.valueOf(regRecord.getJobneedid()), String.valueOf(regRecord.getParent()), String.valueOf(regRecord.getQuestionsetid())};
                long val=db.update(JOBNeed_Table.TABLE_NAME, values, where, args);
            }
            else
            {
                long val= db.insert(JOBNeed_Table.TABLE_NAME, "", values);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void insertOrUpdateParentRecord(JobNeed regRecord, String syncStatus)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(JOBNeed_Table.JOBNEED_SYNC_STATUS, syncStatus);
            values.put(JOBNeed_Table.JOBNEED_ID, regRecord.getJobneedid());
            values.put(JOBNeed_Table.JOBNEED_DESC, regRecord.getJobdesc());
            values.put(JOBNeed_Table.JOBNEED_FREQUENCY, regRecord.getFrequency());
            values.put(JOBNeed_Table.JOBNEED_PLANDATETIME, regRecord.getPlandatetime());
            values.put(JOBNeed_Table.JOBNEED_EXPIRYDATETIME, regRecord.getExpirydatetime());
            values.put(JOBNeed_Table.JOBNEED_GRACETIME, regRecord.getGracetime());
            values.put(JOBNeed_Table.JOBNEED_JOBTYPE, regRecord.getJobtype());
            values.put(JOBNeed_Table.JOBNEED_JOBSTATUS, regRecord.getJobstatus());
            values.put(JOBNeed_Table.JOBNEED_SCANTYPE, regRecord.getScantype());
            values.put(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER, regRecord.getReceivedonserver());
            values.put(JOBNeed_Table.JOBNEED_PRIORITY, regRecord.getPriority());
            values.put(JOBNeed_Table.JOBNEED_JOBSTARTTIME, regRecord.getStarttime());
            values.put(JOBNeed_Table.JOBNEED_JOBENDTIME, regRecord.getEndtime());
            values.put(JOBNeed_Table.JOBNEED_GPSLOCATION, regRecord.getGpslocation());
            values.put(JOBNeed_Table.JOBNEED_REMARK, regRecord.getRemarks());
            values.put(JOBNeed_Table.JOBNEED_CUSER, regRecord.getCuser());
            values.put(JOBNeed_Table.JOBNEED_CDTZ, regRecord.getCdtz());
            values.put(JOBNeed_Table.JOBNEED_MUSER, regRecord.getMuser());
            values.put(JOBNeed_Table.JOBNEED_MDTZ, regRecord.getMdtz());
            //values.put(JOBNeed_Table.JOBNEED_ISDELETED, regRecord.getIsdeleted());
            values.put(JOBNeed_Table.JOBNEED_ASSETID, regRecord.getAssetid());
            values.put(JOBNeed_Table.JOBNEED_GROUPID, regRecord.getGroupid());
            values.put(JOBNeed_Table.JOBNEED_PEOPLEID, regRecord.getPeopleid());
            values.put(JOBNeed_Table.JOBNEED_AATOP, regRecord.getAatop());
            values.put(JOBNeed_Table.JOBNEED_JOBID, regRecord.getJobid());
            values.put(JOBNeed_Table.JOBNEED_PERFORMEDBY, regRecord.getPerformedby());
            values.put(JOBNeed_Table.JOBNEED_QSETID, regRecord.getQuestionsetid());
            values.put(JOBNeed_Table.JOBNEED_IDENTIFIER, regRecord.getIdentifier());
            values.put(JOBNeed_Table.JOBNEED_PARENT, regRecord.getParent());
            values.put(JOBNeed_Table.JOBNEED_SEQNO, regRecord.getSeqno());
            values.put(JOBNeed_Table.JOBNEED_TICKETCATEGORY, regRecord.getTicketcategory());
            values.put(JOBNeed_Table.JOBNEED_BUID, regRecord.getBuid());
            values.put(JOBNeed_Table.JOBNEED_OTHERSITE, regRecord.getOthersite());

            String where="jobneedid = ?";
            String[] args=new String[]{String.valueOf(regRecord.getJobneedid())};
            db.delete(JOBNeed_Table.TABLE_NAME,where,args);

            long val= db.insert(JOBNeed_Table.TABLE_NAME, "", values);

            /*if(isJobNeedPresent(regRecord.getJobneedid()))
            {
                String where="jobneedid = ?";
                String[] args=new String[]{String.valueOf(regRecord.getJobneedid())};
                long val=db.update(JOBNeed_Table.TABLE_NAME, values, where, args);
            }
            else
            {
                long val= db.insert(JOBNeed_Table.TABLE_NAME, "", values);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void  test_insertOrUpdateParentRecord(JobNeed regRecord, String syncStatus)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(JOBNeed_Table.JOBNEED_SYNC_STATUS, syncStatus);
            values.put(JOBNeed_Table.JOBNEED_ID, regRecord.getJobneedid());
            values.put(JOBNeed_Table.JOBNEED_DESC, regRecord.getJobdesc());
            values.put(JOBNeed_Table.JOBNEED_FREQUENCY, regRecord.getFrequency());
            values.put(JOBNeed_Table.JOBNEED_PLANDATETIME, regRecord.getPlandatetime());
            values.put(JOBNeed_Table.JOBNEED_EXPIRYDATETIME, regRecord.getExpirydatetime());
            values.put(JOBNeed_Table.JOBNEED_GRACETIME, regRecord.getGracetime());
            values.put(JOBNeed_Table.JOBNEED_JOBTYPE, regRecord.getJobtype());
            values.put(JOBNeed_Table.JOBNEED_JOBSTATUS, regRecord.getJobstatus());
            values.put(JOBNeed_Table.JOBNEED_SCANTYPE, regRecord.getScantype());
            values.put(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER, regRecord.getReceivedonserver());
            values.put(JOBNeed_Table.JOBNEED_PRIORITY, regRecord.getPriority());
            values.put(JOBNeed_Table.JOBNEED_JOBSTARTTIME, regRecord.getStarttime());
            values.put(JOBNeed_Table.JOBNEED_JOBENDTIME, regRecord.getEndtime());
            values.put(JOBNeed_Table.JOBNEED_GPSLOCATION, regRecord.getGpslocation());
            values.put(JOBNeed_Table.JOBNEED_REMARK, regRecord.getRemarks());
            values.put(JOBNeed_Table.JOBNEED_CUSER, regRecord.getCuser());
            values.put(JOBNeed_Table.JOBNEED_CDTZ, regRecord.getCdtz());
            values.put(JOBNeed_Table.JOBNEED_MUSER, regRecord.getMuser());
            values.put(JOBNeed_Table.JOBNEED_MDTZ, regRecord.getMdtz());
            //values.put(JOBNeed_Table.JOBNEED_ISDELETED, regRecord.getIsdeleted());
            values.put(JOBNeed_Table.JOBNEED_ASSETID, regRecord.getAssetid());
            values.put(JOBNeed_Table.JOBNEED_GROUPID, regRecord.getGroupid());
            values.put(JOBNeed_Table.JOBNEED_PEOPLEID, regRecord.getPeopleid());
            values.put(JOBNeed_Table.JOBNEED_AATOP, regRecord.getAatop());
            values.put(JOBNeed_Table.JOBNEED_JOBID, regRecord.getJobid());
            values.put(JOBNeed_Table.JOBNEED_PERFORMEDBY, regRecord.getPerformedby());
            values.put(JOBNeed_Table.JOBNEED_QSETID, regRecord.getQuestionsetid());
            values.put(JOBNeed_Table.JOBNEED_IDENTIFIER, regRecord.getIdentifier());
            values.put(JOBNeed_Table.JOBNEED_PARENT, regRecord.getParent());
            values.put(JOBNeed_Table.JOBNEED_SEQNO, regRecord.getSeqno());
            values.put(JOBNeed_Table.JOBNEED_TICKETCATEGORY, regRecord.getTicketcategory());
            values.put(JOBNeed_Table.JOBNEED_BUID, regRecord.getBuid());
            values.put(JOBNeed_Table.JOBNEED_OTHERSITE, regRecord.getOthersite());

            System.out.println("regRecord.getJobneedid(): "+regRecord.getJobneedid());
            System.out.println("regRecord.getQuestionsetid(): "+regRecord.getQuestionsetid());
            System.out.println("regRecord.getParent(): "+regRecord.getParent());
            System.out.println("regRecord.getJobdesc(): "+regRecord.getJobdesc());



            String where ="jobneedid=? AND questionsetid=? AND parent=? ";
            String[] args=new String[]{String.valueOf(regRecord.getJobneedid()), String.valueOf(regRecord.getQuestionsetid()), String.valueOf(regRecord.getParent())};
            int val1=db.delete(JOBNeed_Table.TABLE_NAME, where,args);
            System.out.println("deleteval: "+val1);

            /*String where ="parent=? ";
            String[] args=new String[]{ String.valueOf(regRecord.getParent())};
            db.update(JOBNeed_Table.TABLE_NAME, values, where, args);*/


            long val= db.insert(JOBNeed_Table.TABLE_NAME, "", values);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    private boolean isJobNeedPresent(long jobneedid)
    {
        boolean isPresent=false;
        String str="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_ID+" = "+jobneedid;
        System.out.println("isJobNeedPresent : "+str);
        c=db.rawQuery(str, null);
        if(c!=null)
        {
            if(c.moveToFirst()) {
                c.close();
                System.out.println("GOT Same parent jobneed");
                isPresent=true;

            }
            else
            {
                System.out.println("GOT diff parent jobneed");
                isPresent=false;
            }
        }
        return isPresent;
    }

    private boolean isJobNeedParentPresent(long jobneedid, long parentid, long qsetid)
    {
        boolean isPresent=false;
        String str="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_ID+" = "+jobneedid +" AND "+JOBNeed_Table.JOBNEED_PARENT+" = "+parentid +" AND "+JOBNeed_Table.JOBNEED_QSETID+" = "+qsetid;
        System.out.println("isJobNeedParentPresent : "+str);
        c=db.rawQuery(str, null);
        if(c!=null)
        {
            if(c.moveToFirst()) {
                c.close();
                System.out.println("GOT Same");
                isPresent=true;
            }
            else
            {
                System.out.println("GOT Diff");
                isPresent=false;
            }
        }
        return isPresent;
    }
    public String getJobneedCount1(String jobType, String startDayTime, String endDayTime)
    {
        int sCount=0;
        int cCount=0;
        int pCount=0;
        int aCount=0;
        try {
            db = sqlopenHelper.getReadableDatabase();
            String str="SELECT scheduled, completed,pending,closed " +
                    "FROM ( select  (jobneed.PlanDateTime), " +
                    "COUNT(case when jtype.tacode = 'SCHEDULE' then jtype.tacode end) as scheduled, " +
                    "COUNT(jobneed.jobneedid) as tot_task, " +
                    "COUNT(case when (jstatus.tacode = 'ASSIGNED' OR jstatus.tacode = 'PARTIALLYCOMPLETED') then jstatus.tacode end) as pending, " +
                    "COUNT(case when jstatus.tacode = 'AUTOCLOSED' then jstatus.tacode end) as closed, " +
                    "COUNT(case when jstatus.tacode = 'COMPLETED' then jstatus.tacode end) as completed  " +
                    "FROM jobneed  " +
                    "INNER JOIN typeassist jstatus on jstatus.taid = jobneed.JobStatus  " +
                    "INNER JOIN typeassist jtype on jtype.taid = jobneed.JobType  " +
                    "INNER JOIN typeassist i on jobneed.identifier=i.taid  " +
                    "AND i.tacode='"+jobType+"' " +
                    "AND i.tatype='Job Identifier' " +
                    "WHERE jobneed.jobneedid<> -1  " +
                    "AND jtype.tacode = 'SCHEDULE'  " +
                    "AND jobneed.parent = -1  " +
                    "AND jobneed.PlanDateTime >= '"+startDayTime+"' " +
                    "AND jobneed.PlanDateTime <= '"+endDayTime+"' " +
                    "GROUP BY jobneed.PlanDateTime " +
                    "ORDER BY ( jobneed.PlanDateTime) ASC)tsk;";

            System.out.println("getDashboardCount query: "+str);

            c=db.rawQuery(str,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        /*System.out.println("c.getInt(0): "+c.getInt(0));
                        System.out.println("c.getInt(1): "+c.getInt(1));
                        System.out.println("c.getInt(2): "+c.getInt(2));
                        System.out.println("c.getInt(3): "+c.getInt(3));
                        System.out.println("-------------------------------------------");*/

                        sCount=sCount+c.getInt(0);
                        cCount=cCount+c.getInt(1);
                        pCount=pCount+c.getInt(2);
                        aCount=aCount+c.getInt(3);

                        /*if(c.getInt(0)==1)
                            sCount++;
                        if(c.getInt(1)==1)
                            cCount++;
                        if(c.getInt(2)==1)
                            pCount++;
                        if(c.getInt(3)==1)
                            aCount++;*/
                    }while(c.moveToNext());
                }

                /*System.out.println("Scount: "+sCount);
                System.out.println("cCount: "+cCount);
                System.out.println("pCount: "+pCount);
                System.out.println("aCount: "+aCount);*/
                /*System.out.println("cPercentage: "+((cCount*100.0)/sCount));
                System.out.println("pPercentage: "+((pCount*100.0)/sCount));
                System.out.println("aPercentage: "+((aCount*100.0)/sCount));*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return sCount+"~"+cCount+"~"+pCount+"~"+aCount;
    }

    public String getPPMCount(String jobType)
    {
        int sCount=0;
        int cCount=0;
        int pCount=0;
        int aCount=0;
        try {
            db = sqlopenHelper.getReadableDatabase();
            String str="SELECT scheduled, completed,pending,closed " +
                    "FROM ( select  (jobneed.PlanDateTime), " +
                    "COUNT(case when jtype.tacode = 'SCHEDULE' then jtype.tacode end) as scheduled, " +
                    "COUNT(jobneed.jobneedid) as tot_task, " +
                    "COUNT(case when jstatus.tacode = 'ASSIGNED' then jstatus.tacode end) as pending, " +
                    "COUNT(case when jstatus.tacode = 'AUTOCLOSED' then jstatus.tacode end) as closed, " +
                    "COUNT(case when jstatus.tacode = 'COMPLETED' then jstatus.tacode end) as completed  " +
                    "FROM jobneed  " +
                    "INNER JOIN typeassist jstatus on jstatus.taid = jobneed.JobStatus  " +
                    "INNER JOIN typeassist jtype on jtype.taid = jobneed.JobType  " +
                    "INNER JOIN typeassist i on jobneed.identifier=i.taid  " +
                    "AND i.tacode='"+jobType+"' " +
                    "AND i.tatype='Job Identifier' " +
                    "WHERE jobneed.jobneedid<> -1  " +
                    "AND jtype.tacode = 'SCHEDULE'  " +
                    "AND jobneed.parent = -1  " +
                    "GROUP BY jobneed.PlanDateTime " +
                    "ORDER BY ( jobneed.PlanDateTime) ASC)tsk;";

            System.out.println("getDashboardCount query: "+str);

            c=db.rawQuery(str,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        sCount=sCount+c.getInt(0);
                        cCount=cCount+c.getInt(1);
                        pCount=pCount+c.getInt(2);
                        aCount=aCount+c.getInt(3);

                    }while(c.moveToNext());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return sCount+"~"+cCount+"~"+pCount+"~"+aCount;
    }

    public int getJobListCount(String jobneedIdentifier, String jobneedIdentifier1, String jobType)

    {
        String prevDt=CommonFunctions.getTimezoneDate(date(1));

        int retCountVal=0;
        try {
            db = sqlopenHelper.getReadableDatabase();
            String jobNeedSql="select count(*) from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                    +" AND "+ JOBNeed_Table.JOBNEED_SYNC_STATUS+" in(0,-1)"
                    +" AND "+ JOBNeed_Table.JOBNEED_PLANDATETIME+" >= '"+prevDt+"'"
                    +" order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC";
            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    retCountVal= c.getInt(0);
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return retCountVal;
    }

    public String[] getColumnName()
    {
        String[] colName=null;

        db=sqlopenHelper.getReadableDatabase();
        c = db.rawQuery("select * from "+JOBNeed_Table.TABLE_NAME+" limit 1",null);
        colName=c.getColumnNames();

        return colName;
    }

    public ArrayList<JobNeed> getJobList(String jobneedIdentifier, String jobneedIdentifier1, String jobType)
    {
        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        //System.out.println("User id: "+loginDetailPref.getString(Constants.LOGIN_USER_ID,""));
        //System.out.println("people id: "+peopleID);
        String prevDt=CommonFunctions.getTimezoneDate(date(1));
        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        try {
            db = sqlopenHelper.getReadableDatabase();

            //System.out.println("jobneedIdentifier: "+jobneedIdentifier);
            //System.out.println("jobneedIdentifier1: "+jobneedIdentifier1);
            //System.out.println("jobType: "+jobType);
            /*String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                    +" order by "+ JOBNeed_Table.JOBNEED_PLANDATETIME +" ASC";*/

            String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                    +" AND "+ JOBNeed_Table.JOBNEED_SYNC_STATUS+" in(0,-1)"
                    +" AND "+ JOBNeed_Table.JOBNEED_PLANDATETIME+" >= '"+prevDt+"'"
                    +" order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC";


            System.out.println("JOB Query:11"+jobNeedSql);



            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        System.out.println("ros:---"+ jobNeed.getReceivedonserver());
                        jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        //System.out.println("Qset ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        //System.out.println("job need ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        //System.out.println("Jobneed desc"+c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        //System.out.println("JObStatus: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                        jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        //System.out.println("Jobneed identifier: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));
                        jobNeedArrayList.add(jobNeed);
                    }
                    while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/
            if(c!=null)
            {
                c.close();
                c=null;
            }

        }
        return jobNeedArrayList;
    }

    public ArrayList<JobNeed> getTourList(String jobneedIdentifier, String jobneedIdentifier1, String jobType)
    {
        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        String currDt=CommonFunctions.getTimezoneDate(System.currentTimeMillis());


        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        try {
            db = sqlopenHelper.getReadableDatabase();

            //System.out.println("jobneedIdentifier: "+jobneedIdentifier);
            //System.out.println("jobneedIdentifier1: "+jobneedIdentifier1);
            //System.out.println("jobType: "+jobType);
            /*String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                    +" order by "+ JOBNeed_Table.JOBNEED_PLANDATETIME +" ASC";*/

            String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                    /*+" AND "+ JOBNeed_Table.JOBNEED_EXPIRYDATETIME+">=datetime('now')"*/
                  +" AND "+ JOBNeed_Table.JOBNEED_EXPIRYDATETIME+" >= '"+currDt+"'"

                    +" AND "+ JOBNeed_Table.JOBNEED_PLANDATETIME+">=datetime('now', '-1 day')"
/*
                    +" AND "+ JOBNeed_Table.JOBNEED_SYNC_STATUS+" in(0,-1,2)"
*/

                    +" order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC";


            System.out.println("JOB Query: "+jobNeedSql);



            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        //System.out.println("Qset ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        //System.out.println("job need ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        //System.out.println("Jobneed desc"+c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        //System.out.println("JObStatus: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                        jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        //System.out.println("Jobneed identifier: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));
                        jobNeedArrayList.add(jobNeed);
                    }
                    while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }

        }
        return jobNeedArrayList;
    }

    public int getStatusBaseJobListCount(String jobneedIdentifier, String jobneedIdentifier1, String jobType,String statusType)
    {

        String currDt=CommonFunctions.getTimezoneDate(System.currentTimeMillis());
        String prevDt=CommonFunctions.getTimezoneDate(date(1));


        int retCountVal=0;
        try {
            db = sqlopenHelper.getReadableDatabase();

            if(statusType == Constants.JOBNEED_STATUS_COMPLETED){
                String jobNeedSql="select count(*) from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                        +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                        +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_JOBSTATUS+" in(select taid from TypeAssist where tacode = '"+statusType+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_SYNC_STATUS+" in('-1','0','2')"
                        +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                        +" AND "+ JOBNeed_Table.JOBNEED_PLANDATETIME+" >= '"+prevDt+"'"
                        +" order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC";
                c = db.rawQuery(jobNeedSql ,null);
            }else {
                String jobNeedSql="select count(*) from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                        +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                        +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_JOBSTATUS+" in(select taid from TypeAssist where tacode = '"+statusType+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_SYNC_STATUS+" in('-1','0','2')"
                        +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                        +" AND "+ JOBNeed_Table.JOBNEED_EXPIRYDATETIME+" >= '"+currDt+"'"
                        +" order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC";
                c = db.rawQuery(jobNeedSql ,null);
            }


            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    retCountVal= c.getInt(0);
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return retCountVal;
    }

    public ArrayList<JobNeed> getStatusBaseJobList(String jobneedIdentifier, String jobneedIdentifier1, String jobType, String statusType)
    {
        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        String currDt=CommonFunctions.getTimezoneDate(System.currentTimeMillis());
        String prevDt=CommonFunctions.getTimezoneDate(date(1));


        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        try {
            db = sqlopenHelper.getReadableDatabase();

            //System.out.println("jobneedIdentifier: "+jobneedIdentifier);
            //System.out.println("jobneedIdentifier1: "+jobneedIdentifier1);
            //System.out.println("jobType: "+jobType);
            /*String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                    +" order by "+ JOBNeed_Table.JOBNEED_PLANDATETIME +" ASC";*/

            if(jobneedIdentifier.equals(Constants.JOB_NEED_IDENTIFIER_PPM) && statusType == Constants.JOBNEED_STATUS_COMPLETED){
                System.out.println("PPM Completed---");

                String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                        +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                        +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_JOBSTATUS+" in(select taid from TypeAssist where tacode = '"+statusType+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_SYNC_STATUS+" in('-1','0','2')"
                        +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                        /*+" AND "+ JOBNeed_Table.JOBNEED_PLANDATETIME+" >= '"+prevDt+"'"*/
                        +" AND "+ JOBNeed_Table.JOBNEED_EXPIRYDATETIME+" >= '"+currDt+"'"
                        +" order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC";


                System.out.println("JOB Query FOR COMPLETED: "+jobNeedSql);

                c = db.rawQuery(jobNeedSql ,null);
            }else if (statusType == Constants.JOBNEED_STATUS_COMPLETED){
                System.out.println("task/tour Completed---");

                String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                        +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                        +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_JOBSTATUS+" in(select taid from TypeAssist where tacode = '"+statusType+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_SYNC_STATUS+" in('-1','0','2')"
                        +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                        +" AND "+ JOBNeed_Table.JOBNEED_PLANDATETIME+" >= '"+prevDt+"'"
                        +" order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC";


                System.out.println("JOB Query FOR COMPLETED: "+jobNeedSql);

                c = db.rawQuery(jobNeedSql ,null);
            } else {

                String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                        +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                        +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_JOBSTATUS+" in(select taid from TypeAssist where tacode = '"+statusType+"')"
                        +" AND "+ JOBNeed_Table.JOBNEED_SYNC_STATUS+" in('-1','0','2')"
                        +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                        +" AND "+ JOBNeed_Table.JOBNEED_EXPIRYDATETIME+" >= '"+currDt+"'"
                        +" order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC";


                System.out.println("JOB Query: "+jobNeedSql);

                c = db.rawQuery(jobNeedSql ,null);

            }


            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        //System.out.println("Qset ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        //System.out.println("job need ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        //System.out.println("Jobneed desc"+c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        //System.out.println("JObStatus: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                        jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        //System.out.println("Jobneed identifier: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));
                        jobNeedArrayList.add(jobNeed);
                    }
                    while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }

        }

        System.out.println("joneddarray list ppm"+jobNeedArrayList.size());
        return jobNeedArrayList;
    }


    public int deleteAutoClosedTicket()
    {
        int val = -1;
        try {
            db = sqlopenHelper.getReadableDatabase();

            String whereClause=JOBNeed_Table.JOBNEED_DESC+" like '%AUTOCLOSE%'";

            System.out.println("deleteAutoClosedTicket: "+whereClause);
            val=db.delete(JOBNeed_Table.TABLE_NAME, whereClause, null);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
        return val;
    }


    public ArrayList<JobNeed> getAutoClosedTicketList(String jobneedIdentifier, String jobneedIdentifier1, String jobType)
    {
        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        //System.out.println("User id: "+loginDetailPref.getString(Constants.LOGIN_USER_ID,""));
        //System.out.println("people id: "+peopleID);

        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        try {
            db = sqlopenHelper.getReadableDatabase();

            String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_DESC+" like '%AUTOCLOSE%'"
                    +" order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") DESC";


            //select * from JOBNEED where ( cuser =153328936479876 or aatop =153328936479876 or peopleid = 153328936479876 or groupid in (select groupid from pgbelonging where peopleid = 153328936479876))
            // AND identifier in(select taid from TypeAssist where tacode = 'TICKET' AND tatype ='Job Identifier')
            // AND JobType in(select taid from TypeAssist where tacode = 'ADHOC') AND jobdesc like '%AUTOCLOSE%'

            System.out.println("JOB Query auto: "+jobNeedSql);



            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        //System.out.println("Qset ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        //System.out.println("job need ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        //System.out.println("Jobneed desc"+c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        //System.out.println("JObStatus: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                        jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        //System.out.println("Jobneed identifier: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));
                        jobNeedArrayList.add(jobNeed);
                    }
                    while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }

        }
        return jobNeedArrayList;
    }

    public ArrayList<JobNeed> getTicketList(String jobneedIdentifier, String jobneedIdentifier1, String jobType)
    {
        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        //System.out.println("User id: "+loginDetailPref.getString(Constants.LOGIN_USER_ID,""));
        //System.out.println("people id: "+peopleID);

        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        try {
            db = sqlopenHelper.getReadableDatabase();

           /*String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                    +" order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") DESC";*/

           //to show incident report ticket also
            String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_DESC+" not like 'AUTOCLOSE%'"
                    +" order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") DESC";


            System.out.println("JOB Query ticket: "+jobNeedSql);



            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        //System.out.println("Qset ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        //System.out.println("job need ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        //System.out.println("Jobneed desc"+c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        //System.out.println("JObStatus: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                        jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        //System.out.println("Jobneed identifier: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));
                        jobNeedArrayList.add(jobNeed);
                    }
                    while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }

        }
        return jobNeedArrayList;
    }

    public ArrayList<JobNeed> getJobListDesc(String jobneedIdentifier, String jobneedIdentifier1, String jobType)
    {
        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        System.out.println("User id: "+loginDetailPref.getString(Constants.LOGIN_USER_ID,""));
        System.out.println("people id: "+peopleID);

        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        try {
            db = sqlopenHelper.getReadableDatabase();

            System.out.println("jobneedIdentifier: "+jobneedIdentifier);
            System.out.println("jobneedIdentifier1: "+jobneedIdentifier1);
            System.out.println("jobType: "+jobType);
            String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                    +" order by "+ JOBNeed_Table.JOBNEED_PRIORITY +" ASC";

            /*String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" order by "+ JOBNeed_Table.JOBNEED_PLANDATETIME +" DESC";*/

            System.out.println("JOB Query: "+jobNeedSql);

            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        //System.out.println("Qset ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        //System.out.println("job need ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        System.out.println("Jobneed desc"+c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                        jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        System.out.println("Jobneed identifier: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));
                        jobNeedArrayList.add(jobNeed);
                    }
                    while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return jobNeedArrayList;
    }


    public ArrayList<JobNeed> getSavedIRList(String jobneedIdentifier, String jobneedIdentifier1, String jobType)
    {
        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        System.out.println("User id: "+loginDetailPref.getString(Constants.LOGIN_USER_ID,""));
        System.out.println("people id: "+peopleID);

        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        try {
            db = sqlopenHelper.getReadableDatabase();

            System.out.println("jobneedIdentifier: "+jobneedIdentifier);
            System.out.println("jobneedIdentifier1: "+jobneedIdentifier1);
            System.out.println("jobType: "+jobType);

            String jobNeedSql="select distinct * from "+JOBNeed_Table.TABLE_NAME +" where ( "+JOBNeed_Table.JOBNEED_CUSER +" ="+peopleID +" or "+JOBNeed_Table.JOBNEED_AATOP+" ="+peopleID + " or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID +" or "
                    +JOBNeed_Table.JOBNEED_GROUPID +" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "
                    +" AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobneedIdentifier+"' AND tatype ='"+jobneedIdentifier1+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+jobType+"')"
                    +" AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"
                    +" AND ("+ JOBNeed_Table.JOBNEED_SYNC_STATUS+"='3' OR "+JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '0')"
                    +" order by "+ JOBNeed_Table.JOBNEED_PLANDATETIME +" ASC";

            System.out.println("JOB Query: "+jobNeedSql);

            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        //System.out.println("Qset ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        //System.out.println("job need ID"+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        System.out.println("Jobneed desc"+c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                        jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        System.out.println("Jobneed identifier: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSyncstatus(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SYNC_STATUS)));
                        jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));

                        System.out.println("Jobneed status: "+c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_SYNC_STATUS)));
                        System.out.println("Jobneed parent: "+c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeedArrayList.add(jobNeed);
                    }
                    while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return jobNeedArrayList;
    }

    public ArrayList<JobNeed> getSiteReportChildSectionList(long parentID)
    {
        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        Cursor cursor=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            String jobNeedSql="select distinct * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_PARENT+" = "+parentID
                    +" AND "+JOBNeed_Table.JOBNEED_SYNC_STATUS+"= '0'"
                    +" order by "+ JOBNeed_Table.JOBNEED_PLANDATETIME +" ASC";

            System.out.println("JOB Query: "+jobNeedSql);

            cursor = db.rawQuery(jobNeedSql ,null);

            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        jobNeed.setAssetid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setOthersite(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));
                        jobNeedArrayList.add(jobNeed);
                    }
                    while(cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(cursor!=null)
            {
                cursor.close();
                cursor=null;
            }
        }
        return jobNeedArrayList;
    }

    public ArrayList<JobNeed> getChildCheckPointList(long parentID)
    {
        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        Cursor cursor=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            String jobNeedSql="select distinct * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_PARENT+" = "+parentID
                    +" order by "+ JOBNeed_Table. JOBNEED_SEQNO+" ASC";

            System.out.println("JOB Query: "+jobNeedSql);

            cursor = db.rawQuery(jobNeedSql ,null);

            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        jobNeed.setAssetid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setOthersite(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));
                        //jobNeed.setDeviation(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_DEVIATION)));

                        jobNeedArrayList.add(jobNeed);
                    }
                    while(cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(cursor!=null)
            {
                cursor.close();
                cursor=null;
            }
        }

        System.out.println("jobNeedArrayList=="+ jobNeedArrayList.size());
        return jobNeedArrayList;
    }

    public ArrayList<JobNeed> getSiteReportSectionsList(long parentID)
    {
        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();

        Cursor cursor=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_PARENT+" = "+parentID
                    +" order by "+ JOBNeed_Table.JOBNEED_SEQNO +" ASC";

            System.out.println("JOB Query: "+jobNeedSql);

            cursor = db.rawQuery(jobNeedSql ,null);

            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        jobNeed.setAssetid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        //jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));

                        jobNeedArrayList.add(jobNeed);
                    }
                    while(cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(cursor!=null)
            {
                cursor.close();
                cursor=null;
            }
        }
        return jobNeedArrayList;
    }

    public ArrayList<JobNeed> getIrReportchildList(long qsetID)
    {
        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();

        Cursor cursor=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            String jobNeedSql="select distinct * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_PARENT+" = "+qsetID
                    +" order by "+ JOBNeed_Table.JOBNEED_SEQNO +" ASC";

            System.out.println("JOB Query: "+jobNeedSql);

            cursor = db.rawQuery(jobNeedSql ,null);

            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        jobNeed.setAssetid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        //jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));

                        /*String where="parent = ?";
                        String[] args=new String[]{String.valueOf(parentID)};
                        db.delete(JOBNeed_Table.TABLE_NAME,where,args);*/

                        jobNeedArrayList.add(jobNeed);
                    }
                    while(cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(cursor!=null)
            {
                cursor.close();
                cursor=null;
            }
        }
        return jobNeedArrayList;
    }

    public ArrayList<JobNeed> getIrReportSectionsList(long parentID)
    {
        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();

        Cursor cursor=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            String jobNeedSql="select distinct * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_PARENT+" = "+parentID
                    +" order by "+ JOBNeed_Table.JOBNEED_SEQNO +" ASC";

            System.out.println("JOB Query: "+jobNeedSql);

            cursor = db.rawQuery(jobNeedSql ,null);

            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        jobNeed.setAssetid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(cursor.getString(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        //System.out.println("Jobneed parent: "+cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(cursor.getInt(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(cursor.getLong(cursor.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        //jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));

                        /*String where="parent = ?";
                        String[] args=new String[]{String.valueOf(parentID)};
                        db.delete(JOBNeed_Table.TABLE_NAME,where,args);*/

                        jobNeedArrayList.add(jobNeed);
                    }
                    while(cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(cursor!=null)
            {
                cursor.close();
                cursor=null;
            }
        }
        return jobNeedArrayList;
    }

    public ArrayList<JobNeed> getUnsyncIRList()
    {
        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        try {
            db = sqlopenHelper.getReadableDatabase();

            /*String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '0' AND "+JOBNeed_Table.JOBNEED_PARENT+" = -1 AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in (select taid from TypeAssist where tacode = '"+ Constants.JOB_NEED_IDENTIFIER_INCIDENT +"') limit 1";*/
            String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '0' AND "+JOBNeed_Table.JOBNEED_PARENT+" = -1 AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in (select taid from TypeAssist where tacode in( '"+ Constants.JOB_NEED_IDENTIFIER_INCIDENT +"'))";
            System.out.println("JOB Query: "+jobNeedSql);
            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                        jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setCtzoffset(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZOFFSET)));
                        jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));

                        jobNeedArrayList.add(jobNeed);
                    }
                    while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (db!=null) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }

        System.out.println("jobneedarraylist---"+ jobNeedArrayList.size());
        return jobNeedArrayList;
    }

    public ArrayList<JobNeed> getUnsyncSiteReportList()
    {
        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        try {
            db = sqlopenHelper.getReadableDatabase();

            /*String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '0' AND "+JOBNeed_Table.JOBNEED_PARENT+" = -1 AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in (select taid from TypeAssist where tacode = '"+ Constants.JOB_NEED_IDENTIFIER_INCIDENT +"') limit 1";*/
            String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '0' AND "+JOBNeed_Table.JOBNEED_PARENT+" = -1 AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in (select taid from TypeAssist where tacode in( '"+Constants.JOB_NEED_IDENTIFIER_SITEREPORT +"'))";
            System.out.println("JOB Query: "+jobNeedSql);
            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                        jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setCtzoffset(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZOFFSET)));
                        jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));
                        jobNeed.setMultiplicationfactor(c.getDouble(c.getColumnIndex(JOBNeed_Table.JOBNEED_MFACTOR)));

                        jobNeedArrayList.add(jobNeed);
                    }
                    while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (db!=null) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return jobNeedArrayList;
    }

    public ArrayList<JobNeed> getUnsyncJobList(String identifier, int status)
    {
        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        try {
            db = sqlopenHelper.getReadableDatabase();

            /*String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '"+status+"'  AND "+
                    JOBNeed_Table.JOBNEED_IDENTIFIER+" in (select taid from TypeAssist where tacode in("+ identifier +") and tatype in ('"+Constants.IDENTIFIER_JOBNEED+"')) limit 1";*/

            String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '"+status+"'  AND "+
                    JOBNeed_Table.JOBNEED_IDENTIFIER+" in (select taid from TypeAssist where tacode in("+ identifier +") and tatype in ('"+Constants.IDENTIFIER_JOBNEED+"'))";

            System.out.println("JOB Query: "+jobNeedSql);
            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                        jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setCtzoffset(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZOFFSET)));
                        jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));
                        jobNeed.setMultiplicationfactor(c.getDouble(c.getColumnIndex(JOBNeed_Table.JOBNEED_MFACTOR)));
                        jobNeed.setDeviation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DEVIATION)));
                        jobNeedArrayList.add(jobNeed);
                    }
                    while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return jobNeedArrayList;
    }


    public ArrayList<JobNeed> getTempUnsyncJobList(int status)
    {
        ArrayList<JobNeed> jobNeedArrayList=new ArrayList<JobNeed>();
        try {
            db = sqlopenHelper.getReadableDatabase();

            /*String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '"+status+"'  AND "+
                    JOBNeed_Table.JOBNEED_IDENTIFIER+" in (select taid from TypeAssist where tacode in("+ identifier +") and tatype in ('"+Constants.IDENTIFIER_JOBNEED+"')) limit 1";*/

            //String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME;
            String jobNeedSql="select * from  jobneed";

            System.out.println("JOB Query: "+jobNeedSql);
            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        JobNeed jobNeed=new JobNeed();
                        jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                        jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                        jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                        jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                        jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                        jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                        jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                        jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                        jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                        jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                        jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                        jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                        jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                        jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                        jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                        jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                        jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                        jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                        jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                        jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                        //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                        jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                        jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                        jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                        jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                        jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                        jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                        jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                        jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                        jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                        jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                        jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                        jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                        jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                        jobNeed.setCtzoffset(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZOFFSET)));
                        jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));
                        jobNeed.setMultiplicationfactor(c.getDouble(c.getColumnIndex(JOBNeed_Table.JOBNEED_MFACTOR)));
                        jobNeedArrayList.add(jobNeed);
                    }
                    while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return jobNeedArrayList;
    }


    public JobNeed getJobNeedDetails(long jobneedid)
    {
        JobNeed jobNeed=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_ID+" = "+jobneedid;
            System.out.println("JOB Query: "+jobNeedSql);
            c = db.rawQuery(jobNeedSql ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    jobNeed=new JobNeed();
                    jobNeed.setQuestionsetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID)));
                    jobNeed.setJobneedid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ID)));
                    jobNeed.setJobdesc(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                    jobNeed.setFrequency(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY)));
                    jobNeed.setPlandatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                    jobNeed.setExpirydatetime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)));
                    jobNeed.setGracetime(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)));
                    jobNeed.setJobtype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE)));
                    jobNeed.setJobstatus(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                    jobNeed.setScantype(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE)));
                    jobNeed.setReceivedonserver(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER)));
                    jobNeed.setPriority(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY)));
                    jobNeed.setStarttime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME)));
                    jobNeed.setEndtime(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME)));
                    jobNeed.setGpslocation(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION)));
                    jobNeed.setRemarks(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK)));
                    jobNeed.setCuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER)));
                    jobNeed.setCdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ)));
                    jobNeed.setMuser(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER)));
                    jobNeed.setMdtz(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ)));
                    //jobNeed.setIsdeleted(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED)));
                    jobNeed.setAssetid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID)));
                    jobNeed.setGroupid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID)));
                    jobNeed.setAatop(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP)));
                    jobNeed.setJobid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID)));
                    jobNeed.setPerformedby(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY)));
                    jobNeed.setAttachmentcount(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT)));
                    jobNeed.setPeopleid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID)));
                    jobNeed.setIdentifier(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER)));
                    jobNeed.setParent(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT)));
                    jobNeed.setTicketno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO)));
                    jobNeed.setBuid(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                    System.out.println("BUID: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_BUID)));
                    jobNeed.setSeqno(c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO)));
                    jobNeed.setTicketcategory(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                    System.out.println("TC: "+c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY)));
                    jobNeed.setOthersite(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_OTHERSITE)));

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return jobNeed;
    }

    public void updateJobNeedRecord(long jnid, long atop, long atog,String remark, long jobneedstatus, long peopleID)
    {
        String date=null;
        try {

            DateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            date = df.format(Calendar.getInstance().getTime());

            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(JOBNeed_Table.JOBNEED_JOBSTATUS, jobneedstatus);
            values.put(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER, CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            values.put(JOBNeed_Table.JOBNEED_JOBENDTIME, CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            values.put(JOBNeed_Table.JOBNEED_REMARK, remark);
            values.put(JOBNeed_Table.JOBNEED_MUSER, peopleID);
            values.put(JOBNeed_Table.JOBNEED_MDTZ, CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            values.put(JOBNeed_Table.JOBNEED_GPSLOCATION,deviceDetailPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceDetailPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
            values.put(JOBNeed_Table.JOBNEED_PERFORMEDBY, peopleID);
            values.put(JOBNeed_Table.JOBNEED_SYNC_STATUS, "2");
            db.update(JOBNeed_Table.TABLE_NAME, values, "jobneedid=?", new String[] { String.valueOf(jnid) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void updateJobNeedRecordFromAdhoc(long jnid, long jobneedstatus)
    {
        try {

            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(JOBNeed_Table.JOBNEED_JOBSTATUS, jobneedstatus);
            values.put(JOBNeed_Table.JOBNEED_SYNC_STATUS, "1");
            db.update(JOBNeed_Table.TABLE_NAME, values, "jobneedid=?", new String[] { String.valueOf(jnid) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void updateJobNeedStartTime(long jnid)
    {
        try {
            CommonFunctions.EventLog("\n Job Need started at: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+" \n JOB Need ID: "+jnid+" \n");
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(JOBNeed_Table.JOBNEED_JOBSTARTTIME, CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            db.update(JOBNeed_Table.TABLE_NAME, values, "jobneedid=?", new String[] { String.valueOf(jnid) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void updateChildTourCompleted(long jnid, String startTime, String endTime, long performedBy, long status, String gpsLocation, long parent)
    {
        try {
            //checkDeviation(parent, jnid);
            //CommonFunctions.getTimezoneDate(System.currentTimeMillis())
            //System.out.println("final deviation"+checkDeviation(parent, jnid));
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(JOBNeed_Table.JOBNEED_JOBSTARTTIME, startTime);
            values.put(JOBNeed_Table.JOBNEED_JOBENDTIME, endTime);
            values.put(JOBNeed_Table.JOBNEED_PERFORMEDBY, performedBy);
            values.put(JOBNeed_Table.JOBNEED_JOBSTATUS, status);
            values.put(JOBNeed_Table.JOBNEED_GPSLOCATION, gpsLocation);
            values.put(JOBNeed_Table.JOBNEED_DEVIATION, updateChildDeviation(parent, jnid));
            db.update(JOBNeed_Table.TABLE_NAME, values, "jobneedid=?", new String[] { String.valueOf(jnid) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }
    boolean pDev= false;
    public boolean updateChildDeviation(long parent, long joneedid){
        int mySequence= 0;
        boolean deviation= false;
        int count= getCompletedChildCount(parent);
        System.out.println("get child count=="+count);
        if (count == 0){
            mySequence = 0;
        }else {
            mySequence =count;
        }
        int orignalseq= getSeqNo(joneedid);
        System.out.println("get sequence number=="+getSeqNo(joneedid)+ "myseq+="+ mySequence);
        if(mySequence == orignalseq){
             deviation = false;
             pDev = false;
        }else {
             deviation = true;
             pDev = true;
        }
        System.out.println("pdeviation final"+ pDev);

        System.out.println("deviation final"+ deviation);
        return deviation;
    }

    public int getSeqNo(long joneedid)
    {
        int seqNo=-1;
        try{
            db=sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("select "+ JOBNeed_Table.JOBNEED_SEQNO +" from "+JOBNeed_Table.TABLE_NAME+" where "+JOBNeed_Table.JOBNEED_ID+" = '"+joneedid+"'",null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    seqNo=c.getInt(0);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(c!=null)
                c=null;
        }

        return seqNo;
    }
    //MyCoNameMyDOB#12

    public void updateParentTourPartiallyCompleted(long jnid, String startTime,long performedBy, long status, long cJndId)
    {
        try {
            //CommonFunctions.getTimezoneDate(System.currentTimeMillis())
            System.out.println("check for parent----"+pDev);
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(JOBNeed_Table.JOBNEED_JOBSTARTTIME, startTime);
            values.put(JOBNeed_Table.JOBNEED_PERFORMEDBY, performedBy);
            values.put(JOBNeed_Table.JOBNEED_JOBSTATUS, status);
            if(pDev){
                values.put(JOBNeed_Table.JOBNEED_DEVIATION, pDev);
            }
            db.update(JOBNeed_Table.TABLE_NAME, values, "jobneedid=?", new String[] { String.valueOf(jnid) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void updateParentTourCompleted(long jnid, String startTime,String endTime, long performedBy, long status)
    {
        try {
            //CommonFunctions.getTimezoneDate(System.currentTimeMillis())
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(JOBNeed_Table.JOBNEED_JOBSTARTTIME, startTime);
            values.put(JOBNeed_Table.JOBNEED_JOBENDTIME, endTime);
            values.put(JOBNeed_Table.JOBNEED_PERFORMEDBY, performedBy);
            values.put(JOBNeed_Table.JOBNEED_JOBSTATUS, status);
            if(pDev){
                values.put(JOBNeed_Table.JOBNEED_DEVIATION, pDev);
            }
            db.update(JOBNeed_Table.TABLE_NAME, values, "jobneedid=?", new String[] { String.valueOf(jnid) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public int changeJobStatus(long  jnid, long jStatus) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(JOBNeed_Table.JOBNEED_JOBSTATUS, jStatus);
            return db.update(JOBNeed_Table.TABLE_NAME, values, "jobneedid=?", new String[] { String.valueOf(jnid) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public int changeJobStartTime(long  jnid, long status) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(JOBNeed_Table.JOBNEED_JOBSTARTTIME, CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            values.put(JOBNeed_Table.JOBNEED_JOBSTATUS,status );
            return db.update(JOBNeed_Table.TABLE_NAME, values, "jobneedid=?", new String[] { String.valueOf(jnid) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }


    public int changeJobNeedSyncStatus(long  jnid, String status) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(JOBNeed_Table.JOBNEED_SYNC_STATUS, status);
            return db.update(JOBNeed_Table.TABLE_NAME, values, "jobneedid=?", new String[] { String.valueOf(jnid) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public int changeJobNeedChildSyncStatus(long  qSetId, String status) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(JOBNeed_Table.JOBNEED_SYNC_STATUS, status);
            return db.update(JOBNeed_Table.TABLE_NAME, values, "questionsetid=?", new String[] { String.valueOf(qSetId) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void getCount()
    {
        String userName=null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select count(*) from " + JOBNeed_Table.TABLE_NAME ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    System.out.println("Job need Count: "+c.getInt(0));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
    }



    public void getJobNeedCount(long jobneedid)
    {
        String userName=null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select count(*) from " + JOBNeed_Table.TABLE_NAME+" where "+JOBNeed_Table.JOBNEED_PARENT+" ="+jobneedid ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    System.out.println("JobNeedCount: "+c.getInt(0));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
    }

    public int getChildCount(long parentID)
    {
        int childCount=0;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select count(*) from " + JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_PARENT+" = "+parentID ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    System.out.println("Count: "+c.getInt(0));
                    childCount=c.getInt(0);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return childCount;
    }

    public int getCompletedChildCount(long parentID)
    {
        int completedchildCount=0;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select count(*) from " + JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_PARENT+" = "+parentID+" AND "+JOBNeed_Table.JOBNEED_JOBSTATUS+ " in (select taid from TypeAssist where tacode = 'COMPLETED')" ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    System.out.println("Count: "+c.getInt(0));
                    completedchildCount=c.getInt(0);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if (null != db) {
                db.close();
            }*/

            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return completedchildCount;
    }



    public Cursor getScheduleTourList(long currentTime, long scheduleTime)
    {
        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        System.out.println("User id: "+loginDetailPref.getString(Constants.LOGIN_USER_ID,""));
        String currDt=CommonFunctions.getParseDate(currentTime);
        String nxtDt=CommonFunctions.getParseDate(scheduleTime);

        try {
            db = sqlopenHelper.getReadableDatabase();
			String query="select * from "+JOBNeed_Table.TABLE_NAME+" where "+
                    JOBNeed_Table.JOBNEED_PLANDATETIME+" BETWEEN '"+currDt+"' AND '"+nxtDt+"'" +
                    " AND "+JOBNeed_Table.JOBNEED_JOBSTATUS +" not in (select taid from TypeAssist where tacode in('"+Constants.JOBNEED_STATUS_COMPLETED+"','"+Constants.JOBNEED_STATUS_AUTOCLOSED+"'))"+
                    " AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+Constants.JOB_NEED_IDENTIFIER_TOUR+"' AND tatype ='"+Constants.IDENTIFIER_JOBNEED+"')"+
                    " AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+Constants.JOB_TYPE_SCHEDULED+"')"+
                    " AND "+ JOBNeed_Table.JOBNEED_PARENT+"=-1"+
                    " AND ("+JOBNeed_Table.JOBNEED_AATOP+" = "+peopleID+" or "+ JOBNeed_Table.JOBNEED_CUSER+" = "+peopleID+" or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID+" or "+
                    JOBNeed_Table.JOBNEED_GROUPID+" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "+
                    " order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC";

            System.out.println("query for schedule task: "+query);
            c=db.rawQuery(query, null);
            /*if(cursor!=null) {
                if (cursor.moveToFirst()) {

                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {

        }

        return c;
    }


    public Cursor getScheduleTaskList(long currentTime, long scheduleTime, String jobIdentifier )
    {
        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        System.out.println("User id: "+loginDetailPref.getString(Constants.LOGIN_USER_ID,"")+ jobIdentifier);
        /*String currDt=CommonFunctions.getParseDate(currentTime);
        String nxtDt=CommonFunctions.getParseDate(scheduleTime);*/
        String currDt=CommonFunctions.getTimezoneDate(currentTime);
        String nxtDt=CommonFunctions.getTimezoneDate(scheduleTime);

        try {
            db = sqlopenHelper.getReadableDatabase();
			/*String query="select * from "+JOBNeed_Table.TABLE_NAME+" where "+
                    JOBNeed_Table.JOBNEED_PLANDATETIME+">= '"+ currDt +"' AND "+JOBNeed_Table.JOBNEED_PLANDATETIME+" <= '"+nxtDt+"'"+
                    " AND "+JOBNeed_Table.JOBNEED_JOBSTATUS +" not in ('AUTOCLOSED','COMPLETED')"+
                    " AND ("+JOBNeed_Table.JOBNEED_AATOP+" = "+peopleID+" or "+ JOBNeed_Table.JOBNEED_CUSER+" = "+peopleID+" or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID+" or "+
                    JOBNeed_Table.JOBNEED_GROUPID+" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "+
                    " order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC";*/

            String query="select * from "+JOBNeed_Table.TABLE_NAME+" where "+
                    JOBNeed_Table.JOBNEED_PLANDATETIME+" BETWEEN '"+currDt+"' AND '"+nxtDt+"'" +
                    " AND "+JOBNeed_Table.JOBNEED_JOBSTATUS +" not in (select taid from TypeAssist where tacode in('"+Constants.JOBNEED_STATUS_COMPLETED+"','"+Constants.JOBNEED_STATUS_AUTOCLOSED+"'))"+
                    " AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+jobIdentifier+"' AND tatype ='"+Constants.IDENTIFIER_JOBNEED+"')"+
                    " AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+Constants.JOB_TYPE_SCHEDULED+"')"+
                    " AND ("+JOBNeed_Table.JOBNEED_AATOP+" = "+peopleID+" or "+ JOBNeed_Table.JOBNEED_CUSER+" = "+peopleID+" or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID+" or "+
                    JOBNeed_Table.JOBNEED_GROUPID+" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "+
                    " order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC";

		    System.out.println("query for schedule task: "+query);
            c=db.rawQuery(query, null);
            /*if(cursor!=null) {
                if (cursor.moveToFirst()) {

                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {

        }

        return c;
    }


    public Cursor getScheduleJobAlertList(long currentTime, long scheduleTime)
    {
        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        System.out.println("User id: "+loginDetailPref.getString(Constants.LOGIN_USER_ID,""));
        /*String currDt=CommonFunctions.getParseDate(currentTime);
        String nxtDt=CommonFunctions.getParseDate(scheduleTime);*/
        String currDt=CommonFunctions.getTimezoneDate(currentTime);
        String nxtDt=CommonFunctions.getTimezoneDate(scheduleTime);

        try {
            db = sqlopenHelper.getReadableDatabase();
            c=null;
			/*String query="select * from "+JOBNeed_Table.TABLE_NAME+" where "+
                    JOBNeed_Table.JOBNEED_PLANDATETIME+" BETWEEN '"+currDt+"' AND '"+nxtDt+"'" +
                    " AND "+JOBNeed_Table.JOBNEED_JOBSTATUS +" not in (select taid from TypeAssist where tacode in('"+Constants.JOBNEED_STATUS_COMPLETED+"','"+Constants.JOBNEED_STATUS_AUTOCLOSED+"'))"+
                    " AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode in ('"+Constants.JOB_NEED_IDENTIFIER_TASK+"','"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"+Constants.JOB_NEED_IDENTIFIER_SITEREPORT+"' ) AND tatype ='"+Constants.IDENTIFIER_JOBNEED+"')"+
                    " AND "+ JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+Constants.JOB_TYPE_SCHEDULED+"')"+
                    " AND ("+JOBNeed_Table.JOBNEED_AATOP+" = "+peopleID+" or "+ JOBNeed_Table.JOBNEED_CUSER+" = "+peopleID+" or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID+" or "+
                    JOBNeed_Table.JOBNEED_GROUPID+" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "+
                    " order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC limit 1";*/

            String query="select * from "+JOBNeed_Table.TABLE_NAME+" where "+
                    JOBNeed_Table.JOBNEED_PLANDATETIME+" BETWEEN '"+currDt+"' AND '"+nxtDt+"'" +
                    " AND "+JOBNeed_Table.JOBNEED_JOBSTATUS +" not in (select taid from TypeAssist where tacode in('"+Constants.JOBNEED_STATUS_COMPLETED+"','"+Constants.JOBNEED_STATUS_AUTOCLOSED+"'))"+
                    " AND "+JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode in ('"+Constants.JOB_NEED_IDENTIFIER_TASK+"','"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"+Constants.JOB_NEED_IDENTIFIER_SITEREPORT+"' ) AND tatype ='"+Constants.IDENTIFIER_JOBNEED+"')"+
                    " AND "+JOBNeed_Table.JOBNEED_JOBTYPE+" in(select taid from TypeAssist where tacode = '"+Constants.JOB_TYPE_SCHEDULED+"')"+
                    " AND "+JOBNeed_Table.JOBNEED_PARENT+"=-1"+
                    " AND ("+JOBNeed_Table.JOBNEED_AATOP+" = "+peopleID+" or "+ JOBNeed_Table.JOBNEED_CUSER+" = "+peopleID+" or "+JOBNeed_Table.JOBNEED_PEOPLEID+" = "+peopleID+" or "+
                    JOBNeed_Table.JOBNEED_GROUPID+" in (select groupid from PeopleGroupBelonging where peopleid = "+ peopleID +")) "+
                    " order by strftime('%s' ,"+ JOBNeed_Table.JOBNEED_PLANDATETIME +") ASC limit 1";

            System.out.println("JOBAlertBroadcast query for schedule task: "+query);
            c=db.rawQuery(query, null);
            /*if(cursor!=null) {
                if (cursor.moveToFirst()) {

                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {

        }

        return c;
    }

//Delete yestday task: PlanDateTime BETWEEN '2018-08-30 00:00:00' AND '2018-09-06 00:00:00' AND JobStatus in (select taid from TypeAssist where tacode in('COMPLETED','AUTOCLOSED')) AND identifier in(select taid from TypeAssist where tacode in ('TASK','TOUR','SITEREPORT' ) AND tatype ='Job Identifier')

    public int deleteCompletedYesterdayTask()
    {
        int val = -1;
        //long yesDay =date();
        String currDt=CommonFunctions.getTimezoneDate(date(-1));
        String prevDt=CommonFunctions.getTimezoneDate(date(0));
        try {
            db = sqlopenHelper.getReadableDatabase();
            /*String whereClause=JOBNeed_Table.JOBNEED_PLANDATETIME+" BETWEEN '"+prevDt+"' AND '"+currDt+"' AND "+
                    JOBNeed_Table.JOBNEED_JOBSTATUS+" in (select taid from TypeAssist where tacode in('"+Constants.JOBNEED_STATUS_COMPLETED+"','"+Constants.JOBNEED_STATUS_AUTOCLOSED+"')) AND "+
                    JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '0' AND "+
                    JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode in ('"+Constants.JOB_NEED_IDENTIFIER_TASK+"','"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"+Constants.JOB_NEED_IDENTIFIER_SITEREPORT+"' ) AND tatype ='"+Constants.IDENTIFIER_JOBNEED+"')";*/

            String whereClause=JOBNeed_Table.JOBNEED_EXPIRYDATETIME+" <= '"+currDt+"' AND "+
                    JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '1' AND "+
                    JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode in ('"+Constants.JOB_NEED_IDENTIFIER_TASK+"','"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"+Constants.JOB_NEED_IDENTIFIER_SITEREPORT+"' ) AND tatype ='"+Constants.IDENTIFIER_JOBNEED+"')";

            //String whereClause=TaskTable.TASK_PLAN_DATE+"<"+yesDay;
            System.out.println("Delete yestday task: "+whereClause);
            val=db.delete(JOBNeed_Table.TABLE_NAME, whereClause, null);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
        return val;
    }

    public int deleteCompletedPreviousTour()
    {
        int val = -1;
        //long yesDay =date();
        String currDt=CommonFunctions.getTimezoneDate(date(2));
        String prevDt=CommonFunctions.getTimezoneDate(date(0));
        System.out.println("Day Before Yesterday date: " +currDt);
        try {
            db = sqlopenHelper.getReadableDatabase();
            /*String whereClause=JOBNeed_Table.JOBNEED_PLANDATETIME+" BETWEEN '"+prevDt+"' AND '"+currDt+"' AND "+
                    JOBNeed_Table.JOBNEED_JOBSTATUS+" in (select taid from TypeAssist where tacode in('"+Constants.JOBNEED_STATUS_COMPLETED+"','"+Constants.JOBNEED_STATUS_AUTOCLOSED+"')) AND "+
                    JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '0' AND "+
                    JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode in ('"+Constants.JOB_NEED_IDENTIFIER_TASK+"','"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"+Constants.JOB_NEED_IDENTIFIER_SITEREPORT+"' ) AND tatype ='"+Constants.IDENTIFIER_JOBNEED+"')";*/

            String whereClause=JOBNeed_Table.JOBNEED_EXPIRYDATETIME+" <= '"+currDt+"' AND "+
                    JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '1' AND "+
                    JOBNeed_Table.JOBNEED_IDENTIFIER+" in(select taid from TypeAssist where tacode in ('"+Constants.JOB_NEED_IDENTIFIER_TOUR+"' ) AND tatype ='"+Constants.IDENTIFIER_JOBNEED+"')";

            //String whereClause=TaskTable.TASK_PLAN_DATE+"<"+yesDay;
            System.out.println("Delete yestday task: "+whereClause);
            val=db.delete(JOBNeed_Table.TABLE_NAME, whereClause, null);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
        return val;
    }

    public long date(int val)  // get starting time
    {
        if(val==0) {
            Calendar cal = Calendar.getInstance();       // get calendar instance
            cal.setTime(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));                           // set cal to date
            cal.set(Calendar.HOUR_OF_DAY, 00);            // set hour to midnight
            cal.set(Calendar.MINUTE, 00);                 // set minute in hour
            cal.set(Calendar.SECOND, 00);                 // set second in minute
            cal.set(Calendar.MILLISECOND, 0);            // set millis in second
            return cal.getTimeInMillis();
        }else if(val == 1){
            Calendar cal = Calendar.getInstance();       // get calendar instance
            cal.setTime(new Date(System.currentTimeMillis()));                           // set cal to date
            cal.set(Calendar.HOUR_OF_DAY, 00);            // set hour to midnight
            cal.set(Calendar.MINUTE, 00);                 // set minute in hour
            cal.set(Calendar.SECOND, 00);                 // set second in minute
            cal.set(Calendar.MILLISECOND, 0);            // set millis in second
            return cal.getTimeInMillis();
        }
        else if(val == 2){
            System.out.println("val == 2");
            Calendar cal = Calendar.getInstance();       // get calendar instance
            cal.setTime(new Date(System.currentTimeMillis() - 48 * 60 * 60 * 1000));                           // set cal to date
            cal.set(Calendar.HOUR_OF_DAY, 23);            // set hour to midnight
            cal.set(Calendar.MINUTE, 59);                 // set minute in hour
            cal.set(Calendar.SECOND, 59);                 // set second in minute
            cal.set(Calendar.MILLISECOND, 0);            // set millis in second
            return cal.getTimeInMillis();
        }
        else
        {
            Calendar cal = Calendar.getInstance();       // get calendar instance
            cal.setTime(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));                           // set cal to date
            cal.set(Calendar.HOUR_OF_DAY, 23);            // set hour to midnight
            cal.set(Calendar.MINUTE, 59);                 // set minute in hour
            cal.set(Calendar.SECOND, 59);                 // set second in minute
            cal.set(Calendar.MILLISECOND, 0);            // set millis in second
            return cal.getTimeInMillis();
        }
        /*Date date = new Date(System.currentTimeMillis());                      // timestamp now
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);                           // set cal to date
        if(val==-1)
            cal.set(Calendar.DATE,val);
        cal.set(Calendar.HOUR_OF_DAY, 00);            // set hour to midnight
        cal.set(Calendar.MINUTE, 01);                 // set minute in hour
        cal.set(Calendar.SECOND, 00);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millis in second
        return cal.getTimeInMillis();*/
    }

    public void deleteRec(long jnid )
    {
        //db.delete(DATABASE_TABLE, KEY_NAME + "=" + name, null)
        try {
            db = sqlopenHelper.getReadableDatabase();
            db.execSQL("delete from "+ JOBNeed_Table.TABLE_NAME+" where "+JOBNeed_Table.JOBNEED_ID+" ="+jnid);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }
}
