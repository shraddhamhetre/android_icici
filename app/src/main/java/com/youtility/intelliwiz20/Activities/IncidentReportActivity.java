package com.youtility.intelliwiz20.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;

public  class IncidentReportActivity extends AppCompatActivity implements View.OnClickListener, IDialogEventListeners {
    private ImageView addPicture, addVideo, addAudio;
    private EditText suggestionEdittext;
    private final int AUDIO_INTENT=0;
    private final int VIDEO_INTENT=1;
    private final int PICTURE_INTENT=2;
    private ActionBar actionBar;
    private Button fillReportButton;
    private long currentTimestamp=-1l;
    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;

    private long selectedQuestionSetId=-1;
    private long selectedCheckpointId=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report);
        actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        selectedQuestionSetId=getIntent().getLongExtra("QSetID",-1);
        selectedCheckpointId = getIntent().getLongExtra("ASSETID", -1);
        currentTimestamp=System.currentTimeMillis();


        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(IncidentReportActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        componentInitialise();
    }


    private void componentInitialise() {
        addPicture=(ImageView)findViewById(R.id.addPicture);
        addPicture.setOnClickListener(this);
        addVideo=(ImageView)findViewById(R.id.addVideo);
        addVideo.setOnClickListener(this);
        addAudio=(ImageView)findViewById(R.id.addAudio);
        addAudio.setOnClickListener(this);
        fillReportButton=(Button)findViewById(R.id.fillReportButton);
        fillReportButton.setOnClickListener(this);
        suggestionEdittext=(EditText)findViewById(R.id.suggestionEdittext);
    }

    @Override
    public void onClick(View v) {
        int accessValue = CommonFunctions.isAllowToAccessModules(IncidentReportActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        System.out.println("==========="+accessValue);
        System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

        if(accessValue == 0) {
            if (selectedQuestionSetId != -1) {
                switch (v.getId()) {
                    case R.id.addVideo:
                        Intent captureVideoIntent = new Intent(IncidentReportActivity.this, AttachmentListActivity.class);
                        captureVideoIntent.putExtra("FROM", VIDEO_INTENT);
                        captureVideoIntent.putExtra("TIMESTAMP", currentTimestamp);
                        captureVideoIntent.putExtra("PARENT_ACTIVITY", "INCIDENTREPORT");
                        startActivityForResult(captureVideoIntent, VIDEO_INTENT);
                        break;

                    case R.id.addPicture:
                        Intent capturePictureIntent = new Intent(IncidentReportActivity.this, AttachmentListActivity.class);
                        capturePictureIntent.putExtra("FROM", PICTURE_INTENT);
                        capturePictureIntent.putExtra("TIMESTAMP", currentTimestamp);
                        capturePictureIntent.putExtra("PARENT_ACTIVITY", "INCIDENTREPORT");
                        startActivityForResult(capturePictureIntent, PICTURE_INTENT);
                        break;

                    case R.id.addAudio:
                /*Intent recordAudioIntent=new Intent(IncidentReportActivity.this, RecordAudioActivity.class);
                startActivityForResult(recordAudioIntent,AUDIO_INTENT);*/
                        Intent recordAudioIntent = new Intent(IncidentReportActivity.this, AttachmentListActivity.class);
                        recordAudioIntent.putExtra("FROM", AUDIO_INTENT);
                        recordAudioIntent.putExtra("TIMESTAMP", currentTimestamp);
                        recordAudioIntent.putExtra("PARENT_ACTIVITY", "INCIDENTREPORT");
                        startActivityForResult(recordAudioIntent, AUDIO_INTENT);
                        break;
                    case R.id.fillReportButton:
                        Intent fillReportIntent = new Intent(IncidentReportActivity.this, IncidentReportQuestionActivity.class);
                        fillReportIntent.putExtra("FROM", "INCIDENTREPORT");
                        fillReportIntent.putExtra("ID", currentTimestamp);
                        fillReportIntent.putExtra("QUESTIONSETID", selectedQuestionSetId);
                        fillReportIntent.putExtra("ASSETID", selectedCheckpointId);

                        fillReportIntent.putExtra("OBSERVATION", suggestionEdittext.getText().toString().trim());
                        fillReportIntent.putExtra("PARENT_ACTIVITY", "JOBNEED");
                        fillReportIntent.putExtra("FOLDER", "INCIDENTREPORT");
                        startActivityForResult(fillReportIntent, 3);
                        break;
                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==3 && resultCode==RESULT_OK)
        {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }
}
