package com.youtility.intelliwiz20.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.PowerManager;
import android.util.Log;

import com.youtility.intelliwiz20.Activities.DialogActivity;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Tables.JOBNeed_Table;
import com.youtility.intelliwiz20.Utils.CommonFunctions;

import java.util.Calendar;

public class JobAlertBroadcast extends BroadcastReceiver {
    JobNeedDAO jobNeedDAO;
    boolean isAlertReady=false;
    JobNeed jobNeed;
    private TypeAssistDAO typeAssistDAO;

    @Override
    public void onReceive(Context context, Intent intent) {
        //weakup device
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Youtility");
        //Acquire the lock
        wl.acquire();

        //show alert before 10 min
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, +15);

        Log.d("JOBAlertBroadcast","JobAlertBroadcast OnReceiver");

        Calendar cal1=Calendar.getInstance();
        jobNeedDAO=new JobNeedDAO(context);
        typeAssistDAO=new TypeAssistDAO(context);
        Cursor c=jobNeedDAO.getScheduleJobAlertList(cal1.getTimeInMillis(),cal.getTimeInMillis());
        System.out.println("SystemCurrentTime: "+ CommonFunctions.getFormatedDate(cal1.getTimeInMillis()));
        System.out.println("GraceTimeDate: "+CommonFunctions.getFormatedDate(cal.getTimeInMillis()));
        if(c!=null)
        {
            if(c.moveToFirst())
            {
                do{


                    String jobStatus=typeAssistDAO.getEventTypeCode(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                    if(!jobStatus.equalsIgnoreCase("AUTOCLOSED") || !jobStatus.equalsIgnoreCase("COMPLETED"))
                    {

                        isAlertReady=true;
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
                    }
                }while(c.moveToNext());
            }
        }

        //Release the lock
        wl.release();

        if(isAlertReady)
        {

            System.out.println("is ready jobneed");
            //You can do the processing here.
            StringBuilder msgStr = new StringBuilder();

            msgStr.append(context.getResources().getString(R.string.job_alert_msg_yourtaskis)+" "+jobNeed.getJobdesc()+"~");
            msgStr.append(context.getResources().getString(R.string.job_alert_msg_dueattime)+" "+ (jobNeed.getPlandatetime()));

            Intent i = new Intent(context, DialogActivity.class);
            i.putExtra("EventName", msgStr.toString());
            i.putExtra("Allow", 0);
            i.putExtra("JOB_NEED", jobNeed);
            i.putExtra("Activity", "JOBNEED");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        System.out.println("is ready 1-- "+jobNeed.getPlandatetime());
        System.out.println("is ready 2-- "+jobNeed.getGracetime());

    }

    /*public void SetAlarm(Context context)
    {
        Log.d("JOBAlertBroadcast","JOB Alarm started");
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, JobAlertBroadcast.class);
        intent.putExtra("TaskStatus", Boolean.FALSE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        //set 1 min
       am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),((1000 * 60)) , pi);
        //am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis() , pi);

    }*/
}
