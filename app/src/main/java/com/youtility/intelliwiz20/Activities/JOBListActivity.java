package com.youtility.intelliwiz20.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.Model.UploadJobneedParameter;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Tables.JOBNeed_Table;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.ServerRequest;
import com.youtility.intelliwiz20.android.CaptureActivity;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link JOBDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */


//{"aatop":153069724755052,"assetid":-1,"buid":152432663306454,"cdtzoffset":0,"child":[{"details":[{"alerton":"","answer":"Yes","cdtz":"2018-09-05 17:54:00","cuser":153069724755052,"ismandatory":"","jndid":1536150240576,"jobneedid":1536150231957,"max":0.0,"mdtz":"2018-09-05 17:54:00","min":0.0,"muser":153069724755052,"option":"Yes, No, Na","questionid":152440197842806,"seqno":1,"type":57},{"alerton":"","answer":"Yes","cdtz":"2018-09-05 17:54:00","cuser":153069724755052,"ismandatory":"","jndid":1536150240579,"jobneedid":1536150231957,"max":0.0,"mdtz":"2018-09-05 17:54:00","min":0.0,"muser":153069724755052,"option":"Yes, No, Na","questionid":152440197842803,"seqno":2,"type":57}],"jobdesc":"Site Visit Details","questionsetid":152440080363439,"seqno":1},{"details":[{"alerton":"","answer":"auto sync","cdtz":"2018-09-05 17:54:00","cuser":153069724755052,"ismandatory":"","jndid":1536150240585,"jobneedid":1536150231959,"max":0.0,"mdtz":"2018-09-05 17:54:00","min":0.0,"muser":153069724755052,"option":"None","questionid":152464953465936,"seqno":1,"type":52}],"jobdesc":"Client Feedback","questionsetid":152464909485363,"seqno":2}],"cuser":153069724755052,"expirydatetime":"2018-09-05 17:53:51","frequency":-1,"gpslocation":"37.421998333333335,-122.08400000000002","gracetime":0,"groupid":-1,"identifier":13,"jobdesc":"Site Visit Report","jobid":-1,"jobneedid":1536150231671,"jobstatus":30,"jobtype":32,"muser":153069724755052,"othersite":"","parent":-1,"peopleid":153069724755052,"performedby":153069724755052,"plandatetime":"2018-09-05 17:53:51","priority":-1,"questionsetid":152440079371716,"scantype":-1}



public class JOBListActivity extends AppCompatActivity implements View.OnClickListener, IDialogEventListeners, ConnectivityReceiver.ConnectivityReceiverListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private ConnectivityReceiver connectivityReceiver;
    private boolean mTwoPane;
    /*private ArrayList<Asset> assets;
    public static final Map<String, Asset> ITEM_MAP =new HashMap<String, Asset>();
    public static final List<Asset> ITEMS =new ArrayList<Asset>();*/
    private ActionBar actionBar;
    private ProgressDialog pd;

    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private AssetDAO assetDAO;
    private QuestionDAO questionDAO;

    private SharedPreferences loginPref;
    private SharedPreferences deviceInfoPref;

    private ArrayList<JobNeed> jobNeedArrayList;
    public static final Map<String, JobNeed> ITEM_MAP =new HashMap<String, JobNeed>();
    public static final ArrayList<JobNeed> ITEMS =new ArrayList<JobNeed>();
    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;
    private View recyclerView;

    private Boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private FloatingActionButton fabMain,fabAdhocTaskCreate,fabAdhocTaskList;
    private AlertDialog alertDialog;
    private CharSequence[] jobNeedTypeOptions  =null;// {Constants.SCAN_TYPE_ENTERED,Constants.SCAN_TYPE_QR,Constants.SCAN_TYPE_NFC};
    private ArrayList<QuestionSet> questSetArraylist;
    private SharedPreferences adhocJobPef;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences loginDetailPref;
    private CheckNetwork checkNetwork;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private AttachmentDAO attachmentDAO;

    private int currentPointer=0;
    long tId=-1;
    private CustomAlertDialog customAlertDialog;

    private ArrayList<String>scanTypeList=null;
    private SharedPreferences jobalertPref;

    private String fromActivity=null;

    private TextView assignedTextview, completedTextview, autoclosedTextview;

    private String jobType=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.drawable.youtility_actionbar_icon);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setTitle(getResources().getString(R.string.title_activity_dashboard));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        fromActivity=getIntent().getStringExtra("FROM");

        jobNeedDAO=new JobNeedDAO(JOBListActivity.this);
        typeAssistDAO=new TypeAssistDAO(JOBListActivity.this);
        assetDAO=new AssetDAO(JOBListActivity.this);
        questionDAO=new QuestionDAO(JOBListActivity.this);
        jobNeedDetailsDAO=new JobNeedDetailsDAO(JOBListActivity.this);
        attachmentDAO=new AttachmentDAO(JOBListActivity.this);

        customAlertDialog=new CustomAlertDialog(JOBListActivity.this,this);

        checkNetwork=new CheckNetwork(JOBListActivity.this);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        adhocJobPef=getSharedPreferences(Constants.ADHOC_JOB_TIMESTAMP_PREF, MODE_PRIVATE);
        jobalertPref=getSharedPreferences(Constants.JOB_ALERT_PREF, MODE_PRIVATE);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        pd=new ProgressDialog(JOBListActivity.this);
        pd.setMessage(getResources().getString(R.string.please_wait));
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        assignedTextview=(TextView)findViewById(R.id.assignedTextview);
        completedTextview=(TextView)findViewById(R.id.completedTextview);
        autoclosedTextview=(TextView)findViewById(R.id.autoclosedTextview);

        assignedTextview.setOnClickListener(this);
        completedTextview.setOnClickListener(this);
        autoclosedTextview.setOnClickListener(this);

        System.out.println("jobNeedDAO.deleteCompletedYesterdayTask(): "+jobNeedDAO.deleteCompletedYesterdayTask());

        fabAdhocTaskCreate = (FloatingActionButton)findViewById(R.id.fabAdhocTaskCreate);
        fabAdhocTaskCreate.setOnClickListener(this);

        ArrayList<TypeAssist> taScanTypeList=typeAssistDAO.getEventList("Scan Type");
        if(taScanTypeList!=null && taScanTypeList.size()>0)
        {
            scanTypeList=new ArrayList<>();
            for(int i=0;i<taScanTypeList.size();i++)
            {
                scanTypeList.add(taScanTypeList.get(i).getTaname());
            }
        }

        if(scanTypeList!=null && scanTypeList.size()>0)
            jobNeedTypeOptions = scanTypeList.toArray(new CharSequence[scanTypeList.size()]);
        /*assets=new ArrayList<Asset>();
        AssetAsyncTask assetAsynTask=new AssetAsyncTask();
        assetAsynTask.execute();*/

