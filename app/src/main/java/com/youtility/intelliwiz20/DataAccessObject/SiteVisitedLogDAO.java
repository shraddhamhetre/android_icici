package com.youtility.intelliwiz20.DataAccessObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.SiteVisitLogGroup;
import com.youtility.intelliwiz20.Model.SiteVisitedLog;
import com.youtility.intelliwiz20.Tables.PeopleEventLog_Table;
import com.youtility.intelliwiz20.Tables.SiteList_Table;
import com.youtility.intelliwiz20.Tables.SitesVisitedLog_Table;
import com.youtility.intelliwiz20.Tables.TypeAssist_Table;
import com.youtility.intelliwiz20.Utils.CommonFunctions;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * login user assigned sites data access object
 *
 */

public class SiteVisitedLogDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    Cursor c = null;

    public SiteVisitedLogDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }



    public int getCount()
    {
        db = sqlopenHelper.getReadableDatabase();
        c = db.rawQuery("Select Count(*) from " + SitesVisitedLog_Table.TABLE_NAME ,null);
        if(c!=null)
        {
            if(c.moveToFirst())
            {
                return c.getInt(0);
            }
        }
        return  0;
    }

    public ArrayList<SiteVisitLogGroup> getDistinctname()
    {
        ArrayList<SiteVisitLogGroup> siteVisitLogGroupArrayList=new ArrayList<>();
        ArrayList<SiteVisitedLog> sitesArrayList=null;
        Cursor cc=null;
        db = sqlopenHelper.getReadableDatabase();
        c = db.rawQuery("Select DISTINCT buname  from " + SitesVisitedLog_Table.TABLE_NAME ,null);
        if(c!=null)
        {
            if(c.moveToFirst())
            {
                do {
                    SiteVisitLogGroup siteVisitLogGroup=new SiteVisitLogGroup();
                    System.out.println("Site name: "+c.getString(c.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUNAME)));
                    siteVisitLogGroup.setSiteName(c.getString(c.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUNAME)));
                    String ss=c.getString(c.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUNAME));
                    cc=db.rawQuery("select * from "+SitesVisitedLog_Table.TABLE_NAME +" Where "+SitesVisitedLog_Table.SITEVISITEDLOG_BUNAME+" = '"+ss+"' order by strftime('%s' ,"+ SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHTIME +") DESC ",null);
                    if(cc!=null)
                    {
                        sitesArrayList=new ArrayList<>();
                        if(cc.moveToFirst())
                        {
                            do {
                                SiteVisitedLog siteVisitedLog=new SiteVisitedLog();
                                siteVisitedLog.setBucode(cc.getString(cc.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUCODE)));
                                siteVisitedLog.setBuid(cc.getLong(cc.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUID)));
                                siteVisitedLog.setBuname(cc.getString(cc.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUNAME)));
                                siteVisitedLog.setPunchstatus(cc.getString(cc.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHSTATUS)));
                                siteVisitedLog.setPunchtime(cc.getString(cc.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHTIME)));
                                siteVisitedLog.setRemarks(cc.getString(cc.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_OTHERSITE)));
                                System.out.println("Site pstatus: "+cc.getString(cc.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHSTATUS)));
                                System.out.println("Site ptime: "+cc.getString(cc.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHTIME)));
                                sitesArrayList.add(siteVisitedLog);
                            }while (cc.moveToNext());
                        }
                    }
                    siteVisitLogGroup.setSiteVisitedLogArrayList(sitesArrayList);
                    siteVisitLogGroupArrayList.add(siteVisitLogGroup);
                }while (c.moveToNext());
            }
        }
        return siteVisitLogGroupArrayList;
    }

    public void deletRecords()
    {
        db = sqlopenHelper.getReadableDatabase();
        db.execSQL("delete from "+ SitesVisitedLog_Table.TABLE_NAME);
        //c = db.rawQuery("delete from " + SitesVisitedLog_Table.TABLE_NAME ,null);
    }

    public ArrayList<SiteVisitedLog> getSiteVisitLog()
    {
        ArrayList<SiteVisitedLog> sitesArrayList=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + SitesVisitedLog_Table.TABLE_NAME +" order by strftime('%s' ,"+ SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHTIME +") DESC ",null);
            sitesArrayList=new ArrayList<>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        SiteVisitedLog siteVisitedLog=new SiteVisitedLog();

                        System.out.println("Site name: "+c.getString(c.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUNAME)));

                        siteVisitedLog.setBucode(c.getString(c.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUCODE)));
                        siteVisitedLog.setBuid(c.getLong(c.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUID)));
                        siteVisitedLog.setBuname(c.getString(c.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUNAME)));
                        siteVisitedLog.setPunchstatus(c.getString(c.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHSTATUS)));
                        siteVisitedLog.setPunchtime(c.getString(c.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHTIME)));
                        siteVisitedLog.setRemarks(c.getString(c.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_OTHERSITE)));
                        sitesArrayList.add(siteVisitedLog);
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
        return sitesArrayList;
    }


    public ArrayList<SiteVisitedLog> getSiteVisitLogFromDB(long pEventType)
    {
        ArrayList<SiteVisitedLog> sitesArrayList=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            /*String qry="Select DISTINCT PeopleEventLog.*, s.buname, ta.tacode from " + PeopleEventLog_Table.TABLE_NAME +
                    " LEFT JOIN Sites s ON PeopleEventLog.buid=s.buid"+
                    " LEFT JOIN TypeAssist ta ON PeopleEventLog.punchstatus=ta.taid"+
                    " where "+PeopleEventLog_Table.PE_TYPE+" = "+pEventType+
                    " order by strftime('%s' ,"+ PeopleEventLog_Table.PE_DATETIME +") DESC ";*/
            String qry="Select DISTINCT PeopleEventLog.*, s.buname, ta.tacode from " + PeopleEventLog_Table.TABLE_NAME +
                    " LEFT JOIN Sites s ON PeopleEventLog.buid=s.buid"+
                    " LEFT JOIN TypeAssist ta ON PeopleEventLog.punchstatus=ta.taid"+
                    " where "+PeopleEventLog_Table.PE_TYPE+" = "+pEventType+
                    " order by strftime('%s' ,"+ PeopleEventLog_Table.PE_DATETIME +") ASC ";
            System.out.println("getSiteVisitLogFromDB query: "+qry);
            c=db.rawQuery(qry,null);

            /*c = db.rawQuery("Select * from " + PeopleEventLog_Table.TABLE_NAME +
                            " where "+PeopleEventLog_Table.PE_TYPE+" = "+pEventType+
                            " order by strftime('%s' ,"+ PeopleEventLog_Table.PE_DATETIME +") DESC ",null);*/
            sitesArrayList=new ArrayList<>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        SiteVisitedLog siteVisitedLog=new SiteVisitedLog();
                        siteVisitedLog.setBucode(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_REMARKS)));
                        siteVisitedLog.setBuid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_BUID)));
                        siteVisitedLog.setBuname(c.getString(c.getColumnIndex(SiteList_Table.BU_NAME)));
                        //siteVisitedLog.setBuname(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_OTHERLOCATION)));
                        siteVisitedLog.setPunchstatus(c.getString(c.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_CODE)));
                        siteVisitedLog.setPunchtime(CommonFunctions.getTimezoneDate(Long.valueOf(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_DATETIME)))));
                        siteVisitedLog.setRemarks(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_OTHERLOCATION)));
                       // siteVisitedLog.setOtherlocation(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_OTHERLOCATION)));

                        sitesArrayList.add(siteVisitedLog);
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
        return sitesArrayList;
    }


    /*public ArrayList<SiteVisitedLog> getSiteNotVisitLog()
    {
        ArrayList<SiteVisitedLog> sitesArrayList=null;
        String query="Select * from " + Sites_Table.TABLE_NAME +" where "+Sites_Table.BU_NAME +" not in( select DISTINCT buname from SiteVisitedLog ) order by "+Sites_Table.SITE_PEOPLE_SLNO+" ASC";
        System.out.println("SiteNotvisited query: "+query);
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery(query,null);
            sitesArrayList=new ArrayList<SiteVisitedLog>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        SiteVisitedLog siteVisitedLog=new SiteVisitedLog();
                        siteVisitedLog.setBuname(c.getString(c.getColumnIndex(Sites_Table.BU_NAME)));
                        sitesArrayList.add(siteVisitedLog);
                    }while (c.moveToNext());


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sitesArrayList;
    }*/


    public ArrayList<SiteVisitedLog> getSiteNotVisitLog()
    {
        ArrayList<SiteVisitedLog> sitesArrayList=null;
        String query="Select * from " + SiteList_Table.TABLE_NAME +" where "+SiteList_Table.BU_NAME +" not in( select DISTINCT buname from SiteVisitedLog ) order by "+SiteList_Table.BU_NAME+" ASC";
        System.out.println("SiteNotvisited query: "+query);
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery(query,null);
            sitesArrayList=new ArrayList<>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        SiteVisitedLog siteVisitedLog=new SiteVisitedLog();
                        siteVisitedLog.setBuname(c.getString(c.getColumnIndex(SiteList_Table.BU_NAME)));
                        sitesArrayList.add(siteVisitedLog);
                    }while (c.moveToNext());


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sitesArrayList;
    }

}
