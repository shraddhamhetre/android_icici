package com.youtility.intelliwiz20.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import android.content.SharedPreferences;

import com.youtility.intelliwiz20.R;

public abstract class AttendanceActivity extends AppCompatActivity implements View.OnClickListener, IDialogEventListeners {
    private TextView siteAttendanceTextView, selfAttendanceTextView;
    private ActionBar actionBar;
    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        actionBar=getSupportActionBar();
        actionBar.setTitle("Attendance");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        siteAttendanceTextView=(TextView)findViewById(R.id.siteAttendance);
        selfAttendanceTextView=(TextView)findViewById(R.id.selfAttendance);

        siteAttendanceTextView.setOnClickListener(this);
        selfAttendanceTextView.setOnClickListener(this);
        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(AttendanceActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);


    }

    @Override
    public void onClick(View view) {
        int accessValue = CommonFunctions.isAllowToAccessModules(AttendanceActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        System.out.println("==========="+accessValue);
        System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

        if(accessValue == 0) {
            switch (view.getId()) {
                case R.id.selfAttendance:
                /*Intent selfAttendaceIntent=new Intent(AttendanceActivity.this, SelfAttendanceActivity.class);
                startActivityForResult(selfAttendaceIntent,0);*/

                    showPunchTypeDialog();
                    break;
                case R.id.siteAttendance:
                    showAttendanceTypeDialog();
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

    private void showAttendanceTypeDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Select attendance type");
        alertDialogBuilder.setPositiveButton("Mark",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                /*Intent intent= new Intent(AttendanceActivity.this,CaptureActivity.class);
                intent.putExtra("FROM","MARK");
                startActivityForResult(intent, MARK_ATTENDANCE_INTENT);*/
                callAttendanceTypeIntent("MARK");
            }
        });

        alertDialogBuilder.setNegativeButton("Take",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //we need to show UI for take employee attendance on selected site.
                /*Intent markAttendanceIntent=new Intent(AttendanceActivity.this, AttendanceTakeActivity.class);
                markAttendanceIntent.putExtra("SiteName",selectedSiteName);
                startActivityForResult(markAttendanceIntent,TAKE_ATTENDANCE_INTENT);*/
                callAttendanceTypeIntent("TAKE");
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void callAttendanceTypeIntent(String attendanceType)
    {
        Intent siteAttendaceIntent=new Intent(AttendanceActivity.this, SiteListActivity.class);
        siteAttendaceIntent.putExtra("ATTENDANCE_TYPE", attendanceType);
        startActivityForResult(siteAttendaceIntent,1);
    }

    private void showPunchTypeDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Select punch status");
        alertDialogBuilder.setPositiveButton("IN",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                callSelfAttendanceIntent("IN");
            }
        });

        alertDialogBuilder.setNegativeButton("OUT",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callSelfAttendanceIntent("OUT");

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void callSelfAttendanceIntent(String attendType)
    {
        Intent selfAttendaceIntent=new Intent(AttendanceActivity.this, SelfAttendanceActivity.class);
        selfAttendaceIntent.putExtra("PUNCH_TYPE",attendType);
        startActivityForResult(selfAttendaceIntent,0);
    }
}
