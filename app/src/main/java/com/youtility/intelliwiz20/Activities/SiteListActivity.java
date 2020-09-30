package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.youtility.intelliwiz20.Adapters.SiteListViewAdapter;
import com.youtility.intelliwiz20.AsyncTask.AddOtherSiteAsyncTask;
import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.SiteDAO;
import com.youtility.intelliwiz20.DataAccessObject.SiteTemplateDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IAsynCompletedListener;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.Model.SiteList;
import com.youtility.intelliwiz20.Model.Sites;
import com.youtility.intelliwiz20.Model.TemplateList;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Services.BackgroundSoundService;
import com.youtility.intelliwiz20.Services.SiteReportDoneOrNotCheckService;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.android.CaptureActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

public class SiteListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        IDialogEventListeners, IAsynCompletedListener, ConnectivityReceiver.ConnectivityReceiverListener {
    private ListView siteListView;
    private String selectedSiteName = null;
    private long selectedSiteID = -1;
    ArrayList<String> siteList = new ArrayList<>();
    /*private ArrayList<Sites> search_result_arraylist = null;
    private ArrayList<Sites> sitesArrayList = null;*/

    private ArrayList<SiteList> search_result_arraylist = null;
    private ArrayList<SiteList> sitesArrayList = null;

    private SiteListViewAdapter siteListViewAdapter;
    private final int MARK_ATTENDANCE_INTENT = 1;
    private final int TAKE_ATTENDANCE_INTENT = 0;
    private final int CAPTURE_ATTENDANCE_INTENT = 2;
    private final int SITE_TEMPLATE_INTENT=3;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private long peopleID;
    private String peopleReturnID;
    private String returnImgPath = null;
    private String attendanceType = null;
    private String punchStatus = null;
    private PeopleEventLogDAO peopleEventLogDAO;
    private long currentTimestamp = -1;
    private PeopleDAO peopleDAO;
    private TypeAssistDAO typeAssistDAO;
    private QuestionDAO questionDAO;
    private SiteDAO siteDAO;

    private SharedPreferences deviceInfoPref;
    private SharedPreferences loginPref;

    private long selectedQuestionSetId = -1;
    ArrayList<QuestionSet> questionSetArrayList = null;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences siteAuditPref;
    private SharedPreferences siteAttendancePref;

    private String keyword;

    private Vibrator vibrator;
    private Ringtone r;
    private int value = -1;
    private ConnectivityReceiver connectivityReceiver;

    private SharedPreferences autoSyncPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        currentTimestamp = System.currentTimeMillis();

        attendanceType = getIntent().getStringExtra("ATTENDANCE_TYPE");

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(SiteListActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);
        autoSyncPref=getSharedPreferences(Constants.AUTO_SYNC_PREF, MODE_PRIVATE);


        peopleEventLogDAO = new PeopleEventLogDAO(SiteListActivity.this);
        peopleDAO = new PeopleDAO(SiteListActivity.this);
        typeAssistDAO = new TypeAssistDAO(SiteListActivity.this);
        siteDAO = new SiteDAO(SiteListActivity.this);
        questionDAO = new QuestionDAO(SiteListActivity.this);

        customAlertDialog = new CustomAlertDialog(SiteListActivity.this, this);

        loginDetailPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        deviceRelatedPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        siteAuditPref = getSharedPreferences(Constants.SITE_AUDIT_PREF, MODE_PRIVATE);
        siteAttendancePref=getSharedPreferences(Constants.SITE_ATTENDANCE_PREF,MODE_PRIVATE);

