package com.youtility.intelliwiz20.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youtility.intelliwiz20.Adapters.TaskListRecyclerViewAdapter;
import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Interfaces.RecyclerViewClickListener;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Tables.JOBNeed_Table;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.android.CaptureActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskListActivity extends AppCompatActivity implements IDialogEventListeners, RecyclerViewClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    //private TextView mTextMessage;
    private RecyclerView recyclerView;
    private ArrayList<JobNeed> jobNeedArrayList;
    private ArrayList<QuestionSet> questSetArraylist;

    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private AssetDAO assetDAO;
    private QuestionDAO questionDAO;
    private TaskListRecyclerViewAdapter taskListRecyclerViewAdapter;
    private String fromActivity=null;
    private int currentPointer=0;
    private LinearLayout linearLayout;
    long tId=-1;

    private SharedPreferences jobalertPref;
    private SharedPreferences adhocJobPef;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences networkPref;

    private CustomAlertDialog customAlertDialog;
    private BottomNavigationView navigation;
    private View notificationBadge;
    private CharSequence[] jobNeedTypeOptions  =null;
    private ArrayList<String>scanTypeList=null;
    private AlertDialog alertDialog=null;
    private ConnectivityReceiver connectivityReceiver;

    private SharedPreferences loginPref;
    private SharedPreferences deviceInfoPref;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_pending:
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(jobalertPref.getInt(Constants.JOBALERT_POSITION,0));
                            ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPosition(jobalertPref.getInt(Constants.JOBALERT_POSITION,0));
                        }
                    });*/
                    navigation.getMenu().getItem(0).setTitle(getResources().getString(R.string.tasklist_bottomnavigation_count)+"("+jobNeedDAO.getStatusBaseJobListCount(Constants.JOB_NEED_IDENTIFIER_TASK, Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED,Constants.JOBNEED_STATUS_ASSIGNED)+")");
                    navigation.setBackgroundColor(Color.parseColor("#FFC300"));
                    fetchedAssignedTask();
                    return true;
                case R.id.navigation_complated:
                    navigation.getMenu().getItem(1).setTitle(getResources().getString(R.string.tasklist_bottomnavigation_count)+"("+jobNeedDAO.getStatusBaseJobListCount(Constants.JOB_NEED_IDENTIFIER_TASK, Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED,Constants.JOBNEED_STATUS_COMPLETED)+")");
                    navigation.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    fetchedCompletedTask();
                    return true;
                case R.id.navigation_all:
                    navigation.getMenu().getItem(2).setTitle(getResources().getString(R.string.tasklist_bottomnavigation_count)+"("+jobNeedDAO.getJobListCount(Constants.JOB_NEED_IDENTIFIER_TASK, Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED)+")");
                    navigation.setBackground(getResources().getDrawable(R.drawable.gradient_all));
                    fetchedAllTask();
                    return true;
                case R.id.navigation_adhoc:
                    navigation.getMenu().getItem(3).setTitle(getResources().getString(R.string.tasklist_bottomnavigation_count)+"("+jobNeedDAO.getJobListCount(Constants.JOB_NEED_IDENTIFIER_TASK, Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC)+")");
                    navigation.setBackgroundColor(getResources().getColor(R.color.transparent));
                    fetchedAdhocTask();
                    return true;
            }
            return false;
        }
    };

    private void fetchedAssignedTask()
    {
        System.out.println("jobneedArrayList== Size:");
        jobNeedArrayList=new ArrayList<>();
        jobNeedArrayList=jobNeedDAO.getStatusBaseJobList(Constants.JOB_NEED_IDENTIFIER_TASK, Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED,Constants.JOBNEED_STATUS_ASSIGNED);
        taskListRecyclerViewAdapter=new TaskListRecyclerViewAdapter(TaskListActivity.this,jobNeedArrayList,this);
        recyclerView.setAdapter(taskListRecyclerViewAdapter);
    }

    private void fetchedCompletedTask()
    {
        jobNeedArrayList=new ArrayList<>();
        jobNeedArrayList=jobNeedDAO.getStatusBaseJobList(Constants.JOB_NEED_IDENTIFIER_TASK, Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED,Constants.JOBNEED_STATUS_COMPLETED);
        taskListRecyclerViewAdapter=new TaskListRecyclerViewAdapter(TaskListActivity.this,jobNeedArrayList,this);
        recyclerView.setAdapter(taskListRecyclerViewAdapter);
    }

    private void fetchedAllTask()
    {
        jobNeedArrayList=new ArrayList<>();
        jobNeedArrayList=jobNeedDAO.getJobList(Constants.JOB_NEED_IDENTIFIER_TASK, Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED);
        taskListRecyclerViewAdapter=new TaskListRecyclerViewAdapter(TaskListActivity.this,jobNeedArrayList,this);
        recyclerView.setAdapter(taskListRecyclerViewAdapter);
    }

    private void fetchedAdhocTask()
    {
        jobNeedArrayList=new ArrayList<>();
        jobNeedArrayList=jobNeedDAO.getJobList(Constants.JOB_NEED_IDENTIFIER_TASK, Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
        taskListRecyclerViewAdapter=new TaskListRecyclerViewAdapter(TaskListActivity.this,jobNeedArrayList,this);
        recyclerView.setAdapter(taskListRecyclerViewAdapter);
    }

    private void addBadgeView() {
        BottomNavigationMenuView pendingMenuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        BottomNavigationItemView pendingItemView = (BottomNavigationItemView) pendingMenuView.getChildAt(0);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, pendingMenuView, false);
        TextView pTv=(TextView) notificationBadge.findViewById(R.id.badge);
        pTv.setText("2222");
        pendingItemView.addView(notificationBadge);

        BottomNavigationItemView completedItemView = (BottomNavigationItemView) pendingMenuView.getChildAt(1);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, pendingMenuView, false);
        TextView cTv=(TextView) notificationBadge.findViewById(R.id.badge);
        cTv.setText("4");
        completedItemView.addView(notificationBadge);

        BottomNavigationItemView allItemView = (BottomNavigationItemView) pendingMenuView.getChildAt(2);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, pendingMenuView, false);
        TextView allTv=(TextView) notificationBadge.findViewById(R.id.badge);
        allTv.setText("4");
        allItemView.addView(notificationBadge);

        BottomNavigationItemView adhocItemView = (BottomNavigationItemView) pendingMenuView.getChildAt(3);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, pendingMenuView, false);
        TextView adhocTv=(TextView) notificationBadge.findViewById(R.id.badge);
        adhocTv.setText("4");
        adhocItemView.addView(notificationBadge);

        /*BottomNavigationMenuView completedMenuView = (BottomNavigationMenuView) navigation.getChildAt(1);
        BottomNavigationItemView completedItemView = (BottomNavigationItemView) completedMenuView.getChildAt(1);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, completedMenuView, false);
        TextView cTv=(TextView) notificationBadge.findViewById(R.id.badge);
        cTv.setText("4");
        completedItemView.addView(notificationBadge);

        BottomNavigationMenuView allMenuView = (BottomNavigationMenuView) navigation.getChildAt(2);
        BottomNavigationItemView allItemView = (BottomNavigationItemView) allMenuView.getChildAt(2);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, allMenuView, false);
        TextView aTv=(TextView) notificationBadge.findViewById(R.id.badge);
        aTv.setText("6");
        allItemView.addView(notificationBadge);

        BottomNavigationMenuView adhocMenuView = (BottomNavigationMenuView) navigation.getChildAt(3);
        BottomNavigationItemView adhocItemView = (BottomNavigationItemView) adhocMenuView.getChildAt(3);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, adhocMenuView, false);
        TextView adTv=(TextView) notificationBadge.findViewById(R.id.badge);
        adTv.setText("8");
        adhocItemView.addView(notificationBadge);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        fromActivity=getIntent().getStringExtra("FROM");
        jobalertPref=getSharedPreferences(Constants.JOB_ALERT_PREF, MODE_PRIVATE);
        adhocJobPef=getSharedPreferences(Constants.ADHOC_JOB_TIMESTAMP_PREF, MODE_PRIVATE);
        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        networkPref=getSharedPreferences(Constants.NETWORK_PREF,MODE_PRIVATE);

        customAlertDialog=new CustomAlertDialog(TaskListActivity.this,this);
        jobNeedDAO=new JobNeedDAO(TaskListActivity.this);
        typeAssistDAO=new TypeAssistDAO(TaskListActivity.this);
        assetDAO=new AssetDAO(TaskListActivity.this);
        questionDAO=new QuestionDAO(TaskListActivity.this);
        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        System.out.println("jobNeedDAO.deleteCompletedYesterdayTask(): "+jobNeedDAO.deleteCompletedYesterdayTask());

        linearLayout=(LinearLayout)findViewById(R.id.container);
        recyclerView=(RecyclerView)findViewById(R.id.taskListRecyclerview);
        recyclerView.setNestedScrollingEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<TypeAssist> taScanTypeList=typeAssistDAO.getEventList(Constants.SCAN_TYPE);
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

        jobNeedArrayList=new ArrayList<>();
        jobNeedArrayList=jobNeedDAO.getStatusBaseJobList(Constants.JOB_NEED_IDENTIFIER_TASK, Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED,Constants.JOBNEED_STATUS_ASSIGNED);

        if(jobNeedArrayList!=null && jobNeedArrayList.size()>0)
        {
            System.out.println("jobNeedArrayList: "+jobNeedArrayList.size());
        }

        System.out.println("Position------------------------: "+jobalertPref.getInt(Constants.JOBALERT_POSITION,0));

        taskListRecyclerViewAdapter=new TaskListRecyclerViewAdapter(TaskListActivity.this,jobNeedArrayList,this);
        recyclerView.setAdapter(taskListRecyclerViewAdapter);
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(jobalertPref.getInt(Constants.JOBALERT_POSITION,0));
                ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPosition(jobalertPref.getInt(Constants.JOBALERT_POSITION,0));
            }
        });*/

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setBackgroundColor(Color.parseColor("#FFC300"));
        navigation.setSelectedItemId(0);


        navigation.getMenu().getItem(0).setTitle(getResources().getString(R.string.tasklist_bottomnavigation_count)+"("+jobNeedDAO.getStatusBaseJobListCount(Constants.JOB_NEED_IDENTIFIER_TASK, Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED,Constants.JOBNEED_STATUS_ASSIGNED)+")");

        getUpComingJOBFromList(fromActivity);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.adhocFabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int accessValue = CommonFunctions.isAllowToAccessModules(TaskListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
                double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
                System.out.println("==========="+accessValue);
                System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
                if(CommonFunctions.isPermissionGranted(TaskListActivity.this)) {
                    if(accessValue == 0) {
                        if (jobNeedTypeOptions != null && jobNeedTypeOptions.length > 0)
                            showAlertOptions();
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
                    }
                }
                else
                    Snackbar.make(fab,getResources().getString(R.string.error_msg_grant_permission),Snackbar.LENGTH_LONG).show();
            }
        });
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskListActivity.this);


        builder.setTitle(getResources().getString(R.string.joblist_selecturchoice_title));

        builder.setSingleChoiceItems(jobNeedTypeOptions, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        customAlertDialog.custEnteredScanType(getResources().getString(R.string.joblist_enterassetcode), getResources().getString(R.string.joblist_enterassetcode),0);
                        alertDialog.dismiss();
                        break;
                    case 1:
                        if(checkNFCSupported()) {
                            alertDialog.dismiss();
                            Intent nfcIntent = new Intent(TaskListActivity.this, NFCCodeReaderActivity.class);
                            startActivityForResult(nfcIntent, item);
                        }
                        else
                        {
                            Snackbar.make(navigation,getResources().getString(R.string.joblist_nfcnotsupported), Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case 2:
                        alertDialog.dismiss();
                        Intent intent= new Intent(TaskListActivity.this, CaptureActivity.class);
                        intent.putExtra("FROM","CHECKPOINT");
                        startActivityForResult(intent, item);
                        break;
                    case 3:
                        alertDialog.dismiss();
                        CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_SKIP,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                        Intent adhocJobActivity=new Intent(TaskListActivity.this,AdhocJobActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem search = menu.findItem(R.id.taskSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);

        MenuItem schedule=menu.findItem(R.id.action_scheduleList);
        MenuItem adhocList=menu.findItem(R.id.action_adhocList);
        schedule.setVisible(false);
        adhocList.setVisible(false);

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
                if(taskListRecyclerViewAdapter!=null)
                    taskListRecyclerViewAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String jCount=null;
        switch (item.getItemId())
        {
            case R.id.action_info:
                jCount = jobNeedDAO.getJobneedCount1(fromActivity, CommonFunctions.getFromToDate(0), CommonFunctions.getFromToDate(1));
                String[] jobCount = jCount.split("~");//schedule, complete, pending, closed
                customAlertDialog.JOBInfoDialog(getResources().getString(R.string.jobinfo_dialog_title, fromActivity), jobCount[0], jobCount[1], jobCount[2], jobCount[3]);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void getUpComingJOBFromList(String jobIdentifier)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, +15);

        Calendar cal1=Calendar.getInstance();
        cal1.add(Calendar.MINUTE,-15);

        Cursor cc=jobNeedDAO.getScheduleTaskList(cal1.getTimeInMillis(),cal.getTimeInMillis(), jobIdentifier);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(jobalertPref.getInt(Constants.JOBALERT_POSITION,0));
                    }
                });

                jobalertPref.edit().putInt(Constants.JOBALERT_POSITION, currentPointer).apply();
                jobalertPref.edit().putLong(Constants.JOBALERT_ID, tId).apply();
            }

        }

    }

    private int getItemPosition(long id)
    {
        for(int i=0;i<jobNeedArrayList.size();i++)
        {
            Long dd= jobNeedArrayList.get(i).getJobneedid();
            //System.out.println("id: "+id+" : dd: "+dd);
            if(dd==id)
                return i;
        }
        return 0;
    }


    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        if(type==0)
        {
            long assetIDFrmDB=assetDAO.getAssetID(errorMessage);

            if(assetIDFrmDB!=-1) {
                adhocJobPef.edit().putString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_ENTERED).apply();
                CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_ENTERED,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                CommonFunctions.manualSyncEventLog("EnteredTypeResult",errorMessage,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                showDialog(errorMessage);
            }
            else
                Snackbar.make(navigation,getResources().getString(R.string.joblist_assetcodenotfound), Snackbar.LENGTH_LONG).show();
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

                    boolean isAvailable = qSetNameRaw.contains("~");
                    if (isAvailable) {
                        String rIds = qSetIdRaw.replace(" ", ",");
                        questSetArraylist = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_assetadhoc_templates_query, rIds));
                    } else {
                        questSetArraylist = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_assetadhoc_templates_query, qSetIdRaw));
                    }

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TaskListActivity.this, android.R.layout.select_dialog_item);

                    for (QuestionSet questionSet : questSetArraylist) {
                        arrayAdapter.add(questionSet.getQsetname().toString().trim());
                    }
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(TaskListActivity.this);
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
                            showQuestionRelatedQset(questSetArraylist.get(which).getQuestionsetid(), assetCode);

                        }
                    });
                    builderSingle.create();
                    builderSingle.setCancelable(false);
                    builderSingle.show();
                }
                else
                {
                    //Toast.makeText(TaskListActivity.this, getResources().getString(R.string.questionset_not_available),Toast.LENGTH_LONG).show();
                    Snackbar.make(navigation,getResources().getString(R.string.questionset_not_available),Snackbar.LENGTH_LONG).show();
                }
            }
            else
            {
                //Toast.makeText(TaskListActivity.this, getResources().getString(R.string.questionset_not_available),Toast.LENGTH_LONG).show();
                Snackbar.make(navigation,getResources().getString(R.string.questionset_not_available),Snackbar.LENGTH_LONG).show();
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

        Intent ii = new Intent(TaskListActivity.this, IncidentReportQuestionActivity.class);
        ii.putExtra("FROM", "ADHOC_SCAN");
        ii.putExtra("ID", qSetID);//need to pass quest set id
        ii.putExtra("ASSETCODE",assetCode);
        ii.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
        ii.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TASK);
        startActivityForResult(ii, 4);
    }

    @Override
    public void onClick(View view, int position, long jobneedid, int isExpiredValue, String jobStatus) {
        System.out.println("Jobneedid from: "+jobneedid);

        System.out.println("Jobneedid-------: "+jobneedid);
        int accessValue = CommonFunctions.isAllowToAccessModules(TaskListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));

        if (accessValue == 0) {

            Intent intent = new Intent(TaskListActivity.this, TaskDetailsActivity.class);
            intent.putExtra("JOBNEEDID", jobneedid);
            intent.putExtra("JOBEXPIRED", isExpiredValue);
            intent.putExtra("JOBSTATUS", jobStatus);
            startActivityForResult(intent, 5);
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
        } else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
            System.out.println("===========lat long==0.0");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 && resultCode==RESULT_OK)
        {
            //nfc
            if(data!=null) {
                adhocJobPef.edit().putString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_NFC).apply();
                CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_NFC,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                CommonFunctions.manualSyncEventLog("NFCScanResult",data.getStringExtra("SCAN_RESULT"),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                showDialog(data.getStringExtra("SCAN_RESULT"));
            }
        }
        else if(requestCode == 2 && resultCode==RESULT_OK)
        {
            //qr
            if(data!=null) {
                adhocJobPef.edit().putString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_QR).apply();
                CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_QR,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                CommonFunctions.manualSyncEventLog("QRScanResult",data.getStringExtra("SCAN_RESULT"),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                showDialog(data.getStringExtra("SCAN_RESULT"));
                //CommonFunctions.ScannedCodeLog("IMEI Number: "+data.getStringExtra("SCAN_RESULT")+"\n");
            }
        }
        else if(requestCode == 3)
        {//adb connect 192.168.1.112:5556 adb tcpip 5556

        }
        else if(requestCode == 4)
        {
            if(resultCode == RESULT_OK) {
                saveData();
            }
            else if(resultCode == RESULT_CANCELED)
            {
                adhocJobPef.edit().clear().apply();
            }
        }
    }

    private void saveData()
    {
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
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        /*if((!isConnected && networkPref.getBoolean(Constants.NETWORK_AVAILABLE,false)) ||
                (isConnected && !networkPref.getBoolean(Constants.NETWORK_AVAILABLE,false)))
            CommonFunctions.showSnack(TaskListActivity.this, isConnected,navigation);*/

        CommonFunctions.showSnack(TaskListActivity.this, isConnected,navigation);

        //System.out.println("Network connection: "+isConnected);
        //System.out.println("Network pref connection: "+networkPref.getBoolean(Constants.NETWORK_AVAILABLE,false));
        //networkPref.edit().putBoolean(Constants.NETWORK_AVAILABLE,isConnected).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
