package com.youtility.intelliwiz20.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;

import com.youtility.intelliwiz20.AsyncTask.UploadAttachmentLogAsyncTask;
import com.youtility.intelliwiz20.AsyncTask.UploadPeopleEventLogAsyncTask;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadAttachmentLogDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadPELogDataListener;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.Utils.CheckNetwork;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UploadSelfAttendanceService extends IntentService implements IUploadPELogDataListener, IUploadAttachmentLogDataListener {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS


    private static final String ACTION_FOO = "com.youtility.intelliwiz20.Services.action.FOO";
    private static final String ACTION_BAZ = "com.youtility.intelliwiz20.Services.action.BAZ";


    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.youtility.intelliwiz20.Services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.youtility.intelliwiz20.Services.extra.PARAM2";



    private long pelogId=-1;
    private String pelogDatetime=null;
    private PeopleEventLogDAO peopleEventLogDAO;
    private AttachmentDAO attachmentDAO;
    private CheckNetwork checkNetwork;

    public UploadSelfAttendanceService() {
        super("UploadSelfAttendanceService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        peopleEventLogDAO=new PeopleEventLogDAO(UploadSelfAttendanceService.this);
        attachmentDAO=new AttachmentDAO(UploadSelfAttendanceService.this);
        checkNetwork=new CheckNetwork(UploadSelfAttendanceService.this);
        //startActionFoo(UploadSelfAttendanceService.this);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context) {
        Intent intent = new Intent(context, UploadSelfAttendanceService.class);
        intent.setAction(ACTION_FOO);
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
        Intent intent = new Intent(context, UploadSelfAttendanceService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            pelogId=intent.getLongExtra("PELOGID",-1);
            handleActionFoo(peopleEventLogDAO.getEventLog(pelogId));
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(PeopleEventLog peopleEventLog) {
        if(peopleEventLog!=null && peopleEventLog.getPelogid()!=-1) {
            pelogDatetime=peopleEventLog.getDatetime();
            if(checkNetwork.isNetworkConnectionAvailable()) {
                UploadPeopleEventLogAsyncTask uploadPeopleEventLogAsyncTask = new UploadPeopleEventLogAsyncTask(UploadSelfAttendanceService.this, peopleEventLog, this);
                uploadPeopleEventLogAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void uploadSOSPELog(int status, long returnId) {
        if(status==0 && returnId!=-1)
        {
            peopleEventLogDAO.changeSOSSyncStatus(-1,returnId,pelogDatetime);
            attachmentDAO.changePeopleEventLogReturnID(String.valueOf(returnId),String.valueOf(pelogId));
            uploadAttendanceAttachment(returnId);
        }

    }

    private void uploadAttendanceAttachment(long retId)
    {
        if(checkNetwork.isNetworkConnectionAvailable())
        {
            UploadAttachmentLogAsyncTask uploadAttachmentLogAsyncTask=new UploadAttachmentLogAsyncTask(UploadSelfAttendanceService.this,retId,pelogId,this);
            uploadAttachmentLogAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }

    }

    @Override
    public void uploadAttachmentLog(int status, long returnId) {

    }
}
