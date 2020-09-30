package com.youtility.intelliwiz20.Services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
//import android.support.v7.app.NotificationCompat;
import android.support.v4.app.NotificationCompat;

import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.Calendar;

public class SiteReportDoneOrNotCheckService extends Service {
    private static final String ACTION_RESCHEDULE = "service.intent.action.SERVICE_RESCHEDULE";
    private long alarmDuration= 3600000l; //90000;
    private SharedPreferences siteAuditPref;
    private SharedPreferences loginPref;
    private static final int MY_NOTIFICATION_ID=1;
    private NotificationManager notificationManager;
    private Notification myNotification;

    public SiteReportDoneOrNotCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);
        notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Started SiteReportDoneOrNotCheckService");

        siteAuditPref=getSharedPreferences(Constants.SITE_AUDIT_PREF, MODE_PRIVATE);
        Intent i = new Intent();
        i.setClassName(getApplicationContext(),"SiteReportDoneOrNotCheckService");
        i.setAction(ACTION_RESCHEDULE);
        PendingIntent pi = PendingIntent.getService(getBaseContext(), 10001, i,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //alarmManager.setInexactRepeating(AlarmManager.RTC, (SystemClock.elapsedRealtime() + alarmDuration), alarmDuration, pi);

        if (((Calendar.getInstance().getTimeInMillis() - (siteAuditPref.getLong(Constants.SITE_AUDIT_TIMESTAMP, alarmDuration) -60000) ) < alarmDuration))
        {
            System.out.println("1 : SiteReportDoneOrNotCheckService");
            //alarmManager.setInexactRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(),alarmDuration, pi);//changed to 5 mins
            //alarmManager.set(AlarmManager.RTC_WAKEUP, (SystemClock.elapsedRealtime()+alarmDuration), pi);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime() + alarmDuration, pi);
        }
        else if(siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKIN,false) && !siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED,false))
        {
            System.out.println("2 : SiteReportDoneOrNotCheckService");
            //alarmManager.setInexactRepeating(AlarmManager.RTC,SystemClock.elapsedRealtime(),alarmDuration, pi);
            //alarmManager.set(AlarmManager.RTC_WAKEUP, (SystemClock.elapsedRealtime()+alarmDuration), pi);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime() + alarmDuration, pi);
            showNotification("Site Report",getResources().getString(R.string.site_submitreport,siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "")));
            /*if(loginPref.getBoolean(Constants.IS_LOGIN_DONE,false)) {
                Intent i_alert = new Intent(SiteReportDoneOrNotCheckService.this, SiteListActivity.class);
                i_alert.putExtra("FROM_SERVICE", 0);
                i_alert.putExtra("ATTENDANCE_TYPE", "AUDIT");
                i_alert.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i_alert);
            }*/
        }
        else if(siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && !siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT,false))
        {
            System.out.println("3 : SiteReportDoneOrNotCheckService");
            //alarmManager.setInexactRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(), alarmDuration, pi);
            //alarmManager.set(AlarmManager.RTC_WAKEUP, (SystemClock.elapsedRealtime()+alarmDuration), pi);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime() + alarmDuration, pi);
            showNotification(getResources().getString(R.string.checkout_alert_title),getResources().getString(R.string.site_submitreport,siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "")));
            /*if(loginPref.getBoolean(Constants.IS_LOGIN_DONE,false)) {
                Intent i_alert = new Intent(SiteReportDoneOrNotCheckService.this, SiteListActivity.class);
                i_alert.putExtra("FROM_SERVICE", 1);
                i_alert.putExtra("ATTENDANCE_TYPE", "AUDIT");
                i_alert.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i_alert);
            }*/
        }
        /*else
        {
            alarmManager.set(AlarmManager.RTC_WAKEUP, (SystemClock.elapsedRealtime()+alarmDuration), pi);
            System.out.println("0 : SiteReportDoneOrNotCheckService");
        }*/

        return Service.START_NOT_STICKY;
    }

    private void showNotification(String title, String text)
    {
        System.out.println("Notification Text: "+text);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        myNotification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setSound(alarmSound)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setAutoCancel(true)
                .build();


        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
    }

    @Override
    public void onDestroy() {
        /*Intent i = new Intent();
        i.setClassName(getApplicationContext(),"SiteReportDoneOrNotCheckService");
        PendingIntent pi = PendingIntent.getService(getBaseContext(), 10001, i, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
        System.out.println("alert service destroy ");*/
        super.onDestroy();
    }
}
