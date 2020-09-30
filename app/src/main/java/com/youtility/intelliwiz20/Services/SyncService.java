package com.youtility.intelliwiz20.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.youtility.intelliwiz20.Interfaces.SyncInterface;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SyncService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    private static final String ACTION_FOO = "com.youtility.intelliwiz20.Services.action.FOO";
    private static final String ACTION_BAZ = "com.youtility.intelliwiz20.Services.action.BAZ";


    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.youtility.intelliwiz20.Services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.youtility.intelliwiz20.Services.extra.PARAM2";



    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_FAILED = 1;
    public static final int RESULT_PROGRESS = 2;
    public static boolean CANCEL = false;
    public static boolean isRunning = false;
    private Messenger messenger;
    private SharedPreferences applicationSyncTimePref;
    private String currentSyncTime=null;
    private DateFormat df;

    public SyncService() {
        super("SyncService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);

            }
        }


        applicationSyncTimePref=getApplicationContext().getSharedPreferences(Constants.APPLICATION_SYNC_TIME_PREF, Context.MODE_PRIVATE);
        currentSyncTime=applicationSyncTimePref.getString(Constants.APPLICATION_CURR_SYNC_TIME,"1970-01-01 11:23:02");
        messenger = (Messenger) intent.getExtras().get("MESSENGER");
        //sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));

        try
        {
            //1-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m1= new MyThread(this, "assetMaster",currentSyncTime);
            Thread t1= new Thread(m1);
            t1.start();
            //2-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m2= new MyThread(this, "jobNeedMaster",currentSyncTime);
            Thread t2= new Thread(m2);
            t2.start();
            //3-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m3= new MyThread(this, "jobNeedDetailsMaster",currentSyncTime);
            Thread t3= new Thread(m3);
            t3.start();
            //4-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m4= new MyThread(this, "typeAssistMaster",currentSyncTime);
            Thread t4= new Thread(m4);
            t4.start();
            //5-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m5= new MyThread(this, "geoFenceDetailsMaster",currentSyncTime);
            Thread t5= new Thread(m5);
            t5.start();

            //-----------------------------------------------joined and chk return value
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();
            if (!m1.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_assetmaster_failed));
                throw new Exception();
                //return;
            }
            if (!m2.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_jobNeedmaster_failed));
                throw new Exception();
                //return;
            }
            if (!m3.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_jobNeedDetailsmaster_failed));
                throw new Exception();
                //return;
            }
            if (!m4.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_typeassistmaster_failed));
                throw new Exception();
                //return;
            }
            if (!m5.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_geofenceDetailsmaster_failed));
                throw new Exception();
                //return;
            }

            //6-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m6= new MyThread(this, "peopleDetailMaster",currentSyncTime);
            Thread t6= new Thread(m6);
            t6.start();
            //7-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m7= new MyThread(this, "groupDetailMaster",currentSyncTime);
            Thread t7= new Thread(m7);
            t7.start();
            //8-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m8= new MyThread(this, "attendanceHistoryMaster",currentSyncTime);
            Thread t8= new Thread(m8);
            t8.start();
            //9-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m9= new MyThread(this, "questionMaster",currentSyncTime);
            Thread t9= new Thread(m9);
            t9.start();
            //10-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m10= new MyThread(this, "questionSetMaster",currentSyncTime);
            Thread t10= new Thread(m10);
            t10.start();

            //-----------------------------------------------joined and chk return value
            t6.join();
            t7.join();
            t8.join();
            t9.join();
            t10.join();

            if (!m6.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_peoplemaster_failed));
                //return;
                throw new Exception();
            }
            if (!m7.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_groupemaster_failed));
                //return;
                throw new Exception();
            }
            if (!m8.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_attendancehisotymaster_failed));
                //return;
                throw new Exception();
            }
            if (!m9.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_questionmaster_failed));
                //return;
                throw new Exception();
            }
            if (!m10.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_questionsetmaster_failed));
                //return;
                throw new Exception();
            }

            //11-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m11= new MyThread(this, "questionSetBelongingMaster",currentSyncTime);
            Thread t11= new Thread(m11);
            t11.start();
            //12-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m12= new MyThread(this, "peopleGroupBelongingMaster",currentSyncTime);
            Thread t12= new Thread(m12);
            t12.start();
            //13-------------------------------------------------------------------------------------------------------
            sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_download_msg));
            MyThread m13= new MyThread(this, "siteMaster",currentSyncTime);
            Thread t13= new Thread(m13);
            t13.start();
            //-----------------------------------------------joined and chk return value
            t11.join();
            t12.join();
            t13.join();

            if (!m11.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_questionsetbelongingmaster_failed));
                //return;
                throw new Exception();
            }

            if (!m12.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_peoplegroupbelongingmaster_failed));
                //return;
                throw new Exception();
            }

            if (!m13.retVal() || CANCEL)
            {
                sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_sitesmaster_failed));
                //return;
                throw new Exception();
            }

            //--------------------------------------------------------------------------------------------------------------
