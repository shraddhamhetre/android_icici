package com.youtility.intelliwiz20.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.youtility.intelliwiz20.Activities.DashboardActivity;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

public class StepCountBuzzerService extends Service {

    private static final String ACTION_RESCHEDULE = "service.intent.action.SERVICE_RESCHEDULE";


    public StepCountBuzzerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("CallLogService onstartcommand");

        DashboardActivity dashboardActivity=DashboardActivity.instance;
        /*if(dashboardActivity!=null)
        {
            dashboardActivity.getCallDetails();
        }*/
        Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                addCallLogAsAttachment();
            }
        });

        Intent i = new Intent();
        i.setClassName(getApplicationContext(), "StepCountBuzzerService");
        i.setAction(ACTION_RESCHEDULE);
        PendingIntent pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = (6 * 60 * 60 * 1000L);
        //long interval = (5 * 60 * 1000L);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, pi);

        return Service.START_NOT_STICKY;
    }


    private void addCallLogAsAttachment()
    {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        AttachmentDAO attachmentDAO=new AttachmentDAO(StepCountBuzzerService.this);
        TypeAssistDAO typeAssistDAO=new TypeAssistDAO(StepCountBuzzerService.this);
        SharedPreferences loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);

        Attachment attachment=new Attachment();
        attachment.setAttachmentid(System.currentTimeMillis());
        attachment.setFilePath(extStorageDirectory + "/"+ Constants.FOLDER_NAME+"/callLog.txt");
        attachment.setFileName("callLog.txt");
        attachment.setNarration("ATTACHMENT");
        attachment.setGpslocation("0.0,0.0");
        attachment.setDatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setCuser(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        attachment.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setMuser(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        attachment.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_SIGN, Constants.IDENTIFIER_ATTACHMENT));
        System.out.println("attachment.getAttachmentType(): "+attachment.getAttachmentType());
        //attachment.setIsdeleted("False");
        attachment.setOwnerid(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        attachment.setOwnername(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_OWNER_TYPE_PEOPLE, Constants.IDENTIFIER_OWNER));
        System.out.println("attachment.getOwnername(): "+attachment.getOwnername());
        attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH_1);
        attachment.setBuid(loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));
        attachmentDAO.insertCommonRecord(attachment);
    }
}
