package com.youtility.intelliwiz20.DataAccessObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.SiteList;
import com.youtility.intelliwiz20.Model.Sites;
import com.youtility.intelliwiz20.Tables.SiteList_Table;
import com.youtility.intelliwiz20.Tables.Sites_Table;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * login user assigned sites data access object
 *
 */

public class SiteDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    Cursor c = null;

    public SiteDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }




    public Sites getSiteInfo(long siteId)
    {
        Sites sites=null;

        try {
            db= sqlopenHelper.getReadableDatabase();
            c=db.rawQuery("select * from "+ Sites_Table.TABLE_NAME+" where "+Sites_Table.SITE_PEOPLE_BUILD+" = "+siteId,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    sites=new Sites();
                    sites.setSitepeopleid(c.getLong(c.getColumnIndex(Sites_Table.SITE_PEOPLE_ID)));
                    sites.setBuname(c.getString(c.getColumnIndex(Sites_Table.BU_NAME)));
                    sites.setBucode(c.getString(c.getColumnIndex(Sites_Table.BU_CODE)));
                    sites.setReportto(c.getLong(c.getColumnIndex(Sites_Table.SITE_PEOPLE_REPORTTO)));
                    sites.setBuid(c.getLong(c.getColumnIndex(Sites_Table.SITE_PEOPLE_BUILD)));
                    sites.setReportids(c.getString(c.getColumnIndex(Sites_Table.SITE_REPORT_ID)));
                    sites.setReportnames(c.getString(c.getColumnIndex(Sites_Table.SITE_REPORT_NAME)));
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return sites;
    }


    public ArrayList<Sites> getSiteList()
    {
        ArrayList<Sites> sitesArrayList=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            //c = db.rawQuery("Select * from " + Sites_Table.TABLE_NAME +" order by "+Sites_Table.SITE_PEOPLE_SLNO+" ASC",null);
            c = db.rawQuery("Select * from " + Sites_Table.TABLE_NAME +" where "+ Sites_Table.SITE_ENABLE+" = 'True' order by "+Sites_Table.BU_NAME+" ASC",null);
            //c = db.rawQuery("Select * from " + Sites_Table.TABLE_NAME +" order by "+Sites_Table.BU_NAME+" ASC",null);
            sitesArrayList=new ArrayList<>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        if(c.getLong(c.getColumnIndex(Sites_Table.SITE_PEOPLE_ID))!=-1 && !c.getString(c.getColumnIndex(Sites_Table.BU_NAME)).equalsIgnoreCase("NONE")) {
                            Sites sites=new Sites();
                            sites.setSitepeopleid(c.getLong(c.getColumnIndex(Sites_Table.SITE_PEOPLE_ID)));
                            sites.setBuname(c.getString(c.getColumnIndex(Sites_Table.BU_NAME)));
                            sites.setBucode(c.getString(c.getColumnIndex(Sites_Table.BU_CODE)));
                            sites.setReportto(c.getLong(c.getColumnIndex(Sites_Table.SITE_PEOPLE_REPORTTO)));
                            sites.setBuid(c.getLong(c.getColumnIndex(Sites_Table.SITE_PEOPLE_BUILD)));
                            sites.setReportids(c.getString(c.getColumnIndex(Sites_Table.SITE_REPORT_ID)));
                            sites.setReportnames(c.getString(c.getColumnIndex(Sites_Table.SITE_REPORT_NAME)));
                            sitesArrayList.add(sites);
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
        return sitesArrayList;
    }


    public ArrayList<SiteList> getSiteList1()
    {
        ArrayList<SiteList> sitesArrayList=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            //c = db.rawQuery("Select * from " + Sites_Table.TABLE_NAME +" order by "+Sites_Table.SITE_PEOPLE_SLNO+" ASC",null);
            c = db.rawQuery("Select * from " + SiteList_Table.TABLE_NAME +" where "+ SiteList_Table.SITE_ENABLE+" = 'True' order by "+SiteList_Table.BU_NAME+" ASC",null);
            //c = db.rawQuery("Select * from " + Sites_Table.TABLE_NAME +" order by "+Sites_Table.BU_NAME+" ASC",null);
            sitesArrayList=new ArrayList<>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        SiteList sites=new SiteList();
                        sites.setBuname(c.getString(c.getColumnIndex(SiteList_Table.BU_NAME)));
                        sites.setBucode(c.getString(c.getColumnIndex(SiteList_Table.BU_CODE)));
                        sites.setIncharge(c.getString(c.getColumnIndex(SiteList_Table.SITE_INCHARGE)));
                        sites.setBuid(c.getLong(c.getColumnIndex(SiteList_Table.SITE_PEOPLE_BUILD)));
                        sitesArrayList.add(sites);
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

    public SiteList getSiteName(long siteId)
    {
        SiteList sites=null;

        try {
            db= sqlopenHelper.getReadableDatabase();
            c=db.rawQuery("select * from "+SiteList_Table.TABLE_NAME+" where "+SiteList_Table.SITE_PEOPLE_BUILD+" = "+siteId,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    sites=new SiteList();
                    sites.setBuname(c.getString(c.getColumnIndex(Sites_Table.BU_NAME)));
                    sites.setBucode(c.getString(c.getColumnIndex(Sites_Table.BU_CODE)));
                    sites.setBuid(c.getLong(c.getColumnIndex(Sites_Table.SITE_PEOPLE_BUILD)));
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return sites;
    }


}
