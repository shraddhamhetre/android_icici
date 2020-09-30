package com.youtility.intelliwiz20.DataAccessObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.TemplateList;
import com.youtility.intelliwiz20.Tables.SitePerformTemplateList_Table;
import com.youtility.intelliwiz20.Tables.TemplateList_Table;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * login user assigned sites data access object
 *
 */

public class SiteTemplateDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    Cursor c = null;

    public SiteTemplateDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }



    public void insertRecord(long templateid,long peopleid, long siteid, String sitename, long chkintime, long qsetid, String qsetname)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(SitePerformTemplateList_Table.TEMPLATE_ID, templateid);
            values.put(SitePerformTemplateList_Table.TEMPLATE_PEOPLE, peopleid);
            values.put(SitePerformTemplateList_Table.TEMPLATE_SITEID, siteid);
            values.put(SitePerformTemplateList_Table.TEMPLATE_SITENAME, sitename);
            values.put(SitePerformTemplateList_Table.TEMPLATE_CHECKIN_TIMESTAMP, chkintime);
            values.put(SitePerformTemplateList_Table.TEMPLATE_QSETID, qsetid);
            values.put(SitePerformTemplateList_Table.TEMPLATE_QSETNAME, qsetname);

            long val= db.insert(SitePerformTemplateList_Table.TABLE_NAME, "", values);
            System.out.println("Common Data val: "+val);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }




    public void deleteRec()
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            //db.rawQuery("delete from "+SitePerformTemplateList_Table.TABLE_NAME +"  where "+SitePerformTemplateList_Table.TEMPLATE_CHECKIN_TIMESTAMP+" = "+chkInTimestamp, null);
            db.execSQL("delete from "+SitePerformTemplateList_Table.TABLE_NAME);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {


        }
    }

    public int getCount(long qsetid)
    {
        int childCount=0;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select distinct count(*) from " + SitePerformTemplateList_Table.TABLE_NAME +" where "+SitePerformTemplateList_Table.TEMPLATE_QSETID+" = "+qsetid ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    System.out.println("getCount: "+c.getInt(0));
                    childCount=c.getInt(0);
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
        return childCount;
    }

    public int getCount1()
    {
        int childCount=0;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select count(*) from " + SitePerformTemplateList_Table.TABLE_NAME ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    System.out.println("getCount1: "+c.getInt(0));
                    childCount=c.getInt(0);
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
        return childCount;
    }

    public ArrayList<TemplateList> getTemplateList(long siteid)
    {
        ArrayList<TemplateList> templateArrayList=new ArrayList<>();
        try {
            db = sqlopenHelper.getReadableDatabase();
            String qString="Select * from " + TemplateList_Table.TABLE_NAME ;

            String str="SELECT * " +
                    "FROM Templates template " +
                    "INNER JOIN QuestionSet qset on template.questionsetid=qset.questionsetid " +
                    "INNER JOIN typeassist ta on qset.type=ta.taid " +
                    "AND ta.tacode='SITEREPORTTEMPLATE' ";


            System.out.println("qString: "+qString);
            System.out.println("str: "+str);
            c = db.rawQuery(str,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        String ss=c.getString(c.getColumnIndex(TemplateList_Table.TEMPLATE_SITES));
                        System.out.println("Assigned sites: "+ss);
                        System.out.println("Selected Site: "+siteid);
                        if(ss.contains(String.valueOf(siteid)))
                        {
                            TemplateList templateList=new TemplateList();
                            templateList.setQuestionsetid(c.getLong(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETID)));
                            templateList.setQsetname(c.getString(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETNAME)));
                            templateArrayList.add(templateList);
                            System.out.println("siteid: "+siteid+" :Name:  "+c.getString(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETNAME)));
                            System.out.println("siteid: "+siteid+" :ID:  "+c.getLong(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETID)));
                        }
                        else if(ss.contains("-1"))
                        {
                            TemplateList templateList=new TemplateList();
                            templateList.setQuestionsetid(c.getLong(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETID)));
                            templateList.setQsetname(c.getString(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETNAME)));
                            templateArrayList.add(templateList);
                            System.out.println("-1 siteid: "+siteid+" :Name:  "+c.getString(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETNAME)));
                            System.out.println("-1 siteid: "+siteid+" :ID:  "+c.getLong(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETID)));
                        }

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
        return templateArrayList;
    }

    public ArrayList<TemplateList> getRequestedTemplateList(long qsetid)
    {
        ArrayList<TemplateList> templateArrayList=new ArrayList<>();
        try {
            db = sqlopenHelper.getReadableDatabase();
            String qString="Select * from " + TemplateList_Table.TABLE_NAME ;
            System.out.println("qString: "+qString);
            c = db.rawQuery(qString,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        String ss=c.getString(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETID));

                        if(ss.contains(String.valueOf(qsetid)))
                        {
                            TemplateList templateList=new TemplateList();
                            templateList.setQuestionsetid(c.getLong(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETID)));
                            templateList.setQsetname(c.getString(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETNAME)));
                            templateArrayList.add(templateList);
                        }

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
        return templateArrayList;
    }

    public ArrayList<TemplateList> getOtherSiteTemplateList()
    {
        ArrayList<TemplateList> templateArrayList=new ArrayList<>();
        try {
            db = sqlopenHelper.getReadableDatabase();
            String qString="Select * from " + TemplateList_Table.TABLE_NAME ;
            System.out.println("qString: "+qString);
            c = db.rawQuery(qString,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        TemplateList templateList=new TemplateList();
                        templateList.setQuestionsetid(c.getLong(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETID)));
                        templateList.setQsetname(c.getString(c.getColumnIndex(TemplateList_Table.TEMPLATE_QSETNAME)));
                        templateArrayList.add(templateList);

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
        return templateArrayList;
    }

}
