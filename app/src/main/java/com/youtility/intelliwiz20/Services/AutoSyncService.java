package com.youtility.intelliwiz20.Services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.youtility.intelliwiz20.Activities.AboutUsActivity;
import com.youtility.intelliwiz20.Activities.ScreenHandler;
import com.youtility.intelliwiz20.AsyncTask.AdhocInsertAsynctask;
import com.youtility.intelliwiz20.AsyncTask.DeviceEventLogAsyntask;
import com.youtility.intelliwiz20.AsyncTask.DownloadDataAsynctask;
import com.youtility.intelliwiz20.AsyncTask.JobNeedInsertAsynctask;
import com.youtility.intelliwiz20.AsyncTask.JobNeedReplyAsyntask;
import com.youtility.intelliwiz20.AsyncTask.JobneedUpdateAsyntask;
import com.youtility.intelliwiz20.AsyncTask.PeopleEventLogAsyntask;
import com.youtility.intelliwiz20.AsyncTask.PersonLoggerAsyncTask;
import com.youtility.intelliwiz20.AsyncTask.SiteReportLogAsynctask;
import com.youtility.intelliwiz20.BroadcastReceiver.JobAlertBroadcast;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.DeviceEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.Interfaces.IDownloadDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadAdhocInsertDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadDeviceEventLogDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadJobNeedInsertDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadJobNeedReplyDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadJobneedUpdateDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadPeopleEventDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadPersonLoggerDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadSiteReportDataListener;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.DeviceEventLog;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

//import android.support.v7.app.NotificationCompat;

