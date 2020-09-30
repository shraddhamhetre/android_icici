package com.youtility.intelliwiz20.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;

import com.youtility.intelliwiz20.AsyncTask.UserStepCountLogAsyntask;
import com.youtility.intelliwiz20.DataAccessObject.DeviceEventLogDAO;
import com.youtility.intelliwiz20.Model.DeviceEventLog;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

public class GetStepCounterService extends Service {

    public static final String TAG = "StepCounterService";
    private SharedPreferences deviceRelatedPref;
    private static final String ACTION_RESCHEDULE ="service.intent.action.SERVICE_RESCHEDULE";
    private AlarmManager alarmManager;
    private PendingIntent pi;
    private SharedPreferences sharedPreferences;
    private CheckNetwork checkNetwork;
    private DeviceEventLogDAO deviceEventLogDAO;
    private ArrayList<DeviceEventLog> deviceEventLogArrayList=null;
    private SharedPreferences loginPref;
    private StringBuilder stringBuilder;
    private StringBuilder stringBuilderIds;

    public GetStepCounterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkNetwork=new CheckNetwork(GetStepCounterService.this);
        deviceEventLogDAO=new DeviceEventLogDAO(GetStepCounterService.this);
        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("StepCountService Started.............................");

        deviceEventLogArrayList=deviceEventLogDAO.getUnsyncStepEvents(loginPref.getLong(Constants.LOGIN_SITE_ID,-1));
        if(deviceEventLogArrayList!=null && deviceEventLogArrayList.size()>0)
        {
            System.out.println("StepCountService log count............................."+deviceEventLogArrayList.size());
            stringBuilder=new StringBuilder();
            stringBuilderIds=new StringBuilder();
            for(int i=0;i<deviceEventLogArrayList.size();i++)
            {
                stringBuilder.append(deviceEventLogArrayList.get(i).getStepCount()+",");
                stringBuilderIds.append("'"+deviceEventLogArrayList.get(i).getCdtz()+"',");
            }

            DeviceEventLog deviceEventLog=new DeviceEventLog();
            //sb.toString().trim().substring(0, sb.toString().trim().length() - 1);
            deviceEventLog.setStepCount(stringBuilder.toString().trim().substring(0,stringBuilder.toString().trim().length()-1));
            deviceEventLog.setAccuracy(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getAccuracy());
            deviceEventLog.setDeviceid(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getDeviceid());
            deviceEventLog.setEventvalue(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getEventvalue());
            deviceEventLog.setGpslocation(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getGpslocation());
            deviceEventLog.setAltitude(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getAltitude());
            deviceEventLog.setBatterylevel(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getBatterylevel());
            deviceEventLog.setSignalstrength(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getSignalstrength());
            deviceEventLog.setAvailextmemory(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getAvailextmemory());
            deviceEventLog.setAvailintmemory(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getAvailintmemory());
            deviceEventLog.setCdtz(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getCdtz());
            deviceEventLog.setMdtz(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getMdtz());
            deviceEventLog.setCuser(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getCuser());
            deviceEventLog.setMuser(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getMuser());
            deviceEventLog.setPeopleid(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getPeopleid());
            deviceEventLog.setEventtype(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getEventtype());
            deviceEventLog.setSignalbandwidth(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getSignalbandwidth());
            deviceEventLog.setBuid(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getBuid());
            deviceEventLog.setApplicationversion(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getApplicationversion());
            deviceEventLog.setAndroidosversion(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getAndroidosversion());
            deviceEventLog.setModelname(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getModelname());
            deviceEventLog.setInstalledapps(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getInstalledapps());
            deviceEventLog.setSimserialnumber(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getSimserialnumber());
            deviceEventLog.setLinenumber(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getLinenumber());
            deviceEventLog.setNetworkprovidername(deviceEventLogArrayList.get(deviceEventLogArrayList.size()-1).getNetworkprovidername());

            if(checkNetwork.isNetworkConnectionAvailable()) {
                UserStepCountLogAsyntask userStepCountLogAsyntask = new UserStepCountLogAsyntask(GetStepCounterService.this, deviceEventLog, stringBuilderIds.toString().trim().substring(0, stringBuilderIds.toString().trim().length() - 1));
                userStepCountLogAsyntask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        }
        //call service on time interval
        Intent i = new Intent();
        i.setClassName(getApplicationContext(), "GetStepCounterService");
        i.setAction(ACTION_RESCHEDULE);
        pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        int interval=(5*60*1000);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pi);

        return Service.START_NOT_STICKY;
    }



}
