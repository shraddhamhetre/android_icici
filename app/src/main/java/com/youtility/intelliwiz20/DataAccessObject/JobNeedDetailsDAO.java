package com.youtility.intelliwiz20.DataAccessObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.QuestionAnswerTransaction;
import com.youtility.intelliwiz20.Tables.JOBNeedDetails_Table;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by PrashantD on 5/9/17.
 *
 * jobneed details related access object
 *
 */

public class JobNeedDetailsDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private TypeAssistDAO typeAssistDAO;
    private Cursor c=null;

    public JobNeedDetailsDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
        typeAssistDAO=new TypeAssistDAO(context);
    }

    //jndid,seqno,questionname,type,answer,option,min,max,alerton,ismandatory,cdtz,mdtz,isdeleted,cuser,jobneedid,muser
    public void insertRecord(JobNeedDetails regRecord)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ID, regRecord.getJndid());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO, regRecord.getSeqno());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID, regRecord.getQuestionid());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE, regRecord.getType());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER, regRecord.getAnswer());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_OPTION, regRecord.getOption());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MIN, regRecord.getMin());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MAX, regRecord.getMax());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ALERTON, regRecord.getAlerton());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ISMANATORY, regRecord.getIsmandatory());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_CDTZ, regRecord.getCdtz());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MDTZ, regRecord.getMdtz());
            //values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ISDELETED, regRecord.getIsdeleted());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_CUSER, regRecord.getCuser());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID, regRecord.getJobneedid());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MUSER, regRecord.getMuser());

            long val= db.insert(JOBNeedDetails_Table.TABLE_NAME, "", values);
            System.out.println("Common Data val: "+val);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void insertOrUpdateRecord(JobNeedDetails regRecord)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ID, regRecord.getJndid());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO, regRecord.getSeqno());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID, regRecord.getQuestionid());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE, regRecord.getType());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER, regRecord.getAnswer());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_OPTION, regRecord.getOption());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MIN, regRecord.getMin());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MAX, regRecord.getMax());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ALERTON, regRecord.getAlerton());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ISMANATORY, regRecord.getIsmandatory());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_CDTZ, regRecord.getCdtz());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MDTZ, regRecord.getMdtz());
            //values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ISDELETED, regRecord.getIsdeleted());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_CUSER, regRecord.getCuser());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID, regRecord.getJobneedid());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MUSER, regRecord.getMuser());

            String where ="questionid=? AND cuser=? AND jobneedid=? ";
            String[] args=new String[]{String.valueOf(regRecord.getQuestionid()), String.valueOf(regRecord.getCuser()), String.valueOf(regRecord.getJobneedid())};
            int val1=db.delete(JOBNeedDetails_Table.TABLE_NAME, where,args);
            System.out.println("deleteval: "+val1);

            long val= db.insert(JOBNeedDetails_Table.TABLE_NAME, "", values);
            System.out.println("insert val: "+val);

            /*if(isJobNeedDetailsQuestionPresent(regRecord.getJobneedid(), regRecord.getCuser(), regRecord.getQuestionid()))
            {
                String where ="questionid=? AND cuser=? AND jobneedid=? ";
                String[] args=new String[]{String.valueOf(regRecord.getQuestionid()), String.valueOf(regRecord.getCuser()), String.valueOf(regRecord.getJobneedid())};
                long val=db.update(JOBNeedDetails_Table.TABLE_NAME,values,where,args);
            }else
            {
                long val= db.insert(JOBNeedDetails_Table.TABLE_NAME, "", values);
            }*/


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void test_insertOrUpdateRecord(JobNeedDetails regRecord)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ID, regRecord.getJndid());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO, regRecord.getSeqno());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID, regRecord.getQuestionid());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE, regRecord.getType());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER, regRecord.getAnswer());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_OPTION, regRecord.getOption());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MIN, regRecord.getMin());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MAX, regRecord.getMax());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ALERTON, regRecord.getAlerton());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ISMANATORY, regRecord.getIsmandatory());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_CDTZ, regRecord.getCdtz());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MDTZ, regRecord.getMdtz());
            //values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ISDELETED, regRecord.getIsdeleted());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_CUSER, regRecord.getCuser());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID, regRecord.getJobneedid());
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_MUSER, regRecord.getMuser());

            String where ="questionid=? AND cuser=? AND jndid=? ";
            String[] args=new String[]{String.valueOf(regRecord.getQuestionid()), String.valueOf(regRecord.getCuser()), String.valueOf(regRecord.getJndid())};
            int val1=db.delete(JOBNeedDetails_Table.TABLE_NAME, where,args);
            System.out.println("deleteval: "+val1);

            /*String where ="jobneedid=?";
            String[] args=new String[]{String.valueOf(regRecord.getJobneedid())};
            int val1=db.delete(JOBNeedDetails_Table.TABLE_NAME, where,args);
            System.out.println("deleteval: "+val1);*/

            System.out.println("jnd--regRecord.getJobneedid(): "+regRecord.getJobneedid());
            System.out.println("jnd--regRecord.getJndid(): "+regRecord.getJndid());

            long val= db.insert(JOBNeedDetails_Table.TABLE_NAME, "", values);
            System.out.println("insert val: "+val);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    private boolean isJobNeedDetailsQuestionPresent(long jobneedid, long cuser, long questionid)
    {
        String str="select * from "+JOBNeedDetails_Table.TABLE_NAME +" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID+" = "+questionid+" AND "+JOBNeedDetails_Table.JOBNEEDDETAILS_CUSER+" = "+cuser+" AND "+JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID+" = "+jobneedid;
        c=db.rawQuery(str, null);
        if(c!=null)
        {
            if(c.moveToFirst()) {
                c.close();
                return true;
            }
        }
        return false;
    }

    public ArrayList<JobNeedDetails> test_getJobNeedDetailQuestList(long parentid)
    {
        ArrayList<JobNeedDetails> jobNeedDetailsArrayList=new ArrayList<JobNeedDetails>();
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + JOBNeedDetails_Table.TABLE_NAME+" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID+"= '"+parentid+"'"+" order by "+JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO +" ASC" ,null);
            System.out.println("Select * from " + JOBNeedDetails_Table.TABLE_NAME+" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID+"= '"+parentid+"'"+" order by "+JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO +" ASC");

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        System.out.println("JObneedID: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID)));
                        System.out.println("ANS: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)));
                        System.out.println("SEQ NO: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO)));
                        System.out.println("JOBneeddetails id: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ID)));
                        System.out.println("Quest ID: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID)));
                        System.out.println("Quest Type: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)));
                        System.out.println("Quest alerton: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ALERTON)));
                        System.out.println("Quest Type Name: "+typeAssistDAO.getEventTypeName(Long.valueOf(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)))));


                        JobNeedDetails jobNeedDetails = new JobNeedDetails();
                        jobNeedDetails.setSeqno(c.getInt(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO)));
                        jobNeedDetails.setQuestionid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID)));
                        jobNeedDetails.setOption(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_OPTION)));
                        jobNeedDetails.setMax(c.getDouble(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MAX)));
                        jobNeedDetails.setAlerton(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ALERTON)));
                        if(Long.valueOf(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)))!=-1)
                        {
                            if(typeAssistDAO.getEventTypeCode(Long.valueOf(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)))).equalsIgnoreCase("NUMERIC"))
                            {
                                if(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER))!=null && !c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)).equalsIgnoreCase("null"))
                                {
                                    jobNeedDetails.setAnswer(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)));
                                }
                                else
                                {
                                    jobNeedDetails.setAnswer("0.0");
                                }
                            }
                            else
                                jobNeedDetails.setAnswer(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)));
                        }
                        else
                        {
                            jobNeedDetails.setAnswer("-1");
                        }

                        jobNeedDetails.setJndid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ID)));
                        jobNeedDetails.setJobneedid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID)));
                        jobNeedDetails.setMin(c.getDouble(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MIN)));
                        jobNeedDetails.setType(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)));
                        jobNeedDetails.setCuser(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CUSER)));
                        jobNeedDetails.setMuser(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MUSER)));
                        jobNeedDetails.setCdtz(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CDTZ)));
                        jobNeedDetails.setMdtz(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MDTZ)));
                        jobNeedDetails.setIsmandatory(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ISMANATORY)));
                        //jobNeedDetails.setType(5018135086302501l);//numeric
                        jobNeedDetailsArrayList.add(jobNeedDetails);
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
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return jobNeedDetailsArrayList;
    }

    public ArrayList<JobNeedDetails> getIrReportchildList(long jobneedid)
    {

        System.out.println("pid==="+jobneedid);
        ArrayList<JobNeedDetails> jobNeedDetailsArrayList=new ArrayList<JobNeedDetails>();
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + JOBNeedDetails_Table.TABLE_NAME+" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID+"= '"+jobneedid+"'"+" order by "+JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO +" ASC" ,null);
            System.out.println("Select * from " + JOBNeedDetails_Table.TABLE_NAME+" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_ID+"= '"+jobneedid+"'"+" order by "+JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO +" ASC");

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        System.out.println("JObneedID: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID)));
                        System.out.println("ANS: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)));
                        System.out.println("SEQ NO: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO)));
                        System.out.println("JOBneeddetails id: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ID)));
                        System.out.println("Quest ID: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID)));
                        System.out.println("Quest Type: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)));
                        System.out.println("Quest Type Name: "+typeAssistDAO.getEventTypeName(Long.valueOf(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)))));
                        if(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID))!=-1 || c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID))!=0) {
                            JobNeedDetails jobNeedDetails = new JobNeedDetails();
                            jobNeedDetails.setSeqno(c.getInt(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO)));
                            jobNeedDetails.setQuestionid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID)));
                            jobNeedDetails.setOption(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_OPTION)));
                            jobNeedDetails.setMax(c.getDouble(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MAX)));
                            jobNeedDetails.setAlerton(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ALERTON)));
                            if(Long.valueOf(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)))!=-1)
                            {
                                if(typeAssistDAO.getEventTypeCode(Long.valueOf(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)))).equalsIgnoreCase("NUMERIC"))
                                {
                                    if(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER))!=null && !c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)).equalsIgnoreCase("null"))
                                    {
                                        jobNeedDetails.setAnswer(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)));
                                        //jobNeedDetails.setAnswer("String");
                                    }
                                    else
                                    {
                                        jobNeedDetails.setAnswer("0.0");
                                    }
                                }
                                else
                                    jobNeedDetails.setAnswer(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)));
                            }
                            else
                            {
                                jobNeedDetails.setAnswer("-1");
                            }

                            jobNeedDetails.setJndid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ID)));
                            jobNeedDetails.setJobneedid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID)));
                            jobNeedDetails.setMin(c.getDouble(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MIN)));
                            jobNeedDetails.setType(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)));
                            jobNeedDetails.setCuser(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CUSER)));
                            jobNeedDetails.setMuser(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MUSER)));
                            jobNeedDetails.setCdtz(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CDTZ)));
                            jobNeedDetails.setMdtz(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MDTZ)));
                            jobNeedDetails.setIsmandatory(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ISMANATORY)));
                            //jobNeedDetails.setType(5018135086302501l);//numeric

                            /*String where="jobneedid = ?";
                            String[] args=new String[]{String.valueOf(jobneedid)};
                            db.delete(JOBNeedDetails_Table.TABLE_NAME,where,args);*/

                            jobNeedDetailsArrayList.add(jobNeedDetails);
                        }
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
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }

        System.out.println("jobNeedDetailsArrayList=="+jobNeedDetailsArrayList);
        return jobNeedDetailsArrayList;
    }

    public ArrayList<JobNeedDetails> getJobNeedDetailQuestList(long jobneedid)
    {
        ArrayList<JobNeedDetails> jobNeedDetailsArrayList=new ArrayList<JobNeedDetails>();
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + JOBNeedDetails_Table.TABLE_NAME+" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID+"= '"+jobneedid+"'"+" order by "+JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO +" ASC" ,null);
            System.out.println("Select * from " + JOBNeedDetails_Table.TABLE_NAME+" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID+"= '"+jobneedid+"'"+" order by "+JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO +" ASC");

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        System.out.println("JObneedID: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID)));
                        System.out.println("ANS: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)));
                        System.out.println("SEQ NO: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO)));
                        System.out.println("JOBneeddetails id: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ID)));
                        System.out.println("Quest ID: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID)));
                        System.out.println("Quest Type: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)));
                        System.out.println("Quest Type Name: "+typeAssistDAO.getEventTypeName(Long.valueOf(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)))));
                        if(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID))!=-1 || c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID))!=0) {
                            JobNeedDetails jobNeedDetails = new JobNeedDetails();
                            jobNeedDetails.setSeqno(c.getInt(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO)));
                            jobNeedDetails.setQuestionid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID)));
                            jobNeedDetails.setOption(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_OPTION)));
                            jobNeedDetails.setMax(c.getDouble(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MAX)));
                            jobNeedDetails.setAlerton(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ALERTON)));
                            if(Long.valueOf(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)))!=-1)
                            {
                                if(typeAssistDAO.getEventTypeCode(Long.valueOf(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)))).equalsIgnoreCase("NUMERIC"))
                                {
                                    if(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER))!=null && !c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)).equalsIgnoreCase("null"))
                                    {
                                        jobNeedDetails.setAnswer(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)));
                                        //jobNeedDetails.setAnswer("String");
                                    }
                                    else
                                    {
                                        jobNeedDetails.setAnswer("0.0");
                                    }
                                }
                                else
                                    jobNeedDetails.setAnswer(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)));
                            }
                            else
                            {
                                jobNeedDetails.setAnswer("-1");
                            }

                            jobNeedDetails.setJndid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ID)));
                            jobNeedDetails.setJobneedid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID)));
                            jobNeedDetails.setMin(c.getDouble(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MIN)));
                            jobNeedDetails.setType(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)));
                            jobNeedDetails.setCuser(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CUSER)));
                            jobNeedDetails.setMuser(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MUSER)));
                            jobNeedDetails.setCdtz(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CDTZ)));
                            jobNeedDetails.setMdtz(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MDTZ)));
                            jobNeedDetails.setIsmandatory(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ISMANATORY)));
                            //jobNeedDetails.setType(5018135086302501l);//numeric

                            /*String where="jobneedid = ?";
                            String[] args=new String[]{String.valueOf(jobneedid)};
                            db.delete(JOBNeedDetails_Table.TABLE_NAME,where,args);*/

                            jobNeedDetailsArrayList.add(jobNeedDetails);
                        }
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
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }

        System.out.println("jobNeedDetailsArrayList=="+jobNeedDetailsArrayList);
        return jobNeedDetailsArrayList;
    }

    public int getChkPointQuestionCount(long jobneedid)
    {
        int recCount=0;
        try {
            db = sqlopenHelper.getReadableDatabase();

            String query="Select * from " + JOBNeedDetails_Table.TABLE_NAME+" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID+"= '"+jobneedid+"'"+" order by "+JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO +" ASC";

            System.out.println("query: "+query);

            c = db.rawQuery(query ,null);
            if(c!=null)
            {
                System.out.println("questions count: "+c.getCount());
                recCount= c.getCount();
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
        return recCount;
    }

    public ArrayList<QuestionAnswerTransaction> getJNDQuestListForReading(long jobneedid, long buid, String pFolder, String pActivity)
    {
        ArrayList<QuestionAnswerTransaction> jobNeedDetailsArrayList=new ArrayList<QuestionAnswerTransaction>();
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + JOBNeedDetails_Table.TABLE_NAME+" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID+"= '"+jobneedid+"'"+" order by "+JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO +" ASC" ,null);
            System.out.println("Select * from " + JOBNeedDetails_Table.TABLE_NAME+" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID+"= '"+jobneedid+"'"+" order by "+JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO +" ASC");

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        System.out.println("JObneedID: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID)));
                        System.out.println("ANS: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)));
                        System.out.println("SEQ NO: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO)));
                        System.out.println("JOBneeddetails id: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ID)));
                        System.out.println("Quest ID: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID)));
                        System.out.println("Quest Type: "+c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)));
                        if(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID))!=-1 || c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID))!=0) {
                            QuestionAnswerTransaction jobNeedDetails = new QuestionAnswerTransaction();
                            jobNeedDetails.setSeqno(c.getInt(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO)));
                            jobNeedDetails.setQuestionid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID)));
                            jobNeedDetails.setOptions(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_OPTION)));
                            jobNeedDetails.setMax(c.getDouble(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MAX)));
                            jobNeedDetails.setAlerton(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ALERTON)));
                            jobNeedDetails.setQuestAnswer(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER)));
                            jobNeedDetails.setJndid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ID)));
                            jobNeedDetails.setJobneedid(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID)));
                            jobNeedDetails.setMin(c.getDouble(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MIN)));
                            jobNeedDetails.setType(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE)));
                            jobNeedDetails.setCuser(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CUSER)));
                            jobNeedDetails.setMuser(c.getLong(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MUSER)));
                            jobNeedDetails.setCdtz(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CDTZ)));
                            jobNeedDetails.setMdtz(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MDTZ)));
                            jobNeedDetails.setIsmandatory(c.getString(c.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ISMANATORY)));
                            jobNeedDetails.setBuid(buid);
                            jobNeedDetails.setParentActivity(pActivity.toLowerCase(Locale.ENGLISH));
                            jobNeedDetails.setParentFolder(pFolder.toLowerCase(Locale.ENGLISH));
                            jobNeedDetails.setCorrect(true);
                            jobNeedDetailsArrayList.add(jobNeedDetails);
                        }
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
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return jobNeedDetailsArrayList;
    }

    public void deleteRec(long jnid )
    {
        try {
            db = sqlopenHelper.getReadableDatabase();
            db.execSQL("delete from "+ JOBNeedDetails_Table.TABLE_NAME+" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID+" ="+jnid);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    public int changeQuestionAns(long  jndid, String ans, long questID) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER, ans);
            System.out.println("Ans: "+ans);
            System.out.println("Jndid: "+jndid);

            return db.update(JOBNeedDetails_Table.TABLE_NAME, values, "jndid=? AND questionid=?", new String[] { String.valueOf(jndid), String.valueOf(questID) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }



    public int getCount(long jobneedid)
    {
        int cnt=0;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select count(*) from " + JOBNeedDetails_Table.TABLE_NAME +" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID+" = '"+jobneedid+"'" ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    cnt=c.getInt(0);
                    System.out.println("Count: "+c.getInt(0));
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
        return  cnt;
    }
}