public class AutoSyncService extends IntentService implements IUploadDeviceEventLogDataListener, IUploadPeopleEventDataListener,
        IUploadAdhocInsertDataListener, IUploadJobNeedInsertDataListener, IUploadJobneedUpdateDataListener,
        IUploadJobNeedReplyDataListener, IDownloadDataListener, IUploadSiteReportDataListener, IUploadPersonLoggerDataListener, Runnable {
    private static final String ACTION_RESCHEDULE ="service.intent.action.SERVICE_RESCHEDULE";
    private SharedPreferences sharedPreferences;
    private CheckNetwork checkNetwork;
    private static final int MY_NOTIFICATION_ID=111;
    private NotificationManager notificationManager;
    private Notification myNotification;

    private SharedPreferences autoSyncPref;

    private PeopleEventLogDAO peopleEventLogDAO;
    private ArrayList<PeopleEventLog> peopleEventLogArrayList;

    private DeviceEventLogDAO deviceEventLogDAO;
    private ArrayList<DeviceEventLog>deviceEventLogArrayList;

    private JobNeedDAO jobNeedDAO;
    private ArrayList<JobNeed>adhocJobArrayList;
    private ArrayList<JobNeed>incidentReportArrayList;
    private ArrayList<JobNeed>jobUpdateArrayList;

    private AttachmentDAO attachmentDAO;
    private ArrayList<Attachment>jobNeedReplyAttachmentArrayList;

    private SharedPreferences loginPref;
    private int syncReturnValue=0;
    private int counterVal=0;
    private String counterName=null;

    private JobAlertBroadcast jobAlertBroadcast;

    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;



    @Override
    public void run() {
        startPeopleEventLogUploading();
    }


    enum SyncCount
    {
        ASSET,JN, JND, TA, GF, PEOPLE, GROUP, ATTHISTORY,QUEST,QSET,QSB,PGB,SP,TICKET,TEMPLATE
    }

    /*public AutoSyncService()  {
    }*/

    public AutoSyncService() {
        super("AutoSyncService");
    }



    @Override
    public void onCreate() {
        System.out.println("autosync called");
        super.onCreate();
        notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);
        autoSyncPref=getSharedPreferences(Constants.AUTO_SYNC_PREF,MODE_PRIVATE);

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        compName = new ComponentName(this, AutoSyncService.class);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


    public boolean checkActivity() {
        boolean checkActivity= false;
        boolean captureActivity= false;
        long currentTimestamp= System.currentTimeMillis();
        long previousTimestamp= autoSyncPref.getLong(Constants.CAMERA_ON_TIMESTAMP, 0l);
        int diffCaptureActivity= CommonFunctions.getDateDifferenceInMin(currentTimestamp, previousTimestamp );

        System.out.println("diffCaptureActivity ::"+ diffCaptureActivity);

        String[] ListActivities = {"AttendanceCapturePhotoActivity", "CaptureActivity", "SelfAttendanceActivity", "CapturePhotoActivity", "SiteListActivity"};
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        System.out.println("topActivity CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        String activityName= taskInfo.get(0).topActivity.getClassName();
        //System.out.println("@@activityName:="+activityName +"::"+(activityName.endsWith("CheckpointListActivity")));
        boolean readyToSync= true;
        for (int activity = 0; activity < ListActivities.length; activity++) {
            checkActivity = activityName.endsWith(ListActivities[activity]);
            captureActivity = activityName.endsWith("CaptureActivity");
            System.out.println("cameraActivity ::"+ captureActivity);

            if(captureActivity){
                if(diffCaptureActivity < 3) {
                    readyToSync= false;
                }
            }else if(checkActivity) {
                System.out.println("@@ CURRENT Activity(camera) :: true");
                readyToSync= false;
                break;
            }
        }
        System.out.println("readyToSync"+ readyToSync);
        return readyToSync;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(AutoSyncService.this);
        checkNetwork=new CheckNetwork(AutoSyncService.this);
        if(checkNetwork.isNetworkConnectionAvailable()) {
            System.out.println(ScreenHandler.screenOff+"----------check1");

            if (ScreenHandler.screenOff == true) {
                System.out.println(ScreenHandler.screenOff+"----------check2");
                if (!autoSyncPref.getBoolean(Constants.IS_AUTO_SYNC_RUNNING, false)) {
                    callSyncServices();
                } else {
                    System.out.println("AUTO Sync Service Running in background............................................................");
                    autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING, false).apply();
                }
            } else if (checkActivity()) {
                callSyncServices();
                //turnOffScreen();
             } else {
                System.out.println("Ready to Sync :: "+ checkActivity());
            }
        } else {
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.check_internet_connection_msg),"");
        }
        //call service on time interval
        Intent i = new Intent(this, AutoSyncService.class);
        //i.setClassName(getApplicationContext(), "AutoSyncService");
        i.setAction(ACTION_RESCHEDULE);
        PendingIntent pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //int interval=(1*60*1000);
        long interval=Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_UPLOADDATA_FREQ,"15"))*(60*1000);
        //long interval=1*(60*1000);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pi);
        System.out.println("Service rescheduled");
        System.out.println("===service"+Service.START_NOT_STICKY+ "interval"+interval);
        return Service.START_NOT_STICKY;
    }

    public void callSyncServices(){
        System.out.println("AUTO Sync Service Started............................................................" + autoSyncPref.getBoolean(Constants.IS_AUTO_SYNC_RUNNING, false));
        //showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_syncinprogress));
        System.out.println("Datetime for site mark ::" + autoSyncPref.getLong(Constants.CAMERA_ON_TIMESTAMP, 0l));

        int accessValue = CommonFunctions.isAllowToAccessModules(this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        if (accessValue == 0) {
            System.out.println("autosysncstart-----");
            autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING, true).apply();
            System.out.println("@@IsScreenOff: " + ScreenHandler.screenOff);
            startPeopleEventLogUploading();
        } else {
            if (accessValue == 1)
                showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autodatetimeMessage), "Sync Failed");
            else if (accessValue == 2)
                showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGPSMessage), "Sync Failed");
        }
    }

    private void showNotification(String title, String text, String cInfo)
    {
        Intent ii=new Intent(getApplicationContext(), AboutUsActivity.class);
        ii.putExtra("notify_msg", 1);

        //.setContentIntent(i)
        PendingIntent i=PendingIntent.getActivity(this, 0,ii,0);

        myNotification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(text)
                .setContentInfo(cInfo)
                .setTicker(getResources().getString(R.string.intelliwiz))
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .build();
         notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
    }


    public void startPeopleEventLogUploading() {
        /*peopleEventLogDAO=new PeopleEventLogDAO(AutoSyncService.this);
        peopleEventLogArrayList=new ArrayList<>();
        peopleEventLogArrayList=peopleEventLogDAO.getEvents();

        if(peopleEventLogArrayList!=null && peopleEventLogArrayList.size()>0)
        {
            for(int i=0;i<peopleEventLogArrayList.size();i++)
            {
                PeopleEventLog peopleEventLog=new PeopleEventLog();
                peopleEventLog=peopleEventLogArrayList.get(i);

                PeopleEventLogAsyntask peopleEventLogAsyntask=new PeopleEventLogAsyntask(AutoSyncService.this, this, peopleEventLog);
                peopleEventLogAsyntask.execute();
            }
        }
        else
        {
            finishAllPeopleEventLogUpload();
        }*/
        System.out.println("Thread: startPeopleEventLogUploading() current thread" + Thread.currentThread().getName());

        PeopleEventLogAsyntask peopleEventLogAsyntask=new PeopleEventLogAsyntask(AutoSyncService.this, this);
        peopleEventLogAsyntask.execute();

    }

    @Override
    public void finishAllPeopleEventLogUpload() {
        System.out.println("People event Log Data Compeleted or dont have it ");
        //startDeviceEventLogUploading();
        startADHOCDataUploading();
    }

    @Override
    public void finishPeopleEventLogUpload(int status) {
        /*if(status==0)
            startPeopleEventLogUploading();
        else
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg));*/

        if(status==0) {
            System.out.println("People event Log Data Compeleted or dont have it------------------------------------------------------ ");
            //startDeviceEventLogUploading(); //skipping device event log
            startADHOCDataUploading();
        }
        else {
            autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING,false).apply();
            System.out.println("People event Log failed ************************************************* ");
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg),"");
        }


    }

    public void startDeviceEventLogUploading()
    {
        /*deviceEventLogDAO=new DeviceEventLogDAO(AutoSyncService.this);
        deviceEventLogArrayList=new ArrayList<>();
        deviceEventLogArrayList=deviceEventLogDAO.getUnsyncDeviceEvents(loginPref.getLong(Constants.LOGIN_SITE_ID,-1));
        if(deviceEventLogArrayList!=null && deviceEventLogArrayList.size()>0)
        {
            System.out.println("deviceEventLogArrayList : "+deviceEventLogArrayList.size());
            for(int i=0;i<deviceEventLogArrayList.size();i++)
            {
                DeviceEventLog deviceEventLog=new DeviceEventLog();
                deviceEventLog=deviceEventLogArrayList.get(i);

                DeviceEventLogAsyntask deviceEventLogAsyntask=new DeviceEventLogAsyntask(AutoSyncService.this,this,deviceEventLog);
                deviceEventLogAsyntask.execute();

            }
        }
        else
            finishAllDeviceEventLogUpload();*/


        DeviceEventLogAsyntask deviceEventLogAsyntask=new DeviceEventLogAsyntask(AutoSyncService.this,this);
        deviceEventLogAsyntask.execute();
    }

    @Override
    public void finishAllDeviceEventLogUpload() {
        System.out.println("Device log data Compeleted or dont have it ");
        startADHOCDataUploading();
    }

    @Override
    public void finishDeviceEventLogUpload(int status) {
        /*if(status==0)
            startDeviceEventLogUploading();
        else
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg));*/

        if(status==0) {
            System.out.println("Device log data Compeleted or dont have it ---------------------------------------------------------------------------");
            startADHOCDataUploading();
        }
        else {
            System.out.println("Device event Log failed ************************************************* ");
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg),"");
        }
    }



    public void startADHOCDataUploading()
    {
        /*jobNeedDAO=new JobNeedDAO(AutoSyncService.this);
        adhocJobArrayList=new ArrayList<>();
        adhocJobArrayList=jobNeedDAO.getUnsyncJobList("'"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"+Constants.JOB_NEED_IDENTIFIER_TASK+"','"+Constants.JOB_NEED_IDENTIFIER_TICKET+"','"+Constants.JOB_NEED_IDENTIFIER_ASSET+"'",0);
        if(adhocJobArrayList!=null && adhocJobArrayList.size()>0)
        {
            System.out.println("adhocJobArrayList.size(): "+adhocJobArrayList.size());
            for(int i=0;i<adhocJobArrayList.size();i++) {
                JobNeed jobNeed = new JobNeed();
                jobNeed = adhocJobArrayList.get(i);

                AdhocInsertAsynctask adhocInsertAsynctask=new AdhocInsertAsynctask(AutoSyncService.this, this, jobNeed);
                adhocInsertAsynctask.execute();
            }
        }
        else
            finishAllAdhocInsertUpload();*/
        System.out.println("Thread: startADHOCDataUploading() current thread" + Thread.currentThread().getName());

        AdhocInsertAsynctask adhocInsertAsynctask=new AdhocInsertAsynctask(AutoSyncService.this, this);
        adhocInsertAsynctask.execute();
    }

    @Override
    public void finishAllAdhocInsertUpload() {
        System.out.println("ADHOC Data Compeleted or dont have it ");
        startIRDataUploading();
    }

    @Override
    public void finishAdhocInsertUpload(int status) {
        /*if(status==0)
            startADHOCDataUploading();
        else
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg));*/

        if(status==0) {
            System.out.println("ADHOC Data Compeleted or dont have it ---------------------------------------------------------------------------");
            startIRDataUploading();
        }
        else {
            System.out.println("Adhoc Log failed ************************************************* ");
            autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING,false).apply();
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg),"");
        }
    }

    public void startIRDataUploading()
    {
        /*jobNeedDAO=new JobNeedDAO(AutoSyncService.this);
        incidentReportArrayList=new ArrayList<>();
        incidentReportArrayList=jobNeedDAO.getUnsyncIRList();
        if(incidentReportArrayList!=null && incidentReportArrayList.size()>0)
        {
            System.out.println("incidentReportArrayList.size(): "+incidentReportArrayList.size());
            if(incidentReportArrayList!=null && incidentReportArrayList.size()>0)
            {
                for(int i=0;i<incidentReportArrayList.size();i++)
                {
                    JobNeed jobNeed=new JobNeed();
                    jobNeed=incidentReportArrayList.get(i);

                    JobNeedInsertAsynctask jobNeedInsertAsynctask=new JobNeedInsertAsynctask(AutoSyncService.this,this, jobNeed);
                    jobNeedInsertAsynctask.execute();
                }
            }
        }
        else
            finishAllJobNeedInsertUpload();*/

        JobNeedInsertAsynctask jobNeedInsertAsynctask=new JobNeedInsertAsynctask(AutoSyncService.this,this);
        jobNeedInsertAsynctask.execute();

    }

    @Override
    public void finishAllJobNeedInsertUpload() {
        System.out.println("IR Data Compeleted or dont have it ");
        startJobNeedUpdateDataUploading();
    }

    @Override
    public void finishJobNeedInsertUpload(int status) {
        /*if(status==0)
            startIRDataUploading();
        else
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg));*/

        if(status==0) {
            System.out.println("IR Data Compeleted or dont have it ---------------------------------------------------------------");
            startJobNeedUpdateDataUploading();
        }
        else {
            System.out.println("IR event Log failed ************************************************* ");
            autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING,false).apply();
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg),"");
        }
    }

    public void startJobNeedUpdateDataUploading()
    {
        /*jobNeedDAO=new JobNeedDAO(AutoSyncService.this);
        jobUpdateArrayList=new ArrayList<JobNeed>();
        jobUpdateArrayList=jobNeedDAO.getUnsyncJobList("'"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"+Constants.JOB_NEED_IDENTIFIER_TASK+"','"+Constants.JOB_NEED_IDENTIFIER_TICKET+"','"+Constants.JOB_NEED_IDENTIFIER_SITEREPORT+"'",2);

        if(jobUpdateArrayList!=null && jobUpdateArrayList.size()>0)
        {
            for(int i=0;i<jobUpdateArrayList.size();i++)
            {
                JobNeed jobNeed=new JobNeed();
                jobNeed=jobUpdateArrayList.get(i);

                JobneedUpdateAsyntask jobneedUpdateAsyntask=new JobneedUpdateAsyntask(AutoSyncService.this, this, jobNeed);
                jobneedUpdateAsyntask.execute();

            }
        }
        else
            finishAllJobneedUpdateUpload();*/

        JobneedUpdateAsyntask jobneedUpdateAsyntask=new JobneedUpdateAsyntask(AutoSyncService.this, this);
        jobneedUpdateAsyntask.execute();
    }

    @Override
    public void finishAllJobneedUpdateUpload() {
        System.out.println("JOB need updated Data Compeleted or dont have it ");
        startReplyDataUploading();
    }

    @Override
    public void finishJobneedUpdateUpload(int status) {
       /* if(status==0)
            startJobNeedUpdateDataUploading();
        else
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg));*/

        if(status==0) {
            System.out.println("JOB need updated Data Compeleted or dont have it ------------------------------------------------------------------------");
            startReplyDataUploading();
        }
        else {
            System.out.println("jobneed update Log failed ************************************************* ");
            autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING,false).apply();
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg),"");
        }
    }
