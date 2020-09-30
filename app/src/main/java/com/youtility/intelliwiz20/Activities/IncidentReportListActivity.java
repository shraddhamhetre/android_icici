package com.youtility.intelliwiz20.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.youtility.intelliwiz20.Adapters.IncidentReportListAdapter;
import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;

import java.util.ArrayList;

public class IncidentReportListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ConnectivityReceiver.ConnectivityReceiverListener, IDialogEventListeners
{

    private ArrayList<QuestionSet>questionSetArrayList;
    private QuestionDAO questionDAO;
    private long selectedQuestionSetId=-1;

    private ListView irListView;
    private ArrayList<JobNeed> irPendingList;
    private JobNeedDAO jobNeedDAO;
    private IncidentReportListAdapter incidentReportListAdapter;
    private FloatingActionButton fab;
    private SharedPreferences loginPref;
    private ConnectivityReceiver connectivityReceiver;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;
    private ArrayList<Asset>CheckPointList;
    private AssetDAO assetDAO;
    private long selectedCheckpointId=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("ir oncreate---");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        irListView=(ListView)findViewById(R.id.irListview);

        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        assetDAO=new AssetDAO(IncidentReportListActivity.this);


        CheckPointList = new ArrayList<Asset>();
        CheckPointList = assetDAO.getCheckpointList();

        questionDAO=new QuestionDAO(IncidentReportListActivity.this);
        jobNeedDAO=new JobNeedDAO(IncidentReportListActivity.this);
        irPendingList=jobNeedDAO.getSavedIRList(Constants.JOB_NEED_IDENTIFIER_INCIDENT,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
        incidentReportListAdapter=new IncidentReportListAdapter(IncidentReportListActivity.this, irPendingList);
        irListView.setAdapter(incidentReportListAdapter);

        customAlertDialog = new CustomAlertDialog(IncidentReportListActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);


        irListView.setOnItemClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int accessValue = CommonFunctions.isAllowToAccessModules(IncidentReportListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
                double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
                System.out.println("==========="+accessValue);
                System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

                if(accessValue == 0) {
                    if (CommonFunctions.isPermissionGranted(IncidentReportListActivity.this)) {
                        questionSetArrayList = new ArrayList<QuestionSet>();
                        questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_questionsetcode_query, loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID, -1)));
                        if (questionSetArrayList != null && questionSetArrayList.size() > 0)
                            //showDialog();
                            showCheckpointDialog();
                        else
                            Snackbar.make(fab, getResources().getString(R.string.ir_template_not_found), Snackbar.LENGTH_LONG).show();
                    } else
                        Snackbar.make(fab, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
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
    }

    public void showCheckpointDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(IncidentReportListActivity.this);
        builderSingle.setTitle(getResources().getString(R.string.select_checkpoint_set_title));
        //builderSingle.setView();

        //builderSingle.setTitle(Html.fromHtml("<p style='color=#ffffff; background-color=#000000'>"+getResources().getString(R.string.select_quest_set_title)+"</p>"));


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(IncidentReportListActivity.this, android.R.layout.select_dialog_item);

        for (Asset checkpoint: CheckPointList) {
            arrayAdapter.add(checkpoint.getAssetname().trim()+"\n");

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
                selectedCheckpointId=CheckPointList.get(which).getAssetid();
                System.out.println("checkpointid"+ selectedCheckpointId);
                showDialog();
                /*Intent irListIntent=new Intent(IncidentReportListActivity.this, IncidentReportActivity.class);
                irListIntent.putExtra("QSetID",selectedQuestionSetId);
                startActivityForResult(irListIntent,0);*/

                /*Intent fillReportIntent = new Intent(IncidentReportListActivity.this, IncidentReportQuestionActivity.class);
                fillReportIntent.putExtra("FROM", "INCIDENTREPORT");
                fillReportIntent.putExtra("ID", System.currentTimeMillis());
                fillReportIntent.putExtra("QUESTIONSETID", selectedQuestionSetId);
                fillReportIntent.putExtra("PARENT_ACTIVITY","JOBNEED");
                fillReportIntent.putExtra("FOLDER","INCIDENTREPORT");
                startActivityForResult(fillReportIntent, 0);*/


            }
        });
        builderSingle.show();
        builderSingle.setCancelable(false);

    }


    public void showDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(IncidentReportListActivity.this);
        builderSingle.setTitle(getResources().getString(R.string.select_quest_setIR_title));
        //builderSingle.setTitle(Html.fromHtml("<p style='color=#ffffff; background-color=#000000'>"+getResources().getString(R.string.select_quest_set_title)+"</p>"));


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(IncidentReportListActivity.this, android.R.layout.select_dialog_item);

        for (QuestionSet questionSet: questionSetArrayList) {
            arrayAdapter.add(questionSet.getQsetname().trim()+"\n");

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
                selectedQuestionSetId=questionSetArrayList.get(which).getQuestionsetid();
                /*Intent irListIntent=new Intent(IncidentReportListActivity.this, IncidentReportActivity.class);
                irListIntent.putExtra("QSetID",selectedQuestionSetId);
                startActivityForResult(irListIntent,0);*/

                Intent fillReportIntent = new Intent(IncidentReportListActivity.this, IncidentReportQuestionActivity.class);
                fillReportIntent.putExtra("FROM", "INCIDENTREPORT");
                fillReportIntent.putExtra("ID", System.currentTimeMillis());
                fillReportIntent.putExtra("QUESTIONSETID", selectedQuestionSetId);
                fillReportIntent.putExtra("ASSETID", selectedCheckpointId);
                fillReportIntent.putExtra("PARENT_ACTIVITY","JOBNEED");
                fillReportIntent.putExtra("FOLDER","INCIDENTREPORT");
                startActivityForResult(fillReportIntent, 0);


            }
        });
        builderSingle.show();
        builderSingle.setCancelable(false);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(((JobNeed)incidentReportListAdapter.getItem(position)).getSyncstatus()==3) {
            Intent fillReportIntent = new Intent(IncidentReportListActivity.this, IncidentReportQuestionActivity.class);
            fillReportIntent.putExtra("FROM", "INCIDENTREPORT_LIST");
            fillReportIntent.putExtra("ID", ((JobNeed) incidentReportListAdapter.getItem(position)).getJobneedid());
            fillReportIntent.putExtra("QUESTIONSETID", ((JobNeed) incidentReportListAdapter.getItem(position)).getQuestionsetid());
            fillReportIntent.putExtra("PARENT_ACTIVITY","JOBNEED");
            fillReportIntent.putExtra("FOLDER","INCIDENTREPORT");
            startActivityForResult(fillReportIntent, 0);
        }
        else
        {
            Snackbar.make(fab, getResources().getString(R.string.irlistactivity_reportsubmittedalready),Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0 && resultCode==RESULT_OK)
        {
            irPendingList=new ArrayList<>();
            irPendingList=jobNeedDAO.getSavedIRList(Constants.JOB_NEED_IDENTIFIER_INCIDENT,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
            incidentReportListAdapter=new IncidentReportListAdapter(IncidentReportListActivity.this, irPendingList);
            irListView.setAdapter(incidentReportListAdapter);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CommonFunctions.showSnack(IncidentReportListActivity.this, isConnected,fab);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    protected void onRestart() {

        System.out.println("onRestart ilist");
        super.onRestart();


    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        /*register connection status listener*/
        Baseclass.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }
}
