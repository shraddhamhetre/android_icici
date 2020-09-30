package com.youtility.intelliwiz20.Services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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

//import android.support.v7.app.NotificationCompat;

public class JobAlertService extends Service {
    private static final String ACTION_RESCHEDULE ="service.intent.action.SERVICE_RESCHEDULE";
    JobNeedDAO jobNeedDAO;
    boolean isAlertReady=false;
    JobNeed jobNeed;
    private TypeAssistDAO typeAssistDAO;
    private SharedPreferences loginPref;

    public JobAlertService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("JobAlertService","Service started.....");
        System.out.println("onstart alert");

        //CommonFunctions.isAppIsInBackground(JobAlertService.this);
        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);

        if(loginPref.getBoolean(Constants.IS_LOGIN_DONE,false)) {
            getUpcomingTaskFromDB();
            //getUpcomingTaskFromDBforemail();
        }

        Intent i = new Intent();
        i.setClassName(getApplicationContext(), "JobAlertService");
        i.setAction(ACTION_RESCHEDULE);
        PendingIntent pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        long interval=(10*60*1000);
        alarmManager.setInexactRepeating(AlarmManager.RTC, (System.currentTimeMillis()+interval), interval, pi);

        return Service.START_NOT_STICKY;
    }

    private void getUpcomingTaskFromDB()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, +10);
        isAlertReady=false;

        Calendar cal1=Calendar.getInstance();
        jobNeedDAO=new JobNeedDAO(JobAlertService.this);
        typeAssistDAO=new TypeAssistDAO(JobAlertService.this);
        Cursor c=jobNeedDAO.getScheduleJobAlertList(cal1.getTimeInMillis(),cal.getTimeInMillis());
        System.out.println("SystemCurrentTime: "+ CommonFunctions.getFormatedDate(cal1.getTimeInMillis()));
        System.out.println("GraceTimeDate: "+CommonFunctions.getFormatedDate(cal.getTimeInMillis()));

        System.out.println("SystemCurrentTime:--- "+ cal1.getTimeInMillis());
        System.out.println("GraceTimeDate:--- "+cal.getTimeInMillis());
        if(c!=null && c.getCount()>0)
        {
            if(c.moveToFirst())
            {
                //do{
                    //if(checkTaskExpiry(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)),c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)), c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)))==1)
                    String jobStatus = typeAssistDAO.getEventTypeCode(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                    if (!jobStatus.equalsIgnoreCase("AUTOCLOSED") || !jobStatus.equalsIgnoreCase("COMPLETED")) {
                                                    //AUTOCLOSED
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

               // }while(c.moveToNext());
            }
        }

        if(c!=null)
            c=null;
        if(isAlertReady)
        {

            //You can do the processing here.
            StringBuilder msgStr = new StringBuilder();

            msgStr.append(getResources().getString(R.string.job_alert_msg_yourtaskis)+" "+jobNeed.getJobdesc()+"~");
            msgStr.append(getResources().getString(R.string.job_alert_msg_dueattime)+" "+ (jobNeed.getPlandatetime()));

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(JobAlertService.this);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msgStr.toString()));
            mBuilder.setContentTitle("Task Alert!");
            mBuilder.setAutoCancel(true);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

            Intent i = new Intent(JobAlertService.this, DialogActivity.class);
            i.putExtra("EventName", msgStr.toString());
            i.putExtra("Allow", 0);
            i.putExtra("JOB_NEED", jobNeed);
            i.putExtra("Activity", "JOBNEED");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pIntent = PendingIntent.getActivity(JobAlertService.this, 0,i, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(pIntent);


            /*Notification note = mBuilder.build();
            note.defaults |= Notification.DEFAULT_VIBRATE;
            note.defaults |= Notification.DEFAULT_SOUND;*/

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());

            /*Intent i = new Intent(context, DialogActivity.class);
            i.putExtra("EventName", msgStr.toString());
            i.putExtra("Allow", 0);
            i.putExtra("JOB_NEED", jobNeed);
            i.putExtra("Activity", "JOBNEED");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);*/


        }
    }

/*    private void getUpcomingTaskFromDBforemail()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, +10);
        isAlertReady=false;

        Calendar cal2=Calendar.getInstance();
        cal.add(Calendar.MINUTE, +10);

        jobNeedDAO=new JobNeedDAO(JobAlertService.this);
        typeAssistDAO=new TypeAssistDAO(JobAlertService.this);
        Cursor c=jobNeedDAO.getScheduleJobAlertList(cal2.getTimeInMillis(),cal.getTimeInMillis());
        System.out.println("SystemCurrentTime: "+ CommonFunctions.getFormatedDate(cal2.getTimeInMillis()));
        System.out.println("GraceTimeDate: "+CommonFunctions.getFormatedDate(cal.getTimeInMillis()));

        if(c!=null && c.getCount()>0)
        {
            System.out.println("count"+c.getCount());
            if(c.moveToFirst())
            {
                //do{
                //if(checkTaskExpiry(c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME)),c.getInt(c.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME)), c.getString(c.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME)))==1)
                String jobStatus = typeAssistDAO.getEventTypeCode(c.getLong(c.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS)));
                if (!jobStatus.equalsIgnoreCase("AUTOCLOSED") || !jobStatus.equalsIgnoreCase("COMPLETED")) {
                    //AUTOCLOSED
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
                // }while(c.moveToNext());
            }
        }

        if(c!=null)
            c=null;

        long currDt=cal2.getTimeInMillis();
        long plandatetime = CommonFunctions.getParseDate(jobNeed.getPlandatetime());
        System.out.println("plan--"+CommonFunctions.getParseDate(jobNeed.getPlandatetime()));
        long Fplandatetime=plandatetime + TimeUnit.MINUTES.toMillis(jobNeed.getGracetime());
        if(currDt >= Fplandatetime){
            System.out.println("Send alert");
        }else {
            System.out.println("Do not Send alert");
        }
        Calendar cal3=Calendar.getInstance();
        cal2.add(Calendar.MINUTE, -20);
        Cursor d=jobNeedDAO.getScheduleJobAlertList(cal3.getTimeInMillis(),cal.getTimeInMillis());
        System.out.println("SystemCurrentTime111: "+ CommonFunctions.getFormatedDate(cal3.getTimeInMillis()));

    }*/

    private int checkTaskExpiry(String planDate, int graceTime, String expDate)
    {
        long backDate=new Date( CommonFunctions.getParse24HrsDate(planDate)- (graceTime * 60 * 1000)).getTime();
        return  CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParse24HrsDate(expDate));
        /*long backDate=new Date( CommonFunctions.getParse24HrsDate((mItem.getPlandatetime()))- (mItem.getGracetime() * 60 * 1000)).getTime();
        isJobExpired=CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParse24HrsDate((mItem.getExpirydatetime())));*/
    }
}