//===================
    public void startReplyDataUploading()
    {
        /*attachmentDAO=new AttachmentDAO(AutoSyncService.this);
        jobNeedReplyAttachmentArrayList=new ArrayList<Attachment>();
        jobNeedReplyAttachmentArrayList=attachmentDAO.getUnsyncJobNeedReplyAttachments();
        if(jobNeedReplyAttachmentArrayList!=null && jobNeedReplyAttachmentArrayList.size()>0)
        {
            for(int i=0;i<jobNeedReplyAttachmentArrayList.size();i++)
            {
                Attachment attachment=new Attachment();
                attachment=jobNeedReplyAttachmentArrayList.get(i);
                JobNeedReplyAsyntask jobNeedReplyAsyntask=new JobNeedReplyAsyntask(AutoSyncService.this, this, attachment);
                jobNeedReplyAsyntask.execute();
            }
        }
        else
            finishAllJobNeedReplyUpload();*/

        JobNeedReplyAsyntask jobNeedReplyAsyntask=new JobNeedReplyAsyntask(AutoSyncService.this, this);
        jobNeedReplyAsyntask.execute();
    }

    @Override
    public void finishAllJobNeedReplyUpload() {
        System.out.println("Reply Data Compeleted or dont have it ");
        counterVal=0;
        //startDataDownloading();
    }

    @Override
    public void finishJobNeedReplyUpload(int status) {
        /*if(status==0)
            startReplyDataUploading();
        else
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg));*/

        if(status==0) {

            //showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_success_msg));
            //jobAlertBroadcast.SetAlarm(AutoSyncService.this);
            /*System.out.println("Reply Data Compeleted or dont have it ----------------------------------------------------------------");
            counterVal=0;
            startDataDownloading();*/

            startSiteAuditReportUploading();
        }
        else {
            System.out.println("reply Log failed ************************************************* ");
            autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING,false).apply();
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg),"");
        }
    }