//152809358791890
        jobNeedArrayList=new ArrayList<>();

        if(fromActivity!=null) {
            if (fromActivity.equalsIgnoreCase(Constants.JOB_NEED_IDENTIFIER_TASK)) {
                this.setTitle(getResources().getString(R.string.module_task));
                fabAdhocTaskCreate.setVisibility(View.VISIBLE);
            } else {
                this.setTitle(getResources().getString(R.string.module_ppm));
                fabAdhocTaskCreate.setVisibility(View.INVISIBLE);
            }
        }

        //jobNeedArrayList=jobNeedDAO.getJobList(fromActivity,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED);
        jobType=Constants.JOB_TYPE_SCHEDULED;
        jobNeedArrayList=jobNeedDAO.getStatusBaseJobList(fromActivity,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED,Constants.JOBNEED_STATUS_ASSIGNED);
        System.out.println("jobNeedArrayList.size(): "+jobNeedArrayList.size());

        if(jobNeedArrayList!=null && jobNeedArrayList.size()>0) {
            for (int j = 0; j < jobNeedArrayList.size(); j++) {
                ITEMS.add(jobNeedArrayList.get(j));
                ITEM_MAP.put(jobNeedArrayList.get(j).getJobneedid() + "", jobNeedArrayList.get(j));
            }
        }

        changeTextColor(0);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAdhocTask);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent adhocJobActivity=new Intent(JOBListActivity.this,AdhocJobActivity.class);
                startActivityForResult(adhocJobActivity,0);
                Snackbar.make(view, "Adhoc task creation", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        getUpComingJOBFromList(fromActivity);

        if(jobNeedArrayList!=null && jobNeedArrayList.size()>0) {
            recyclerView = findViewById(R.id.item_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);

            if (findViewById(R.id.item_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-w900dp).
                // If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;
            }
        }

        jobNeedDAO.getCount();

        /*fabMain = (FloatingActionButton)findViewById(R.id.fabMain);
        fabAdhocTaskCreate = (FloatingActionButton)findViewById(R.id.fabAdhocTaskCreate);
        fabAdhocTaskList = (FloatingActionButton)findViewById(R.id.fabAdhocTaskList);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fabMain.setOnClickListener(this);
        fabAdhocTaskCreate.setOnClickListener(this);
        fabAdhocTaskList.setOnClickListener(this);*/






    }

    private int getItemPosition(long id)
    {
        for(int i=0;i<jobNeedArrayList.size();i++)
        {
            Long dd= jobNeedArrayList.get(i).getJobneedid();
            System.out.println("id: "+id+" : dd: "+dd);
            if(dd==id)
                return i;
        }
        return 0;
    }

    private void getUpComingJOBFromList(String jobIdentifier)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, +15);

        Calendar cal1=Calendar.getInstance();
        cal1.add(Calendar.MINUTE,-15);
        Cursor cc=jobNeedDAO.getScheduleTaskList(cal1.getTimeInMillis(),cal.getTimeInMillis(), jobIdentifier);
        System.out.println("cal1"+cal1.getTimeInMillis()+"cal"+cal.getTimeInMillis());
        if(cc!=null)
        {
            if(cc.moveToFirst())
            {
                System.out.println("Upcoming task date: "+(cc.getString(cc.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME))));
                tId=cc.getLong(cc.getColumnIndex(JOBNeed_Table.JOBNEED_ID));
                System.out.println("JOB Id: "+tId);

            }
            System.out.println("getItemPosition: "+getItemPosition(tId));
            currentPointer=getItemPosition(tId);
            if(currentPointer!=0) {
                jobalertPref.edit().putInt(Constants.JOBALERT_POSITION, currentPointer).apply();
                jobalertPref.edit().putLong(Constants.JOBALERT_ID, tId).apply();
            }

        }

    }




    private void refreshView()
    {
        if(jobNeedArrayList!=null && jobNeedArrayList.size()>0) {
            for (int j = 0; j < jobNeedArrayList.size(); j++) {
                ITEMS.add(jobNeedArrayList.get(j));
                ITEM_MAP.put(jobNeedArrayList.get(j).getJobneedid() + "", jobNeedArrayList.get(j));
            }
        }

        recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("fromactivity: "+fromActivity);
        //prepareAdapter(Constants.JOB_TYPE_SCHEDULED);
        //jobType=Constants.JOB_TYPE_SCHEDULED;
        /*changeTextColor(0);
        prepareStatuswiseAdapter(jobType,Constants.JOBNEED_STATUS_ASSIGNED);*/
    }

    private void prepareAdapter(String type)
    {
        jobNeedArrayList=new ArrayList<>();
        jobNeedArrayList=jobNeedDAO.getJobList(fromActivity,Constants.IDENTIFIER_JOBNEED,type);
        if(jobNeedArrayList.size()==0)
            Snackbar.make(fabAdhocTaskCreate,getResources().getString(R.string.data_not_found), Snackbar.LENGTH_LONG).show();
        refreshView();
    }

    private void prepareStatuswiseAdapter(String type, String statusType)
    {
        jobNeedArrayList=new ArrayList<>();
        jobNeedArrayList=jobNeedDAO.getStatusBaseJobList(fromActivity,Constants.IDENTIFIER_JOBNEED,type,statusType);
        if(jobNeedArrayList.size()==0)
            Snackbar.make(fabAdhocTaskCreate,getResources().getString(R.string.data_not_found), Snackbar.LENGTH_LONG).show();
        refreshView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String jCount=null;
        switch (item.getItemId())
        {
            case R.id.action_info:
                System.out.println("From Date: "+ CommonFunctions.getFromToDate(0));
                System.out.println("To Date: "+CommonFunctions.getFromToDate(1));
                /*if(fromActivity.equalsIgnoreCase(Constants.JOB_NEED_IDENTIFIER_TASK)) {
                    jCount=jobNeedDAO.getJobneedCount1(Constants.JOB_NEED_IDENTIFIER_TASK,CommonFunctions.getFromToDate(0), CommonFunctions.getFromToDate(1));
                }
                else if(fromActivity.equalsIgnoreCase(Constants.JOB_NEED_IDENTIFIER_PPM))
                {
                    jCount=jobNeedDAO.getJobneedCount1(Constants.JOB_NEED_IDENTIFIER_PPM,CommonFunctions.getFromToDate(0), CommonFunctions.getFromToDate(1));
                }*/
                if(!fromActivity.equalsIgnoreCase(Constants.JOB_NEED_IDENTIFIER_PPM)) {
                    jCount = jobNeedDAO.getJobneedCount1(fromActivity, CommonFunctions.getFromToDate(0), CommonFunctions.getFromToDate(1));
                    String[] jobCount = jCount.split("~");//schedule, complete, pending, closed
                    customAlertDialog.JOBInfoDialog(getResources().getString(R.string.jobinfo_dialog_title, fromActivity), jobCount[0], jobCount[1], jobCount[2], jobCount[3]);
                }
                else
                {
                    jCount=jobNeedDAO.getPPMCount(fromActivity);
                    String[] jobCount = jCount.split("~");//schedule, complete, pending, closed
                    customAlertDialog.JOBInfoDialog(getResources().getString(R.string.jobinfo_dialog_title, fromActivity), jobCount[0], jobCount[1], jobCount[2], jobCount[3]);
                }
                return true;
            case R.id.action_scheduleList:
                //prepareAdapter(Constants.JOB_TYPE_SCHEDULED);
                jobType=Constants.JOB_TYPE_SCHEDULED;
                changeTextColor(0);
                prepareStatuswiseAdapter(Constants.JOB_TYPE_SCHEDULED,Constants.JOBNEED_STATUS_ASSIGNED);
                return true;
            case R.id.action_adhocList:
                //prepareAdapter(Constants.JOB_TYPE_ADHOC);
                jobType=Constants.JOB_TYPE_ADHOC;
                changeTextColor(0);
                prepareStatuswiseAdapter(Constants.JOB_TYPE_ADHOC,Constants.JOBNEED_STATUS_ASSIGNED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem search = menu.findItem(R.id.taskSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);

        MenuItem schedule=menu.findItem(R.id.action_scheduleList);
        MenuItem adhocList=menu.findItem(R.id.action_adhocList);
        if(fromActivity!=null && !fromActivity.equalsIgnoreCase("null")) {
            if (fromActivity.equalsIgnoreCase(Constants.JOB_NEED_IDENTIFIER_TASK)) {
                schedule.setVisible(true);
                adhocList.setVisible(true);
            } else if (fromActivity.equalsIgnoreCase(Constants.JOB_NEED_IDENTIFIER_PPM)) {
                schedule.setVisible(false);
                adhocList.setVisible(false);
            }
        }

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
                if(simpleItemRecyclerViewAdapter!=null)
                    simpleItemRecyclerViewAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("back to job list");
        if(requestCode==2 && resultCode==RESULT_OK)
        {
            if(data!=null) {
                adhocJobPef.edit().putString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_QR).apply();
                CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_QR,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                CommonFunctions.manualSyncEventLog("QRScanResult",data.getStringExtra("SCAN_RESULT"),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                showDialog(data.getStringExtra("SCAN_RESULT"));
            }
        }
        else if(requestCode==1 && resultCode==RESULT_OK)
        {
            if(data!=null) {
                adhocJobPef.edit().putString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_NFC).apply();
                CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_NFC,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                CommonFunctions.manualSyncEventLog("NFCScanResult",data.getStringExtra("SCAN_RESULT"),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                showDialog(data.getStringExtra("SCAN_RESULT"));
            }
        }
        else if(requestCode==12)
        {
            if(resultCode==RESULT_OK) {
                System.out.println("Back to screen from adhoc scan jobneed");
                saveData();
            }
            else if(resultCode==RESULT_CANCELED)
            {
                adhocJobPef.edit().clear().apply();
            }
        }

    }

    public void showDialog(final String assetCode) {

        if(assetCode!=null && assetCode.trim().length()>0) {

            System.out.println("-----------------------------------------Scan AssetCode: "+assetCode);

            Asset asset=assetDAO.getAssetAssignedReport(assetCode);
            if(asset!=null && asset.getAssetid()!=-1) {

                adhocJobPef.edit().putLong(Constants.ADHOC_ASSET_ID,asset.getAssetid()).apply();
                CommonFunctions.manualSyncEventLog("AssetIDFromScanResult",asset.getAssetid()+" : "+assetCode,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                CommonFunctions.manualSyncEventLog("QSetBelongToAsset",asset.getAssetid()+" : "+asset.getQsetname(),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));

                String qSetNameRaw = asset.getQsetname();
                String qSetIdRaw = asset.getQsetids();

                System.out.println("qSetNameRaw: " + qSetNameRaw);
                System.out.println("qSetIdRaw: " + qSetIdRaw);

                if(qSetIdRaw!=null && !qSetIdRaw.equalsIgnoreCase("null") && qSetIdRaw.trim().length()>0) {

                    questSetArraylist = new ArrayList<QuestionSet>();
                    //questSetArraylist = questionDAO.getQuestionSetCodeList(assetCode);

                    boolean isAvailable = qSetNameRaw.contains("~");
                    if (isAvailable) {
                        String rIds = qSetIdRaw.replace(" ", ",");
                        questSetArraylist = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_assetadhoc_templates_query, rIds));
                    } else {
                        questSetArraylist = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_assetadhoc_templates_query, qSetIdRaw));
                    }

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(JOBListActivity.this, android.R.layout.select_dialog_item);

                    for (QuestionSet questionSet : questSetArraylist) {
                        arrayAdapter.add(questionSet.getQsetname().toString().trim());
                    }
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(JOBListActivity.this);
                    builderSingle.setTitle(getResources().getString(R.string.select_quest_set_title1));

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
                            showQuestionRelatedQset(questSetArraylist.get(which).getQuestionsetid(), assetCode);

                        }
                    });
                    builderSingle.create();
                    builderSingle.setCancelable(false);
                    builderSingle.show();
                }
                else
                {
                    Toast.makeText(JOBListActivity.this, getResources().getString(R.string.questionset_not_available),Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(JOBListActivity.this, getResources().getString(R.string.questionset_not_available),Toast.LENGTH_LONG).show();
            }
        }

    }

    private void saveData()
    {
        /*if(checkNetwork.isNetworkConnectionAvailable()) {
            JOBNeedInsertAsynTask jobNeedInsertAsynTask = new JOBNeedInsertAsynTask();
            jobNeedInsertAsynTask.execute();
        }
        else
        {
            insertIntoDatabase();
        }*/
        CommonFunctions.EventLog("\n <ADHOC JOB Saved in DB> \n"+CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1))+"\n\n");
        insertIntoDatabase();

    }


    @SuppressLint("StringFormatInvalid")
    private void insertIntoDatabase()
    {
        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        JobNeed jobNeed=new JobNeed();
        jobNeed.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()));
        jobNeed.setJobdesc(getResources().getString(R.string.adhoc_default_description, CommonFunctions.getFormatedDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()))));
        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));
        jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED,Constants.STATUS_TYPE_JOBNEED));
        jobNeed.setScantype(typeAssistDAO.getEventTypeID(adhocJobPef.getString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_QR), Constants.IDENTIFIER_SCANTYPE));
        jobNeed.setReceivedonserver(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
        jobNeed.setPriority(typeAssistDAO.getEventTypeID("HIGH", Constants.IDENTIFIER_PRIORITY));
        jobNeed.setStarttime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
        jobNeed.setEndtime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setRemarks(getResources().getString(R.string.adhoc_default_remark));
        jobNeed.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setCdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
        jobNeed.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setMdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
        jobNeed.setAssetid(adhocJobPef.getLong(Constants.ADHOC_ASSET_ID,-1));
        jobNeed.setAatop(-1);
        jobNeed.setGroupid(-1);
        jobNeed.setPeopleid(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setQuestionsetid(adhocJobPef.getLong(Constants.ADHOC_QSET,-1));
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TASK,Constants.IDENTIFIER_JOBNEED));
        jobNeed.setParent(-1);
        jobNeed.setTicketno(-1);
        jobNeed.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
        jobNeed.setTicketcategory(-1);
        jobNeed.setSeqno(0);
        jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
        jobNeed.setPerformedby(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setJobid(-1);
        jobNeedDAO.insertRecord(jobNeed, "0");

        CommonFunctions.manualSyncEventLog("ADHOC_END","JOBID: "+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1),jobNeed.getEndtime());

        adhocJobPef.edit().clear().apply();
        //finish();
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        if(type==1)
        {
            long assetIDFrmDB=assetDAO.getAssetID(errorMessage);

            if(assetIDFrmDB!=-1) {
                adhocJobPef.edit().putString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_ENTERED).apply();
                CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_ENTERED,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                CommonFunctions.manualSyncEventLog("EnteredTypeResult",errorMessage,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                showDialog(errorMessage);
            }
            else
                Snackbar.make(fabAdhocTaskCreate,getResources().getString(R.string.joblist_assetcodenotfound), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CommonFunctions.showSnack(JOBListActivity.this, isConnected,assignedTextview);
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

    private class JOBNeedInsertAsynTask extends AsyncTask<Void, Integer, Void>
    {
        MediaType JSON;
        OkHttpClient client1;
        StringBuffer sb;

        InputStream is;
        byte[] buffer = null;
        int byteread = 0;
        UploadJobneedParameter uploadJobneedParameter;
        ArrayList<JobNeedDetails>jobNeedDetailsArrayList;
        Gson gson;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            client1 = new OkHttpClient();
            JSON = MediaType.parse("application/json; charset=utf-8");
            gson = new Gson();
        }



        @Override
        protected Void doInBackground(Void... voids) {

            {
                try {
                    String date = null;
                    URL url = new URL(Constants.BASE_URL); // here is your URL path
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    date = df.format(Calendar.getInstance().getTime());

                    System.out.println("Current date format: " + date);

                    String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

                    jobNeedDetailsArrayList=new ArrayList<>();
                    jobNeedDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                    uploadJobneedParameter=new UploadJobneedParameter();
                    uploadJobneedParameter.setDetails(jobNeedDetailsArrayList);
                    uploadJobneedParameter.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                    uploadJobneedParameter.setJobdesc(getResources().getString(R.string.adhoc_default_description, CommonFunctions.getFormatedDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()))));
                    uploadJobneedParameter.setAatop(-1);
                    uploadJobneedParameter.setAssetid(assetDAO.getAssetID(adhocJobPef.getString(Constants.ADHOC_ASSET,"")));
                    uploadJobneedParameter.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                    uploadJobneedParameter.setFrequency(-1);
                    uploadJobneedParameter.setPlandatetime(CommonFunctions.getTimezoneDate(Calendar.getInstance().getTimeInMillis()));
                    uploadJobneedParameter.setExpirydatetime(CommonFunctions.getTimezoneDate(Calendar.getInstance().getTimeInMillis()));
                    uploadJobneedParameter.setStarttime(CommonFunctions.getTimezoneDate(Calendar.getInstance().getTimeInMillis()));
                    uploadJobneedParameter.setEndtime(CommonFunctions.getTimezoneDate(Calendar.getInstance().getTimeInMillis()));
                    uploadJobneedParameter.setCdtz(CommonFunctions.getTimezoneDate(Calendar.getInstance().getTimeInMillis()));
                    uploadJobneedParameter.setMdtz(CommonFunctions.getTimezoneDate(Calendar.getInstance().getTimeInMillis()));
                    uploadJobneedParameter.setGracetime(0);
                    uploadJobneedParameter.setGroupid(-1);
                    uploadJobneedParameter.setIdentifier(typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TASK,Constants.IDENTIFIER_JOBNEED));
                    uploadJobneedParameter.setJobid(-1);
                    uploadJobneedParameter.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED,Constants.STATUS_TYPE_JOBNEED));
                    uploadJobneedParameter.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));
                    uploadJobneedParameter.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                    uploadJobneedParameter.setParent(-1);
                    uploadJobneedParameter.setPeopleid(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                    uploadJobneedParameter.setPerformedby(-1);
                    uploadJobneedParameter.setPriority(typeAssistDAO.getEventTypeID("HIGH", Constants.IDENTIFIER_PRIORITY));
                    uploadJobneedParameter.setScantype(typeAssistDAO.getEventTypeID(adhocJobPef.getString(Constants.ADHOC_TYPE,""), Constants.IDENTIFIER_SCANTYPE));
                    uploadJobneedParameter.setQuestionsetid(adhocJobPef.getLong(Constants.ADHOC_QSET,-1));
                    uploadJobneedParameter.setGpslocation(gpsLocation);
                    uploadJobneedParameter.setRemarks(getResources().getString(R.string.adhoc_default_remark));
                    uploadJobneedParameter.setTicketcategory(-1);
                    uploadJobneedParameter.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));

                    String ss=gson.toJson(uploadJobneedParameter);

                    System.out.println("upData SS: "+ss);

                    //--------------------------------------------------

                    ServerRequest serverRequest=new ServerRequest(JOBListActivity.this);
                    HttpResponse response=serverRequest.getAdhocLogResponse(ss.trim(),
                            loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                            loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                            loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                            loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                    if(response!=null && response.getStatusLine().getStatusCode()==200)
                    {
                        is = response.getEntity().getContent();

                        sb = new StringBuffer("");
                        buffer = new byte[1024];
                        byteread = 0;
                        try {
                            while ((byteread = is.read(buffer)) != -1) {
                                sb.append(new String(buffer));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        is.close();
                        System.out.println("SB: " + sb.toString());
                        response.getEntity().consumeContent();
                    }


                    //------------------------------------------------------------------------------------


                    /*UploadParameters uploadParameters=new UploadParameters();
                    uploadParameters.setServicename(Constants.SERVICE_ADHOC);
                    uploadParameters.setQuery(ss);
                    uploadParameters.setBiodata(ss);
                    uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);


                    String upData = gson.toJson(uploadParameters);
                    System.out.println("upData: "+upData);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);//Don't use a cached Copy
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");

                    StringEntity data=new StringEntity(upData, HTTP.UTF_8);
                    OutputStream out = new BufferedOutputStream(conn.getOutputStream());


                    is = data.getContent();
                    buffer = new byte[1024];
                    byteread = 0;
                    while ((byteread = is.read(buffer)) != -1)
                    {
                        out.write(buffer, 0, byteread);
                    }
                    out.flush();
                    out.close();
                    is.close();


                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                        conn.getInputStream()));
                        sb = new StringBuffer("");
                        String line = "";

                        while ((line = in.readLine()) != null) {

                            sb.append(line);
                            break;
                        }

                        in.close();
                        System.out.println("SB: " + sb.toString());

                    } else {
                        System.out.println("SB: " + responseCode);
                    }*/
                } catch (Exception e) {
                    System.out.println("SB: " + e.toString());
                }

            }

            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {

            //super.onPostExecute(aVoid);

            try {
                if(sb!=null && sb.toString().trim().length()>0) {
                    JSONObject ob = new JSONObject(sb.toString());

                    int status = ob.getInt(Constants.RESPONSE_RC);//0 means success data, 1 means failed
                    long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                    System.out.println("status: " + status);
                    System.out.println("returnID: " + returnidResp);
                    String adhocReturnID = String.valueOf(returnidResp);

                    if (status == 0) {
                        attachmentDAO.changeAdhocReturnID(adhocReturnID, String.valueOf(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1)));
                        jobNeedDAO.updateJobNeedRecordFromAdhoc(returnidResp, typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));
                    }
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                adhocJobPef.edit().clear().apply();
                //finish();
            }

        }
    }





    private void showQuestionRelatedQset(long qSetID, String assetCode)
    {
        adhocJobPef=getSharedPreferences(Constants.ADHOC_JOB_TIMESTAMP_PREF, MODE_PRIVATE);
        adhocJobPef.edit().putLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()).apply();
        adhocJobPef.edit().putString(Constants.ADHOC_ASSET,assetCode).apply();
        adhocJobPef.edit().putLong(Constants.ADHOC_QSET,qSetID).apply();
        CommonFunctions.manualSyncEventLog("ADHOC_START",assetCode+" : "+qSetID,CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));

        Intent ii = new Intent(JOBListActivity.this, IncidentReportQuestionActivity.class);
        ii.putExtra("FROM", "ADHOC_SCAN");
        ii.putExtra("ID", qSetID);//need to pass quest set id
        ii.putExtra("ASSETCODE",assetCode);
        ii.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
        ii.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TASK);
        startActivityForResult(ii, 12);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        simpleItemRecyclerViewAdapter=new SimpleItemRecyclerViewAdapter(jobNeedArrayList);
        //recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(jobNeedArrayList));
        recyclerView.setAdapter(simpleItemRecyclerViewAdapter);
        if(currentPointer==0)
        {
            if(jobalertPref.getInt(Constants.JOBALERT_POSITION,-1)!=-1)
            {
                recyclerView.scrollToPosition(jobalertPref.getInt(Constants.JOBALERT_POSITION, 0));
                //Snackbar.make(fabAdhocTaskCreate, "Scroll to position "+jobalertPref.getInt(Constants.JOBALERT_POSITION, 0), Snackbar.LENGTH_SHORT).show();
            }
            else
            {
                recyclerView.scrollToPosition(currentPointer);
                //Snackbar.make(fabAdhocTaskCreate, "Scroll to position "+currentPointer, Snackbar.LENGTH_SHORT).show();
            }
        }
        else
        {
            recyclerView.scrollToPosition(currentPointer);
            //Snackbar.make(fabAdhocTaskCreate, "Scroll to position "+currentPointer, Snackbar.LENGTH_SHORT).show();
        }

        recyclerView.invalidate();
    }

    @Override
    public void onClick(View v) {
        int accessValue = CommonFunctions.isAllowToAccessModules(JOBListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
        double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
        System.out.println("===========" + accessValue);
        System.out.println("===========" + loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

        if (accessValue == 0 ) {
            switch (v.getId()) {
            /*case R.id.fabMain:
                animateFAB();
                break;*/
                case R.id.fabAdhocTaskCreate:
                    if (CommonFunctions.isPermissionGranted(JOBListActivity.this)) {
                        if (jobNeedTypeOptions != null && jobNeedTypeOptions.length > 0)
                            showAlertOptions();
                    } else
                        Snackbar.make(fabAdhocTaskCreate, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
                    break;
            /*case R.id.fabAdhocTaskList:
                Intent adhocJobListActivity=new Intent(JOBListActivity.this, AdhocJobListActivityListActivity.class);
                startActivityForResult(adhocJobListActivity,11);
                break;*/
                case R.id.assignedTextview:
                    changeTextColor(0);
                    prepareStatuswiseAdapter(jobType, Constants.JOBNEED_STATUS_ASSIGNED);
                    break;
                case R.id.completedTextview:
                    changeTextColor(1);
                    prepareStatuswiseAdapter(jobType, Constants.JOBNEED_STATUS_COMPLETED);
                    break;
                case R.id.autoclosedTextview:
                    changeTextColor(2);
                    prepareAdapter(jobType);
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

    private void changeTextColor(int val)
    {
        switch(val)
        {

            case 0:
                assignedTextview.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                assignedTextview.setTextColor(getResources().getColor(R.color.button_background));

                completedTextview.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                completedTextview.setTextColor(getResources().getColor(R.color.colorWhite));

                autoclosedTextview.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                autoclosedTextview.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
            case 1:
                assignedTextview.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                assignedTextview.setTextColor(getResources().getColor(R.color.colorWhite));

                completedTextview.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                completedTextview.setTextColor(getResources().getColor(R.color.button_background));

                autoclosedTextview.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                autoclosedTextview.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
            case 2:
                assignedTextview.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                assignedTextview.setTextColor(getResources().getColor(R.color.colorWhite));

                completedTextview.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                completedTextview.setTextColor(getResources().getColor(R.color.colorWhite));

                autoclosedTextview.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                autoclosedTextview.setTextColor(getResources().getColor(R.color.button_background));
                break;
            /*case 0:
                assignedTextview.setBackground(getResources().getDrawable(R.drawable.rounder_corner_button));
                completedTextview.setBackground(getResources().getDrawable(R.drawable.rounder_cancel_button));
                autoclosedTextview.setBackground(getResources().getDrawable(R.drawable.rounder_cancel_button));
                break;
            case 1:
                assignedTextview.setBackground(getResources().getDrawable(R.drawable.rounder_cancel_button));
                completedTextview.setBackground(getResources().getDrawable(R.drawable.rounder_corner_button));
                autoclosedTextview.setBackground(getResources().getDrawable(R.drawable.rounder_cancel_button));
                break;
            case 2:
                assignedTextview.setBackground(getResources().getDrawable(R.drawable.rounder_cancel_button));
                completedTextview.setBackground(getResources().getDrawable(R.drawable.rounder_cancel_button));
                autoclosedTextview.setBackground(getResources().getDrawable(R.drawable.rounder_corner_button));
                break;*/
        }
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

    private void showAlertOptions()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(JOBListActivity.this);


        builder.setTitle(getResources().getString(R.string.joblist_selecturchoice_title));

        builder.setSingleChoiceItems(jobNeedTypeOptions, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        customAlertDialog.custEnteredScanType(getResources().getString(R.string.joblist_enterassetcode), getResources().getString(R.string.joblist_enterassetcode),1);
                        alertDialog.dismiss();
                        break;
                    case 1:
                        if(checkNFCSupported()) {
                            alertDialog.dismiss();
                            Intent nfcIntent = new Intent(JOBListActivity.this, NFCCodeReaderActivity.class);
                            startActivityForResult(nfcIntent, item);
                        }
                        else
                        {
                            Snackbar.make(fabAdhocTaskCreate,getResources().getString(R.string.joblist_nfcnotsupported), Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case 2:
                        alertDialog.dismiss();
                        Intent intent= new Intent(JOBListActivity.this, CaptureActivity.class);
                        intent.putExtra("FROM","CHECKPOINT");
                        startActivityForResult(intent, item);
                        break;
                    case 3:
                        alertDialog.dismiss();
                        CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_SKIP,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                        Intent adhocJobActivity=new Intent(JOBListActivity.this,AdhocJobActivity.class);
                        startActivityForResult(adhocJobActivity,item);
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

    public void animateFAB(){

        if(isFabOpen){

            fabMain.startAnimation(rotate_backward);
            fabAdhocTaskCreate.startAnimation(fab_close);
            fabAdhocTaskList.startAnimation(fab_close);
            fabAdhocTaskCreate.setClickable(false);
            fabAdhocTaskList.setClickable(false);
            isFabOpen = false;

        } else {

            fabMain.startAnimation(rotate_forward);
            fabAdhocTaskCreate.startAnimation(fab_open);
            fabAdhocTaskList.startAnimation(fab_open);
            fabAdhocTaskCreate.setClickable(true);
            fabAdhocTaskList.setClickable(true);
            isFabOpen = true;

        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> implements Filterable {

        private ArrayList<JobNeed> mValues;
        private ArrayList<JobNeed> mFilteredList;
        private Drawable bulletAssigned;
        private Drawable bulletInprogress;
        private Drawable bulletCompleted;
        private Drawable bulletArchived;
        private Drawable bulletClosed;
        private Drawable assignedToPeople;
        private Drawable assignedToGroup;



        public SimpleItemRecyclerViewAdapter(ArrayList<JobNeed> items) {
            mValues = items;
            mFilteredList=items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.job_item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if(mValues.get(position)!=null)
            {
                holder.mItem = mValues.get(position);
                holder.mIdView.setText(mValues.get(position).getJobneedid()+"");
                if(assetDAO.getAssetName(mValues.get(position).getAssetid()).trim().length()>0)
                    holder.mContentView.setText(assetDAO.getAssetName(mValues.get(position).getAssetid())+"");
                else
                    holder.mContentView.setText(assetDAO.getAssetCode(mValues.get(position).getAssetid())+"");

                System.out.println("Status: "+mValues.get(position).getJobstatus());
                if(mValues.get(position).getJobstatus()!=-1) {
                    String jStatus = typeAssistDAO.getEventTypeCode(mValues.get(position).getJobstatus());

                    if(jStatus!=null) {
                        if (jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_ASSIGNED)) {
                            //holder.mContentView.setCompoundDrawablesWithIntrinsicBounds(bulletAssigned,null,null,null);
                            holder.mColorCodeBulletView.setImageDrawable(bulletAssigned);
                        } else if (jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_INPROGRESS)) {
                            //holder.mContentView.setCompoundDrawablesWithIntrinsicBounds(bulletInprogress,null,null,null);
                            holder.mColorCodeBulletView.setImageDrawable(bulletInprogress);
                        } else if (jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED)) {
                            //holder.mContentView.setCompoundDrawablesWithIntrinsicBounds(bulletCompleted,null,null,null);
                            holder.mColorCodeBulletView.setImageDrawable(bulletCompleted);
                        } else if (jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED)) {
                            //holder.mContentView.setCompoundDrawablesWithIntrinsicBounds(bulletClosed,null,null,null);
                            holder.mColorCodeBulletView.setImageDrawable(bulletClosed);
                        }
                    }
                }


                //holder.mpDateView.setText(CommonFunctions.getFormatedDateWithoutTime(mValues.get(position).getPlandatetime())+" "+mValues.get(position).getJobdesc());

                System.out.println("-----------------------From DB: "+mValues.get(position).getPlandatetime());
                System.out.println("-----------------------From Conversion: "+CommonFunctions.getDeviceTimezoneFormatDate(mValues.get(position).getPlandatetime()));
                String text = "<font color=#18B064>"+((mValues.get(position).getPlandatetime()))+"</font> <font color=#919191>"+" "+mValues.get(position).getJobdesc()+"</font>";
                holder.mpDateView.setText(Html.fromHtml(text));

                if(mValues.get(position).getPeopleid()!=-1)
                    holder.mAssignedToImageview.setImageDrawable(assignedToPeople);
                else if(mValues.get(position).getGroupid()!=-1)
                    holder.mAssignedToImageview.setImageDrawable(assignedToGroup);


                //holder.mDescView.setText(mValues.get(position).getJobdesc());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int accessValue = CommonFunctions.isAllowToAccessModules(JOBListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                        double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
                        double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
                        System.out.println("===========" + accessValue);
                        System.out.println("===========" + loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

                        if (accessValue == 0 ) {
                            if (CommonFunctions.isPermissionGranted(JOBListActivity.this)) {
                                //isTaskExpired(holder.mItem);
                                if (isTaskExpired(holder.mItem)) {
                                    //Snackbar.make(v,getResources().getString(R.string.job_has_expired),Snackbar.LENGTH_LONG).show();
                                    Toast.makeText(JOBListActivity.this, getResources().getString(R.string.job_has_expired), Toast.LENGTH_LONG).show();
                                    //jobNeedDAO.changeJobStatus(holder.mItem.getJobneedid(),typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_AUTOCLOSED, Constants.STATUS_TYPE_JOBNEED));
                                }

                                if (mTwoPane) {
                                    Bundle arguments = new Bundle();
                                    arguments.putString(JOBDetailFragment.ARG_ITEM_ID, holder.mItem.getJobneedid() + "");
                                    JOBDetailFragment fragment = new JOBDetailFragment();
                                    fragment.setArguments(arguments);
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.item_detail_container, fragment)
                                            .commit();
                                } else {
                                    Context context = v.getContext();
                                    Intent intent = new Intent(context, JOBDetailActivity.class);
                                    intent.putExtra(JOBDetailFragment.ARG_ITEM_ID, holder.mItem.getJobneedid() + "");
                                    context.startActivity(intent);
                                }
                            } else
                                Snackbar.make(fabAdhocTaskCreate, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
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

        }


        private boolean isTaskExpired(JobNeed jobNeed)
        {
            boolean isExpired=false;
            System.out.println("Jobneed plandate: "+jobNeed.getPlandatetime());
            System.out.println("Jobneed expirydate: "+jobNeed.getExpirydatetime());
            System.out.println("Jobneed gracetime: "+jobNeed.getGracetime());

            long backDate=new Date( CommonFunctions.getParse24HrsDate((jobNeed.getPlandatetime()))- (jobNeed.getGracetime() * 60 * 1000)).getTime();
            if(CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParse24HrsDate((jobNeed.getExpirydatetime())))==2)
                return true;
            else
                return false;


        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    String charString = charSequence.toString();
                    System.out.println("charString: "+charString);

                    if (charString.isEmpty()) {

                        mValues = mFilteredList;
                    } else {

                        ArrayList<JobNeed> filteredList = new ArrayList<>();

                        for (JobNeed jobNeed : mFilteredList    ) {

                           if (jobNeed.getJobdesc().trim().toUpperCase().contains(charString.toUpperCase().trim())) {

                                filteredList.add(jobNeed);
                            }

                        }

                        mValues=filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mValues;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults results) {
                    mValues = (ArrayList<JobNeed>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public final TextView mpDateView;
            public final ImageView mColorCodeBulletView;
            public final ImageView mAssignedToImageview;
            //public final TextView mDescView;
            public JobNeed mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mpDateView = (TextView) view.findViewById(R.id.jnPDateDesc);
                mColorCodeBulletView=(ImageView)view.findViewById(R.id.colorCodeBullet);
                mAssignedToImageview=(ImageView)view.findViewById(R.id.assignedToImageview);
                //mDescView = (TextView) view.findViewById(R.id.jnDesc);

                bulletAssigned=getResources().getDrawable(R.drawable.bulletassigned);
                bulletInprogress=getResources().getDrawable(R.drawable.bulletinprogress);
                bulletCompleted=getResources().getDrawable(R.drawable.bulletcompleted);
                bulletArchived=getResources().getDrawable(R.drawable.bulletarchived);
                bulletClosed=getResources().getDrawable(R.drawable.bulletclosed);
                assignedToGroup=getResources().getDrawable(R.drawable.ic_group_black);
                assignedToPeople=getResources().getDrawable(R.drawable.ic_person_black);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}

