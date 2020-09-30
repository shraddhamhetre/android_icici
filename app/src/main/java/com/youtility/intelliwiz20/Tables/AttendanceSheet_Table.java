package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * Attendance Sheet approval for area manager or branch manager
 */

public class AttendanceSheet_Table implements BaseColumns {

    public static final String TABLE_NAME= "AttendanceSheet";

    public static final String ATTENDANCESHEET_PEOPLE_ID="peopleid";
    public static final String ATTENDANCESHEET_PEOPLE_NAME="peoplename";
    public static final String ATTENDANCESHEET_PEOPLE_LOGINID="peopleLoginId";
    public static final String ATTENDANCESHEET_MDTZ="mdtz";
    public static final String ATTENDANCESHEET_CDTZ="cdtz";
    public static final String ATTENDANCESHEET_MUSER="muser";
    public static final String ATTENDANCESHEET_CUSER="cuser";
    public static final String ATTENDANCESHEET_ABSENT_OR_PRESNT="absentOrPresent";
    public static final String ATTENDANCESHEET_DATES="attendanceDates";
    public static final String ATTENDANCESHEET_SYNC_STATUS="syncStatus";
    public static final String ATTENDANCESHEET_APPROVAL_STATUS="approvalStatus";
    public static final String ATTENDANCESHEET_MONTH="attendanceMonth";
    public static final String ATTENDANCESHEET_SITEID="siteid";

    //public static final String ATTENDANCESHEET_PRESENT_DAYS="attendanceDates";
    public static final String ATTENDANCESHEET_WEEKLYOFF_DAYS="weeklyOffDays";
    public static final String ATTENDANCESHEET_NATIONAL_HOLIDAY="nationalHoliday";
    public static final String ATTENDANCESHEET_EXTRA_DUTY="extraDuty";

    public static final String ATTENDANCESHEET_PRESENT_DAYS_COUNT="presentDaysCount";
    public static final String ATTENDANCESHEET_WEEKLYOFF_DAYS_COUNT="weeklyOffDaysCount";
    public static final String ATTENDANCESHEET_NATIONAL_HOLIDAY_COUNTS="nationalHolidayCount";
    public static final String ATTENDANCESHEET_EXTRA_DUTY_COUNT="extraDutyCount";

    public static final String ATTENDANCESHEET_REMARK="remark";

    public static final String ATTENDANCESHEET_TOTAL_PD_WO="totalCount1";
    public static final String ATTENDANCESHEET_TOTAL_PD_WO_ED_NH="totalCount2";


    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

   /* private static final String DATABASE_ALTER_JOBNEED_MFACTOR1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + JOBNEED_MFACTOR1 + " real;";*/

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                                              +" "+   ID_TYPE     +   ","+
            ATTENDANCESHEET_PEOPLE_ID                        +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_PEOPLE_NAME                      +" "+   TEXT_TYPE    +   ","+
            ATTENDANCESHEET_PEOPLE_LOGINID                   +" "+   TEXT_TYPE    +   ","+
            ATTENDANCESHEET_MDTZ                             +" "+   TEXT_TYPE    +   ","+
            ATTENDANCESHEET_CDTZ                             +" "+   TEXT_TYPE    +   ","+
            ATTENDANCESHEET_MUSER                            +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_CUSER                            +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_ABSENT_OR_PRESNT                 +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_DATES                            +" "+   TEXT_TYPE   +   ","+
            ATTENDANCESHEET_SYNC_STATUS                      +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_APPROVAL_STATUS                  +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_MONTH                            +" "+   TEXT_TYPE    +   ","+
            ATTENDANCESHEET_PRESENT_DAYS_COUNT               +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_WEEKLYOFF_DAYS_COUNT             +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_NATIONAL_HOLIDAY_COUNTS          +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_EXTRA_DUTY_COUNT                 +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_WEEKLYOFF_DAYS                   +" "+   TEXT_TYPE    +   ","+
            ATTENDANCESHEET_NATIONAL_HOLIDAY                 +" "+   TEXT_TYPE    +   ","+
            ATTENDANCESHEET_EXTRA_DUTY                       +" "+   TEXT_TYPE    +   ","+
            ATTENDANCESHEET_TOTAL_PD_WO                      +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_TOTAL_PD_WO_ED_NH                +" "+   INT_TYPE    +   ","+
            ATTENDANCESHEET_REMARK                           +" "+   TEXT_TYPE    +   ","+
            ATTENDANCESHEET_SITEID                           +" "+   INT_TYPE    +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("AttendanceSheet Table Created");
    }

    public static void onUpgrade(SQLiteDatabase db, int oldv, int newv)
    {
        /*db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        OnCreate(db);*/

        /*if(newv>oldv)
            db.execSQL(DATABASE_ALTER_JOBNEED_MFACTOR1);*/
    }

}
