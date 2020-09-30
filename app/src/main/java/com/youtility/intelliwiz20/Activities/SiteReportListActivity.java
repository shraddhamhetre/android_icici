package com.youtility.intelliwiz20.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.youtility.intelliwiz20.Adapters.SiteReportListViewAdapter;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;

import java.util.ArrayList;
import java.util.Date;

public class SiteReportListActivity extends AppCompatActivity implements SiteReportListViewAdapter.OnItemClickListener, IDialogEventListeners {
    private ProgressDialog pd;
    private ArrayList<JobNeed> jobNeedArrayList;
    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private QuestionDAO questionDAO;
    private RecyclerView recyclerView;
    private SiteReportListViewAdapter siteReportListViewAdapter;
    private SharedPreferences siteAuditPref;
    private SharedPreferences loginDetailPref;
    private CustomAlertDialog customAlertDialog;
    private JobNeed jobNeed;
    private FloatingActionButton fab;

    private SharedPreferences loginPref;
    private SharedPreferences deviceInfoPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_report_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        siteAuditPref=getSharedPreferences(Constants.SITE_AUDIT_PREF, MODE_PRIVATE);
        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog=new CustomAlertDialog(SiteReportListActivity.this, this);

        questionDAO=new QuestionDAO(SiteReportListActivity.this);
        jobNeedDAO=new JobNeedDAO(SiteReportListActivity.this);
        typeAssistDAO=new TypeAssistDAO(SiteReportListActivity.this);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(SiteReportListActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        System.out.println("jobNeedDAO.deleteCompletedYesterdayTask(): "+jobNeedDAO.deleteCompletedYesterdayTask());

        pd=new ProgressDialog(SiteReportListActivity.this);
        pd.setMessage(getResources().getString(R.string.please_wait));
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        //prepareAdapter(Constants.JOB_TYPE_ADHOC);
        prepareAdapter(Constants.JOB_TYPE_SCHEDULED);



        fab = (FloatingActionButton) findViewById(R.id.fabAdhocSiteReport);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CommonFunctions.isPermissionGranted(SiteReportListActivity.this)) {
                    Intent nxtActivity = new Intent(SiteReportListActivity.this, SiteListActivity.class);
                    nxtActivity.putExtra("ATTENDANCE_TYPE", "AUDIT");
                    startActivityForResult(nxtActivity, 0);
                }
                else
                    Snackbar.make(view,getResources().getString(R.string.error_msg_grant_permission),Snackbar.LENGTH_LONG).show();
            }
        });

        if(siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKIN,false) && !siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT,false))
        {
            Intent nxtActivity = new Intent(SiteReportListActivity.this, SiteListActivity.class);
            nxtActivity.putExtra("ATTENDANCE_TYPE", "AUDIT");
            startActivity(nxtActivity);
            finish();
        }


    }

    private void prepareAdapter(String type)
    {
        jobNeedArrayList=new ArrayList<>();
        jobNeedArrayList=jobNeedDAO.getJobList(Constants.JOB_NEED_IDENTIFIER_SITEREPORT,Constants.IDENTIFIER_JOBNEED,type);
        siteReportListViewAdapter=new SiteReportListViewAdapter(SiteReportListActivity.this, jobNeedArrayList, this);
        recyclerView.setAdapter(siteReportListViewAdapter);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        prepareAdapter(Constants.JOB_TYPE_SCHEDULED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.filter_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.action_scheduleList:
                prepareAdapter(Constants.JOB_TYPE_SCHEDULED);
                return true;
            case R.id.action_adhocList:
                prepareAdapter(Constants.JOB_TYPE_ADHOC);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(JobNeed item) {

        jobNeed = new JobNeed();
        jobNeed=item;

        long backDate=new Date( CommonFunctions.getParse24HrsDate((jobNeed.getPlandatetime()))- (jobNeed.getGracetime() * 60 * 1000)).getTime();
        int isJobExpired=CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParse24HrsDate((jobNeed.getExpirydatetime())));
        String jobStatus=typeAssistDAO.getEventTypeName(jobNeed.getJobstatus());

        int accessValue = CommonFunctions.isAllowToAccessModules(SiteReportListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));

        if (accessValue == 0 ) {

            if (jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED) || jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED)) {
                if (jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED))
                    Snackbar.make(fab, getResources().getString(R.string.job_has_closed), Snackbar.LENGTH_LONG).show();
                else if (jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
                    Snackbar.make(fab, getResources().getString(R.string.job_has_completed), Snackbar.LENGTH_LONG).show();
            } else {
                if (isJobExpired == 1) {
                    customAlertDialog.customButtonAlertBox(getResources().getString(R.string.button_start), getResources().getString(R.string.button_cancel), "Do you want to start " + item.getJobdesc() + "?", "JOB", 0);
                } else if (isJobExpired == 0) {
                    Snackbar.make(fab, getResources().getString(R.string.job_is_future, jobNeed.getPlandatetime()), Snackbar.LENGTH_LONG).show();
                } else if (isJobExpired == 2) {
                    Snackbar.make(fab, getResources().getString(R.string.job_has_expired), Snackbar.LENGTH_LONG).show();
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
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

        /*siteAuditPref.edit().putLong(Constants.SITE_AUDIT_TIMESTAMP, System.currentTimeMillis()).apply();
        siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED, false).apply();
        siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITEID, jobNeed.getBuid()).apply();
        siteAuditPref.edit().putString(Constants.SITE_AUDIT_SITENAME, "").apply();
        siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKIN, true).apply();
        siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP, System.currentTimeMillis()).apply();
        siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false).apply();
        siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1).apply();


        PeopleEventLogInsertion peopleEventLogInsertion=new PeopleEventLogInsertion(SiteReportListActivity.this);
        peopleEventLogInsertion.insertPeopleEventLogRecord(Constants.ATTENDANCE_PUNCH_TYPE_IN,
                                        loginDetailPref.getString(Constants.LOGIN_PEOPLE_CODE, ""),
                                        siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP,System.currentTimeMillis()),
                                        siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID,-1),
                                        loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID, -1),
                                        "AUDIT");*/

        Intent fillReportIntent = new Intent(SiteReportListActivity.this, IncidentReportQuestionActivity.class);
        fillReportIntent.putExtra("FROM", "SITEREPORT_SCHEDULE");
        fillReportIntent.putExtra("ID", jobNeed.getJobneedid());
        fillReportIntent.putExtra("QUESTIONSETID", jobNeed.getQuestionsetid());
        fillReportIntent.putExtra("PARENT_ACTIVITY","JOBNEED");
        fillReportIntent.putExtra("FOLDER","SITEREPORT");
        startActivityForResult(fillReportIntent, 1);
    }
}