//=====================
    public void startSiteAuditReportUploading()
    {
        SiteReportLogAsynctask siteReportLogAsynctask=new SiteReportLogAsynctask(AutoSyncService.this, this);
        siteReportLogAsynctask.execute();
    }

    @Override
    public void finishAllSiteReportUpload() {

    }

    @Override
    public void finishSiteReportUpload(int status) {
        if(status==0)
        {
            System.out.println("Site Audit Data Compeleted or dont have it ------------------------------------------------------------------------");
            startPersonLoggerUploading();
        }
        else {
            System.out.println("report Log failed ************************************************* ");
            autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING,false).apply();
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg),"");
        }
    }
//========================
    private void startPersonLoggerUploading()
    {
        PersonLoggerAsyncTask personLoggerAsyncTask=new PersonLoggerAsyncTask(AutoSyncService.this, this);
        personLoggerAsyncTask.execute();
    }

    @Override
    public void finishAllPersonLoggerUpload() {

    }

    @Override
    public void finishPersonLoggerUpload(int status) {
        if(status==0)
        {
            System.out.println("Person logger Data Compeleted or dont have it ------------------------------------------------------------------------");

            Intent startIntent = new Intent(AutoSyncService.this, UploadImageService.class);
            startService(startIntent);

            autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING,false).apply();

        }
        else {
            System.out.println("report Log failed ************************************************* ");
            autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING,false).apply();
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg),"");
        }
    }
