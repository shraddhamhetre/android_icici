package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class MediaRecoderView extends AppCompatActivity implements View.OnClickListener, IDialogEventListeners {

    private Button startButton;
    private Button stopButton;
    private TextView countdownText;
    private ImageView microphoneImageView;

    private MediaRecorder recorder;
    private File audiofile = null;
    private String fileName=null;
    private String filePath=null;
    private String audioUUID=null;

    private File sampleDir=null;

    AnimationDrawable frameAnimation;
    private long startTime = 0L;
    private Handler myHandler = new Handler();
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;
    long timestamp=0L;

    private int fromActivity=-1;
    private String peopleScannedCode=null;
    private long attachmentTimestamp=-1;
    private String extStorageDirectory = "";
    private boolean isSDPresent;
    private long jobneedid=-1;
    private SharedPreferences deviceRelatedPref;
    private String parentActivity=null;
    private String parentFolder=null;
    private SharedPreferences loginDetailPref;
    private TypeAssistDAO typeAssistDAO;

    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recoder_view);

        parentActivity=getIntent().getStringExtra("PARENT_ACTIVITY");
        parentFolder=getIntent().getStringExtra("FOLDER");


        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(MediaRecoderView.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);


        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        typeAssistDAO=new TypeAssistDAO(MediaRecoderView.this);

        startButton = (Button)findViewById(R.id.startRecButton);
        startButton.setOnClickListener(this);
        stopButton = (Button)findViewById(R.id.stopRecButton);
        stopButton.setOnClickListener(this);
        countdownText=(TextView)findViewById(R.id.countdownTextview);
        microphoneImageView=(ImageView)findViewById(R.id.microphone_image);

        if(getIntent().hasExtra("JOBNEEDID"))
        {
            jobneedid=getIntent().getLongExtra("JOBNEEDID",-1);
        }

        fromActivity=getIntent().getIntExtra("FROM",0);
        peopleScannedCode=getIntent().getStringExtra("CODE");
        attachmentTimestamp=getIntent().getLongExtra("TIMESTAMP",-1);

        extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        isSDPresent = android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public void startRecording() {

        try {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);

            startTime = SystemClock.uptimeMillis();
            myHandler.postDelayed(updateTimerMethod, 0);

            microphoneImageView.setImageResource(R.drawable.blink_animation);
            frameAnimation = (AnimationDrawable) microphoneImageView.getDrawable();
            frameAnimation.start();

            filePath=extStorageDirectory+"/"+ Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/";
            fileName= CommonFunctions.getFileNameFromDate(System.currentTimeMillis())+".3gp";

            sampleDir=new File(filePath,fileName);

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(sampleDir.getAbsolutePath());
            System.out.println("Audio file path: "+sampleDir.getAbsolutePath());
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {

        try {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            myHandler.removeCallbacks(updateTimerMethod);
            frameAnimation.stop();
            recorder.stop();
            recorder.release();
            saveToSDCard();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    private void saveToSDCard()
    {
        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        //check attachement new database columns

        Attachment attachment=new Attachment();
        attachment.setAttachmentid(attachmentTimestamp);
        attachment.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_ATTACHMENT, Constants.IDENTIFIER_ATTACHMENT));
        attachment.setFilePath(filePath+fileName);
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
        attachment.setAttachmentCategory(Constants.ATTACHMENT_AUDIO);
        attachment.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));

        AttachmentDAO attachmentDAO=new AttachmentDAO(MediaRecoderView.this);
        attachmentDAO.insertCommonRecord(attachment);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("IMG_PATH", filePath+fileName);
        setResult(RESULT_OK,returnIntent);
        finish();

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

    @Override
    public void onClick(View v) {
        int accessValue = CommonFunctions.isAllowToAccessModules(MediaRecoderView.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
        double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
        System.out.println("===========" + accessValue);
        System.out.println("===========" + loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

        if (accessValue == 0) {
            switch (v.getId()) {
                case R.id.startRecButton:
                    startRecording();
                    break;
                case R.id.stopRecButton:
                    stopRecording();
                    break;
            }
        }else if (accessValue == 1) {
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autodatetimeMessage));
        } else if (accessValue == 2) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autoGPSMessage), accessValue);
            System.out.println("==========="+accessValue);
        } else if (accessValue == 3) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autowifiMessage), accessValue);
            System.out.println("==========="+accessValue);
        } else if (accessValue == 4) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autonetworkMessage), accessValue);
            System.out.println("==========="+accessValue);
        }else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
            System.out.println("===========lat long==0.0");
        }
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }
}
