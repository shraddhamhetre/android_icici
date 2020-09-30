package com.youtility.intelliwiz20.Activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class VideoCaptureActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback, MediaRecorder.OnInfoListener, IDialogEventListeners {
    private int fromActivity=-1;
    private String peopleScannedCode=null;
    private long attachmentTimestamp=-1;

    private long startTime = 0L;
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;

    private Button startBtn;
    private Button listVideoBtn;
    private TextView countdownText;

    private Handler myHandler = new Handler();
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    public MediaRecorder mrec ;
    private Camera mCamera;
    private String extStorageDirectory = "";
    private boolean isSDPresent;
    public static int rotate;
    private String fileName=null;
    private String dirPath=null;
    private long jobneedid=-1;
    private SharedPreferences deviceRelatedPref;
    private String parentActivity=null;
    private String parentFolder=null;
    private SharedPreferences loginDetailPref;
    private TypeAssistDAO typeAssistDAO;
    private SharedPreferences sharedPreferences;
    private CustomAlertDialog customAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_capture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().hasExtra("JOBNEEDID"))
        {
            jobneedid=getIntent().getLongExtra("JOBNEEDID",-1);
        }


        customAlertDialog=new CustomAlertDialog(VideoCaptureActivity.this, this);

        parentActivity=getIntent().getStringExtra("PARENT_ACTIVITY");
        parentFolder=getIntent().getStringExtra("FOLDER");

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(VideoCaptureActivity.this);

        typeAssistDAO=new TypeAssistDAO(VideoCaptureActivity.this);

        fromActivity=getIntent().getIntExtra("FROM",0);
        peopleScannedCode=getIntent().getStringExtra("CODE");
        attachmentTimestamp=getIntent().getLongExtra("TIMESTAMP",-1);

        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        startBtn=(Button)findViewById(R.id.startvideocapture);
        listVideoBtn=(Button)findViewById(R.id.stopvideocapture);



        startBtn.setOnClickListener(this);
        listVideoBtn.setOnClickListener(this);

        countdownText=(TextView)findViewById(R.id.countdownTextview);

        //initMediaRecorder();
        surfaceView = (SurfaceView)findViewById(R.id.videocaptureview);
        mCamera = Camera.open();
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        isSDPresent = android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.startvideocapture:
                startRecording();
                break;
        }
    }



    private Runnable updateTimerMethod = new Runnable()
    {

        @Override
        public void run()
        {
            timeInMillies = SystemClock.uptimeMillis()-startTime;
            finalTime = timeSwap + timeInMillies;

            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            countdownText.setText("" + minutes + ":"
                    + String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliseconds));
            myHandler.postDelayed(this, 0);
        }

    };

    private void startRecording()
    {
        startBtn.setClickable(false);

        if(mCamera==null)
            mCamera = Camera.open();
        if(isSDPresent)
        {
            fileName= CommonFunctions.getFileNameFromDate(System.currentTimeMillis())+".mp4";
            dirPath=extStorageDirectory+"/"+ Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/";

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

            File file=new File(dirPath,fileName);

            Parameters params = mCamera.getParameters();
            if(VideoCaptureActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
                params.setFlashMode(Parameters.FLASH_MODE_TORCH);

            if (params.getSupportedFocusModes().contains(
                    Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            else
            {
                System.out.println("Auto focus not available");
            }

            mCamera.setParameters(params);

            mrec = new MediaRecorder();

            mCamera.lock();
            mCamera.unlock();

            mrec.setCamera(mCamera);

            mrec.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mrec.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

            CamcorderProfile camcorderProfile_HQ = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
            mrec.setProfile(camcorderProfile_HQ);

            int videoInterval=Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_VIDEO_REC_FREQ,"10000"));

            int interval=10*1000;
            System.out.println("interval "+ videoInterval);
//            mrec.setMaxDuration(videoInterval);
            mrec.setMaxDuration(interval);

            mrec.setOnInfoListener(this);

            mrec.setVideoFrameRate(30);
            mrec.setPreviewDisplay(surfaceHolder.getSurface());
            mrec.setOrientationHint(90);
            mrec.setOutputFile(dirPath+fileName);
            try {
                mrec.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mrec.start();

            startTime = SystemClock.uptimeMillis();
            myHandler.postDelayed(updateTimerMethod, 0);
        }


    }

    private void saveToSDCard()
    {
        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        //check with new database columns

        Attachment attachment=new Attachment();
        attachment.setAttachmentid(attachmentTimestamp);
        attachment.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_ATTACHMENT, Constants.IDENTIFIER_ATTACHMENT));
        attachment.setFilePath(dirPath+fileName);
        attachment.setFileName(fileName);
        attachment.setNarration(peopleScannedCode);
        attachment.setGpslocation(gpsLocation);
        attachment.setDatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        attachment.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        attachment.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setOwnername(typeAssistDAO.getEventTypeID(parentActivity, Constants.IDENTIFIER_OWNER));//need to pass table name according to
        attachment.setOwnerid(jobneedid);//need to pass jobneedid/peopleeventlogid
        //attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+parentActivity+"/"+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+"/");
        attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+parentActivity.toLowerCase(Locale.ENGLISH)+"/"+parentFolder.toLowerCase(Locale.ENGLISH)+"/");
        //attachment.setIsdeleted("False");
        attachment.setAttachmentCategory(Constants.ATTACHMENT_VIDEO);
        attachment.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));

        AttachmentDAO attachmentDAO=new AttachmentDAO(VideoCaptureActivity.this);

        System.out.println("Attachment Inserted owner id: "+jobneedid);
        attachmentDAO.insertCommonRecord(attachment);



    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        prepareMediaRecorder(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshCamera(mCamera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera!=null)
            mCamera.release();
    }

    private void prepareMediaRecorder(SurfaceHolder holder)
    {
        //mCamera = Camera.open();
        if (mCamera != null) {
            /*Parameters params = mCamera.getParameters();

            if(VideoCaptureActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
                params.setFlashMode(Parameters.FLASH_MODE_TORCH);

            mCamera.setParameters(params);*/
            mCamera.setDisplayOrientation(90);
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        else {

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_camera_framework_bug),
                    Toast.LENGTH_LONG).show();

            finish();
        }
    }

    public void refreshCamera(Camera camera) {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        stopPreview();
        // set preview size and make any resize, rotate or
        // reformatting changes here
        setCamera(camera);

        // start preview with new settings
        startPreview();
    }

    public void startPreview(){
        try {
            if(mCamera != null) {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
        setCameraRotation();
    }

    public void stopPreview(){
        try {
            if(mCamera != null)
                mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            e.printStackTrace();
        }
    }


    public void setCameraRotation() {
        try {

            Camera.CameraInfo camInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);
            int cameraRotationOffset = camInfo.orientation;
            // ...

            Camera.Parameters parameters = mCamera.getParameters();


            int rotation = 0;
            rotation=getWindowManager().getDefaultDisplay().getRotation();

            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break; // Natural orientation
                case Surface.ROTATION_90:
                    degrees = 90;
                    break; // Landscape left
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;// Upside down
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;// Landscape right
            }
            int displayRotation;
            if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                displayRotation = (cameraRotationOffset + degrees) % 360;
                displayRotation = (360 - displayRotation) % 360; // compensate
                // the
                // mirror
            } else { // back-facing
                displayRotation = (cameraRotationOffset - degrees + 360) % 360;
            }

            mCamera.setDisplayOrientation(displayRotation);


            if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotate = (360 + cameraRotationOffset + degrees) % 360;
            } else {
                rotate = (360 + cameraRotationOffset - degrees) % 360;
            }

            parameters.set("orientation", "portrait");
            parameters.setRotation(rotate);
            mCamera.setParameters(parameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int what, int i1) {
        if(what==MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
        {
            System.out.println("Recording has been stopped");

            mrec.stop();
            mrec.release();
            mCamera.stopPreview();

            mCamera.lock();
            mCamera.release();
            myHandler.removeCallbacks(updateTimerMethod);

            SaveVideoAsyntask saveVideoAsyntask=new SaveVideoAsyntask();
            saveVideoAsyntask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("IMG_PATH", dirPath+fileName);
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        startRecordingAgain();
    }

    private void startRecordingAgain()
    {
        startBtn.setClickable(true);
         //initMediaRecorder();
        mCamera = Camera.open();
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        refreshCamera(mCamera);
        startRecording();
    }

    private class SaveVideoAsyntask extends AsyncTask<Void, Integer, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(parentFolder.equalsIgnoreCase("SOS")) {
                customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.title_activity_video_capture),getResources().getString(R.string.videorecording_recordvideomoreseconds,(Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_VIDEO_REC_FREQ, "10000")) / 1000)),getResources().getString(R.string.title_activity_video_capture),0);
                //customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.title_activity_video_capture), "Do you want to record video for more " + (Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_VIDEO_REC_FREQ, "10000")) / 1000) + " seconds?", getResources().getString(R.string.title_activity_video_capture), 0);
            }
            else
            {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("IMG_PATH", dirPath+fileName);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    saveToSDCard();
                }
            });

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}