//=======================
    public void startDataDownloading()
    {
        /*if(syncReturnValue==0 && counterVal<SyncCount.values().length)
        {
            System.out.println("Countervalue: "+counterVal);
            counterName=getSyncServiceName(counterVal);
            System.out.println("CounterName: "+counterName);
            DownloadDataAsynctask downloadDataAsynctask=new DownloadDataAsynctask(AutoSyncService.this, counterVal, counterName,this);
            downloadDataAsynctask.execute();
        }*/

        DownloadDataAsynctask downloadDataAsynctask=new DownloadDataAsynctask(AutoSyncService.this,this);
        downloadDataAsynctask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

    }

    @Override
    public void finishDownloadingData(int status) {
        /*if(status==0) {
            syncReturnValue = status;
            counterVal++;
            startDataDownloading();
        }
        else {
            System.out.println("Sync failed");
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg));
        }*/

        if(status==0) {

        }
        else {
            System.out.println("Sync failed");
            showNotification(getResources().getString(R.string.alerttitle), getResources().getString(R.string.sync_dialog_failed_msg),"");
        }
    }

    /*private String getSyncServiceName(int counterVal)
    {
        for(SyncCount c: SyncCount.values())
        {
            if(c.ordinal()==counterVal)
                return c.name();
        }
        return "";
    }*/
}
