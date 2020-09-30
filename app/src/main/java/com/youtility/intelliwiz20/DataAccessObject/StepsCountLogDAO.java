package com.youtility.intelliwiz20.DataAccessObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.StepCount;
import com.youtility.intelliwiz20.Tables.StepCountLog_Table;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * login user assigned sites data access object
 *
 */

public class StepsCountLogDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    Cursor c = null;

    public StepsCountLogDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }

    public void insertRecord(StepCount regRecord)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(StepCountLog_Table.STEPCOUNT_TIMESTAMP, regRecord.getStepCountTimestamp());
            values.put(StepCountLog_Table.STEPCOUNT_STEPS, regRecord.getSteps());

            values.put(StepCountLog_Table.STEPCOUNT_TOTALSTEPS, regRecord.getTotalSteps());
            values.put(StepCountLog_Table.STEPCOUNT_STEPS_TAKEN, regRecord.getStepsTaken());
            long val= db.insert(StepCountLog_Table.TABLE_NAME, "", values);
            System.out.println("Step Count data inserted: "+val);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public void deletRecords()
    {
        db = sqlopenHelper.getReadableDatabase();
        db.execSQL("delete from "+ StepCountLog_Table.TABLE_NAME);
    }

    public ArrayList<StepCount> getStepCountsLog()
    {
        ArrayList<StepCount> stepCountArrayList=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + StepCountLog_Table.TABLE_NAME +" order by strftime('%s' ,"+ StepCountLog_Table.STEPCOUNT_TIMESTAMP +") DESC ",null);
            stepCountArrayList=new ArrayList<StepCount>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        StepCount stepCount=new StepCount();
                        stepCount.setStepsTaken(c.getString(c.getColumnIndex(StepCountLog_Table.STEPCOUNT_STEPS_TAKEN)));
                        stepCount.setSteps(c.getLong(c.getColumnIndex(StepCountLog_Table.STEPCOUNT_STEPS)));
                        stepCount.setTotalSteps(c.getLong(c.getColumnIndex(StepCountLog_Table.STEPCOUNT_TOTALSTEPS)));
                        stepCount.setStepCountTimestamp(c.getLong(c.getColumnIndex(StepCountLog_Table.STEPCOUNT_TIMESTAMP)));
                        stepCountArrayList.add(stepCount);
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
        return stepCountArrayList;
    }





}
