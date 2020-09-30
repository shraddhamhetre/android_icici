package com.youtility.intelliwiz20.DataAccessObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Tables.AssetDetail_Table;
import com.youtility.intelliwiz20.Tables.Attachment_Table;
import com.youtility.intelliwiz20.Tables.AttendanceHistoy_Table;
import com.youtility.intelliwiz20.Tables.AttendanceSheet_Table;
import com.youtility.intelliwiz20.Tables.DeviceEventLog_Table;
import com.youtility.intelliwiz20.Tables.Geofence_Table;
import com.youtility.intelliwiz20.Tables.Group_Table;
import com.youtility.intelliwiz20.Tables.JOBNeedDetails_Table;
import com.youtility.intelliwiz20.Tables.JOBNeed_Table;
import com.youtility.intelliwiz20.Tables.PeopleEventLog_Table;
import com.youtility.intelliwiz20.Tables.PeopleGroupBelongin_Table;
import com.youtility.intelliwiz20.Tables.People_Table;
import com.youtility.intelliwiz20.Tables.QuestionSetBelonging_Table;
import com.youtility.intelliwiz20.Tables.QuestionSet_Table;
import com.youtility.intelliwiz20.Tables.Question_Table;
import com.youtility.intelliwiz20.Tables.SiteList_Table;
import com.youtility.intelliwiz20.Tables.SitesVisitedLog_Table;
import com.youtility.intelliwiz20.Tables.TemplateList_Table;
import com.youtility.intelliwiz20.Tables.TypeAssist_Table;
import com.youtility.intelliwiz20.Utils.Constants;

/**
 * Created by PrashantD on 5/9/17.
 *
 * login user assigned sites data access object
 *
 */