        if (attendanceType.equalsIgnoreCase("AUDIT")) {
            if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKIN, false) && !siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false)) {
                Intent siteChkOutIntent = new Intent(SiteListActivity.this, SiteReportTemplateActivity.class);
                startActivityForResult(siteChkOutIntent, SITE_TEMPLATE_INTENT);
            }
        }

        peopleID = loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID, -1);

        /*sitesArrayList = siteDAO.getSiteList();
        search_result_arraylist = new ArrayList<>();

        if (sitesArrayList != null && sitesArrayList.size() > 0) {
            for (int i = 0; i < sitesArrayList.size(); i++) {
                siteList.add(sitesArrayList.get(i).getBuname() + "");

            }
        }

        siteListView = (ListView) findViewById(R.id.siteListView);
        siteListViewAdapter = new SiteListViewAdapter(SiteListActivity.this, sitesArrayList);
        siteListView.setAdapter(siteListViewAdapter);*/

        sitesArrayList = siteDAO.getSiteList1();
        search_result_arraylist = new ArrayList<>();

        if (sitesArrayList != null && sitesArrayList.size() > 0) {
            for (int i = 0; i < sitesArrayList.size(); i++) {
                siteList.add(sitesArrayList.get(i).getBuname() + "");

            }
        }

        siteListView = (ListView) findViewById(R.id.siteListView);
        siteListViewAdapter = new SiteListViewAdapter(SiteListActivity.this, sitesArrayList);
        siteListView.setAdapter(siteListViewAdapter);

        siteListView.setOnItemClickListener(this);

        //registerForContextMenu(siteListView);

        if (getIntent().hasExtra("FROM_SERVICE")) {
            value = getIntent().getIntExtra("FROM_SERVICE", -1);
            Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            r = RingtoneManager.getRingtone(getApplicationContext(), alarm);
            r.play();
            showAlertDialog();
        }

        SiteTemplateDAO siteTemplateDAO=new SiteTemplateDAO(SiteListActivity.this);
        siteTemplateDAO.getCount1();

    }

    private void showAlertDialog() {

        Intent alertService = new Intent(SiteListActivity.this, SiteReportDoneOrNotCheckService.class);
        stopService(alertService);

        /*vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = { 0, 500, 500 };
        vibrator.vibrate(pattern, 0);*/

        if (value == 0) {
            customAlertDialog.commonDialog(getResources().getString(R.string.alerttitle), getResources().getString(R.string.site_submitreport,siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "")));
        } else if (value == 1) {
            customAlertDialog.commonDialog(getResources().getString(R.string.alerttitle), getResources().getString(R.string.site_checkout_msg2,siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "")));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sitelist_menu, menu);

        MenuItem othersite_item = menu.findItem(R.id.action_addothersite);
        if (attendanceType.equalsIgnoreCase("MARK") || attendanceType.equalsIgnoreCase("TAKE")) {
            othersite_item.setVisible(false);
        } else {
            othersite_item.setVisible(true);
        }


        MenuItem search_item = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) search_item.getActionView();
        searchView.setFocusable(false);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        ImageView searchClose = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_dialog_close_dark);

        EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        txtSearch.setTextColor(Color.WHITE);
        txtSearch.setHintTextColor(Color.WHITE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*search_result_arraylist.clear();
                keyword = query.toUpperCase();
                for(int i =0 ;i < sitesArrayList.size();i++){
                    if(sitesArrayList.get(i).getBuname().contains(keyword)){
                        search_result_arraylist.add(sitesArrayList.get(i));
                    }
                }

                siteListViewAdapter =new SiteListViewAdapter(SiteListActivity.this, search_result_arraylist);
                siteListView.setAdapter(siteListViewAdapter);*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*search_result_arraylist.clear();
                search_result_arraylist = new ArrayList<Sites>();
                keyword = newText;
                for (int i = 0; i < sitesArrayList.size(); i++) {
                    if (sitesArrayList.get(i).getBuname().trim().toLowerCase().contains(keyword.toLowerCase()) || sitesArrayList.get(i).getBuname().trim().toUpperCase().contains(keyword.toUpperCase())) {
                        search_result_arraylist.add(sitesArrayList.get(i));
                    }
                }

                siteListViewAdapter = new SiteListViewAdapter(SiteListActivity.this, search_result_arraylist);
                siteListView.setAdapter(siteListViewAdapter);
                return false;*/

                search_result_arraylist.clear();
                search_result_arraylist = new ArrayList<SiteList>();
                keyword = newText;
                for (int i = 0; i < sitesArrayList.size(); i++) {
                    if (sitesArrayList.get(i).getBuname().trim().toLowerCase().contains(keyword.toLowerCase()) || sitesArrayList.get(i).getBuname().trim().toUpperCase().contains(keyword.toUpperCase())) {
                        search_result_arraylist.add(sitesArrayList.get(i));
                    }
                }

                siteListViewAdapter = new SiteListViewAdapter(SiteListActivity.this, search_result_arraylist);
                siteListView.setAdapter(siteListViewAdapter);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addothersite:
                double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
                double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
                int accessValue = CommonFunctions.isAllowToAccessModules(SiteListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                if (accessValue == 0) {
                    if (CommonFunctions.isPermissionGranted(SiteListActivity.this)) {
                        if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && !siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false)) {
                            Spanned text = Html.fromHtml(getResources().getString(R.string.site_checkout_msg, siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "")));
                            System.out.println("clicked othersite11");

                            //customAlertDialog.showYesNoAlertBox("Please proceed with site CHECK_OUT for "+siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME,""),Constants.ATTENDANCE_PUNCH_TYPE_OUT,2);
                            customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.checkout_alert_title), String.valueOf(text), Constants.ATTENDANCE_PUNCH_TYPE_OUT, 2);
                        } else if (!siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKIN, false)) {
                            if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION, false)) {
                                questionSetArrayList = new ArrayList<>();
                                questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_siteaudit_questionsetcode_query, loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID, -1)));
                                System.out.println("clicked othersite22");

                                showDialog();

                            } else
                                System.out.println("clicked othersite33");

                            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.site_submitreport, siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "")));

                        } else
                            System.out.println("clicked othersite44");

                        customAlertDialog.addOtherSiteForm(getResources().getString(R.string.othersite_dialog_title), loginDetailPref.getString(Constants.LOGIN_USER_CLIENT_NAME, ""), 3);
                    } else
                        Snackbar.make(siteListView, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
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
                return true;
            /*case R.id.action_asc:
                *//*Collections.sort(siteList,new CustomCompartor());
                for(int i=0; i < siteList.size(); i++){
                    System.out.println(siteList.get(i));
                }
                siteListViewAdapter = new SiteListViewAdapter(SiteListActivity.this, siteList);
                siteListView.setAdapter(siteListViewAdapter);
                siteListViewAdapter.notifyDataSetChanged();*//*
                return true;
            case R.id.action_desc:
                *//*Collections.reverse(siteList);
                for(int i=0; i < siteList.size(); i++){
                    System.out.println(siteList.get(i));
                }
                siteListViewAdapter = new SiteListViewAdapter(SiteListActivity.this, siteList);
                siteListView.setAdapter(siteListViewAdapter);
                siteListViewAdapter.notifyDataSetChanged();*//*
                return  true;*/
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {
        if (type == 1) {
            showDialog();
        } else if (type == 0) {
            if (value != -1) {
                if (vibrator != null)
                    vibrator.cancel();
                if (r != null && r.isPlaying())
                    r.stop();
                if (siteAuditPref.getLong(Constants.SITE_AUDIT_QUESTIONSETID, -1) != -1) {
                    Intent fillReportIntent = new Intent(SiteListActivity.this, IncidentReportQuestionActivity.class);
                    fillReportIntent.putExtra("FROM", "SITEREPORT");
                    fillReportIntent.putExtra("ID", siteAuditPref.getLong(Constants.SITE_AUDIT_TIMESTAMP, -1));
                    fillReportIntent.putExtra("QUESTIONSETID", siteAuditPref.getLong(Constants.SITE_AUDIT_QUESTIONSETID, -1));
                    fillReportIntent.putExtra("PARENT_ACTIVITY", "JOBNEED");
                    fillReportIntent.putExtra("FOLDER", "SITEREPORT");
                    startActivity(fillReportIntent);
                }
                //finish();
            }
        }
    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        if (type == 0) {
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITEID, selectedSiteID).apply();
            siteAuditPref.edit().putString(Constants.SITE_AUDIT_SITENAME, selectedSiteName).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKIN, true).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP, System.currentTimeMillis()).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION, false).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED,false).apply();
            insertPeopleEventLogRecord(errorMessage, loginDetailPref.getString(Constants.LOGIN_PEOPLE_CODE, ""), siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP, -1), siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1), "");
            CommonFunctions.EventLog("\n Site Check in Event: \n"+CommonFunctions.getTimezoneDate(siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP, -1))+" : "+selectedSiteName+"\n");
            Intent siteChkOutIntent=new Intent(SiteListActivity.this, SiteReportTemplateActivity.class);
            startActivityForResult(siteChkOutIntent,SITE_TEMPLATE_INTENT);
            //showDialog();
        } else if (type == 1 || type == 2) {
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, true).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, System.currentTimeMillis()).apply();
            if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION, false))
                insertPeopleEventLogRecord(errorMessage, loginDetailPref.getString(Constants.LOGIN_PEOPLE_CODE, ""), siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1), siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1), siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, ""));
            else
                insertPeopleEventLogRecord(errorMessage, loginDetailPref.getString(Constants.LOGIN_PEOPLE_CODE, ""), siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1), siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1), "");

            CommonFunctions.EventLog("\n Site Check out Event: \n"+CommonFunctions.getTimezoneDate(siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1))+" : "+siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "")+"\n");
            Snackbar.make(siteListView,"Checked out",Snackbar.LENGTH_SHORT).show();
        } else if (type == 3) {
            System.out.println("othersitename: " + errorMessage.toString().trim());
            String[] otherSiteEnteredinfo = errorMessage.split("~");
            if (otherSiteEnteredinfo[0].toString().trim().length() > 0)
                selectedSiteName = (otherSiteEnteredinfo[0]);
            else if (otherSiteEnteredinfo[1].toString().trim().length() > 0)
                selectedSiteName = (otherSiteEnteredinfo[1]);

            String siteClientName = otherSiteEnteredinfo[2];

            System.out.println("selectedSiteName: "+selectedSiteName);

            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_TIMESTAMP, System.currentTimeMillis()).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED, false).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITEID, loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID, -1)).apply();
            siteAuditPref.edit().putString(Constants.SITE_AUDIT_SITENAME, selectedSiteName).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKIN, true).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP, System.currentTimeMillis()).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION, true).apply();

            CommonFunctions.EventLog("\n Site Check in other site Event: \n"+CommonFunctions.getTimezoneDate(siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP, -1))+" : "+selectedSiteName+"\n");

            insertPeopleEventLogRecord(Constants.ATTENDANCE_PUNCH_TYPE_IN, loginDetailPref.getString(Constants.LOGIN_PEOPLE_CODE, ""), siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP, -1), siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1), selectedSiteName);

            /*Intent alertService = new Intent(SiteListActivity.this, SiteReportDoneOrNotCheckService.class);
            startService(alertService);

            questionSetArrayList = new ArrayList<>();
            questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_siteaudit_questionsetcode_query, loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID, -1)));

            showDialog();*/

            Intent siteChkOutIntent=new Intent(SiteListActivity.this, SiteReportTemplateActivity.class);
            startActivityForResult(siteChkOutIntent,SITE_TEMPLATE_INTENT);
        }
    }


    private void callUploadAsyncTask(String siteCode) {
        AddOtherSiteAsyncTask addOtherSiteAsyncTask = new AddOtherSiteAsyncTask(SiteListActivity.this, this, selectedSiteName, siteCode);
        addOtherSiteAsyncTask.execute();
    }

    @Override
    public void asyncComplete(boolean success, int statusCode, long returnId) {
        if (statusCode == 0) {
            selectedSiteID = returnId;

            questionSetArrayList = new ArrayList<>();
            questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_siteaudit_questionsetcode_query, loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID, -1)));

            /*siteAuditPref.edit().putLong(Constants.SITE_AUDIT_TIMESTAMP, currentTimestamp).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED, false).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITEID, selectedSiteID).apply();
            siteAuditPref.edit().putString(Constants.SITE_AUDIT_SITENAME, selectedSiteName).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKIN, true).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP, System.currentTimeMillis()).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION,true).apply();
            insertPeopleEventLogRecord(Constants.ATTENDANCE_PUNCH_TYPE_IN, loginDetailPref.getString(Constants.LOGIN_PEOPLE_CODE, ""), siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP,-1), siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID,-1));
*/

            showDialog();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CommonFunctions.showSnack(SiteListActivity.this, isConnected,siteListView);
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

    public class CustomCompartor implements Comparator<String> {
        @Override
        public int compare(String s, String t1) {
            return s.toString().compareTo(t1.toString());
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int accessValue = CommonFunctions.isAllowToAccessModules(SiteListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));

        if (CommonFunctions.isPermissionGranted(SiteListActivity.this)) {
            if (accessValue == 0) {

                SiteList site = (SiteList) adapterView.getAdapter().getItem(i);

                selectedSiteName = site.getBuname();
                selectedSiteID = site.getBuid();


                if (attendanceType.equalsIgnoreCase("MARK"))
                    showPunchTypeDialog();
                else if (attendanceType.equalsIgnoreCase("TAKE")) {
                    Intent takeAttendanceIntent = new Intent(SiteListActivity.this, AttendanceTakeActivity.class);
                    takeAttendanceIntent.putExtra("SITENAME", selectedSiteName);
                    takeAttendanceIntent.putExtra("SITEID", selectedSiteID);
                    takeAttendanceIntent.putExtra("ATTENDANCE_TYPE", attendanceType);
                    startActivityForResult(takeAttendanceIntent, TAKE_ATTENDANCE_INTENT);
                } else if (attendanceType.equalsIgnoreCase("AUDIT")) {
                    SiteTemplateDAO siteTemplateDAO = new SiteTemplateDAO(SiteListActivity.this);
                    ArrayList<TemplateList> templateList = new ArrayList<>();
                    templateList = siteTemplateDAO.getTemplateList(selectedSiteID);

                    if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false)) {
                        if (templateList != null && templateList.size() > 0) {
                            String text = (getResources().getString(R.string.site_checkin_msg, selectedSiteName));
                            customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.checkin_alert_title), String.valueOf(text), Constants.ATTENDANCE_PUNCH_TYPE_IN, 0);
                        } else
                            Snackbar.make(siteListView, getResources().getString(R.string.site_templatenotfound), Snackbar.LENGTH_LONG).show();
                    } else if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && !siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false) && siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1) == selectedSiteID) {
                        customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.checkout_alert_title), getResources().getString(R.string.site_checkout_msg1, selectedSiteName), Constants.ATTENDANCE_PUNCH_TYPE_OUT, 1);
                    } else if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && !siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false) && siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1) != selectedSiteID) {
                        customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.checkout_alert_title), getResources().getString(R.string.site_checkout_msg2, siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "")), Constants.ATTENDANCE_PUNCH_TYPE_OUT, 2);
                    } else if (!siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKIN, false) && siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1) == selectedSiteID) {
                        showDialog();
                    } else if (!siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKIN, false) && siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1) != selectedSiteID) {
                        customAlertDialog.commonDialog(getResources().getString(R.string.alerttitle), getResources().getString(R.string.site_submitreport, siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "")));
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
            } else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
                customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
                System.out.println("===========lat long==0.0");
            }

        } else
            Snackbar.make(view, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();

        /*if (CommonFunctions.isPermissionGranted(SiteListActivity.this)) {

            Sites site = (Sites) adapterView.getAdapter().getItem(i);
            System.out.println("site.getBuname(): "+site.getBuname());
            System.out.println("site.getReportnames(): "+site.getReportnames());

            selectedSiteName = site.getBuname();
            selectedSiteID = site.getBuid();


            if (attendanceType.equalsIgnoreCase("MARK"))
                showPunchTypeDialog();
            else if (attendanceType.equalsIgnoreCase("TAKE")) {
                Intent takeAttendanceIntent = new Intent(SiteListActivity.this, AttendanceTakeActivity.class);
                takeAttendanceIntent.putExtra("SITENAME", selectedSiteName);
                takeAttendanceIntent.putExtra("SITEID", selectedSiteID);
                takeAttendanceIntent.putExtra("ATTENDANCE_TYPE", attendanceType);
                startActivityForResult(takeAttendanceIntent, TAKE_ATTENDANCE_INTENT);
            } else if (attendanceType.equalsIgnoreCase("AUDIT")) {
                String reportName = site.getReportnames();
                System.out.println("reportName: " + reportName);
                String reportIDs = site.getReportids();
                System.out.println("reportIds: " + reportIDs);
                ArrayList<Long> reportidArraylist = new ArrayList<>();
                questionSetArrayList = new ArrayList<QuestionSet>();

                if (reportIDs.equalsIgnoreCase("-1")) {
                    questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_siteaudit_questionsetcode_query, loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID, -1)));
                } else {
                    boolean isAvailable = reportName.contains("~");
                    if (isAvailable) {
                        String rIds = reportIDs.replace(" ", ",");
                        questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_siteaudit_templates_query, rIds));
                    } else {
                        questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_siteaudit_templates_query, reportIDs));
                    }
                }

               if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false)) {
                    if (questionSetArrayList != null && questionSetArrayList.size() > 0) {
                        for (int q = 0; q < questionSetArrayList.size(); q++) {
                            System.out.println("Qsetname: " + questionSetArrayList.get(q).getQsetname());
                        }
                        //showDialog();
                        //Spanned text = Html.fromHtml(getResources().getString(R.string.site_checkin_msg, selectedSiteName));
                        String text = (getResources().getString(R.string.site_checkin_msg, selectedSiteName));
                        customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.checkin_alert_title),String.valueOf(text), Constants.ATTENDANCE_PUNCH_TYPE_IN, 0);
                    } else
                        Snackbar.make(siteListView, getResources().getString(R.string.site_templatenotfound), Snackbar.LENGTH_LONG).show();
                } else if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && !siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false) && siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1) == selectedSiteID) {
                    customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.checkout_alert_title),getResources().getString(R.string.site_checkout_msg1, selectedSiteName ), Constants.ATTENDANCE_PUNCH_TYPE_OUT, 1);
                } else if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && !siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false) && siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1) != selectedSiteID) {
                    customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.checkout_alert_title),getResources().getString(R.string.site_checkout_msg2,  siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "")), Constants.ATTENDANCE_PUNCH_TYPE_OUT, 2);
                } else if (!siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKIN, false) && siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1) == selectedSiteID) {
                    showDialog();
                } else if (!siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false) && siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKIN, false) && siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1) != selectedSiteID) {
                    customAlertDialog.commonDialog(getResources().getString(R.string.alerttitle),getResources().getString(R.string.site_submitreport,siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "")));
                }

            }
        } else
            Snackbar.make(view, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();*/


    }

    public void showDialog() {

        if (questionSetArrayList != null && questionSetArrayList.size() > 0) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(SiteListActivity.this);
            builderSingle.setTitle(getResources().getString(R.string.select_quest_set_title));
            //builderSingle.setTitle(Html.fromHtml("<p style='color=#ffffff; background-color=#000000'>"+getResources().getString(R.string.select_quest_set_title)+"</p>"));


            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SiteListActivity.this, android.R.layout.select_dialog_item);

            for (QuestionSet questionSet : questionSetArrayList) {
                arrayAdapter.add(questionSet.getQsetname().trim());
            }

            builderSingle.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED, false).apply();
                    dialog.dismiss();

                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    siteAuditPref.edit().putLong(Constants.SITE_AUDIT_TIMESTAMP, System.currentTimeMillis()).apply();
                    siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED, false).apply();

                    Intent alertService = new Intent(SiteListActivity.this, SiteReportDoneOrNotCheckService.class);
                    startService(alertService);

                    selectedQuestionSetId = questionSetArrayList.get(which).getQuestionsetid();
                    siteAuditPref.edit().putLong(Constants.SITE_AUDIT_QUESTIONSETID, selectedQuestionSetId).apply();
                    Intent fillReportIntent = new Intent(SiteListActivity.this, IncidentReportQuestionActivity.class);
                    fillReportIntent.putExtra("FROM", "SITEREPORT");
                    fillReportIntent.putExtra("ID", siteAuditPref.getLong(Constants.SITE_AUDIT_TIMESTAMP, -1));
                    fillReportIntent.putExtra("QUESTIONSETID", selectedQuestionSetId);
                    fillReportIntent.putExtra("PARENT_ACTIVITY", "JOBNEED");
                    fillReportIntent.putExtra("FOLDER", "SITEREPORT");
                    startActivityForResult(fillReportIntent, 0);


                }
            });
            builderSingle.create();
            builderSingle.show();
            builderSingle.setCancelable(false);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.siteListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.siteinfo_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_siteinfo:
                //add here site info screen activity

                Sites sitesInfo = (Sites) siteListView.getAdapter().getItem(info.position);
                System.out.println("Site Info: " + sitesInfo.getReportto() + " : " + peopleDAO.getPeopleMobile(sitesInfo.getReportto()));
                /*if (peopleDAO.getPeopleMobile(sitesInfo.getReportto()) != null && peopleDAO.getPeopleMobile(sitesInfo.getReportto()).toString().length() > 0) {
                    callDialIntent(peopleDAO.getPeopleMobile(sitesInfo.getReportto()));
                }*/
                return true;
            default:
                return super.onContextItemSelected(item);
        }


    }

    private void callDialIntent(String dialNumber) {

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dialNumber));
            startActivity(intent);
        }*/

    }

    private void showPunchTypeDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.selectpunchtype_dialogtitle));
                alertDialogBuilder.setPositiveButton(Constants.ATTENDANCE_PUNCH_TYPE_IN,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.dismiss();
                                punchStatus=Constants.ATTENDANCE_PUNCH_TYPE_IN;
                                callMarkAttendanceIntent();
                            }
                        });

        alertDialogBuilder.setNegativeButton(Constants.ATTENDANCE_PUNCH_TYPE_OUT,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                punchStatus=Constants.ATTENDANCE_PUNCH_TYPE_OUT;
                callMarkAttendanceIntent();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void callMarkAttendanceIntent()
    {
        if(!autoSyncPref.getBoolean(Constants.IS_AUTO_SYNC_RUNNING,true)) {
            currentTimestamp = System.currentTimeMillis();
            System.out.println("before capture activity called12");
            Intent intent = new Intent(SiteListActivity.this, CaptureActivity.class);
            System.out.println("after capture activity called12");
            intent.putExtra("FROM", attendanceType);
            intent.putExtra("TYPE", punchStatus);
            startActivityForResult(intent, MARK_ATTENDANCE_INTENT);
        }else {
            customAlertDialog.commonDialog1("Alert","Please wait... Synchronization in progress");
        }

    }

    private void insertPeopleEventLogRecord(String punchStatus, String empCode, long inoutTimestamp, long siteID, String siteName )
    {
        //accuracy, datetime, gpslocation, photorecognitionthreshold, photorecognitionscore, photorecognitiontimestamp, photorecognitionserviceresponse,
        //  facerecognition, peopleid, peventtype, punchstatus, verifiedby, siteid, cuser, muser, cdtz, mdtz, isdeleted, gfid-, deviceid

        System.out.println("insertPeopleEventLogRecord punchStatus: "+punchStatus);
        System.out.println("insertPeopleEventLogRecord attendance type: "+attendanceType);
        System.out.println("insertPeopleEventLogRecord attendance time: "+CommonFunctions.getTimezoneDate(inoutTimestamp));
        DateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        long scannedPeopleId=peopleDAO.getPeopleId(empCode);
        System.out.println("scannedPeopleId::"+ scannedPeopleId + empCode);

        PeopleEventLog peopleEventLog=new PeopleEventLog();
        peopleEventLog.setAccuracy(-1);
        peopleEventLog.setDeviceid("-1");
        peopleEventLog.setDatetime(String.valueOf(inoutTimestamp));
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
        peopleEventLog.setPeventtype(typeAssistDAO.getEventTypeID(attendanceType, Constants.IDENTIFIER_ATTENDANCE));
        peopleEventLog.setPunchstatus(typeAssistDAO.getEventTypeID(punchStatus, Constants.IDENTIFIER_PUNCHSTATUS));
        peopleEventLog.setVerifiedby(-1);
        peopleEventLog.setScanPeopleCode(empCode);
        peopleEventLog.setBuid(siteID);
        peopleEventLog.setGfid(-1);
        peopleEventLog.setDistance(0);
        peopleEventLog.setDuration(0);
        peopleEventLog.setExpamt(0.0);
        peopleEventLog.setReference("");
        if(scannedPeopleId == -1) {
            peopleEventLog.setRemarks("ScanCode: "+empCode);
        }else {
            peopleEventLog.setRemarks(siteName);
        }
        peopleEventLog.setTransportmode(-1);
        peopleEventLog.setOtherlocation(siteName);
        peopleEventLogDAO.insertRecord(peopleEventLog);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==MARK_ATTENDANCE_INTENT)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null)
                {
                    if(!autoSyncPref.getBoolean(Constants.IS_AUTO_SYNC_RUNNING,true))
                    {
                        System.out.println("called mark attendance intent");
                        currentTimestamp=System.currentTimeMillis();
                        //currentTimestamp = System.currentTimeMillis();
                        //Toast.makeText(SiteListActivity.this,data.getStringExtra("SCAN_RESULT"), Toast.LENGTH_LONG).show();
                        siteAttendancePref.edit().putString(Constants.SITE_ATTENDANCE_QR_RESULT,data.getStringExtra("SCAN_RESULT")).apply();
                        siteAttendancePref.edit().putString(Constants.SITE_ATTENDANCE_QR_RESULT_NAME,peopleDAO.getPeopleName(data.getStringExtra("SCAN_RESULT"))).apply();

                        insertPeopleEventLogRecord(punchStatus,data.getStringExtra("SCAN_RESULT"), currentTimestamp, selectedSiteID,"");
                        autoSyncPref.edit().putLong(Constants.CAMERA_ON_TIMESTAMP, currentTimestamp).apply();

                        Intent backgroudSoundService = new Intent(SiteListActivity.this, BackgroundSoundService.class);
                        backgroudSoundService.putExtra("key",101);
                        startService(backgroudSoundService);

                        Intent attenCapPicIntent=new Intent(SiteListActivity.this, AttendanceCapturePhotoActivity.class);
                        attenCapPicIntent.putExtra("FROM",attendanceType);
                        attenCapPicIntent.putExtra("CODE",data.getStringExtra("SCAN_RESULT"));
                        attenCapPicIntent.putExtra("TIMESTAMP",currentTimestamp);
                        startActivityForResult(attenCapPicIntent,CAPTURE_ATTENDANCE_INTENT);
                    }
                    else {
                        customAlertDialog.commonDialog1("Alert","Please wait.. Autosync is going on");
                    }
                }
            }

        }
        else if(requestCode==CAPTURE_ATTENDANCE_INTENT)
        {
            if(resultCode==RESULT_OK)
            {
                System.out.println("called capture attendance intent");

                if(data!=null) {
                    returnImgPath=data.getStringExtra("IMG_PATH");
                }
                callMarkAttendanceIntent();
                /*Intent intent= new Intent(SiteListActivity.this,CaptureActivity.class);
                intent.putExtra("FROM",attendanceType);
                startActivityForResult(intent, MARK_ATTENDANCE_INTENT);*/
            }
            else if(resultCode==RESULT_CANCELED) {
                System.out.println(" ");

            }
        }
    }



}
