package com.youtility.intelliwiz20.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.android.CaptureActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AttendanceTakeActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, IDialogEventListeners {
    private String selectedSiteName;
    private long selectedSiteId=-1;

    private TextView siteNameTextView, employeeCodeTextView, currentTimestampTextView;
    private Button attendanceInButton;//, attendanceOutButton;
    private ImageView captureImageView;
    private ImageView grpCaptureImageView;
    private TextView captureTextView, grpCaptureTextView;
    private RadioGroup radioGroup;
    //private EditText enterEmployeeCodeEditText;
    private int checkedRadioButton;
    private final int NFC_CODE=2;
    private final int SCAN_CODE=1;
    private final int ENTER_CODE=0;
    private final int CAPTURE_ATTENDANCE_INTENT=3;
    private final int CAPTURE_SELFIE_ATTENDANCE_INTENT=4;
    private NfcAdapter mNfcAdapter;
    private boolean isNFCAvailable=false;
    private String empCode=null;
    private String returnImgPath=null;
    private String peopleReturnID=null;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private long peopleID=-1;
    private Boolean isSDPresent=false;
    private String extStorageDirectory="";
    private PeopleEventLogDAO peopleEventLogDAO;
    private TypeAssistDAO typeAssistDAO;
    private PeopleDAO peopleDAO;
    private AttachmentDAO attachmentDAO;
    private long currentTimestamp=-1;
    private String fromActivity=null;

    private boolean isGotEmpCode=false;
    private boolean isGotEmpSelfiePic=false;
    private boolean isGotEmpPic=false;

    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_take);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(AttendanceTakeActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        peopleEventLogDAO=new PeopleEventLogDAO(AttendanceTakeActivity.this);
        attachmentDAO=new AttachmentDAO(AttendanceTakeActivity.this);
        typeAssistDAO=new TypeAssistDAO(AttendanceTakeActivity.this);
        peopleDAO=new PeopleDAO(AttendanceTakeActivity.this);

        currentTimestamp=System.currentTimeMillis();

        selectedSiteName=getIntent().getStringExtra("SITENAME");
        selectedSiteId=getIntent().getLongExtra("SITEID",-1);
        fromActivity=getIntent().getStringExtra("ATTENDANCE_TYPE");

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);

        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);

        siteNameTextView=(TextView)findViewById(R.id.siteNameTextView);
        employeeCodeTextView=(TextView)findViewById(R.id.employeeCodeTextView);
        currentTimestampTextView=(TextView)findViewById(R.id.currentTimestampTextView);
        /*enterEmployeeCodeEditText=(EditText)findViewById(R.id.enterEmployeeCodeEditText);
        enterEmployeeCodeEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);*/
        captureImageView=(ImageView)findViewById(R.id.captureImageView);
        grpCaptureImageView=(ImageView)findViewById(R.id.grpCaptureImageView);

        captureTextView=(TextView)findViewById(R.id.captureTextView);
        grpCaptureTextView=(TextView)findViewById(R.id.grpCaptureTextView);

        captureTextView.setOnClickListener(this);
        grpCaptureTextView.setOnClickListener(this);

        attendanceInButton=(Button)findViewById(R.id.attendanceInButton);
        /*attendanceOutButton=(Button)findViewById(R.id.attendanceOutButton);*/

        attendanceInButton.setEnabled(false);
        attendanceInButton.setBackgroundResource(R.drawable.rounder_cancel_button);
        /*attendanceOutButton.setEnabled(false);*/

        attendanceInButton.setOnClickListener(this);
        /*attendanceOutButton.setOnClickListener(this);*/

        radioGroup=(RadioGroup)findViewById(R.id.attendanceRadioGroup);
        radioGroup.setOnCheckedChangeListener(this);



        /*enterEmployeeCodeEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    empCode=enterEmployeeCodeEditText.getText().toString().trim();
                    employeeCodeTextView.setText(enterEmployeeCodeEditText.getText().toString().trim());
                    enterEmployeeCodeEditText.setText("");
                    enterEmployeeCodeEditText.setVisibility(View.INVISIBLE);

                    attendanceInButton.setEnabled(true);
                    attendanceOutButton.setEnabled(true);

                    callAttendancePhotoCapture(empCode);
                    return true;
                }
                return false;
            }
        });*/

        siteNameTextView.setText(selectedSiteName);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });


        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                java.util.Date dt= Calendar.getInstance().getTime();
                                currentTimestampTextView.setText(dt.toString());
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        extStorageDirectory = Environment.getExternalStorageDirectory().toString();

    }



    private void callAttendancePhotoCapture(String eCode, int camera)
    {
        Intent attenCapPicIntent=new Intent(AttendanceTakeActivity.this, AttendanceCapturePhotoActivity.class);
        attenCapPicIntent.putExtra("FROM",fromActivity);
        attenCapPicIntent.putExtra("CODE",eCode);
        attenCapPicIntent.putExtra("CAMERA",camera);
        attenCapPicIntent.putExtra("TIMESTAMP",currentTimestamp);
        startActivityForResult(attenCapPicIntent,CAPTURE_ATTENDANCE_INTENT);
    }

    private void callSelfiAttendancePhotoCapture(String eCode, int camera)
    {
        Intent attenCapPicIntent=new Intent(AttendanceTakeActivity.this, AttendanceCapturePhotoActivity.class);
        attenCapPicIntent.putExtra("FROM",fromActivity);
        attenCapPicIntent.putExtra("CODE",eCode);
        attenCapPicIntent.putExtra("CAMERA",camera);
        attenCapPicIntent.putExtra("TIMESTAMP",currentTimestamp);
        startActivityForResult(attenCapPicIntent,CAPTURE_SELFIE_ATTENDANCE_INTENT);
    }

    private boolean isNFCFeatureAvailable()
    {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            return false;
        }
        else
            return true;
    }

    @Override
    public void onClick(View view) {
        int accessValue = CommonFunctions.isAllowToAccessModules(AttendanceTakeActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        System.out.println("==========="+accessValue);
        System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
        if (accessValue == 0 ) {
            if (CommonFunctions.isPermissionGranted(AttendanceTakeActivity.this)) {
                switch (view.getId()) {
                    case R.id.attendanceInButton:
                        if (isGotEmpCode && isGotEmpPic && isGotEmpSelfiePic)
                            insertPeopleEventLogRecord(Constants.ATTENDANCE_PUNCH_TYPE_IN);
                        break;
                /*case R.id.attendanceOutButton:
                    insertPeopleEventLogRecord(Constants.ATTENDANCE_PUNCH_TYPE_OUT);
                    break;*/
                    case R.id.captureTextView:
                        if (isGotEmpCode)
                            callAttendancePhotoCapture(empCode, 0);
                        else
                            Snackbar.make(captureImageView, getResources().getString(R.string.take_attendance_enter_employee_code), Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.grpCaptureTextView:
                        if (isGotEmpCode)
                            callSelfiAttendancePhotoCapture(empCode, 1);
                        else
                            Snackbar.make(captureImageView, getResources().getString(R.string.take_attendance_enter_employee_code), Snackbar.LENGTH_LONG).show();
                        break;
                }
            /*setResult(RESULT_OK);
            finish();*/
            } else
                Snackbar.make(view, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
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


    //accuracy-, datetime-, gpslocation-, photorecognitionthreshold-, photorecognitionscore-, photorecognitiontimestamp-, photorecognitionserviceresponse-,
    //facerecognition-, peopleid-, peventtype-, punchstatus-, verifiedby-, siteid-, cuser-, muser-, cdtz-, mdtz-, isdeleted-, gfid, deviceid-
    private void insertPeopleEventLogRecord(String punchStatus)
    {
        DateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");
        long scannedPeopleId=peopleDAO.getPeopleId(empCode);


        PeopleEventLog peopleEventLog=new PeopleEventLog();
        peopleEventLog.setAccuracy(-1);
        //peopleEventLog.setDeviceid(deviceRelatedPref.getLong(Constants.DEVICE_IMEI,0));
        peopleEventLog.setDeviceid(deviceRelatedPref.getString(Constants.DEVICE_IMEI,"0"));

        peopleEventLog.setDatetime(String.valueOf(currentTimestamp));
        peopleEventLog.setGpslocation(gpsLocation);
        peopleEventLog.setPhotorecognitionthreshold(-1);
        peopleEventLog.setPhotorecognitionscore(-1);
        peopleEventLog.setPhotorecognitiontimestamp(null);
        peopleEventLog.setPhotorecognitionserviceresponse(null);
        peopleEventLog.setFacerecognition("false");
        peopleEventLog.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        peopleEventLog.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        //peopleEventLog.setIsdeleted("false");
        peopleEventLog.setCuser(peopleID);
        peopleEventLog.setMuser(peopleID);
        peopleEventLog.setPeopleid(scannedPeopleId);
        peopleEventLog.setPeventtype(typeAssistDAO.getEventTypeID("TAKE",Constants.IDENTIFIER_ATTENDANCE));
        peopleEventLog.setPunchstatus(typeAssistDAO.getEventTypeID(punchStatus, Constants.IDENTIFIER_PUNCHSTATUS));
        peopleEventLog.setVerifiedby(-1);
        peopleEventLog.setScanPeopleCode(empCode);
        peopleEventLog.setBuid(selectedSiteId);
        peopleEventLog.setGfid(-1);
        peopleEventLog.setDistance(0);
        peopleEventLog.setDuration(0);
        peopleEventLog.setExpamt(0.0);
        peopleEventLog.setReference("");
        peopleEventLog.setRemarks("");
        peopleEventLog.setTransportmode(-1);
        peopleEventLog.setOtherlocation("");
        peopleEventLogDAO.insertRecord(peopleEventLog);

        employeeCodeTextView.setText("");
        captureImageView.setImageBitmap(null);
        captureTextView.setVisibility(View.VISIBLE);
        grpCaptureImageView.setImageBitmap(null);
        grpCaptureTextView.setVisibility(View.VISIBLE);

        attendanceInButton.setEnabled(false);
        attendanceInButton.setBackgroundResource(R.drawable.rounder_cancel_button);

        isGotEmpCode=false;

        Toast.makeText(AttendanceTakeActivity.this, getResources().getString(R.string.take_attendance_mark_present_msg), Toast.LENGTH_SHORT).show();
    }



    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SCAN_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null)
                {
                    empCode=data.getStringExtra("SCAN_RESULT");
                    //employeeCodeTextView.setText("Employee code: "+data.getStringExtra("SCAN_RESULT"));
                    employeeCodeTextView.setText(getResources().getString(R.string.attendance_empcode, data.getStringExtra("SCAN_RESULT")));
                    isGotEmpCode=true;
                    /*attendanceInButton.setEnabled(true);*/
                    /*attendanceOutButton.setEnabled(true);*/
                    /*callAttendancePhotoCapture(empCode);*/
                }
            }
        }
        else if(requestCode==NFC_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null)
                {
                    empCode=data.getStringExtra("SCAN_RESULT");
                    //employeeCodeTextView.setText("Employee code: "+data.getStringExtra("SCAN_RESULT"));
                    employeeCodeTextView.setText(getResources().getString(R.string.attendance_empcode, data.getStringExtra("SCAN_RESULT")));
                    isGotEmpCode=true;
                    /*attendanceInButton.setEnabled(true);*/
                    /*attendanceOutButton.setEnabled(true);*/
                    /*callAttendancePhotoCapture(empCode);*/
                }
            }
        }
        else if(requestCode==CAPTURE_ATTENDANCE_INTENT)
        {
            if(resultCode==RESULT_OK && data!=null) {
                returnImgPath=data.getStringExtra("IMG_PATH");
                System.out.println("REturn Imag Path: "+returnImgPath);
                isGotEmpPic=true;
                attendanceInButton.setEnabled(true);
                attendanceInButton.setBackgroundResource(R.drawable.rounder_corner_button);
                File imgFile = new File(data.getStringExtra("IMG_PATH"));
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    captureImageView.setImageBitmap(myBitmap);
                    captureTextView.setVisibility(View.INVISIBLE);

                }
            }

        }
        else if(requestCode==CAPTURE_SELFIE_ATTENDANCE_INTENT)
        {
            if(resultCode==RESULT_OK && data!=null) {
                returnImgPath=data.getStringExtra("IMG_PATH");
                isGotEmpSelfiePic=true;
                System.out.println("REturn Imag Path: "+returnImgPath);
                File imgFile = new File(data.getStringExtra("IMG_PATH"));
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    grpCaptureImageView.setImageBitmap(myBitmap);
                    grpCaptureTextView.setVisibility(View.INVISIBLE);
                }
            }

        }
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        checkedRadioButton=radioGroup.getCheckedRadioButtonId();
        attendanceInButton.setEnabled(false);
        attendanceInButton.setBackgroundResource(R.drawable.rounder_cancel_button);
        /*attendanceOutButton.setEnabled(false);*/
        prepareAttendanceView(checkedRadioButton);
    }

    private void prepareAttendanceView(int chkRadioButton)
    {
        empCode=null;
        if(CommonFunctions.isPermissionGranted(AttendanceTakeActivity.this)) {
            switch (chkRadioButton) {
                case R.id.radioButton3://nfc
                    if (isNFCFeatureAvailable()) {
                        employeeCodeTextView.setText("");
                        /*enterEmployeeCodeEditText.setVisibility(View.INVISIBLE);*/
                        Intent i = new Intent(AttendanceTakeActivity.this, NFCCodeReaderActivity.class);
                        startActivityForResult(i, NFC_CODE);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.attendance_nfc_error), Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.radioButton2://qr code
                    employeeCodeTextView.setText("");
                    /*enterEmployeeCodeEditText.setVisibility(View.INVISIBLE);*/
                    Intent intent = new Intent(AttendanceTakeActivity.this, CaptureActivity.class);
                    intent.putExtra("FROM", fromActivity);
                    startActivityForResult(intent, SCAN_CODE);
                    break;
                case R.id.radioButton://enter
                    employeeCodeTextView.setText("");
                    /*enterEmployeeCodeEditText.setVisibility(View.VISIBLE);*/
                    CustomAlertDialog customAlertDialog=new CustomAlertDialog(AttendanceTakeActivity.this, this);
                    customAlertDialog.takeEnteredEmpCode(getResources().getString(R.string.take_attendance_entercode_type),"",0);
                    break;
            }
        }
        else
            Snackbar.make(attendanceInButton,getResources().getString(R.string.error_msg_grant_permission),Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        if(type==0)
        {
            empCode=errorMessage.trim();
            employeeCodeTextView.setText(errorMessage.trim());
            isGotEmpCode=true;

            /*attendanceInButton.setEnabled(true);*/

            /*callAttendancePhotoCapture(empCode);*/
        }
    }
}