public class CommonDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    Cursor c = null;
    Context mContext;

    public CommonDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
        mContext=context;
    }

    public void deleteAllData()
    {
        /*db.execSQL("delete from "+ AssetDetail_Table.TABLE_NAME);
        db.execSQL("delete from "+ JOBNeed_Table.TABLE_NAME);
        db.execSQL("delete from "+ JOBNeedDetails_Table.TABLE_NAME);
        db.execSQL("delete from "+ TypeAssist_Table.TABLE_NAME);
        db.execSQL("delete from "+ Geofence_Table.TABLE_NAME);
        db.execSQL("delete from "+ Attachment_Table.TABLE_NAME);
        db.execSQL("delete from "+ People_Table.TABLE_NAME);
        db.execSQL("delete from "+ Group_Table.TABLE_NAME);
        db.execSQL("delete from "+ PeopleEventLog_Table.TABLE_NAME);
        db.execSQL("delete from "+ AttendanceHistoy_Table.TABLE_NAME);
        db.execSQL("delete from "+ Question_Table.TABLE_NAME);
        db.execSQL("delete from "+ QuestionSet_Table.TABLE_NAME);
        db.execSQL("delete from "+ QuestionSetBelonging_Table.TABLE_NAME);
        db.execSQL("delete from "+ PeopleGroupBelongin_Table.TABLE_NAME);
        db.execSQL("delete from "+ DeviceEventLog_Table.TABLE_NAME);
        db.execSQL("delete from "+ Sites_Table.TABLE_NAME);
        db.execSQL("delete from "+ AttendanceSheet_Table.TABLE_NAME);
        db.execSQL("delete from "+ SitesVisitedLog_Table.TABLE_NAME);*/

        db.delete(AssetDetail_Table.TABLE_NAME,null,null);
        db.delete(JOBNeed_Table.TABLE_NAME,null,null);
        db.delete(JOBNeedDetails_Table.TABLE_NAME,null,null);
        db.delete(TypeAssist_Table.TABLE_NAME,null,null);
        db.delete(Geofence_Table.TABLE_NAME,null,null);
        db.delete(Attachment_Table.TABLE_NAME,null,null);
        db.delete(People_Table.TABLE_NAME,null,null);
        db.delete(Group_Table.TABLE_NAME,null,null);
        db.delete(PeopleEventLog_Table.TABLE_NAME,null,null);
        db.delete(AttendanceHistoy_Table.TABLE_NAME,null,null);
        db.delete(Question_Table.TABLE_NAME,null,null);
        db.delete(QuestionSet_Table.TABLE_NAME,null,null);
        db.delete(QuestionSetBelonging_Table.TABLE_NAME,null,null);
        db.delete(PeopleGroupBelongin_Table.TABLE_NAME,null,null);
        db.delete(DeviceEventLog_Table.TABLE_NAME,null,null);
        //db.delete(Sites_Table.TABLE_NAME,null,null);
        db.delete(SiteList_Table.TABLE_NAME,null,null);
        db.delete(AttendanceSheet_Table.TABLE_NAME,null,null);
        db.delete(SitesVisitedLog_Table.TABLE_NAME,null,null);
        db.delete(TemplateList_Table.TABLE_NAME,null,null);

    }

    public int getUnsyncDataCount()
    {
        int cnt=0;
        c = null;
        SharedPreferences syncSummaryPref=mContext.getSharedPreferences(Constants.SYNC_SUMMARY_PREF,Context.MODE_PRIVATE);
        try {
            db = sqlopenHelper.getReadableDatabase();

            /*String getCountQuery="select " +
                    "(select count(*) from peopleeventlog where syncStatus='0')+" +
                    "(select count(*) from JOBNEED where syncStatus='0' AND identifier in (select taid from TypeAssist where tacode in('TOUR','TASK','TICKET','ASSETLOG','ASSETMAINTENANCE') AND tatype in('Job Identifier')))+"+
                    "(select count(*) from JOBNEED where syncStatus='0' AND parent=-1 AND identifier in(select taid from TypeAssist where tacode in('INCIDENTREPORT')))+"+
                    "(select count(*) from JOBNEED where syncStatus='2' AND identifier in (select taid from TypeAssist where tacode in('TOUR','TASK','TICKET','SITEREPORT','PPM') AND tatype in('Job Identifier')))+"+
                    "(select count(*) from Attachment where syncStatus='0' AND AttachmentType in(select taid from TypeAssist where tacode='REPLY'))+"+
                    "(select count(*) from PersonLogger where syncStatus='0' AND identifier in(select taid from TypeAssist where tacode in('EMPLOYEEREFERENCE') AND tatype in ('Person Logger')))+"+
                    "(select count(*) from JOBNEED where syncStatus='0' AND parent=-1 AND identifier in(select taid from TypeAssist where tacode in('SITEREPORT')))"+
                    "as sumCount";*/

            String getCountQuery="select " +
                    "(select count(*) from peopleeventlog where syncStatus='0') as peventLogCount ," +
                    "(select count(*) from JOBNEED where syncStatus='0' AND identifier in (select taid from TypeAssist where tacode in('TOUR','TASK','TICKET','ASSETLOG','ASSETMAINTENANCE') AND tatype in('Job Identifier')))+"+
                    "(select count(*) from JOBNEED where syncStatus='0' AND parent=-1 AND identifier in(select taid from TypeAssist where tacode in('INCIDENTREPORT')))+"+
                    "(select count(*) from JOBNEED where syncStatus='2' AND identifier in (select taid from TypeAssist where tacode in('TOUR','TASK','TICKET','SITEREPORT','PPM') AND tatype in('Job Identifier')))+"+
                    "(select count(*) from JOBNEED where syncStatus='0' AND parent=-1 AND identifier in(select taid from TypeAssist where tacode in('SITEREPORT'))) as jobneedCount ,"+
                    "(select count(*) from Attachment where syncStatus='0' AND AttachmentType in(select taid from TypeAssist where tacode='REPLY')) as replyCount ,"+
                    "(select count(*) from PersonLogger where syncStatus='0' AND identifier in(select taid from TypeAssist where tacode in('EMPLOYEEREFERENCE') AND tatype in ('Person Logger')))"+
                    "as empRefCount";

            c = db.rawQuery(getCountQuery ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    System.out.println("peopleeventlog Count: "+c.getInt(0));
                    System.out.println("JOBNEED Count: "+c.getInt(1));
                    System.out.println("Attachment Count: "+c.getInt(2));
                    System.out.println("PersonLogger Count: "+c.getInt(3));
                    syncSummaryPref.edit().putInt(Constants.SYNC_SUMMARY_PENDING_PEOPLEEVENTLOG_COUNT,c.getInt(0)).apply();
                    syncSummaryPref.edit().putInt(Constants.SYNC_SUMMARY_PENDING_JOBNEED_COUNT,c.getInt(1)).apply();
                    syncSummaryPref.edit().putInt(Constants.SYNC_SUMMARY_PENDING_REPLY_COUNT,c.getInt(2)).apply();
                    syncSummaryPref.edit().putInt(Constants.SYNC_SUMMARY_PENDING_EMPREF_COUNT,c.getInt(3)).apply();
                    cnt=c.getInt(0)+c.getInt(1)+c.getInt(2)+c.getInt(3);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {

        }
        return cnt;
    }
}
