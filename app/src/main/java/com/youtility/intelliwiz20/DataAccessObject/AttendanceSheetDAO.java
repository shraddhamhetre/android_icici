package com.youtility.intelliwiz20.DataAccessObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.SubmitAttendance;
import com.youtility.intelliwiz20.Tables.AttendanceSheet_Table;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * data access object for area manager and/ branch manager attendance sheet
 */

public class AttendanceSheetDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private Cursor c = null;

    public AttendanceSheetDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }



    public int getCount()
    {
        int pcount=0;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select count(*) from " + AttendanceSheet_Table.TABLE_NAME+" WHERE "+AttendanceSheet_Table.ATTENDANCESHEET_APPROVAL_STATUS+" = 0 AND "+AttendanceSheet_Table.ATTENDANCESHEET_SYNC_STATUS+" = 0" ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    System.out.println("Count: "+c.getInt(0));
                    pcount=c.getInt(0);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if(c!=null)
                c=null;
        }
        return pcount;
    }


    public ArrayList<SubmitAttendance> getPeopleAttendanceList()
    {
        ArrayList<SubmitAttendance> submitAttendanceArrayList=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + AttendanceSheet_Table.TABLE_NAME +" WHERE "+AttendanceSheet_Table.ATTENDANCESHEET_APPROVAL_STATUS+" = 0 AND "
                    +AttendanceSheet_Table.ATTENDANCESHEET_SYNC_STATUS+" = 0"
                    +" order by "+AttendanceSheet_Table.ATTENDANCESHEET_PEOPLE_NAME+" ASC",null);
            submitAttendanceArrayList=new ArrayList<SubmitAttendance>();

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        if(c.getLong(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_PEOPLE_ID))!=-1) {
                            SubmitAttendance people=new SubmitAttendance();
                            people.setPeopleId(c.getLong(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_PEOPLE_ID)));
                            people.setPeopleName(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_PEOPLE_NAME)));
                            people.setMdtz(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_MDTZ)));
                            people.setCdtz(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_CDTZ)));
                            people.setMuser(c.getLong(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_MUSER)));
                            people.setCuser(c.getLong(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_CUSER)));
                            people.setAbsentOrPresent(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_ABSENT_OR_PRESNT)));
                            people.setAttendanceDates(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_DATES)));
                            people.setSyncStatus(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_SYNC_STATUS)));
                            people.setApprovalStatus(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_APPROVAL_STATUS)));
                            people.setAttendanceMonth(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_MONTH)));
                            people.setSiteid(c.getLong(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_SITEID)));
                            people.setPresentDaysCount(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_PRESENT_DAYS_COUNT)));
                            people.setWeeklyOffDays(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_WEEKLYOFF_DAYS_COUNT)));
                            people.setNationalHoliday(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_NATIONAL_HOLIDAY_COUNTS)));
                            people.setExtraDuty(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_EXTRA_DUTY_COUNT)));
                            people.setRemark(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_REMARK)));
                            people.setTotalCount1(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_TOTAL_PD_WO)));
                            people.setTotalCount2(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_TOTAL_PD_WO_ED_NH)));


                            submitAttendanceArrayList.add(people);
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
                c=null;
        }
        return submitAttendanceArrayList;
    }

    public ArrayList<SubmitAttendance> getSubmittedPeopleAttendanceList()
    {
        ArrayList<SubmitAttendance> submitAttendanceArrayList=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + AttendanceSheet_Table.TABLE_NAME +" WHERE "+AttendanceSheet_Table.ATTENDANCESHEET_APPROVAL_STATUS+" = 1 AND "
                    +AttendanceSheet_Table.ATTENDANCESHEET_SYNC_STATUS+" = 1"
                    +" order by "+AttendanceSheet_Table.ATTENDANCESHEET_PEOPLE_NAME+" ASC",null);
            submitAttendanceArrayList=new ArrayList<SubmitAttendance>();

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        if(c.getLong(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_PEOPLE_ID))!=-1) {
                            SubmitAttendance people=new SubmitAttendance();
                            people.setPeopleId(c.getLong(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_PEOPLE_ID)));
                            people.setPeopleName(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_PEOPLE_NAME)));
                            people.setMdtz(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_MDTZ)));
                            people.setCdtz(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_CDTZ)));
                            people.setMuser(c.getLong(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_MUSER)));
                            people.setCuser(c.getLong(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_CUSER)));
                            people.setAbsentOrPresent(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_ABSENT_OR_PRESNT)));
                            people.setAttendanceDates(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_DATES)));
                            people.setSyncStatus(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_SYNC_STATUS)));
                            people.setApprovalStatus(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_APPROVAL_STATUS)));
                            people.setAttendanceMonth(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_MONTH)));
                            people.setSiteid(c.getLong(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_SITEID)));
                            people.setPresentDaysCount(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_PRESENT_DAYS_COUNT)));
                            people.setWeeklyOffDays(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_WEEKLYOFF_DAYS_COUNT)));
                            people.setNationalHoliday(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_NATIONAL_HOLIDAY_COUNTS)));
                            people.setExtraDuty(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_EXTRA_DUTY_COUNT)));
                            people.setRemark(c.getString(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_REMARK)));
                            people.setTotalCount1(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_TOTAL_PD_WO)));
                            people.setTotalCount2(c.getInt(c.getColumnIndex(AttendanceSheet_Table.ATTENDANCESHEET_TOTAL_PD_WO_ED_NH)));

                            submitAttendanceArrayList.add(people);
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
                c=null;
        }
        return submitAttendanceArrayList;
    }

    public void insertOrUpdateRecord(SubmitAttendance regRecord) {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_PEOPLE_ID, regRecord.getPeopleId());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_PEOPLE_NAME, regRecord.getPeopleName());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_MDTZ, regRecord.getMdtz());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_CDTZ, regRecord.getCdtz());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_MUSER, regRecord.getMuser());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_CUSER, regRecord.getCuser());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_ABSENT_OR_PRESNT, regRecord.getAbsentOrPresent());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_DATES, regRecord.getAttendanceDates());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_SYNC_STATUS, regRecord.getSyncStatus());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_APPROVAL_STATUS, regRecord.getApprovalStatus());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_MONTH, regRecord.getAttendanceMonth());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_SITEID, regRecord.getSiteid());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_PRESENT_DAYS_COUNT, regRecord.getPresentDaysCount());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_WEEKLYOFF_DAYS_COUNT, regRecord.getWeeklyOffDays());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_NATIONAL_HOLIDAY_COUNTS, regRecord.getNationalHoliday());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_EXTRA_DUTY_COUNT, regRecord.getExtraDuty());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_REMARK, regRecord.getRemark());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_TOTAL_PD_WO,regRecord.getTotalCount1());
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_TOTAL_PD_WO_ED_NH, regRecord.getTotalCount2());

            //long val = db.insert(JOBNeedDetails_Table.TABLE_NAME, "", values);

            if(isPeoplePresent(regRecord.getPeopleId()))
            {
                String where ="peopleid=?";
                String[] args=new String[]{String.valueOf(regRecord.getPeopleId())};
                long val=db.update(AttendanceSheet_Table.TABLE_NAME,values,where,args);
                System.out.println("Updated");
            }else
            {
                long val= db.insert(AttendanceSheet_Table.TABLE_NAME, "", values);
                System.out.println("Inserted");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }


    private boolean isPeoplePresent(long peopleid)
    {
        String str="select * from "+AttendanceSheet_Table.TABLE_NAME +" where "+AttendanceSheet_Table.ATTENDANCESHEET_PEOPLE_ID+" = "+peopleid;
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

    public int changeStatus(long  peopleid, long appStatus, long syncStatus) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_APPROVAL_STATUS, appStatus);
            values.put(AttendanceSheet_Table.ATTENDANCESHEET_SYNC_STATUS, syncStatus);
            return db.update(AttendanceSheet_Table.TABLE_NAME, values, "peopleid=?", new String[] { String.valueOf(peopleid) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void deleteRecords()
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            db.delete(AttendanceSheet_Table.TABLE_NAME,null,null);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

}
