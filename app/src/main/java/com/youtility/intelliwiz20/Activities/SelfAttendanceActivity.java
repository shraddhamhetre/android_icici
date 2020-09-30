package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.AsyncTask.UploadAttachmentLogAsyncTask;
import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Interfaces.IUploadAttachmentLogDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadPELogDataListener;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Services.UploadSelfAttendanceService;
import com.youtility.intelliwiz20.Utils.CameraPreview;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SelfAttendanceActivity extends AppCompatActivity implements IDialogEventListeners,
        View.OnClickListener, IUploadPELogDataListener, IUploadAttachmentLogDataListener, ConnectivityReceiver.ConnectivityReceiverListener {
    private Camera camera;
    private CameraPreview cpreview;
    private Boolean isSDPresent=false;
    private String extStorageDirectory="";
    private String peopleReturnID=null;
    private String imgeURI=null;
    private SharedPreferences loginDetailPref;
    private CheckNetwork checkNetwork;
    private String userName;
    private long peopleID;
    private String punchType=null;
    private PeopleEventLogDAO peopleEventLogDAO;
    private AttachmentDAO attachmentDAO;
    private TypeAssistDAO typeAssistDAO;
    private long currentTimestamp=-1;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences selfAttendancePref;
    private TextView currentDateTextView;
    private TextView userNameTextView;
    private Button punchIn, punchOut;
    private PeopleEventLog peopleEventLog;
    MenuItem miActionProgressItem;
    private ConnectivityReceiver connectivityReceiver;
    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;
    private SharedPreferences autoSyncPref;

    //TextToSpeech textToSpeech;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.progress_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentTimestamp=System.currentTimeMillis();

        //test====

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(SelfAttendanceActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        //end test=====
        autoSyncPref=getSharedPreferences(Constants.AUTO_SYNC_PREF, MODE_PRIVATE);

        //punchType=getIntent().getStringExtra("PUNCH_TYPE");

        peopleEventLogDAO=new PeopleEventLogDAO(SelfAttendanceActivity.this);
        attachmentDAO=new AttachmentDAO(SelfAttendanceActivity.this);
        typeAssistDAO=new TypeAssistDAO(SelfAttendanceActivity.this);

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        selfAttendancePref=getSharedPreferences(Constants.SELF_ATTENDANCE_PREF, MODE_PRIVATE);

        checkNetwork=new CheckNetwork(SelfAttendanceActivity.this);

        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        userName=loginDetailPref.getString(Constants.LOGIN_PEOPLE_NAME,"");

        currentDateTextView=(TextView)findViewById(R.id.currentdateTextView);
        currentDateTextView.setText(CommonFunctions.getFormatedDateWithoutTime(currentTimestamp));

        userNameTextView=(TextView)findViewById(R.id.userNameTextView);
        userNameTextView.setText(userName);

        punchIn=(Button)findViewById(R.id.punchInButton);
        punchOut=(Button)findViewById(R.id.punchOutButton);

        punchIn.setOnClickListener(this);
        punchOut.setOnClickListener(this);

        if(selfAttendancePref.getString(Constants.SELF_ATTENDANCE_STATUS,"").equalsIgnoreCase(Constants.ATTENDANCE_PUNCH_TYPE_IN)){
            //punchIn.setEnabled(false);
            punchIn.setVisibility(View.INVISIBLE);
            startAnimation(punchOut);
        }

        else if(selfAttendancePref.getString(Constants.SELF_ATTENDANCE_STATUS,"").equalsIgnoreCase(Constants.ATTENDANCE_PUNCH_TYPE_OUT)){
            startAnimation(punchIn);
            punchOut.setVisibility(View.INVISIBLE);
            //punchOut.setEnabled(false);
        }


        if(checkcamera(this))
        {
            camera=getCameraInstance(SelfAttendanceActivity.this);

            cpreview=new CameraPreview(SelfAttendanceActivity.this, SelfAttendanceActivity.this, camera);
            LinearLayout preview=(LinearLayout) findViewById(R.id.camera_preview);

            preview.addView(cpreview);

        }

        isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        extStorageDirectory = Environment.getExternalStorageDirectory().toString();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        camera.takePicture(null, null, mPicture);
                    }
                }, 0);
                Snackbar.make(view, "Capture Image", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*textToSpeech=new TextToSpeech(SelfAttendanceActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.getDefault());
                textToSpeech.setSpeechRate(2f);
            }
        });*/
    }

    @Override
    protected void onStop() {
        /*if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }*/
        super.onStop();
    }

    private void startAnimation(Button button)
    {
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_blink_animation);
        button.startAnimation(startAnimation);
    }

    public static Camera getCameraInstance(Context context){
        Camera c = null;
        try {

            int cameras= Camera.getNumberOfCameras();
            System.out.println("Cameras "+cameras);
	    	/*Toast.makeText(context, "cameras"+cameras, Toast.LENGTH_LONG).show();*/
            if(cameras > 1)
            {
                c = Camera.open(1); // attempt to get a Camera instance
            }else{
                Toast.makeText(context, context.getResources().getString(R.string.no_front_camera), Toast.LENGTH_LONG).show();
            }

        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private boolean checkcamera(Context context)
    {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            byte[] image=data;

            Bitmap bitmap = BitmapFactory.decodeByteArray(image , 0, image.length);
            System.out.println(" " +bitmap.getWidth() + " height "+bitmap.getHeight());
            int rotation = getWindowManager().getDefaultDisplay().getRotation();

            if(rotation == Surface.ROTATION_0){
                Matrix matrix = new Matrix();

                matrix.postRotate(-90);
                int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                System.out.println("nh==: "+nh);
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 512, nh, false);
                bitmap=Bitmap.createBitmap(bitmap1 , 0, 0, bitmap1 .getWidth(), bitmap1.getHeight(), matrix, true);

                //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

                //Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            }
            showProgressBar();

            PictureTakenAsyntask pictureTakenAsyntask=new PictureTakenAsyntask(bitmap);
            pictureTakenAsyntask.execute();
            //saveToSDCard(bitmap);

            /*try {
                // thread to sleep for 1000 milliseconds plus 500 nanoseconds
                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println(e.toString());
            }*/

            System.out.println("data.length: "+data.length);

        }
    };

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CommonFunctions.showSnack(SelfAttendanceActivity.this, isConnected,punchIn);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("=====onresume self att=====");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        /*register connection status listener*/
        Baseclass.getInstance().setConnectivityListener(this);
    }

    private class PictureTakenAsyntask extends AsyncTask<Void, Integer, Void>
    {
        Bitmap bitmap1;
        //ProgressDialog dialog;
        public PictureTakenAsyntask(Bitmap bitmap1)
        {
            this.bitmap1=bitmap1;
            //dialog = new ProgressDialog(SelfAttendanceActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*dialog.setMessage(getResources().getString(R.string.please_wait));
            dialog.show();*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            insertPeopleEventLogRecord();
            saveToSDCard(bitmap1);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ///super.onPostExecute(aVoid);
            /*if(dialog!=null && dialog.isShowing())
                dialog.dismiss();*/

            //callAlertMessage();
            uploadAttendanceData();
        }
    }


    private void uploadAttendanceData()
    {
        hideProgressBar();

        Intent serviceIntent=new Intent(SelfAttendanceActivity.this, UploadSelfAttendanceService.class);
        serviceIntent.putExtra("PELOGID", currentTimestamp);
        startService(serviceIntent);
        callAlertMessage();

        /*if(checkNetwork.isNetworkConnectionAvailable()) {
            UploadPeopleEventLogAsyncTask uploadPeopleEventLogAsyncTask = new UploadPeopleEventLogAsyncTask(SelfAttendanceActivity.this, peopleEventLog, this);
            uploadPeopleEventLogAsyncTask.execute();
        }
        else
        {
            peopleEventLog=null;
            hideProgressBar();
            callAlertMessage();
        }*/
    }

    @Override
    public void uploadSOSPELog(int status, long returnId) {
        if(status==0 && returnId!=-1)
        {
            peopleEventLogDAO.changeSOSSyncStatus(-1,returnId,peopleEventLog.getDatetime());
            attachmentDAO.changePeopleEventLogReturnID(String.valueOf(returnId),String.valueOf(currentTimestamp));
            uploadAttendanceAttachment(returnId);
        }
        else
        {
            peopleEventLog=null;
            hideProgressBar();
            callAlertMessage();
        }


    }

    private void uploadAttendanceAttachment(long retId)
    {
        if(checkNetwork.isNetworkConnectionAvailable())
        {
            UploadAttachmentLogAsyncTask uploadAttachmentLogAsyncTask=new UploadAttachmentLogAsyncTask(SelfAttendanceActivity.this,retId,currentTimestamp,this);
            uploadAttachmentLogAsyncTask.execute();
        }
        else
        {
            peopleEventLog=null;
            hideProgressBar();
            callAlertMessage();
        }
    }

    @Override
    public void uploadAttachmentLog(int status, long returnId) {
        peopleEventLog=null;
        hideProgressBar();
        callAlertMessage();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(camera!=null)
            camera.release();

        setResult(RESULT_CANCELED);
        finish();
    }

    private void releaseCamera()
    {
        if(camera!=null)
            camera.release();
    }


    private void insertPeopleEventLogRecord()
    {
        //accuracy-, deviceid-, datetime-, gpslocation-, photorecognitionthreshold-,photorecognitionscore-, " +
       // "photorecognitiontimestamp-, photorecognitionserviceresponse-,facerecognition-, cdtz-, mdtz-, isdeleted-, cuser-, muser-, peoplecode-, peventtype-, punchstatus-, verifiedby-) " +
        peopleEventLog=new PeopleEventLog();
        peopleEventLog.setPelogid(currentTimestamp);
        peopleEventLog.setAccuracy(Float.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ACCURACY,"-1")));
        //peopleEventLog.setDeviceid(deviceRelatedPref.getLong(Constants.DEVICE_IMEI,-1));
        peopleEventLog.setDeviceid(deviceRelatedPref.getString(Constants.DEVICE_IMEI,"-1"));

        peopleEventLog.setDatetime(String.valueOf(currentTimestamp));
        peopleEventLog.setGpslocation(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        peopleEventLog.setPhotorecognitionthreshold(-1);
        peopleEventLog.setPhotorecognitionscore(-1);
        peopleEventLog.setPhotorecognitiontimestamp(String.valueOf(currentTimestamp));
        peopleEventLog.setPhotorecognitionserviceresponse(null);
        peopleEventLog.setFacerecognition("false");
        peopleEventLog.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        peopleEventLog.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        //peopleEventLog.setIsdeleted("false");
        peopleEventLog.setCuser(peopleID);
        peopleEventLog.setMuser(peopleID);
        peopleEventLog.setPeopleid(peopleID);
        peopleEventLog.setPeventtype(typeAssistDAO.getEventTypeID("SELF", Constants.IDENTIFIER_ATTENDANCE));
        peopleEventLog.setPunchstatus(typeAssistDAO.getEventTypeID(punchType, Constants.IDENTIFIER_PUNCHSTATUS));
        peopleEventLog.setVerifiedby(-1);
        peopleEventLog.setScanPeopleCode(peopleID+"");
        peopleEventLog.setGfid(-1);
        peopleEventLog.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
        peopleEventLog.setDistance(0);
        peopleEventLog.setDuration(0);
        peopleEventLog.setExpamt(0.0);
        peopleEventLog.setReference("");
        peopleEventLog.setRemarks("");
        peopleEventLog.setTransportmode(-1);
        peopleEventLog.setOtherlocation("");
        peopleEventLogDAO.insertRecord(peopleEventLog);

        selfAttendancePref.edit().putString(Constants.SELF_ATTENDANCE_STATUS,punchType).apply();

    }

    private void callAlertMessage()
    {
        //textToSpeech.speak("Thank you "+loginDetailPref.getString(Constants.LOGIN_PEOPLE_NAME,""),TextToSpeech.QUEUE_FLUSH,null);
        CustomAlertDialog alertView=new CustomAlertDialog(SelfAttendanceActivity.this,this);
        alertView.commonDialog(getResources().getString(R.string.selfie_attendance_alert_title),getResources().getString(R.string.selfie_attendance_alert_message));
    }



    private void saveToSDCard(Bitmap pics)
    {
        OutputStream outStream = null;
        if(isSDPresent)
        {
            /*UUID uuid=UUID.randomUUID();
            String randomString=uuid.toString();*/
            //String randomString=peopleID+"_"+System.currentTimeMillis()+"_Attendance";
            String randomString=CommonFunctions.getFileNameFromDate(System.currentTimeMillis());
            String dirPath=extStorageDirectory+"/"+Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/";

            try {
                if(CommonFunctions.checkFileExists(dirPath))
                {
                    System.out.println("Directory already exits created");
                }
                else
                {
                    File dir = new File(dirPath);
                    dir.mkdirs();
                    System.out.println("Directory created");
                }
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            imgeURI=null;
            File file = new File(dirPath+randomString+".png");
            try {
                outStream = new FileOutputStream(file);
                pics.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                imgeURI=dirPath+randomString+".png";
                System.out.println("imgeURI" +imgeURI);
                int fileSize=(int) file.length();
                Bitmap bitmapOrg1 = BitmapFactory.decodeFile(imgeURI);
                Bitmap immagex=bitmapOrg1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(b);
                fos.close();


                Attachment attachment=new Attachment();
                attachment.setAttachmentid(currentTimestamp);
                attachment.setFilePath(imgeURI);
                attachment.setFileName(randomString+".png");
                attachment.setNarration("ATTACHMENT");
                attachment.setGpslocation(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
                attachment.setDatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                attachment.setCuser(peopleID);
                attachment.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                attachment.setMuser(peopleID);
                attachment.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                attachment.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_ATTACHMENT, Constants.IDENTIFIER_ATTACHMENT));
                //attachment.setIsdeleted("False");
                attachment.setOwnerid(peopleID);
                attachment.setOwnername(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_OWNER_TYPE_PEOPLEEVENTLOG, Constants.IDENTIFIER_OWNER));
                attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+"peopleeventlog/"+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis()));
                attachment.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));
                attachmentDAO.insertCommonRecord(attachment);

                Log.e("image stored"," "+ file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

			} catch (IOException e) {
                e.printStackTrace();

			}
        }
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                releaseCamera();
            }
        }, 500);
        System.out.println("camera activity finish");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }


    @Override
    public void onClick(View v) {
        int accessValue = CommonFunctions.isAllowToAccessModules(SelfAttendanceActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        System.out.println("==========="+accessValue);
        System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

        if(CommonFunctions.isPermissionGranted(SelfAttendanceActivity.this)) {
/*
            if(!autoSyncPref.getBoolean(Constants.IS_AUTO_SYNC_RUNNING,true)) {
*/
                if (accessValue == 0) {
                    switch (v.getId()) {
                        case R.id.punchInButton:
                            //textToSpeech.speak("Thank you "+loginDetailPref.getString(Constants.LOGIN_PEOPLE_NAME,""),TextToSpeech.QUEUE_FLUSH,null);
                            punchType = Constants.ATTENDANCE_PUNCH_TYPE_IN;
                            punchIn.setEnabled(false);
                            v.clearAnimation();
                            takeAttendance();
                            break;
                        case R.id.punchOutButton:
                            punchType = Constants.ATTENDANCE_PUNCH_TYPE_OUT;
                            punchOut.setEnabled(false);
                            v.clearAnimation();
                            takeAttendance();
                            break;
                    }
                } else if (accessValue == 1) {
                    customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autodatetimeMessage));
                } else if (accessValue == 2) {
                    customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autoGPSMessage), accessValue);
                    System.out.println("===========" + accessValue);
                } else if (accessValue == 3) {
                    customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autowifiMessage), accessValue);
                    System.out.println("===========" + accessValue);
                } else if (accessValue == 4) {
                    customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autonetworkMessage), accessValue);
                    System.out.println("===========" + accessValue);
                } else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
                    customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
                    System.out.println("===========lat long==0.0");
                }
            /*}else {
                customAlertDialog.commonDialog1("Alert","Please wait... Synchronization in progress");
            }*/
        }
        else
            Snackbar.make(v,getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
    }

    private void takeAttendance()
    {
        if(camera!=null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    camera.takePicture(null, null, mPicture);
                }
            }, 0);
        }
    }
}
