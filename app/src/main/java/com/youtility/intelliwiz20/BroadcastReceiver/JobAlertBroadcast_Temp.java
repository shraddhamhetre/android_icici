package com.youtility.intelliwiz20.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.PowerManager;
import android.util.Log;

import com.youtility.intelliwiz20.Activities.DashboardActivity;
import com.youtility.intelliwiz20.Activities.DialogActivity;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Tables.JOBNeed_Table;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.Calendar;
import java.util.Date;

public class JobAlertBroadcast_Temp extends BroadcastReceiver {
    JobNeedDAO jobNeedDAO;
    boolean isAlertReady=false;
    JobNeed jobNeed;
    private TypeAssistDAO typeAssistDAO;
    private SharedPreferences loginPref;
    private Cursor c=null;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(c!=null)
            c=null;

        //show alert before 10 min
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, +10);


        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE,Context.MODE_PRIVATE);

        Log.d("JOBAlertBroadcast","Temp JobAlertBroadcast OnReceiver");

        Calendar cal1=Calendar.getInstance();

        jobNeedDAO=new JobNeedDAO(context);
        typeAssistDAO=new TypeAssistDAO(context);
        c=jobNeedDAO.getScheduleJobAlertList(cal1.getTimeInMillis(),cal.getTimeInMillis());
        System.out.println("JOBAlertBroadcast SystemCurrentTime: "+ CommonFunctions.getFormatedDate(cal1.getTimeInMillis()));
        System.out.println("JOBAlertBroadcast GraceTimeDate: "+CommonFunctions.getFormatedDate(cal.getTimeInMillis()));
        if(c!=null && c.getCount()>0)
        {
            System.out.println("JOBAlertBroadcast "+ c.getCount());
            if(c.moveToFirst())
            {
                //do
                {
                    System.out.println("JOBAlertBroadcast TaskAlert "+checkTaskExpiry(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)),c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)), c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME))));
                    System.out.println("JOBAlertBroadcast TaskAlert Plan date"+c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)));
                    System.out.println("JOBAlertBroadcast TaskAlert Description"+c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_DESC)));
                    //if(checkTaskExpiry(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)),c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)), c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)))==1)

                    String jobStatus = typeAssistDAO.getEventTypeCode(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                    Log.d("JOBAlertBroadcast", "jobStatus: " + jobStatus);
                    if (!jobStatus.equalsIgnoreCase("AUTOCLOSED") || !jobStatus.equalsIgnoreCase("COMPLETED")) {

                        isAlertReady = true;
                        jobNeed = new JobNeed();
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


                }//while(c.moveToNext());
            }
        }

        if(c!=null) {
            c.close();
            c = null;
        }

        if(isAlertReady)
        {
            if(loginPref.getBoolean(Constants.IS_LOGIN_DONE,false)) {
                if (CommonFunctions.isAppIsInBackground(context)) {
                    Intent i_alert = new Intent(context, DashboardActivity.class);
                    i_alert.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i_alert.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i_alert);
                }
            }

            //weakup device
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            if(!pm.isScreenOn())
            {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
                wl.acquire(10000);
            }

            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK , "Youtility");
            //Acquire the loc
            wl.acquire();
            //You can do the processing here.
            StringBuilder msgStr = new StringBuilder();

            msgStr.append(context.getResources().getString(R.string.job_alert_msg_yourtaskis)+" "+jobNeed.getJobdesc()+"~");
            msgStr.append(context.getResources().getString(R.string.job_alert_msg_dueattime)+" "+ (jobNeed.getPlandatetime()));

            /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msgStr.toString()));
            mBuilder.setContentTitle("Broadcast Alert!");
            //mBuilder.setContentText(msgStr.toString());
            mBuilder.setAutoCancel(true);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });


            *//*Notification note = mBuilder.build();
            note.defaults |= Notification.DEFAULT_VIBRATE;
            note.defaults |= Notification.DEFAULT_SOUND;*//*

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());*/

            //Release the lock
            wl.release();
            isAlertReady=false;

            Intent i = new Intent(context, DialogActivity.class);
            i.putExtra("EventName", msgStr.toString());
            i.putExtra("Allow", 0);
            i.putExtra("JOB_NEED", jobNeed);
            i.putExtra("Activity", "JOBNEED");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }

    private int checkTaskExpiry(String planDate, int graceTime, String expDate)
    {
        long backDate=new Date( CommonFunctions.getParse24HrsDate(planDate)- (graceTime * 60 * 1000)).getTime();
        return  CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParse24HrsDate(expDate));
        /*long backDate=new Date( CommonFunctions.getParse24HrsDate((mItem.getPlandatetime()))- (mItem.getGracetime() * 60 * 1000)).getTime();
        isJobExpired=CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParse24HrsDate((mItem.getExpirydatetime())));*/
    }


}
