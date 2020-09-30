package com.youtility.intelliwiz20.DataAccessObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.Question;
import com.youtility.intelliwiz20.Model.QuestionAnswerTransaction;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Tables.QuestionSet_Table;
import com.youtility.intelliwiz20.Tables.Question_Table;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * question master data access object
 */

public class QuestionDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private Cursor c = null;
    private Context context;
    public QuestionDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
        this.context=context;
    }

    public String getQuestionName(long questionID)
    {
        String qName=null;

        try {
            db = sqlopenHelper.getReadableDatabase();

            String query="select "+ Question_Table.QUESTION_NAME+" from "+Question_Table.TABLE_NAME+" where "+Question_Table.QUESTION_ID+" = "+questionID;

            c = db.rawQuery(query ,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        qName=c.getString(0);
                    }while (c.moveToNext());

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
        return  qName;
    }


    public ArrayList<Question> getQuestions(long qSetId)
    {
        ArrayList<Question> questionArrayList=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            /*String query="select qs.questionsetid, qs.seqno,qs.qsetname,q.questionid,q.questionname,qsb.min,qsb.max,q.type,qsb.option,q.unit,q.cuser,q.cdtz,q.muser,q.mdtz,qsb.seqno, qsb.alerton " +
                    "from questionset qs " +
                    "inner join qsetbelonging qsb on qsb.questionsetid= qs.questionsetid " +
                    "inner join question q on qsb.questionid = q.questionid " +
                    "where qs.questionsetid= '"+qSetId+"' order by qsb.seqno ASC";*/

            //0 qs.questionsetid,1 q.questionname,2 q.questionid,3 q.type,4 q.unit,5 q.cuser,6 q.cdtz,7 q.muser,8 q.mdtz, 9 qs.assetid, 10 qsb.min,11 qsb.max,12  qsb.option, 13 qsb.seqno, 14 qsb.alerton , 15 qsb.ismandatory

            String query=context.getString(R.string.get_question_query, qSetId);
            System.out.println("getQuestion query: "+query);
            c = db.rawQuery(query ,null);
            questionArrayList=new ArrayList<Question>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        Question question=new Question();

                        question.setQuestionname(c.getString(1));
                        question.setQuestionid(c.getLong(2));
                        question.setType(c.getLong(3));
                        question.setUnit(c.getLong(4));
                        question.setCuser(c.getLong(5));
                        question.setCdtz(c.getString(6));
                        question.setMuser(c.getLong(7));
                        question.setMdtz(c.getString(8));
                        question.setMin(c.getDouble(10));
                        question.setMax(c.getDouble(11));
                        question.setOptions(c.getString(12));
                        question.setSeqno(c.getInt(13));
                        question.setAlertOn(c.getString(14));
                        question.setIsmandatory(c.getString(15));
                        question.setAlertOn(c.getString(14));

                        System.out.println("--SeqNo: "+c.getInt(13));
                        System.out.println("--IsMandetory: "+c.getString(15));
                        System.out.println("--alerton question: "+c.getString(16));
                        System.out.println("--alerton qbs: "+c.getString(14));
                        System.out.println("--QuestName: "+c.getString(1));
                        questionArrayList.add(question);
                    }while (c.moveToNext());


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
        return questionArrayList;
    }

    public int getChkPointQuestionsCount(long qSetID)
    {
        int cnt=0;
        try {
            db = sqlopenHelper.getReadableDatabase();

            String query=context.getString(R.string.get_question_count_query, qSetID);

            System.out.println("query: "+query);

            c = db.rawQuery(query ,null);
            if(c!=null)
            {
                if(c!=null)
                {
                    if(c.moveToFirst())
                    {
                        cnt=c.getInt(0);
                    }
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
        return cnt;
    }

    public ArrayList<QuestionAnswerTransaction> getChkPointQuestions(long qSetID, long timestamp, long buid, String pFolder, String pActivity)
    {
        ArrayList<QuestionAnswerTransaction> questionArrayList=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            /*String query="select qs.questionsetid, qs.seqno,q.questionname,q.questionid,q.min,q.max,q.type,q.options,q.unit,q.cuser,q.cdtz,q.muser,q.mdtz, qs.assetid, qsb.min, qsb.max, qsb.option, qsb.seqno, qsb.alerton " +
                    "from questionset qs " +
                    "inner join qsetbelonging qsb on qsb.questionsetid= qs.questionsetid " +
                    "inner join question q on qsb.questionid = q.questionid " +
                    "where qs.questionsetid= '"+qSetID+"'"+
                    " order by qsb.seqno asc";*/


            //qs.questionsetid,q.questionname,q.questionid,q.type,q.unit,q.cuser,q.cdtz,q.muser,q.mdtz, qs.assetid, qsb.min, qsb.max, qsb.option, qsb.seqno, qsb.alerton
            //qs.questionsetid,q.questionname,q.questionid,q.type,q.unit,q.cuser,q.cdtz,q.muser,q.mdtz, qs.assetid, qsb.min, qsb.max, qsb.option, qsb.seqno, qsb.alerton, qsb.ismandatory
            String query=context.getString(R.string.get_question_query, qSetID);
            System.out.println("query: "+query);
            c = db.rawQuery(query ,null);
            questionArrayList=new ArrayList<QuestionAnswerTransaction>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {

                        QuestionAnswerTransaction question=new QuestionAnswerTransaction();
                        question.setQuestAnsTransId(timestamp);
                        question.setQsetID(c.getLong(0));//questionsetid
                        question.setQuestionname(c.getString(1));//question name
                        question.setQuestionid(c.getLong(2));//question id
                        question.setType(c.getLong(3));//type
                        question.setUnit(c.getLong(4));//unit
                        question.setCuser(c.getLong(5));
                        question.setCdtz(c.getString(6));
                        question.setMuser(c.getLong(7));
                        question.setMdtz(c.getString(8));
                        question.setAssetID(c.getLong(9));
                        question.setMin(c.getDouble(10));//qsb min
                        question.setMax(c.getDouble(11));//qsb max
                        question.setOptions(c.getString(12));//qsb options
                        question.setSeqno(c.getInt(13));//seqno from qsb
                        question.setAlerton(c.getString(14));//alert on frm qsb
                        question.setIsmandatory(c.getString(15));//ismanatory
                        question.setQuestAnswer("");
                        question.setImagePath("");
                        question.setBuid(buid);
                        question.setParentFolder(pFolder);
                        question.setParentActivity(pActivity);
                        question.setCorrect(true);


                        questionArrayList.add(question);
                    }while (c.moveToNext());


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
        return questionArrayList;
    }

    public ArrayList<QuestionSet> getRequestQuestionSet(long reqType)
    {
        ArrayList<QuestionSet> requestTemplateList=null;
        try
        {
            db =  sqlopenHelper.getReadableDatabase();

            String query1="SELECT questionsetid, qsetname,seqno,type from questionset  WHERE parent=-1 AND type="+reqType;

            String query="select qs.questionsetid,qs.qsetname,qs.seqno,qs.type, t.questionsetid " +
                    "from questionset qs inner join Templates t on t.questionsetid= qs.questionsetid " +
                    "where qs.type="+reqType+" order by qs.seqno ASC limit 1";

            System.out.println("reqTemplate: "+query);
            c = db.rawQuery(query ,null);
            if(c!=null)
            {
                requestTemplateList=new ArrayList<>();
                if(c.moveToFirst())
                {
                    do {
                        QuestionSet questionSet = new QuestionSet();
                        questionSet.setQuestionsetid(c.getLong(0));
                        questionSet.setQsetname(c.getString(1));
                        questionSet.setSeqno(c.getInt(2));
                        questionSet.setType(c.getLong(3));
                        requestTemplateList.add(questionSet);
                    }while (c.moveToNext());
                }
            }
        }
        catch (Exception e)
        {

        }

        return requestTemplateList;
    }


    public ArrayList<QuestionSet> getWorkFlowQuestionSet()
    {
        ArrayList<QuestionSet> workflowTemplateList=null;
        try
        {
            db =  sqlopenHelper.getReadableDatabase();
            String query="SELECT qs.questionsetid, qs.qsetname,qs.seqno,qs.url, ta.taname" +
                    " FROM questionset qs" +
                    " INNER JOIN typeassist ta ON ta.taid=qs.type AND ta.tacode='WORKFLOW' WHERE qs.parent=-1";

            System.out.println("workflowurl: "+query);

            c = db.rawQuery(query ,null);
            if(c!=null)
            {
                workflowTemplateList=new ArrayList<>();
                if(c.moveToFirst())
                {
                    do {
                        if(c.getString(3)!=null && !c.getString(3).equalsIgnoreCase("null")) {
                            QuestionSet questionSet = new QuestionSet();
                            questionSet.setQuestionsetid(c.getLong(0));
                            questionSet.setQsetname(c.getString(1));
                            questionSet.setSeqno(c.getInt(2));
                            questionSet.setUrl(c.getString(3));
                            workflowTemplateList.add(questionSet);
                        }
                    }while (c.moveToNext());
                }
            }
        }
        catch (Exception e)
        {

        }

        return workflowTemplateList;
    }


    public ArrayList<QuestionSet> getQuestionSetCodeListFromTemplate(String templateQuery)
    {
        ArrayList<QuestionSet> questionSetList=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            /*String query="SELECT qs.questionsetid, qs.qsetname,qs.seqno, ta.taname" +
                    " FROM questionset qs" +
                    " INNER JOIN typeassist ta ON ta.taid=qs.type AND ta.tacode='INCIDENTREPORTTEMPLATE' WHERE qs.parent=-1";*/

            String query=templateQuery;//context.getResources().getString(R.string.get_questionsetcode_query);

            System.out.println("get qset list query: "+query);
            c = db.rawQuery(query ,null);

            if(c!=null)
            {
                questionSetList=new ArrayList<QuestionSet>();
                if(c.moveToFirst())
                {
                    do {
                        QuestionSet questionSet=new QuestionSet();
                        questionSet.setQuestionsetid(c.getLong(0));
                        questionSet.setQsetname(c.getString(1));
                        questionSet.setSeqno(c.getInt(2));

                        System.out.println("Qset Id: "+questionSet.getQuestionsetid());
                        System.out.println("Qset Name: "+questionSet.getQsetname());

                        questionSetList.add(questionSet);
                    }while (c.moveToNext());


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
        return questionSetList;
    }

    public ArrayList<QuestionSet> getQuestionSetCodeList(String assetCode)
    {
        ArrayList<QuestionSet> questionSetList=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            String query="select * from "+ QuestionSet_Table.TABLE_NAME +" where "+QuestionSet_Table.QUESTION_SET_ASSETID +" in (select assetid from AssetDetails where assetcode = '"+assetCode+"')";

            c = db.rawQuery(query ,null);
            questionSetList=new ArrayList<QuestionSet>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        QuestionSet questionSet=new QuestionSet();
                        questionSet.setQuestionsetid(c.getLong(c.getColumnIndex(QuestionSet_Table.QUESTION_SET_ID)));
                        questionSet.setQsetname(c.getString(c.getColumnIndex(QuestionSet_Table.QUESTION_SET_NAME)));
                        questionSetList.add(questionSet);
                    }while (c.moveToNext());


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
        return questionSetList;
    }

    public ArrayList<QuestionSet> getQuestionSetCodeList(long assetID)
    {
        ArrayList<QuestionSet> questionSetList=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            String query="select * from "+ QuestionSet_Table.TABLE_NAME +" where "+QuestionSet_Table.QUESTION_SET_ASSETID +"="+assetID;

            c = db.rawQuery(query ,null);
            questionSetList=new ArrayList<QuestionSet>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        QuestionSet questionSet=new QuestionSet();
                        questionSet.setQuestionsetid(c.getLong(c.getColumnIndex(QuestionSet_Table.QUESTION_SET_ID)));
                        questionSet.setQsetname(c.getString(c.getColumnIndex(QuestionSet_Table.QUESTION_SET_NAME)));
                        questionSetList.add(questionSet);
                    }while (c.moveToNext());


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
        return questionSetList;
    }


    public String getQuestionSetName(long questionsetID)
    {
        String qSetname=null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            /*String query="SELECT qsetname"+
                    " FROM questionset" +
                    " WHERE questionsetid="+questionsetID;*/

            String query=context.getResources().getString(R.string.get_questionsetname_query, questionsetID);
            System.out.println("get qset name query: "+query);
            c = db.rawQuery(query ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        qSetname=c.getString(0);
                    }while (c.moveToNext());


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
        return qSetname;
    }



    public ArrayList<QuestionSet> getQuestionSubSetCodeList(long parentid)
    {
        ArrayList<QuestionSet> questionSetList=null;
        try {
            db = sqlopenHelper.getReadableDatabase();

            //String query="SELECT distinct qs.questionsetid, qs.qsetname,qs.seqno, qsb.questionsetid FROM questionset qs INNER JOIN qsetbelonging qsb ON qsb.questionsetid=qs.questionsetid WHERE qs.parent="+parentid +" order by qs.seqno";
            String query=context.getResources().getString(R.string.get_question_subsetcode_query, parentid);
            System.out.println("get qset sub set list query: "+query);
            c = db.rawQuery(query ,null);

            if(c!=null)
            {
                questionSetList=new ArrayList<QuestionSet>();
                if(c.moveToFirst())
                {
                    do {
                        QuestionSet questionSet=new QuestionSet();
                        questionSet.setQuestionsetid(c.getLong(0));
                        questionSet.setQsetname(c.getString(1));
                        questionSet.setSeqno(c.getInt(2));

                        System.out.println("Qset Id: "+questionSet.getQuestionsetid());
                        System.out.println("Qset Name: "+questionSet.getQsetname());
                        System.out.println("Qset Seq: "+questionSet.getSeqno());

                        questionSetList.add(questionSet);
                    }while (c.moveToNext());


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
        return questionSetList;
    }
}
//SELECT qs.questionsetid, qs.qsetname,qs.seqno, qsb.questionsetid FROM questionset qs INNER JOIN qsetbelonging qsb ON qsb.questionsetid=qs.questionsetid WHERE qs.parent=5026515459605879