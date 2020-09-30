package com.youtility.intelliwiz20.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import com.youtility.intelliwiz20.Adapters.AssetAuditAdapter;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.android.CaptureActivity;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public abstract class AssetAuditActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, IDialogEventListeners {
    private AssetAuditAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<JobNeed> jobNeedArrayList;
    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private AssetDAO assetDAO;
    private QuestionDAO questionDAO;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences loginDetailPref;
    private SharedPreferences adhocJobPef;
    private ArrayList<QuestionSet>questionSetArrayList;

    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_audit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);
        adhocJobPef=getSharedPreferences(Constants.ADHOC_JOB_TIMESTAMP_PREF, MODE_PRIVATE);
        adhocJobPef.edit().putString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_QR).apply();

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(AssetAuditActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int accessValue = CommonFunctions.isAllowToAccessModules(AssetAuditActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
                double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
                System.out.println("===========" + accessValue);
                System.out.println("===========" + loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

                if (accessValue == 0) {
                    if (CommonFunctions.isPermissionGranted(AssetAuditActivity.this))
                        callScanQrCode();
                    else
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
        });

        jobNeedDAO=new JobNeedDAO(AssetAuditActivity.this);
        typeAssistDAO=new TypeAssistDAO(AssetAuditActivity.this);
        assetDAO=new AssetDAO(AssetAuditActivity.this);
        questionDAO=new QuestionDAO(AssetAuditActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.assetAuditRecylerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        prepareAdapter();
    }

    private void prepareAdapter()
    {
        jobNeedArrayList=jobNeedDAO.getJobList(Constants.JOB_NEED_IDENTIFIER_ASSET_AUDIT, Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
        adapter = new AssetAuditAdapter(AssetAuditActivity.this, jobNeedArrayList);
        recyclerView.setAdapter(adapter);
    }

    private void callScanQrCode()
    {
        adhocJobPef.edit().putLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()).apply();
        Intent intent= new Intent(AssetAuditActivity.this,CaptureActivity.class);
        intent.putExtra("FROM","CHECKPOINT");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0)
        {
            if(resultCode==RESULT_OK && data!=null)
            {
                adhocJobPef.edit().putString(Constants.ADHOC_ASSET,data.getStringExtra("SCAN_RESULT")).apply();
                questionSetArrayList=new ArrayList<>();
                questionSetArrayList=questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_assetauditadhoc_templates_query));
                if(questionSetArrayList!=null && questionSetArrayList.size()>0)
                {

                    if(questionSetArrayList.size()>1)
                        showDialog();
                    else
                        callNextScreen(0);
                }
            }
            else if(resultCode==RESULT_CANCELED)
            {

            }
        }
        else if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {
                addScanCodeToDB();
            }
            else if(resultCode==RESULT_CANCELED)
            {
                adhocJobPef.edit().clear().apply();
            }
        }
    }


    private void showDialog()
    {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AssetAuditActivity.this);
        builderSingle.setTitle(getResources().getString(R.string.select_quest_set_title));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AssetAuditActivity.this, android.R.layout.select_dialog_item);

        for (QuestionSet questionSet: questionSetArrayList) {
            arrayAdapter.add(questionSet.getQsetname().trim());
        }

        builderSingle.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callNextScreen(which);
            }
        });
        builderSingle.create();
        builderSingle.setCancelable(false);
        builderSingle.show();
    }

    private void callNextScreen(int which)
    {
        adhocJobPef.edit().putLong(Constants.ADHOC_QSET,questionSetArrayList.get(which).getQuestionsetid()).apply();
        //Toast.makeText(AssetAuditActivity.this, questionSetArrayList.get(which).getQsetname()+"",Toast.LENGTH_LONG).show();
        Intent fillReportIntent = new Intent(AssetAuditActivity.this, IncidentReportQuestionActivity.class);
        fillReportIntent.putExtra("FROM", "ADHOC");
        fillReportIntent.putExtra("ID", questionSetArrayList.get(which).getQuestionsetid());
        fillReportIntent.putExtra("PARENT_ACTIVITY","JOBNEED");
        fillReportIntent.putExtra("FOLDER","ASSETAUDIT");
        startActivityForResult(fillReportIntent, 1);
    }

    private void addScanCodeToDB()
    {
        long atop=-1;

        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        atop=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);

        long expDate=System.currentTimeMillis();
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(expDate);
        cal.add(Calendar.MINUTE, +10);
        System.out.println("ExpiryTime: "+CommonFunctions.getTimezoneDate(cal.getTimeInMillis()));

        JobNeed jobNeed=new JobNeed();
        jobNeed.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()));
        jobNeed.setJobdesc("ADHOC_ASSETAUDIT_"+adhocJobPef.getString(Constants.ADHOC_ASSET,"NULL"));
        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));
        jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED,Constants.STATUS_TYPE_JOBNEED));

        jobNeed.setScantype(typeAssistDAO.getEventTypeID(Constants.SCAN_TYPE_QR, Constants.IDENTIFIER_SCANTYPE));
        jobNeed.setReceivedonserver(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setPriority(typeAssistDAO.getEventTypeID("LOW", Constants.IDENTIFIER_PRIORITY));
        jobNeed.setStarttime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setEndtime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setRemarks(adhocJobPef.getString(Constants.ADHOC_ASSET,""));
        jobNeed.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setAssetid(assetDAO.getAssetID(adhocJobPef.getString(Constants.ADHOC_ASSET,"")));

        jobNeed.setAatop(atop);
        jobNeed.setGroupid(-1);
        jobNeed.setPeopleid(atop);
        jobNeed.setQuestionsetid(adhocJobPef.getLong(Constants.ADHOC_QSET,-1));
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_ASSET_AUDIT,Constants.IDENTIFIER_JOBNEED));
        jobNeed.setParent(-1);
        jobNeed.setTicketno(-1);
        jobNeed.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
        jobNeed.setTicketcategory(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_AUTOCLOSED, "Ticket Category"));
        jobNeed.setSeqno(0);
        jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
        jobNeed.setPerformedby(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setJobid(-1);
        jobNeedDAO.insertRecord(jobNeed, "0");

        //CommonFunctions.manualSyncEventLog("ADHOC_SKIP","JOBNEEDID: "+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)+"\n JOB_STARTED: "+jobNeed.getStarttime()+"\n JOB_END: "+jobNeed.getEndtime(),jobNeed.getEndtime());
        prepareAdapter();
    }
}
