package com.youtility.intelliwiz20.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.youtility.intelliwiz20.BroadcastReceiver.JobAlertBroadcast;
import com.youtility.intelliwiz20.Utils.Constants;

public class JobAlertBroadcastService extends Service {
    private static final String ACTION_RESCHEDULE ="service.intent.action.SERVICE_RESCHEDULE";
    public static String MY_ACTION ="com.youtility.intelliwiz20.BroadcastReceiver";
    //public static String MY_ACTION ="com.youtility.istaging.BroadcastReceiver";
    private JobAlertBroadcast jobAlertBroadcast;
    private SharedPreferences loginPref;
    public JobAlertBroadcastService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("JOBAlertBroadcast","Service started.....");

        /*if(loginPref.getBoolean(Constants.IS_LOGIN_DONE,false)) {
            if (CommonFunctions.isAppIsInBackground(JobAlertBroadcastService.this)) {
                Intent i_alert = new Intent(JobAlertBroadcastService.this, DashboardActivity.class);
                i_alert.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i_alert.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i_alert);
            }
        }*/

        /*jobAlertBroadcast=new JobAlertBroadcast();
        jobAlertBroadcast.SetAlarm(JobAlertBroadcastService.this);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(DashboardActivity.mBroadcastStringAction);
                broadcastIntent.putExtra("Data", "Broadcast Data");
                sendBroadcast(broadcastIntent);*/

                Intent bIntent = new Intent();
                bIntent.setAction(MY_ACTION);
                bIntent.putExtra("DATAPASSED", 0);
                sendBroadcast(bIntent);
            }
        }).start();


        //call service on time interval

        Intent i = new Intent();
        i.setClassName(getApplicationContext(), "JobAlertBroadcastService");
        i.setAction(ACTION_RESCHEDULE);
        PendingIntent pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        long interval=(5*60*1000);
        alarmManager.setInexactRepeating(AlarmManager.RTC, (System.currentTimeMillis()+interval), interval, pi);

        /*Intent i = new Intent();
        i.setClassName(getApplicationContext(), "JobAlertBroadcastService");
        i.setAction(ACTION_RESCHEDULE);
        PendingIntent pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        long interval=(2*60*1000);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pi);*/

        return Service.START_REDELIVER_INTENT;
    }
}