//=========================================================================================================================================

//=========================================================================================================================================

        } catch (Exception e) {
            e.printStackTrace();
        }

        sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_update_msg));
        UpdateTables up = new UpdateTables(this);
        if (!up.updateTable()) {
            sendMessage(RESULT_FAILED, getResources().getString(R.string.sync_dataupdating_failed));
            return;
        }
        //sendMessage(RESULT_PROGRESS, getResources().getString(R.string.sync_dialog_finalize_msg));


        /*try {
            up.copyDataBase();
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        sendMessage(RESULT_SUCCESS, "Success");

        Intent startIntent = new Intent(SyncService.this, UploadImageService.class);
        startService(startIntent);

        //add sync time
        applicationSyncTimePref.edit().putString(Constants.APPLICATION_PREV_SYNC_TIME, applicationSyncTimePref.getString(Constants.APPLICATION_CURR_SYNC_TIME,"1970-01-01 11:23:02")).commit();
        applicationSyncTimePref.edit().putString(Constants.APPLICATION_CURR_SYNC_TIME,df.format(System.currentTimeMillis())).commit();

        //isRunning=false;

    }

    public void sendMessage(int result, String message) {
        Message msg = Message.obtain();
        System.out.println("sync service send message result: "+result+" Message: "+message );
        msg.arg1 = result;
        msg.obj = message;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            Log.w(getClass().getName(), "Exception sending message", e);
        }



    }

    private static class MyThread implements Runnable {
        private boolean ret = false;
        private SyncInterface Syncapi;
        private String methodToCall;
        private  YoutilityServer youServer;
        private SharedPreferences pref;

        public MyThread( Context context, String methodToCall, String date)
        {
            youServer = new YoutilityServer(context);
            Syncapi = new RestApi( youServer, context,date);
            this.methodToCall=methodToCall;
            System.out.println("methodToCall: "+methodToCall);
        }

        @Override
        public void run() {

            try
            {
                if(methodToCall.equals("assetMaster"))
                {
                    ret=Syncapi.assetMaster();
                    System.out.println("assetMaster Ret Val: "+ret);
                }
                else if(methodToCall.equals("jobNeedMaster"))
                {
                    ret=Syncapi.jobNeedMaster();
                }
                else if(methodToCall.equals("jobNeedDetailsMaster"))
                {
                    ret=Syncapi.jobNeedDetailsMaster();
                }
                else if(methodToCall.equals("typeAssistMaster"))
                {
                    ret=Syncapi.typeAssistMaster();
                }
                else if(methodToCall.equals("geoFenceDetailsMaster"))
                {
                    ret=Syncapi.geoFenceDetailsMaster();
                }
                else if(methodToCall.equals("peopleDetailMaster"))
                {
                    ret=Syncapi.peopleDetailMaster();
                }
                else if(methodToCall.equals("groupDetailMaster"))
                {
                    ret=Syncapi.groupDetailMaster();
                }
                else if(methodToCall.equals("attendanceHistoryMaster"))
                {
                    ret=Syncapi.attendanceHistoryMaster();
                }
                else if(methodToCall.equals("questionMaster"))
                {
                    ret=Syncapi.questionMaster();
                }
                else if(methodToCall.equals("questionSetMaster"))
                {
                    ret=Syncapi.questionSetMaster();
                }//
                else if(methodToCall.equals("questionSetBelongingMaster"))
                {
                    ret=Syncapi.questionSetBelongingMaster();
                }//
                else if(methodToCall.equals("peopleGroupBelongingMaster"))
                {
                    ret=Syncapi.peopleGroupBelongingMaster();
                }
                else if(methodToCall.equals("siteMaster"))
                {
                    ret=Syncapi.siteMaster();
                }

            } catch (Exception e) {
                ret=false;
                e.printStackTrace();
                //Util.log(""+getClass().getName()+" Error : ", e);
                System.out.println("Run Catch: "+e.toString());
            }
        }

        public boolean retVal()
        {
            return ret;
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
