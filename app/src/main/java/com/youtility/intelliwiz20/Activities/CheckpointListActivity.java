package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.youtility.intelliwiz20.Adapters.CheckPointListViewAdapter;
import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Tables.JOBNeed_Table;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.android.CaptureActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CheckpointListActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, IDialogEventListeners, ConnectivityReceiver.ConnectivityReceiverListener {
    //private Button gtListButton, adhocChekpointListButton;
    private ListView checkPointListView;
    private CheckPointListViewAdapter checkPointListViewAdapter;
    private ArrayList<JobNeed>checkpointArrayList;
    private JobNeedDAO jobNeedDAO;
    private QuestionDAO questionDAO;
    private TypeAssistDAO typeAssistDAO;
    private AssetDAO assetDAO;
    private ArrayList<QuestionSet> questSetArraylist;
    private long identifierid=-1, jobtypeID=-1;
    private int selectedList=-1;
    private SharedPreferences adhocJobPef;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;

    private SharedPreferences loginPref;
    private SharedPreferences deviceInfoPref;

    final Handler handler = new Handler();
    private int currentPointer=-1;
    private CustomAlertDialog customAlertDialog;
    long tId=-1;
    private ConnectivityReceiver connectivityReceiver;
    private FloatingActionButton fab;
    private AlertDialog alertDialog;
    private CharSequence[] jobNeedTypeOptions = {Constants.SCAN_TYPE_QR,Constants.SCAN_TYPE_NFC};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpoint_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        jobNeedDAO=new JobNeedDAO(CheckpointListActivity.this);
        questionDAO=new QuestionDAO(CheckpointListActivity.this);
        typeAssistDAO=new TypeAssistDAO(CheckpointListActivity.this);
        assetDAO=new AssetDAO(CheckpointListActivity.this);

        customAlertDialog=new CustomAlertDialog(CheckpointListActivity.this, this);

        //System.out.println("jobNeedDAO.deleteCompletedYesterdayTask(): "+jobNeedDAO.deleteCompletedYesterdayTask());
        System.out.println("jobNeedDAO.deleteCompletedYesterdayTask(): "+jobNeedDAO.deleteCompletedPreviousTour());

        checkpointArrayList=new ArrayList<JobNeed>();
        selectedList=0;
        //checkpointArrayList=jobNeedDAO.getJobList(Constants.JOB_NEED_IDENTIFIER_TOUR,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED);
        checkpointArrayList=jobNeedDAO.getTourList(Constants.JOB_NEED_IDENTIFIER_TOUR,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED);

        checkPointListView=(ListView)findViewById(R.id.checkpointlistview);
        checkPointListViewAdapter=new CheckPointListViewAdapter(CheckpointListActivity.this, checkpointArrayList,Constants.JOB_TYPE_SCHEDULED);
        checkPointListView.setAdapter(checkPointListViewAdapter);

        checkPointListView.setOnItemClickListener(this);

        /*gtListButton=(Button)findViewById(R.id.gtlistButton);
        adhocChekpointListButton=(Button)findViewById(R.id.adhoclistButton);*/

        changeButtonTextColor(Constants.CHECKPOINT_GUARD_TOUR_LIST);

        /*gtListButton.setOnClickListener(this);
        adhocChekpointListButton.setOnClickListener(this);*/

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "ADHOC check point started.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                if(CommonFunctions.isPermissionGranted(CheckpointListActivity.this))
                    showAlertOptions();//callScanQrCode();
                else
                    Snackbar.make(view,getResources().getString(R.string.error_msg_grant_permission),Snackbar.LENGTH_LONG).show();
            }
        });

        //getUpComingJOBFromList();
    }


    private void showAlertOptions()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckpointListActivity.this);


        builder.setTitle(getResources().getString(R.string.joblist_selecturchoice_title));

        builder.setSingleChoiceItems(jobNeedTypeOptions, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        alertDialog.dismiss();
                        Intent intent= new Intent(CheckpointListActivity.this, CaptureActivity.class);
                        intent.putExtra("FROM","CHECKPOINT");
                        startActivityForResult(intent, 0);
                        break;
                    case 1:
                        if(checkNFCSupported()) {
                            alertDialog.dismiss();
                            Intent nfcIntent = new Intent(CheckpointListActivity.this, NFCCodeReaderActivity.class);
                            startActivityForResult(nfcIntent, 3);
                        }
                        else
                        {
                            Snackbar.make(fab,getResources().getString(R.string.joblist_nfcnotsupported), Snackbar.LENGTH_LONG).show();
                        }
                        break;
                }

            }
            })
                .setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    private boolean checkNFCSupported()
    {
        NfcManager manager = (NfcManager)getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            return true;
        }else{
            return false;
        }
    }


    @Override
    protected void onStart() {
        handler.postDelayed( new Runnable()
        {
            @Override
            public void run()
            {
                if(currentPointer<checkPointListViewAdapter.getCount())
                {
                    checkPointListView.setSelection(currentPointer);
                    handler.postDelayed(this, 10 * 1000 );
                }
            }
        }, 10 * 1000 );
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checkpoint_menu, menu);

        MenuItem search = menu.findItem(R.id.taskSearch);
        search.setVisible(true);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(checkPointListViewAdapter!=null)
                    checkPointListViewAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_info:
                String jCount=jobNeedDAO.getJobneedCount1(Constants.JOB_NEED_IDENTIFIER_TOUR, CommonFunctions.getFromToDate(0), CommonFunctions.getFromToDate(1));
                String[] jobCount=jCount.split("~");//schedule, complete, pending, closed
                customAlertDialog.JOBInfoDialog(getResources().getString(R.string.jobinfo_dialog_title,"TOUR"), jobCount[0],jobCount[1],jobCount[2],jobCount[3]);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getItemPosition(long id)
    {
        for(int i=0;i<checkPointListView.getAdapter().getCount();i++)
        {
            JobNeed jobNeed=(JobNeed) checkPointListView.getAdapter().getItem(i);
            //System.out.println("id: "+id+" : dd: "+dd);
            if(jobNeed.getJobneedid()==id)
                return i;
        }
        return 0;
    }

    private void getUpComingJOBFromList()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, +15);

        Calendar cal1=Calendar.getInstance();
        cal1.add(Calendar.MINUTE,-15);

        Cursor cc=jobNeedDAO.getScheduleTourList(cal1.getTimeInMillis(),cal.getTimeInMillis());
        if(cc!=null)
        {
            if(cc.moveToFirst())
            {
                System.out.println("Upcoming task date: "+(cc.getString(cc.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME))));
                tId=cc.getLong(cc.getColumnIndex(JOBNeed_Table.JOBNEED_ID));
                System.out.println("JOB Id: "+tId);
                System.out.println("getItemPosition: "+getItemPosition(tId));
                currentPointer=getItemPosition(tId);
            }
        }
    }


    private void callScanQrCode()
    {
        Intent intent= new Intent(CheckpointListActivity.this,CaptureActivity.class);
        intent.putExtra("FROM","CHECKPOINT");
        startActivityForResult(intent, 0);

        //showDialog("SAMSUNG AC");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 || requestCode==3)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null)
                {
                    showDialog(data.getStringExtra("SCAN_RESULT"),requestCode);
                }
            }

        }
        else if(requestCode==1 && resultCode==RESULT_OK)
        {

        }
        else if(requestCode==2 && resultCode==RESULT_OK)
        {
            perpareJobList(Constants.JOB_NEED_IDENTIFIER_TOUR,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED);
        }
    }

    public void showDialog(final String assetCode, final int scanType) {

        if(assetCode!=null && assetCode.trim().length()>0)
        {
            Asset asset=assetDAO.getAssetAssignedReport(assetCode);
            if(asset!=null) {
                String qSetNameRaw = asset.getQsetname();
                String qSetIdRaw = asset.getQsetids();

                System.out.println("qSetNameRaw: " + qSetNameRaw);
                System.out.println("qSetIdRaw: " + qSetIdRaw);

                if(qSetIdRaw!=null && !qSetIdRaw.equalsIgnoreCase("null") && qSetIdRaw.trim().length()>0) {

                    questSetArraylist = new ArrayList<QuestionSet>();
                    boolean isAvailable = qSetNameRaw.contains("~");
                    if (isAvailable) {
                        String rIds = qSetIdRaw.replace(" ", ",");
                        questSetArraylist = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_checkpointadhoc_templates_query, rIds));
                    } else {
                        questSetArraylist = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_checkpointadhoc_templates_query, qSetIdRaw));
                    }

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CheckpointListActivity.this, android.R.layout.select_dialog_item);

                    for (QuestionSet questionSet : questSetArraylist) {
                        arrayAdapter.add(questionSet.getQsetname().toString().trim());
                    }

                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(CheckpointListActivity.this);
                    builderSingle.setTitle(getResources().getString(R.string.select_quest_set_title2));

                    builderSingle.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //String strName = arrayAdapter.getItem(which);
                            showQuestionRelatedQset(questSetArraylist.get(which).getQuestionsetid(), assetCode, scanType);

                        }
                    });
                    builderSingle.show();
                    builderSingle.setCancelable(false);
                }
                else
                {
                    Toast.makeText(CheckpointListActivity.this, getResources().getString(R.string.tour_checklistnotavailable),Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(CheckpointListActivity.this, getResources().getString(R.string.tour_checklistnotavailable),Toast.LENGTH_LONG).show();
            }
        }



    }


    private void showQuestionRelatedQset(long qSetId, String assetCode, int scanType)
    {
        adhocJobPef=getSharedPreferences(Constants.ADHOC_JOB_TIMESTAMP_PREF, MODE_PRIVATE);
        adhocJobPef.edit().putLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()).apply();
        adhocJobPef.edit().putString(Constants.ADHOC_ASSET,assetCode).apply();
        adhocJobPef.edit().putLong(Constants.ADHOC_QSET,qSetId).apply();
        if(scanType==0)
            adhocJobPef.edit().putString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_QR).apply();
        else if(scanType==3)
            adhocJobPef.edit().putString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_NFC).apply();

        int queCount= questionDAO.getChkPointQuestionsCount(qSetId);
        System.out.println("queCount: "+queCount);

        if(queCount>0)
        {
            Intent ii = new Intent(CheckpointListActivity.this, IncidentReportQuestionActivity.class);
            ii.putExtra("FROM", "CHECKPOINT");
            ii.putExtra("ID", qSetId);//need to pass quest set id
            ii.putExtra("ASSETCODE",assetCode);
            ii.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
            ii.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TOUR);
            startActivityForResult(ii, 1);
        }
        else
        {
            addAdhocJobNeed();
        }
    }

    private void addAdhocJobNeed()
    {
        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        JobNeed jobNeed=new JobNeed();
        jobNeed.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()));
        jobNeed.setJobdesc(Constants.JOB_TYPE_ADHOC + " " +adhocJobPef.getString(Constants.ADHOC_ASSET,""));
        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
        jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));//need to get id form type assist for Assigned status
        jobNeed.setScantype(typeAssistDAO.getEventTypeID(adhocJobPef.getString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_QR), Constants.IDENTIFIER_SCANTYPE));
        jobNeed.setReceivedonserver("");
        jobNeed.setPriority(typeAssistDAO.getEventTypeID("LOW", Constants.IDENTIFIER_PRIORITY));
        jobNeed.setStarttime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setEndtime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setRemarks("");
        jobNeed.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setCdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setMdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        //jobNeed.setIsdeleted("false");
        jobNeed.setAssetid(assetDAO.getAssetID(adhocJobPef.getString(Constants.ADHOC_ASSET,"")));//aadd asset id scan
        jobNeed.setGroupid(-1);
        jobNeed.setPeopleid(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setAatop(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setJobid(-1);
        jobNeed.setPerformedby(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setQuestionsetid(adhocJobPef.getLong(Constants.ADHOC_QSET,-1));
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeIDForAdhoc(Constants.JOB_NEED_IDENTIFIER_TOUR));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
        jobNeed.setParent(-1);
        jobNeed.setTicketcategory(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_AUTOCLOSED, "Ticket Category"));
        jobNeed.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
        jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
        jobNeedDAO.insertRecord(jobNeed,Constants.SYNC_STATUS_ZERO);
        CommonFunctions.EventLog("\n ADHOC EMPTY CHECKLIST Checkpoint Done: \n JOBNeed Id: "+jobNeed.getJobneedid()+"\n Time: "+CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1))+"\n");

        adhocJobPef.edit().clear().apply();
        Snackbar.make(fab,getResources().getString(R.string.adhoc_checkpoint_completed_msg),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            /*case R.id.gtlistButton:
                changeButtonTextColor(Constants.CHECKPOINT_GUARD_TOUR_LIST);
                selectedList=0;
                break;
            case R.id.adhoclistButton:
                changeButtonTextColor(Constants.CHECKPOINT_ADHOC_LIST);
                selectedList=1;
                break;*/
        }
    }

    private void changeButtonTextColor(int val)
    {
        if(val==Constants.CHECKPOINT_GUARD_TOUR_LIST)
        {
            perpareJobList(Constants.JOB_NEED_IDENTIFIER_TOUR,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED);
            /*gtListButton.setTextColor(getResources().getColor(R.color.text_header));
            adhocChekpointListButton.setTextColor(getResources().getColor(R.color.text_color));*/

        }
        else if(val==Constants.CHECKPOINT_ADHOC_LIST)
        {
            perpareJobList(Constants.JOB_NEED_IDENTIFIER_TOUR,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
            /*adhocChekpointListButton.setTextColor(getResources().getColor(R.color.text_header));
            gtListButton.setTextColor(getResources().getColor(R.color.text_color));*/

        }
    }

    private void perpareJobList(String identifier, String identifier1,String jobType)
    {
        System.out.println("preparejoblist called");
        checkpointArrayList=new ArrayList<JobNeed>();

        //checkpointArrayList=jobNeedDAO.getJobList(identifier,identifier1,jobType);
        checkpointArrayList=jobNeedDAO.getTourList(identifier,identifier1,jobType);
        checkPointListViewAdapter=new CheckPointListViewAdapter(CheckpointListActivity.this, checkpointArrayList,jobType);
        checkPointListView.setAdapter(checkPointListViewAdapter);
        checkPointListViewAdapter.notifyDataSetChanged();

        //getUpComingJOBFromList();

        handler.postDelayed( new Runnable()
        {
            @Override
            public void run()
            {
                if(currentPointer<checkPointListViewAdapter.getCount())
                {
                    checkPointListView.setSelection(currentPointer);
                    handler.postDelayed(this, 10 * 1000 );
                }
            }
        }, 10 * 1000 );

        if(checkpointArrayList.size()==0)
            Snackbar.make(checkPointListView, getResources().getString(R.string.data_not_found), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int accessValue = CommonFunctions.isAllowToAccessModules(CheckpointListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        System.out.println("==========="+accessValue);
        System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

        if(accessValue == 0) {
            if(CommonFunctions.isPermissionGranted(CheckpointListActivity.this)) {
                if (selectedList == 0) {
                    long backDate = new Date(CommonFunctions.getParse24HrsDate(checkpointArrayList.get(i).getPlandatetime())/* - (childCheckPointArraylist.get(i).getGracetime() * 60 * 1000)*/).getTime();
                    System.out.println("NextDate: " + (checkpointArrayList.get(i).getExpirydatetime()));
                    System.out.println("backDate: " + backDate);


                    int isJobExpired = CommonFunctions.isInBetweenDate(backDate, CommonFunctions.getParse24HrsDate(checkpointArrayList.get(i).getExpirydatetime()));
                    if (isJobExpired == 1) {
                        Intent childCPIntent = new Intent(CheckpointListActivity.this, CheckpointChildListActivity.class);
                        childCPIntent.putExtra("JOBNEEDID", checkpointArrayList.get(i).getJobneedid());
                        childCPIntent.putExtra("JOBNEEDDESC", checkpointArrayList.get(i).getJobdesc());
                        startActivityForResult(childCPIntent, 2);

                    } else if (isJobExpired == 0) {
                    Toast.makeText(CheckpointListActivity.this, getResources().getString(R.string.job_is_future, checkpointArrayList.get(i).getPlandatetime()), Toast.LENGTH_LONG).show();
                    //showCustomToastMsg(getResources().getString(R.string.job_is_future,childCheckPointArraylist.get(i).getPlandatetime()));
                } else if (isJobExpired == 2) {
                    Toast.makeText(CheckpointListActivity.this, getResources().getString(R.string.job_has_expired), Toast.LENGTH_SHORT).show();
                }

                }
            }
            else
                Snackbar.make(view,getResources().getString(R.string.error_msg_grant_permission),Snackbar.LENGTH_LONG).show();
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
        /*else {
            System.out.println("Scan Type: "+checkpointArrayList.get(i).getScantype());
            if(checkpointArrayList.get(i).getScantype()!=-1)
            {
                callScanQrCode();
            }
            else
            {
                Intent ii = new Intent(CheckpointListActivity.this, IncidentReportQuestionActivity.class);
                ii.putExtra("FROM", "ADHOC_TOUR");
                ii.putExtra("ID", checkpointArrayList.get(i).getQuestionsetid());
                ii.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                ii.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TOUR);
                startActivityForResult(ii, 0);
            }

        }*/


    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CommonFunctions.showSnack(CheckpointListActivity.this, isConnected,fab);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
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
}
