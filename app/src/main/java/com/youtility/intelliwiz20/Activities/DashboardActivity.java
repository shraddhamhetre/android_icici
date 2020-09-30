package com.youtility.intelliwiz20.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.LayerDrawable;
import android.media.Ringtone;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.youtility.intelliwiz20.Adapters.DashboardGridViewAdapter;
import com.youtility.intelliwiz20.AsyncTask.EmailReadingDataLogFileAsynTask;
import com.youtility.intelliwiz20.BroadcastReceiver.AirplaneModeReceiver;
import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.BroadcastReceiver.JobAlertBroadcast;
import com.youtility.intelliwiz20.BroadcastReceiver.JobAlertBroadcast_Temp;
import com.youtility.intelliwiz20.BroadcastReceiver.NetworkDataReceiver;
import com.youtility.intelliwiz20.BroadcastReceiver.TrackPhoneEventReceiver;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.CaptchaConfigSettingDAO;
import com.youtility.intelliwiz20.DataAccessObject.CommonDAO;
import com.youtility.intelliwiz20.DataAccessObject.GeofenceDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.SiteVisitedLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.StepsCountLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Interfaces.IGridviewItemClickListeners;
import com.youtility.intelliwiz20.Model.ApplicationAccess;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.CaptchaConfigSetting;
import com.youtility.intelliwiz20.Model.LogoutResponse;
import com.youtility.intelliwiz20.Model.StepCount;
import com.youtility.intelliwiz20.Model.UploadLoginParameters;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Services.AutoSyncService;
import com.youtility.intelliwiz20.Services.CaptchaConfigSettingService;
import com.youtility.intelliwiz20.Services.GetLocationService;
import com.youtility.intelliwiz20.Services.GetStepCounterService;
import com.youtility.intelliwiz20.Services.JobAlertBroadcastService;
import com.youtility.intelliwiz20.Services.JobAlertService;
import com.youtility.intelliwiz20.Services.UploadImageService;
import com.youtility.intelliwiz20.Services.UserGeofenceBreachService;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.EventLogInsertion;
import com.youtility.intelliwiz20.Utils.MemoryInfo;
import com.youtility.intelliwiz20.Utils.RetrofitClient;
import com.youtility.intelliwiz20.Utils.RetrofitServices;
import com.youtility.intelliwiz20.Utils.ServerRequest;
import com.youtility.intelliwiz20.android.CaptureActivity;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IGridviewItemClickListeners,
        IDialogEventListeners, View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    public static final String TAG = "DashbaordActivity";

    private ImageView nav_header_imageView;
    private TextView nav_header_username;
    private TextView nav_header_appName;
    private TextView nav_header_usersite;
    private TextView nav_header_userCurrentLoc;

    private ImageView nav_header_geofenceStatusImg;
    private SharedPreferences loginPref;
    private SharedPreferences selfAttendancePref;
    private SharedPreferences deviceInfoPref;
    private SharedPreferences networkPref;
    private SharedPreferences appliationMainPref;

    private SiteVisitedLogDAO siteVisitedLogDAO;

    private SharedPreferences applicationMainPref;


    private GridView modulesGridView;
    private List<String> dashboardModulesList;
    private List<String> dashboardModulesCodeList;
    private List<String> dashboardModulesDescList;
    private DashboardGridViewAdapter dashboardGridViewAdapter;
    private TypedArray dashboardImgList;
    private final int SETTING_INTENT = 21;
    private final int ABOUTUS_INTENT = 22;

    private final int EMERGENCY_INTENT = 0;
    private final int JOB_INTENT = 1;
    private final int ASSET_INTENT = 2;
    private final int INCIDENTREPORT_INTENT = 3;
    private final int SOS_INTENT = 4;
    private final int CHECKPOINT_INTENT = 5;
    private final int SITETOUR_INTENT = 6;
    private final int SITELOG_INTENT = 7;
    private final int SITENOTVISITED_INTENT = 8;
    private final int USERLOCATION_INTENT = 9;
    private final int ADDCONVEYANCE_INTENT = 10;
    private final int LEADERBOARD_INTENT = 11;
    private final int READING_INTENT = 12;
    private final int SELFATTENDANCE_INTENT = 13;
    private final int SITEATTENDANCE_INTENT = 14;
    private final int TAKEATTENDANCE_INTENT = 15;
    private final int SYNC_INTENT = 16;
    private final int TICKET_INTENT = 17;
    private final int SUBMITATTENDANCE_INTENT = 18;
    private final int APPROVEATTENDANCE_INTENT = 19;
    private final int WORKFLOW_INTENT = 19;
    private final int ASSETAUDIT_INTENT = 20;
    private final int REQUEST_INTENT = 21;
    private final int PLAN_INTENT = 23;

    private CommonDAO commonDAO;

    private SharedPreferences fenceBreachPref;

    private FloatingActionButton fab;

    private ArrayList<ApplicationAccess> applicationAccessArrayList;
    private ArrayList<ApplicationAccess> applicationAccessArrayList1;

    private JobAlertBroadcast jobAlertBroadcast;

    private JobAlertBroadcast_Temp jobAlertBroadcastTemp;

    private boolean doubleBackToExitPressedOnce = false;
    private CheckNetwork checkNetwork;
    private CustomAlertDialog customAlertDialog;

    private SimpleDateFormat sdf;
    private Calendar current;
    private long miliSeconds;
    private Date resultdate;

    private SharedPreferences syncSummaryPref;
    private SharedPreferences syncPref;
    private SharedPreferences deviceRelatedPref;


    SharedPreferences sharedPreferences;

    private PeopleDAO peopleDAO;
    private StepsCountLogDAO stepsCountLogDAO;

    private ConnectivityReceiver connectivityReceiver;
    private TrackPhoneEventReceiver trackPhoneEventReceiver;
    private AirplaneModeReceiver airplaneModeReceiver;
    private NetworkDataReceiver networkDataReceiver;

    private CaptchaConfigSettingDAO captchaConfigSettingDAO;

    //--------------------------------------- site report alert related-----------------------------------------------
    private Vibrator vibrator;
    private Ringtone r;
    private int value = -1;
    private SharedPreferences siteAuditPref;

    public static final String mBroadcastStringAction = "com.youtility.broadcast.string";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private SharedPreferences stepCounterPref;
    private int buzzCounter = 0;
    private MenuItem item_stepcount;

    boolean isRunning = false;

    public static DashboardActivity instance;


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Dashboard", "Onstart");
        /*System.out.println("Application Version: "+CommonFunctions.getApplicationVersion(DashboardActivity.this));
        System.out.println("Android Version: "+CommonFunctions.getAndroidVersion(DashboardActivity.this));
        System.out.println("Android Version Name: "+CommonFunctions.getOSVerName(CommonFunctions.getAndroidVersion(DashboardActivity.this)));
        CommonFunctions.getDeviceInformation();*/



        SqliteOpenHelper dbh = new SqliteOpenHelper(this);
        SQLiteDatabase db = dbh.getWritableDatabase();
        System.out.println("InDashboard--db.getVersion(): "+db.getVersion());

        try {
            jobAlertBroadcastTemp = new JobAlertBroadcast_Temp();
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            intentFilter.addAction(JobAlertBroadcastService.MY_ACTION);
            //intentFilter.addAction(mBroadcastStringAction);
            getApplicationContext().registerReceiver(jobAlertBroadcastTemp, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        getApplicationContext().registerReceiver(connectivityReceiver, intentFilter);

        /*register connection status listener*/
        Baseclass.getInstance().setConnectivityListener(this);

        final IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("android.location.PROVIDERS_CHANGED");
        trackPhoneEventReceiver=new TrackPhoneEventReceiver();
        getApplicationContext().registerReceiver(trackPhoneEventReceiver,intentFilter1);

        airplaneModeReceiver = new AirplaneModeReceiver();
        IntentFilter intentFilter2 = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        getApplicationContext().registerReceiver(airplaneModeReceiver,intentFilter2);

        networkDataReceiver=new NetworkDataReceiver();
        final IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getApplicationContext().registerReceiver(networkDataReceiver,intentFilter3);

        Intent userRouteBreachService = new Intent(DashboardActivity.this, UserGeofenceBreachService.class);
        startService(userRouteBreachService);
        /*Intent jobAlertService = new Intent(DashboardActivity.this, JobAlertService.class);
        startService(jobAlertService);*/
        //attachScreenOnOffListener();
    }

    private void attachScreenOnOffListener() {
        System.out.println("attachScreenOnOffListener called");
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenHandler();
        registerReceiver(mReceiver, filter);
    }


    @Override
    protected void onPause() {
        /*try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        super.onPause();

        Log.d("Dashboard", "OnPause");
    }

    private void setStepCountAPI() {
        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .addDataType(DataType.TYPE_DISTANCE_CUMULATIVE)
                        .addDataType(DataType.TYPE_DISTANCE_DELTA)
                        .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            subscribe();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(connectivityReceiver!=null)
            getApplicationContext().unregisterReceiver(connectivityReceiver);
        if(trackPhoneEventReceiver!=null)
            getApplicationContext().unregisterReceiver(trackPhoneEventReceiver);
        if(airplaneModeReceiver!=null)
            getApplicationContext().unregisterReceiver(airplaneModeReceiver);
        if(networkDataReceiver!=null)
            getApplicationContext().unregisterReceiver(networkDataReceiver);
        Log.d("Dashboard", "Onstop");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Log.d("Dashboard", "OnCreate");
        instance = this;

        System.out.println("oncreate===");
        createShortcutOfApp();

        if (Constants.countDownTimer != null) {
            Constants.countDownTimer.cancel();
            Constants.countDownTimer = null;
        }

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        fenceBreachPref = getSharedPreferences(Constants.GEOFENCE_BREACH_PREF, MODE_PRIVATE);
        selfAttendancePref = getSharedPreferences(Constants.SELF_ATTENDANCE_PREF, MODE_PRIVATE);
        siteAuditPref = getSharedPreferences(Constants.SITE_AUDIT_PREF, MODE_PRIVATE);
        syncSummaryPref = getSharedPreferences(Constants.SYNC_SUMMARY_PREF, MODE_PRIVATE);
        syncPref = getSharedPreferences(Constants.SYNC_PREF, MODE_PRIVATE);
        stepCounterPref = getSharedPreferences(Constants.STEP_COUNTER_PREF, MODE_PRIVATE);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);
        networkPref=getSharedPreferences(Constants.NETWORK_PREF, Context.MODE_PRIVATE);
        appliationMainPref=getSharedPreferences(Constants.APPLICATION_MAIN_PREF, MODE_PRIVATE);
        deviceRelatedPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);



        captchaConfigSettingDAO=new CaptchaConfigSettingDAO(DashboardActivity.this);

        stepCounterPref.edit().putInt(Constants.STEP_COUNTER_BUZZ_TIMER,loginPref.getInt(Constants.LOGIN_CONFIG_SGUARD_CAPTCHA_FREQ,10)).apply();
                //stepCounterPref.getInt(Constants.STEP_COUNTER_BUZZ_TIMER

        if(loginPref.getString(Constants.LOGIN_CONFIG_SGUARD_ENABLE,"false").equalsIgnoreCase("true")) {
            CaptchaConfigSetting captchaConfigSetting = captchaConfigSettingDAO.getCaptchaSetting();
            if(CommonFunctions.isTimeLiesInBetween(captchaConfigSetting.getStarttime(), captchaConfigSetting.getEndtime()))
                setStepCountAPI();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setBackgroundColor(getResources().getColor(R.color.screen_header_background));
        toolbar.setTitle(getResources().getString(R.string.title_activity_dashboard));
        //toolbar.setSubtitle(loginPref.getString(Constants.LOGIN_ENTERED_USER_ID, ""));
        setSupportActionBar(toolbar);

        commonDAO = new CommonDAO(DashboardActivity.this);
        siteVisitedLogDAO = new SiteVisitedLogDAO(DashboardActivity.this);
        peopleDAO = new PeopleDAO(DashboardActivity.this);
        stepsCountLogDAO=new StepsCountLogDAO(DashboardActivity.this);

        checkNetwork = new CheckNetwork(DashboardActivity.this);
        customAlertDialog = new CustomAlertDialog(DashboardActivity.this, this);


        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
                double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
                int accessValue = CommonFunctions.isAllowToAccessModules(DashboardActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                //int accessValue=0;
                System.out.println("========"+accessValue);
                System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
                if (accessValue == 0) {
                    System.out.println("on click sync");

                    startSyncProcess();
                }
                else if (accessValue == 1) {
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        nav_header_imageView = (ImageView) header.findViewById(R.id.nav_header_imageview);
        nav_header_username = (TextView) header.findViewById(R.id.nav_header_username);
        nav_header_appName = (TextView) header.findViewById(R.id.nav_header_appname);
        nav_header_usersite = (TextView) header.findViewById(R.id.nav_header_usersitecode);
        nav_header_userCurrentLoc=(TextView)header.findViewById(R.id.nav_header_usercurrentLoc);
        nav_header_geofenceStatusImg = (ImageView) header.findViewById(R.id.nav_header_geofenceStatusImg);
        nav_header_geofenceStatusImg.setOnClickListener(this);

        System.out.println("AppName1: " + getIntent().getStringExtra("AppName"));
        //nav_header_appName.setText(getResources().getString(R.string.app_name)+">>"+getIntent().getStringExtra("AppName"));
        /*if (loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, "").equalsIgnoreCase("ALSTOM"))
            nav_header_imageView.setImageResource(R.drawable.alstom);
        else
            nav_header_imageView.setImageResource(R.drawable.youtility_logo);*/

        if (loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, "").contains("ALSTOM"))
            nav_header_imageView.setImageResource(R.drawable.alstom);
        else
            nav_header_imageView.setImageResource(R.drawable.youtility_logo);

        nav_header_appName.setText(getResources().getString(R.string.dashboard_nav_bar_header_title, getResources().getString(R.string.app_name)));
        nav_header_username.setText(loginPref.getString(Constants.LOGIN_PEOPLE_NAME, ""));
        nav_header_usersite.setText(loginPref.getString(Constants.LOGIN_USER_CLIENT_NAME, "") + " . " + loginPref.getString(Constants.LOGIN_SITE_NAME, ""));
        if (fenceBreachPref.getInt(Constants.GEOFENCE_STATUS, -1) == 0)
            nav_header_geofenceStatusImg.setImageResource(R.drawable.entering_geo_fence_48);
        else if (fenceBreachPref.getInt(Constants.GEOFENCE_STATUS, -1) == 1)
            nav_header_geofenceStatusImg.setImageResource(R.drawable.leaving_geo_fence_48);
        else
            nav_header_geofenceStatusImg.setImageResource(R.drawable.bulletdisable);

        /*double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        if(latitude!=0.0 && longitude!=0.0) {
            if(deviceInfoPref.getString(Constants.DEVICE_LOC_PROVIDER,"").equalsIgnoreCase("GPS"))
                nav_header_userCurrentLoc.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_gps_fixed_white_48,0,0,0);
            else if(deviceInfoPref.getString(Constants.DEVICE_LOC_PROVIDER,"").equalsIgnoreCase("NETWORK"))
                nav_header_userCurrentLoc.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_signal_cellular_alt_white_48,0,0,0);
            else
                nav_header_userCurrentLoc.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_location_disabled_white_48,0,0,0);

            nav_header_userCurrentLoc.setText(CommonFunctions.getDMSFormatLocation(latitude, longitude));
        }
        else
            nav_header_userCurrentLoc.setText("GPS coordinates are not available ");*/

        refreshLocationCoordinates();

        //String s1= "TASK TICKET ASSET SITEATTENDANCE PANIC";
        String s1 = loginPref.getString(Constants.LOGIN_MODULE_ACCESS, "");
        String[] array = null;
        if (s1.toString().trim().length() > 0) {
            array = s1.split(" ");
            for (int i = 0; i < array.length; i++) {
                System.out.println("From Web: " + array[i]);
            }
        }

        if (array == null) {
            SharedPreferences.Editor editor = loginPref.edit();
            editor.clear();
            editor.apply();
            Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(loginIntent);
            DashboardActivity.this.finish();
        }


        dashboardModulesList = Arrays.asList(getResources().getStringArray(R.array.application_modules_array));
        dashboardModulesCodeList = Arrays.asList(getResources().getStringArray(R.array.application_modules_code_array));
        dashboardImgList = getResources().obtainTypedArray(R.array.application_modules_img);
        dashboardModulesDescList = Arrays.asList(getResources().getStringArray(R.array.application_modules_desc_array));
        applicationAccessArrayList = new ArrayList<>();
        applicationAccessArrayList1 = new ArrayList<>();

        for (int i = 0; i < dashboardModulesList.size(); i++) {
            System.out.println("Module Name: " + dashboardModulesList.get(i).toString());
            /*ApplicationAccess applicationAccess = new ApplicationAccess();
            applicationAccess.setAppName(dashboardModulesList.get(i).toString().trim());
            applicationAccess.setAppDesc(dashboardModulesDescList.get(i).toString().trim());
            applicationAccess.setAppImage(dashboardImgList.getResourceId(i, 0));
            applicationAccess.setAppCode(dashboardModulesCodeList.get(i).toString().trim());*/

            boolean found = Arrays.asList(array).contains(dashboardModulesCodeList.get(i).toString().trim());
            if (found) {
                ApplicationAccess applicationAccess = new ApplicationAccess();
                applicationAccess.setAppName(dashboardModulesList.get(i).toString().trim());
                applicationAccess.setAppDesc(dashboardModulesDescList.get(i).toString().trim());
                applicationAccess.setAppImage(dashboardImgList.getResourceId(i, 0));
                applicationAccess.setAppCode(dashboardModulesCodeList.get(i).toString().trim());
                applicationAccess.setIsAccess(true);

                applicationAccessArrayList.add(applicationAccess);
            }
            else{
                System.out.println("else arralist");
            }
                //applicationAccess.setIsAccess(false);

            //applicationAccessArrayList.add(applicationAccess);
        }

        Collections.sort(applicationAccessArrayList, new Comparator<ApplicationAccess>() {
            @Override
            public int compare(ApplicationAccess o1, ApplicationAccess o2) {
                return Boolean.compare(o2.getIsAccess(), o1.getIsAccess());
            }

        });

        System.out.println("modulegrid=="+applicationAccessArrayList.size());
        modulesGridView = (GridView) findViewById(R.id.dashboard_gridview);
        dashboardGridViewAdapter = new DashboardGridViewAdapter(DashboardActivity.this, applicationAccessArrayList, this);
        modulesGridView.setAdapter(dashboardGridViewAdapter);


        jobAlertBroadcast = new JobAlertBroadcast();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
        sharedPreferences.edit().putString("login_user_name", loginPref.getString(Constants.LOGIN_PEOPLE_NAME, "")+" ("+loginPref.getString(Constants.LOGIN_USER_ID,"")+")").apply();
        sharedPreferences.edit().putString("login_user_site", loginPref.getString(Constants.LOGIN_SITE_NAME, "")).apply();
        //sharedPreferences.edit().putString("login_user_imei", String.valueOf(deviceInfoPref.getLong(Constants.DEVICE_IMEI, -1))).apply();
        sharedPreferences.edit().putString("login_user_imei", deviceInfoPref.getString(Constants.DEVICE_IMEI, "-1")).apply();

        sharedPreferences.edit().putString("emergency_contact_number", loginPref.getString(Constants.LOGIN_EMERGENCY_CONTACT, "")).apply();
        sharedPreferences.edit().putString("emergency_contact_email", loginPref.getString(Constants.LOGIN_EMERGENCY_EMAIL, "")).apply();
        //emergency_contact_number
        //emergency_contact_email

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        getGMTTime();

        TimeZone timezone = TimeZone.getDefault();//TimeZone.getTimeZone("Asia/Kolkata");
        String TimeZoneName = timezone.getDisplayName();

        System.out.println("DeviceTimezone Name: " + TimeZone.getDefault().getDisplayName());

        int TimeZoneOffset = timezone.getRawOffset() / (60 * 1000);

        int hrs = TimeZoneOffset / 60;
        int mins = TimeZoneOffset % 60;

        miliSeconds = miliSeconds + timezone.getRawOffset();

        resultdate = new Date(miliSeconds);
        System.out.println(sdf.format(resultdate));

        System.out.println(TimeZoneName + " : GMT " + hrs + "." + mins);
        //System.out.println("" + sdf.format(resultdate));
        miliSeconds = 0;
        System.out.println("------====");
        if (loginPref.getBoolean(Constants.IS_SYNC_DONE, false)) {

            System.out.println("------true");
            if(!isServiceRunning(2))//GetLocationService
            {
                System.out.println("------true2");
                Intent locationService = new Intent(DashboardActivity.this, GetLocationService.class);
                startService(locationService);
            }

            if (!isServiceRunning(0))//user route breach
            {
                System.out.println("------true0");

                Intent userRouteBreachService = new Intent(DashboardActivity.this, UserGeofenceBreachService.class);
                startService(userRouteBreachService);
            }

            if (!isServiceRunning(3))//Auto sync service
            {
                System.out.println("------true3");

                Intent autoSyncService = new Intent(DashboardActivity.this, AutoSyncService.class);
                startService(autoSyncService);
            }

            /*if (!isServiceRunning(4))//GetLocationGoogleAPIService
            {
                Intent autoSyncService = new Intent(DashboardActivity.this, GetLocationGoogleAPIService.class);
                startService(autoSyncService);
            }*/

            if (!isServiceRunning(5))//JOb alert broadcast service
            {
                System.out.println("------true5");

                Intent autoSyncService = new Intent(DashboardActivity.this, JobAlertBroadcastService.class);
                startService(autoSyncService);
            }

            if (!isServiceRunning(7))//job alert service
            {
                System.out.println("------true2");

                Intent jobAlertService = new Intent(DashboardActivity.this, JobAlertService.class);
                startService(jobAlertService);
            }

            if (!isServiceRunning(8)) {
                System.out.println("------true8");

                Intent stepCountService = new Intent(DashboardActivity.this, GetStepCounterService.class);
                startService(stepCountService);
            }

            if(!isServiceRunning(9))
            {
                System.out.println("------true9");

                if(loginPref.getString(Constants.LOGIN_CONFIG_SGUARD_ENABLE,"true").equalsIgnoreCase("true")) {
                    Intent captchaConfigService = new Intent(DashboardActivity.this, CaptchaConfigSettingService.class);
                    startService(captchaConfigService);
                }
            }

            /*if (!isServiceRunning(9)) {
                Intent callLogService = new Intent(DashboardActivity.this, StepCountBuzzerService.class);
                startService(callLogService);
            }*/

        }


        if (!loginPref.getBoolean(Constants.IS_SYNC_DONE, false)) {
            startSyncProcess();
            System.out.println("after login sync");
        }


        CommonFunctions.getInstalledAppList(DashboardActivity.this);




        //getCallDetails();

    }

    private void refreshLocationCoordinates()
    {
        Intent locationService = new Intent(DashboardActivity.this, GetLocationService.class);
        startService(locationService);

        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        System.out.println("lat long"+ latitude+":"+longitude);
        if(latitude!=0.0 && longitude!=0.0) {
            if(deviceInfoPref.getString(Constants.DEVICE_LOC_PROVIDER,"").equalsIgnoreCase("GPS"))
                nav_header_userCurrentLoc.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_gps_fixed_white_48,0,0,0);
            else if(deviceInfoPref.getString(Constants.DEVICE_LOC_PROVIDER,"").equalsIgnoreCase("NETWORK"))
                nav_header_userCurrentLoc.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_signal_cellular_alt_white_48,0,0,0);
            else
                nav_header_userCurrentLoc.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_location_disabled_white_48,0,0,0);

            nav_header_userCurrentLoc.setText(CommonFunctions.getDMSFormatLocation(latitude, longitude));
        }
        else
            nav_header_userCurrentLoc.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_location_disabled_white_48,0,0,0);
            nav_header_userCurrentLoc.setText(CommonFunctions.getDMSFormatLocation(latitude, longitude));

    }

    @Override
    public void onClick(View view1) {
        switch (view1.getId()) {
            case R.id.nav_header_usercurrentLoc:
                System.out.println("refresh cordinates");
                refreshLocationCoordinates();
                break;
        }
    }


    private void addContactAsAttachment() {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        AttachmentDAO attachmentDAO = new AttachmentDAO(DashboardActivity.this);
        TypeAssistDAO typeAssistDAO = new TypeAssistDAO(DashboardActivity.this);

        Attachment attachment = new Attachment();
        attachment.setAttachmentid(System.currentTimeMillis());
        attachment.setFilePath(extStorageDirectory + "/" + Constants.FOLDER_NAME + "/contacts.csv");
        attachment.setFileName("contacts.csv");
        attachment.setNarration("ATTACHMENT");
        attachment.setGpslocation("0.0,0.0");
        attachment.setDatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setCuser(loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1));
        attachment.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setMuser(loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1));
        attachment.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_SIGN, Constants.IDENTIFIER_ATTACHMENT));
        System.out.println("attachment.getAttachmentType(): " + attachment.getAttachmentType());
        //attachment.setIsdeleted("False");
        attachment.setOwnerid(loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1));
        attachment.setOwnername(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_OWNER_TYPE_PEOPLE, Constants.IDENTIFIER_OWNER));
        System.out.println("attachment.getOwnername(): " + attachment.getOwnername());
        attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH_1);
        //youtility2_avpt/master/people
        attachment.setBuid(loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID, -1));
        attachmentDAO.insertCommonRecord(attachment);
    }



    /*private void showAlertDialog()
    {

        Intent alertService=new Intent(DashboardActivity.this, SiteReportDoneOrNotCheckService.class);
        stopService(alertService);

        *//*vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = { 0, 500, 500 };
        vibrator.vibrate(pattern, 0);*//*

        if(value==0)
        {
            customAlertDialog.commonDialog("Alert", "Please submit site report for "+siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME,""));
        }
        else if(value==1)
        {
            customAlertDialog.commonDialog("Alert","Please proceed with site CHECK_OUT for "+siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME,""));
        }
    }*/

    /*private int isAllowToAccessModules(String clientCode)
    {
        int val=0;
        if(isNeedToCheckGPSAndDATE(clientCode))
        {
            if(CommonFunctions.isAutoDateTimeEnable(DashboardActivity.this) && CommonFunctions.isGPSStateOn(DashboardActivity.this))
                val=0;
            else
            {
                if(!CommonFunctions.isAutoDateTimeEnable(DashboardActivity.this))
                    val=1;
                else if(!CommonFunctions.isGPSStateOn(DashboardActivity.this))
                    val=2;
            }
        }
        else
        {
            if(!CommonFunctions.isAutoDateTimeEnable(DashboardActivity.this))
                val=1;
        }



        return val;
    }

    private boolean isNeedToCheckGPSAndDATE(String clientCode)
    {
        return clientCode.equalsIgnoreCase("KAYJAYL");
    }*/

    private void getGMTTime() {
        current = Calendar.getInstance();
        System.out.println("Current Time: " + current.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse("2018-03-21 13:20:55");
            current.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        miliSeconds = current.getTimeInMillis();

        TimeZone tzCurrent = current.getTimeZone();
        int offset = tzCurrent.getRawOffset();
        if (tzCurrent.inDaylightTime(new Date())) {
            offset = offset + tzCurrent.getDSTSavings();
        }

        miliSeconds = miliSeconds - offset;

        resultdate = new Date(miliSeconds);
        System.out.println("Formated date: " + sdf.format(resultdate));
    }

    private boolean isServiceRunning(int serviceNum) {
        android.app.ActivityManager manager = (android.app.ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceNum == 0) {
                if ("UserGeofenceBreachService".equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            } else if (serviceNum == 1) {
                if ("ActivityRecognitionIntentService".equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            } else if (serviceNum == 2) {
                if ("GetLocationService".equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            } else if (serviceNum == 3) {
                if ("AutoSyncService".equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            } else if (serviceNum == 4) {
                if ("GetLocationGoogleAPIService".equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            } else if (serviceNum == 5) {
                if ("JobAlertBroadcastService".equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            } else if (serviceNum == 6) {
                if ("SiteReportDoneOrNotCheckService".equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            } else if (serviceNum == 7) {
                if ("JobAlertService".equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            } else if (serviceNum == 8) {
                if ("GetStepCounterService".equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            }
            else if (serviceNum == 9) {
                if ("CaptchaConfigSettingService".equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            } /*else if (serviceNum == 9) {
                if ("StepCountBuzzerService".equalsIgnoreCase(service.service.getClassName())) {
                    return true;
                }
            }*/
        }
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        System.out.println("onrestart====");
        Log.d("Dashboard", "OnRestart");
        Baseclass.getInstance().trackScreenView("Dashboard Screen");


/*        Intent startIntent = new Intent(DashboardActivity.this, UploadImageService.class);
        startService(startIntent);*/

        refreshLocationCoordinates();

        if (fenceBreachPref.getInt(Constants.GEOFENCE_STATUS, -1) == 0)
            nav_header_geofenceStatusImg.setImageResource(R.drawable.bulletcompleted);
        else if (fenceBreachPref.getInt(Constants.GEOFENCE_STATUS, -1) == 1)
            nav_header_geofenceStatusImg.setImageResource(R.drawable.bulletclosed);
        else
            nav_header_geofenceStatusImg.setImageResource(R.drawable.bulletinprogress);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
        String languageCode = sharedPreferences.getString(Constants.SETTING_GENERAL_LANGUAGE, "en");
        System.out.println("languageCode: " + languageCode);

        if (loginPref.getString(Constants.PREVIOUS_SELECTED_LANGUAGE, "").trim().length() == 0)
            loginPref.edit().putString(Constants.PREVIOUS_SELECTED_LANGUAGE, languageCode).apply();
        else {
            if (loginPref.getString(Constants.PREVIOUS_SELECTED_LANGUAGE, "").trim().equalsIgnoreCase(languageCode)) {
                loginPref.edit().putBoolean(Constants.SELECTED_LANGUAGE_CHANGE, false).apply();
            } else {
                loginPref.edit().putString(Constants.PREVIOUS_SELECTED_LANGUAGE, languageCode).apply();
                loginPref.edit().putBoolean(Constants.SELECTED_LANGUAGE_CHANGE, true).apply();
                changeLanguage(languageCode);
                recreate();
            }

        }


    }


    private void changeLanguage(String lang) {
        String seletedLan = lang;
        Locale locale = null;
        /*if (!seletedLan.equalsIgnoreCase("en")|| !seletedLan.equalsIgnoreCase("hi")) {
            String[] contryCode = seletedLan.split("_");
            System.out.println("contryCode[0]: " + contryCode[0]);
            //System.out.println("contryCode[1]: " + contryCode[1]);
            if (contryCode[1].equalsIgnoreCase("CN"))
                locale = Locale.SIMPLIFIED_CHINESE;
            else if (contryCode[1].equalsIgnoreCase("TW"))
                locale = Locale.TRADITIONAL_CHINESE;
        } else {
            locale = new Locale(lang);
        }*/
        switch(lang)
        {
            case "en":
                locale = new Locale(sharedPreferences.getString("language_type", "en"));
                break;
            case "hi":
                locale = new Locale(lang);
                break;
            case "zh_CN":
                locale=Locale.SIMPLIFIED_CHINESE;
                break;
            case "zh_TW":
                locale=Locale.TRADITIONAL_CHINESE;
                break;
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());

    }

    @Override
    public void onBackPressed() {
        if (vibrator != null)
            vibrator.cancel();
        if (r != null && r.isPlaying())
            r.stop();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }

            this.doubleBackToExitPressedOnce = true;
            Snackbar.make(fab, getResources().getString(R.string.dashboard_backbutton_msg), Snackbar.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        item_stepcount = menu.findItem(R.id.action_stepcountsummary);
        if(loginPref.getString(Constants.LOGIN_CONFIG_SGUARD_ENABLE,"false").equalsIgnoreCase("true"))
            item_stepcount.setVisible(true);
        else
            item_stepcount.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        int accessValue = CommonFunctions.isAllowToAccessModules(DashboardActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        ///int accessValue=0;
        System.out.println("===========D"+accessValue);
        System.out.println("===========D"+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
        if (accessValue == 0) {
        if (id == R.id.action_summary) {

                System.out.println("summary");
                customAlertDialog.syncSummaryReport();

            return true;
        } else if (id == R.id.action_stepcountsummary) {
            Intent ii = new Intent(DashboardActivity.this, StepCounterActivity.class);
            startActivity(ii);
            return true;
        }
        } else if (accessValue == 1) {
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        LayerDrawable stepCountIcon = (LayerDrawable) item_stepcount.getIcon();
        CommonFunctions.setBadgeCount(DashboardActivity.this, stepCountIcon, "" + stepCounterPref.getLong(Constants.STEP_COUNTER_COUNT, 0), R.id.ic_stepcounter_badge);
        invalidateOptionsMenu();
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        int accessValue = CommonFunctions.isAllowToAccessModules(DashboardActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        ///int accessValue=0;
        System.out.println("===========D"+accessValue);
        System.out.println("===========D"+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
        if (accessValue == 0) {

            if (id == R.id.nav_changePassword) {
                customAlertDialog.changePasswordForm("Change Password", "");
            } else if (id == R.id.nav_geofence) {
                Intent nxtActivity = new Intent(DashboardActivity.this, UserLocationViewActivity.class);
                nxtActivity.putExtra("FROM", "GEOFENCE");
                startActivityForResult(nxtActivity, USERLOCATION_INTENT);
            } else if (id == R.id.nav_settting) {
                Intent settingIntent = new Intent(DashboardActivity.this, SettingsActivity.class);
                startActivity(settingIntent);
            }else if (id == R.id.nav_soslog) {
                Intent settingIntent = new Intent(DashboardActivity.this, SosLogActivity.class);
                startActivity(settingIntent);
            } else if (id == R.id.nav_logout) {

                int unsynCount = commonDAO.getUnsyncDataCount();
                if (unsynCount > 0) {
                    if (unsynCount == 1)
                        customAlertDialog.showPendingEntryDialog(getResources().getString(R.string.alerttitle), unsynCount + " " + getResources().getString(R.string.dashboard_entrynotsync_msg));
                    else
                        customAlertDialog.showPendingEntryDialog(getResources().getString(R.string.alerttitle), unsynCount + " " + getResources().getString(R.string.dashboard_entrynotsync_msg));
                } else {
                    if (checkNetwork.isNetworkConnectionAvailable())
                        customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.navigation_drawer_logout), getResources().getString(R.string.dashboard_doyouwantlogoutmsg), "", 1);
                    else
                        customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.offlinemode_alert_message));
                }

            } else if (id == R.id.nav_aboutus) {
                Intent aboutusIntent = new Intent(DashboardActivity.this, AboutUsActivity.class);
                startActivityForResult(aboutusIntent, ABOUTUS_INTENT);
            } else if (id == R.id.nav_privacypolicy) {
            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/e/2PACX-1vTnjMGjxiSmE2PF5OUgK4KMa_MVMwECz1ngWsES1fY2A_OSwQwJ4ViDjVRCq2KwVUNlgsm7M5Mt9ErC/pub"));
            startActivity(browserIntent);*/
                String url = "https://docs.google.com/document/d/e/2PACX-1vTnjMGjxiSmE2PF5OUgK4KMa_MVMwECz1ngWsES1fY2A_OSwQwJ4ViDjVRCq2KwVUNlgsm7M5Mt9ErC/pub";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

/*    public void addShortcut() {
        System.out.println("Long Click Panic1");

        Intent target = new Intent(getApplicationContext(), SOSActivity.class);
        target.setAction(Intent.ACTION_MAIN);

        Intent shout = new Intent();
        shout.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
        shout.putExtra(Intent.EXTRA_SHORTCUT_NAME, "SOS");
        shout.putExtra(Intent.EXTRA_SHORTCUT_ICON, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.dashbaord_sos));

        shout.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        getApplicationContext().sendBroadcast(shout);

    }*/

    private void createShortcutOfApp() {

        System.out.println("create shortcut=");
        /*Intent shortcutIntent = new Intent(getApplicationContext(),
                SOSActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "App shortcut name");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.dashbaord_sos));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);  //may it's already there so   don't duplicate
        getApplicationContext().sendBroadcast(addIntent);*/

        System.out.println("shortcut created=");

    }

/*    public void onLongGridViewItemClick(int position, String appName, boolean isAccess, String appCode){
        Intent nxtActivity = null;
        System.out.println("AppName: " + appName);
        System.out.println("AppCode: " + appCode);
        //int accessValue=isAllowToAccessModules(loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE,""));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        int accessValue = CommonFunctions.isAllowToAccessModules(DashboardActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        ///int accessValue=0;
        System.out.println("===========D"+accessValue);
        System.out.println("===========D"+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
        if (accessValue == 0) {
            switch (appCode) {
                case "PANIC"://
                   *//* nxtActivity = new Intent(DashboardActivity.this, SOSActivity.class);
                    startActivityForResult(nxtActivity, SOS_INTENT);*//*

                    System.out.println("Long Click Panic2");
                    break;
            }
        } else if (accessValue == 1) {
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
    }*/

    @Override
    public void onGridViewItemClick(int position, String appName, boolean isAccess, String appCode) {
        Intent nxtActivity = null;
        System.out.println("AppName: " + appName);
        System.out.println("AppCode: " + appCode);
        //int accessValue=isAllowToAccessModules(loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE,""));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        int accessValue = CommonFunctions.isAllowToAccessModules(DashboardActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        ///int accessValue=0;
        System.out.println("===========D"+accessValue);
        System.out.println("===========D"+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
        if (accessValue == 0) {
            switch (appCode) {
                case "TASK"://task
                    /*nxtActivity = new Intent(DashboardActivity.this, JOBListActivity.class);
                    nxtActivity.putExtra("FROM", Constants.JOB_NEED_IDENTIFIER_TASK);
                    startActivityForResult(nxtActivity, JOB_INTENT);*/

                    nxtActivity = new Intent(DashboardActivity.this, TaskListActivity.class);
                    nxtActivity.putExtra("FROM", Constants.JOB_NEED_IDENTIFIER_TASK);
                    startActivityForResult(nxtActivity, JOB_INTENT);
                    break;
                case "TICKET"://ticket
                    /*nxtActivity = new Intent(DashboardActivity.this, HelpDeskListActivity.class);
                    startActivityForResult(nxtActivity, TICKET_INTENT);*/
                    nxtActivity = new Intent(DashboardActivity.this, TicketViewActivity.class);
                    startActivityForResult(nxtActivity, TICKET_INTENT);
                    break;
                case "ASSET"://asset
                    nxtActivity = new Intent(DashboardActivity.this, AssetListActivity.class);
                    startActivityForResult(nxtActivity, ASSET_INTENT);
                    break;
                case "INCIDENTREPORT"://"INCIDENT REPORT"://incident report
                    nxtActivity = new Intent(DashboardActivity.this, IncidentReportListActivity.class);
                    startActivityForResult(nxtActivity, INCIDENTREPORT_INTENT);
                    break;
                case "PANIC"://
                    createShortcutOfApp();
                    nxtActivity = new Intent(DashboardActivity.this, SOSActivity.class);
                    startActivityForResult(nxtActivity, SOS_INTENT);
                    break;
                case "CHECKPOINT"://"CHECK POINT":
                    nxtActivity = new Intent(DashboardActivity.this, CheckpointListActivity.class);
                    startActivityForResult(nxtActivity, CHECKPOINT_INTENT);
                    break;
                case "SITETOUR"://"SITE TOUR":
                    nxtActivity = new Intent(DashboardActivity.this, SiteListActivity.class);
                    //nxtActivity = new Intent(DashboardActivity.this, SiteReportListActivity.class);

                    nxtActivity.putExtra("ATTENDANCE_TYPE", "AUDIT");
                    startActivityForResult(nxtActivity, SITETOUR_INTENT);
                    break;
                case "SITEVISITLOG"://"SITE LOG":
                    nxtActivity = new Intent(DashboardActivity.this, SiteVisitedLogActivity.class);
                    startActivityForResult(nxtActivity, SITELOG_INTENT);

                    /*nxtActivity=new Intent(DashboardActivity.this, SiteVisitedLogExpLVActivity.class);
                    startActivityForResult(nxtActivity, SITELOG_INTENT);*/
                    break;
                case "USERLOCATION"://"USER LOCATION":
                    nxtActivity = new Intent(DashboardActivity.this, UserLocationViewActivity.class);
                    nxtActivity.putExtra("FROM", "USERLOCATION");
                    startActivityForResult(nxtActivity, USERLOCATION_INTENT);
                    break;
                case "CONVEYANCE"://"CONVEYANCE":
                    nxtActivity = new Intent(DashboardActivity.this, ConveyanceActivity.class);
                    startActivityForResult(nxtActivity, ADDCONVEYANCE_INTENT);
                    /*nxtActivity = new Intent(DashboardActivity.this, PPMPlannerView.class);
                    startActivityForResult(nxtActivity, JOB_INTENT);*/
                    break;
                case "PPMPLANNER"://"PPM PLANNER":
                    nxtActivity = new Intent(DashboardActivity.this, JOBListActivity.class);
                    nxtActivity.putExtra("FROM", Constants.JOB_NEED_IDENTIFIER_PPM);
                    startActivityForResult(nxtActivity, JOB_INTENT);
                    /*nxtActivity = new Intent(DashboardActivity.this, PPMPlannerView.class);
                    startActivityForResult(nxtActivity, JOB_INTENT);*/
                    break;
                case "SELFATTENDANCE"://"SELF ATTENDANCE":
                    //showPunchTypeDialog();
                    Intent selfAttendaceIntent = new Intent(DashboardActivity.this, SelfAttendanceActivity.class);
                    startActivityForResult(selfAttendaceIntent, SELFATTENDANCE_INTENT);
                    break;
                case "SITEATTENDANCE"://"SITE ATTENDANCE":
                    nxtActivity = new Intent(DashboardActivity.this, SiteListActivity.class);
                    nxtActivity.putExtra("ATTENDANCE_TYPE", "MARK");
                    startActivityForResult(nxtActivity, SITEATTENDANCE_INTENT);
                    break;
                case "TAKEATTENDANCE"://"TAKE ATTENDANCE":
                    nxtActivity = new Intent(DashboardActivity.this, SiteListActivity.class);
                    nxtActivity.putExtra("ATTENDANCE_TYPE", "TAKE");
                    startActivityForResult(nxtActivity, TAKEATTENDANCE_INTENT);
                    break;
                case "AREAOFFICER"://"AREA OFFICER":
                    nxtActivity = new Intent(DashboardActivity.this, SubmitMonthlyAttendanceActivity.class);
                    startActivityForResult(nxtActivity, SUBMITATTENDANCE_INTENT);

                    /*nxtActivity = new Intent(DashboardActivity.this, CameraFocusActivity.class);
                    startActivityForResult(nxtActivity,SUBMITATTENDANCE_INTENT);*/

                    break;
                case "BRANCHMANAGER":
                    nxtActivity = new Intent(DashboardActivity.this, ApproveMonthlyAttendanceActivity.class);
                    startActivityForResult(nxtActivity, APPROVEATTENDANCE_INTENT);
                    break;
                case "EMPLOYEEREFERENCE"://"EMP REFERENCE":
                    nxtActivity = new Intent(DashboardActivity.this, NewGuardReferenceEntryActivity.class);
                    startActivityForResult(nxtActivity, SUBMITATTENDANCE_INTENT);
                    break;
                case "WORKFLOW"://"WORKFLOW":
                    nxtActivity = new Intent(DashboardActivity.this, WorkflowActivity.class);
                    startActivityForResult(nxtActivity, WORKFLOW_INTENT);
                    break;
                case "ASSETAUDIT"://"ASSETAUDIT":
                    nxtActivity = new Intent(DashboardActivity.this, AssetAuditActivity.class);
                    startActivityForResult(nxtActivity, ASSETAUDIT_INTENT);
                    break;
                case "REQUEST"://"REQUEST":
                    nxtActivity = new Intent(DashboardActivity.this, RequestViewActivity.class);
                    startActivityForResult(nxtActivity, REQUEST_INTENT);
                    break;
            }
        } else if (accessValue == 1) {
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
    public void onLongGridViewItemClick(int position, String appName, boolean isAccess, String appCode) {

        System.out.println("Long click"+appCode);
        Intent nxtActivity = null;
        System.out.println("AppName: " + appName);
        System.out.println("AppCode: " + appCode);
        //int accessValue=isAllowToAccessModules(loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE,""));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        int accessValue = CommonFunctions.isAllowToAccessModules(DashboardActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        ///int accessValue=0;
        System.out.println("===========D"+accessValue);
        System.out.println("===========D"+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
        if (accessValue == 0) {
            switch (appCode) {
                case "PANIC"://
                    createShortcutOfApp();
                    nxtActivity = new Intent(DashboardActivity.this, SosLogActivity.class);
                    startActivityForResult(nxtActivity, SOS_INTENT);
                    break;
                case "SITEVISITLOG"://"SITE LOG":
                    nxtActivity = new Intent(DashboardActivity.this, SiteVisitedLogActivity.class);
                    startActivityForResult(nxtActivity, SITELOG_INTENT);

                    /*nxtActivity=new Intent(DashboardActivity.this, SiteVisitedLogExpLVActivity.class);
                    startActivityForResult(nxtActivity, SITELOG_INTENT);*/
                    break;
            }
        } else if (accessValue == 1) {
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


    private void showPunchTypeDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Select punch status");
        alertDialogBuilder.setPositiveButton(Constants.ATTENDANCE_PUNCH_TYPE_IN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                callSelfAttendanceIntent(Constants.ATTENDANCE_PUNCH_TYPE_IN);
            }
        });

        alertDialogBuilder.setNegativeButton(Constants.ATTENDANCE_PUNCH_TYPE_OUT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callSelfAttendanceIntent(Constants.ATTENDANCE_PUNCH_TYPE_OUT);

            }
        });

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void callSelfAttendanceIntent(String attendType) {
        Intent selfAttendaceIntent = new Intent(DashboardActivity.this, SelfAttendanceActivity.class);
        selfAttendaceIntent.putExtra("PUNCH_TYPE", attendType);
        startActivityForResult(selfAttendaceIntent, SELFATTENDANCE_INTENT);
    }

    private void startSyncProcess() {
        if (checkNetwork.isNetworkConnectionAvailable()) {
            if (MemoryInfo.checkMemoryInternalAvailable() || MemoryInfo.checkMemoryExternalAvailable()) {
                if (!MemoryInfo.checkMemoryInternalAvailable()) {
                    customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), "Phone internal memory 80% full. Please clear some space.");
                } else if (!MemoryInfo.checkMemoryExternalAvailable()) {
                    customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), "SD card memory 80% full. Please clear some space.");
                } else {
                    SharedPreferences syncOffsetPref=getSharedPreferences(Constants.SYNC_OFFSET_PREF,MODE_PRIVATE);
                    syncOffsetPref.edit().putInt(Constants.SYNC_TICKET_OFFSET,0).apply();
                    Intent syncActivity = new Intent(DashboardActivity.this, SyncronizationViewActivity.class);
                    startActivityForResult(syncActivity, SYNC_INTENT);
                }
            }

        } else {
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.no_internet_connection));
        }

    }

    /*@Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x=0.0f;
        float y=0.0f;
        switch (motionEvent.getAction()){

            case MotionEvent.ACTION_MOVE:
                fab.setX(fab.getX() + (motionEvent.getX() - x));
                fab.setY(fab.getY() + (motionEvent.getY() - y));
                return true;
            case MotionEvent.ACTION_DOWN:
                x = motionEvent.getX();
                y = motionEvent.getY();
                return true;
        }
        return false;
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("requestcode="+ requestCode+"resultCode="+ resultCode +"SYNC_INTENT="+ SYNC_INTENT+ "RESULT_OK="+ RESULT_OK);

        if (requestCode == SYNC_INTENT) {
            if (resultCode == RESULT_OK) {
                System.out.println("upload image started");
                Intent startIntent = new Intent(DashboardActivity.this, UploadImageService.class);
                startService(startIntent);

                if(!isServiceRunning(2))//get user location
                {
                    Intent locationService = new Intent(DashboardActivity.this, GetLocationService.class);
                    startService(locationService);
                }

                if (!isServiceRunning(0))//user route breach
                {
                    GeofenceDAO geofenceDAO = new GeofenceDAO(DashboardActivity.this);
                    if (geofenceDAO.getGeoFenceCount(loginPref.getLong(Constants.LOGIN_SITE_ID, -1)) > 0) {
                        Intent userRouteBreachService = new Intent(DashboardActivity.this, UserGeofenceBreachService.class);
                        startService(userRouteBreachService);
                    }
                }

                if (!isServiceRunning(3))//Auto sync service
                {
                    if (!syncPref.getBoolean(Constants.SYNC_MANUAL_RUNNING, false)) {
                        Intent autoSyncService = new Intent(DashboardActivity.this, AutoSyncService.class);
                        startService(autoSyncService);
                    } else
                        System.out.println("Manual Sync is running------------------------------------------------------------------------");
                }

                /*if (!isServiceRunning(4))//Auto sync service
                {
                    Intent autoSyncService = new Intent(DashboardActivity.this, GetLocationGoogleAPIService.class);
                    startService(autoSyncService);
                }*/


                if (!isServiceRunning(5))//JOb alert broadcast service
                {
                    Intent autoSyncService = new Intent(DashboardActivity.this, JobAlertBroadcastService.class);
                    startService(autoSyncService);
                }

                if (!isServiceRunning(7))//job alert service
                {
                    Intent jobAlertService = new Intent(DashboardActivity.this, JobAlertService.class);
                    startService(jobAlertService);
                }

                if (!isServiceRunning(8)) {
                    Intent stepCountService = new Intent(DashboardActivity.this, GetStepCounterService.class);
                    startService(stepCountService);
                }

                if (!isServiceRunning(9)) {
                    if(loginPref.getString(Constants.LOGIN_CONFIG_SGUARD_ENABLE,"true").equalsIgnoreCase("true")) {
                        Intent capConfigSetting = new Intent(DashboardActivity.this, CaptchaConfigSettingService.class);
                        startService(capConfigSetting);
                    }
                }

                /*if (!isServiceRunning(9)) {
                    Intent intent = new Intent(DashboardActivity.this, StepCountBuzzerService.class);
                    startService(intent);
                }*/

                if (peopleDAO.isCheckUserLog(loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1))) {
                    String configEmailId = peopleDAO.getUserMobileLogEmailId(loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1));
                    System.out.println("configEmailId: " + configEmailId);

                    EmailReadingDataLogFileAsynTask emailReadingDataLogFileAsynTask = new EmailReadingDataLogFileAsynTask(DashboardActivity.this, configEmailId);
                    emailReadingDataLogFileAsynTask.execute();
                } else {
                    System.out.println("Send Log Not Checked");
                }

                //addContactAsAttachment();


            }
        } else if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                subscribe();
            }
        }


        /*else if(requestCode==SETTING_INTENT)
        {
            if(resultCode==RESULT_OK)
            {

                SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
                String languageCode=sharedPreferences.getString(Constants.SETTING_GENERAL_LANGUAGE,"en");
                System.out.println("languageCode: "+languageCode);

                changeLanguage(languageCode);
                recreate();
            }
        }*/
        /*else
        {
            PersonLoggerDAO personLoggerDAO=new PersonLoggerDAO(DashboardActivity.this);
            personLoggerDAO.getCount();
        }*/
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {
        //startSyncProcess();
        /*if(type==0) {

            if(vibrator!=null)
                vibrator.cancel();
            if(r!=null && r.isPlaying())
                r.stop();
            if(siteAuditPref.getLong(Constants.SITE_AUDIT_QUESTIONSETID,-1)!=-1) {
                Intent fillReportIntent = new Intent(DashboardActivity.this, IncidentReportQuestionActivity.class);
                fillReportIntent.putExtra("FROM", "SITEREPORT");
                fillReportIntent.putExtra("ID", siteAuditPref.getLong(Constants.SITE_AUDIT_TIMESTAMP, -1));
                fillReportIntent.putExtra("QUESTIONSETID", siteAuditPref.getLong(Constants.SITE_AUDIT_QUESTIONSETID, -1));
                fillReportIntent.putExtra("PARENT_ACTIVITY", "JOBNEED");
                fillReportIntent.putExtra("FOLDER", "SITEREPORT");
                startActivity(fillReportIntent);
            }
            else {
                //Snackbar.make(fab,"Site survey report not selected, Please select site from list and ",Snackbar.LENGTH_LONG).show();
                Intent nxtActivity = new Intent(DashboardActivity.this, SiteListActivity.class);
                nxtActivity.putExtra("ATTENDANCE_TYPE", "AUDIT");
                startActivity(nxtActivity);
            }
        }*/
    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        if (type == 1) //for logout conformation
        {
            /*LogoutAsynTask logoutAsynTask = new LogoutAsynTask(loginPref.getString(Constants.LOGIN_ENTERED_USER_ID, ""),
                    loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS, ""),
                    loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE, ""));
            logoutAsynTask.execute();*/

            prepareLogout();

        }
        else if(type==0)
        {
            Snackbar.make(fab,"Password changed successfully.",Snackbar.LENGTH_LONG).show();
        }
        else if(type==2) //for showing more details about unsync entry
        {
            SharedPreferences synPref=getSharedPreferences(Constants.SYNC_SUMMARY_PREF, MODE_PRIVATE);
            String message="Event Log: "+synPref.getInt(Constants.SYNC_SUMMARY_PENDING_PEOPLEEVENTLOG_COUNT,0)+" \n"+
                    "Task/Ticket/Tour/SiteAudit/IR: "+synPref.getInt(Constants.SYNC_SUMMARY_PENDING_JOBNEED_COUNT,0)+" \n"+
                    "Reply: "+synPref.getInt(Constants.SYNC_SUMMARY_PENDING_REPLY_COUNT,0)+" \n"+
                    "Employee Ref: "+synPref.getInt(Constants.SYNC_SUMMARY_PENDING_EMPREF_COUNT,0);
            customAlertDialog.commonDialog1("Pending Entries Count",message);
        }

    }

    private void prepareLogout()
    {



        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message
        progressDialog.show(); // show progress dialog

        UploadLoginParameters uploadParameters=new UploadLoginParameters();
        uploadParameters.setServicename(Constants.SERVICE_LOGOUT);
        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
        uploadParameters.setLoginid(loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""));
        uploadParameters.setPassword(loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS, ""));
        uploadParameters.setSitecode(appliationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE, ""));
        //uploadParameters.setDeviceid(String.valueOf(deviceInfoPref.getLong(Constants.DEVICE_IMEI, -1)));
        uploadParameters.setDeviceid(String.valueOf(deviceInfoPref.getString(Constants.DEVICE_IMEI, "-1")));

        Gson gson = new Gson();
        String upData = gson.toJson(uploadParameters);
        System.out.println("upData: "+upData);

        CommonFunctions.UploadLog("\n <Logout Upload> \n"+upData+"\n");

        RetrofitServices retrofitServices= RetrofitClient.getClient().create(RetrofitServices.class);
        Call<LogoutResponse> call=retrofitServices.logout(Constants.SERVICE_LOGOUT,uploadParameters);
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(@NonNull Call<LogoutResponse> call, @NonNull Response<LogoutResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful() && response.body()!=null)
                {
                    if (response.body().getRc() == 0) {

                        System.out.println("response.body().getMsg(): "+response.body().getMsg());
                        buzzCounter = 0;
                        commonDAO.deleteAllData();

                        siteVisitedLogDAO.deletRecords();

                        SharedPreferences.Editor editor = loginPref.edit();
                        editor.clear();
                        editor.apply();
                        editor.commit();

                        SharedPreferences.Editor editor1 = selfAttendancePref.edit();
                        editor1.clear();
                        editor1.apply();
                        editor1.commit();


                        sharedPreferences.edit().putString(Constants.SETTING_GENERAL_CONTACT_NUMBER, "").apply();
                        sharedPreferences.edit().putString(Constants.SETTING_GENERAL_CONTACT_EMAILID, "").apply();

                        if(GoogleSignIn.getLastSignedInAccount(DashboardActivity.this)!=null)
                            Fitness.getConfigClient(DashboardActivity.this, GoogleSignIn.getLastSignedInAccount(DashboardActivity.this)).disableFit();

                        CommonFunctions.deleteCache(DashboardActivity.this);

                        Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);

                        DashboardActivity.this.finish();
                    }
                    else
                    {
                        System.out.println("response.body().getMsg(): "+response.body().getMsg());
                        Snackbar.make(fab, response.body().getMsg(),Snackbar.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Snackbar.make(fab, getResources().getString(R.string.check_internet_connection_msg),Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LogoutResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public void SetAlarm(Context context) {
        Log.d("JOBAlertBroadcast", "JOB Alarm started");
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(DashboardActivity.this, JobAlertBroadcast.class);
        intent.putExtra("TaskStatus", Boolean.FALSE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        /*syncpref=context.getSharedPreferences(Constants.SETTING_PARAMETER_PREF, Context.MODE_PRIVATE);
        long dur=(syncpref.getInt(Constants.DIALOG_SETTING, 5)*60);
        System.out.println("SetAlertDuration: "+dur);
        System.out.println("millisec: "+(1000 * dur));*/
        //set 1 min
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), ((1000 * 60)), pi);
        //am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis() , pi);

    }

 /*   @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_header_geofenceStatusImg:

                break;
        }
    }*/

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        /*if((!isConnected && networkPref.getBoolean(Constants.NETWORK_AVAILABLE,false)) ||
                (isConnected && !networkPref.getBoolean(Constants.NETWORK_AVAILABLE,false))) {
            //showSnack(isConnected);
            CommonFunctions.showSnack(DashboardActivity.this,isConnected,fab);
        }*/

        CommonFunctions.showSnack(DashboardActivity.this,isConnected,fab);

        //System.out.println("Network connection: "+isConnected);
        //System.out.println("Network pref connection: "+networkPref.getBoolean(Constants.NETWORK_AVAILABLE,false));
        //networkPref.edit().putBoolean(Constants.NETWORK_AVAILABLE,isConnected).apply();
    }

    /*private void showSnack(boolean isConnected) {
        String message;
        int color;
        int bColor;
        if (isConnected) {
            message = getResources().getString(R.string.internet_connection_available);
            color = Color.WHITE;
            bColor=Color.parseColor("#01b140");
        } else {
            message = getResources().getString(R.string.no_internet_connection);
            color = Color.parseColor("#ec563b");
            bColor=Color.BLACK;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(bColor);
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }*/


    private class LogoutAsynTask extends AsyncTask<Void, Integer, Void> {
        String loginId;
        String loginPass;
        String loginSiteCode;
        ProgressDialog dialog;
        InputStream is;
        byte[] buffer = null;
        int byteread = 0;
        int status = -1;
        boolean auth = false;
        String msg = null;

        public LogoutAsynTask(String loginId, String loginPass, String loginSiteCode) {
            this.loginId = loginId;
            this.loginPass = loginPass;
            this.loginSiteCode = loginSiteCode;
            dialog = new ProgressDialog(DashboardActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage(getResources().getString(R.string.please_wait));
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringBuffer sb;

            try {
                ServerRequest serverRequest = new ServerRequest(DashboardActivity.this);
                HttpResponse response = serverRequest.getLogoutResponse(loginId, loginPass, loginSiteCode);

                if (response != null && response.getStatusLine().getStatusCode() == 200) {
                    try {
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

                        JSONObject ob = new JSONObject(sb.toString());
                        status = ob.getInt(Constants.RESPONSE_RC);
                        //auth=ob.getBoolean(Constants.RESPONSE_AUTH);
                        msg = ob.getString(Constants.RESPONSE_MSG);


                    } catch (IOException e) {
                        Baseclass.getInstance().trackException(e);
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        Baseclass.getInstance().trackException(e);
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Baseclass.getInstance().trackException(e);
                        e.printStackTrace();
                    }
                }

            } catch (UnrecoverableKeyException e) {
                Baseclass.getInstance().trackException(e);
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                Baseclass.getInstance().trackException(e);
                e.printStackTrace();
            } catch (KeyStoreException e) {
                Baseclass.getInstance().trackException(e);
                e.printStackTrace();
            } catch (KeyManagementException e) {
                Baseclass.getInstance().trackException(e);
                e.printStackTrace();
            } catch (IOException e) {
                Baseclass.getInstance().trackException(e);
                e.printStackTrace();
            } catch (CertificateException e) {
                Baseclass.getInstance().trackException(e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (status == 0) {
                buzzCounter = 0;
                commonDAO.deleteAllData();

                siteVisitedLogDAO.deletRecords();

                JobNeedDAO jobNeedDAO = new JobNeedDAO(DashboardActivity.this);
                jobNeedDAO.getCount();

                SharedPreferences.Editor editor = loginPref.edit();
                editor.clear();
                editor.apply();

                SharedPreferences.Editor editor1 = selfAttendancePref.edit();
                editor1.clear();
                editor1.apply();

                sharedPreferences.edit().putString(Constants.SETTING_GENERAL_CONTACT_NUMBER, "").apply();
                sharedPreferences.edit().putString(Constants.SETTING_GENERAL_CONTACT_EMAILID, "").apply();

                if(GoogleSignIn.getLastSignedInAccount(DashboardActivity.this)!=null)
                    Fitness.getConfigClient(DashboardActivity.this, GoogleSignIn.getLastSignedInAccount(DashboardActivity.this)).disableFit();

                Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);

                DashboardActivity.this.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            getApplicationContext().unregisterReceiver(jobAlertBroadcastTemp);

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed!");
                                    getCount();

                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                            }
                        });


    }

    private void getCount() {
        //CountDownTimer countDownTimer;
         /*final Handler handler = new Handler();
         handler.postDelayed( new Runnable() {

             @Override
             public void run() {
                 readData();
                 handler.postDelayed( this, 60 * 1000 );
             }
         }, 60 * 1000 );*/
        final Handler handler = new Handler();
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {

                //Constants.countDownTimer = new CountDownTimer(60000, 1000) {
                Constants.countDownTimer = new CountDownTimer(10000, 100) {

                    public void onTick(long millisUntilFinished) {
                        //System.out.println("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        System.out.println("seconds done!");
                        Constants.countDownTimer = null;
                        /*if (stepCounterPref.getBoolean(Constants.STEP_COUNTER_ENABLE, false)) {
                            readData();
                        }*/
                        readData();
                        getCount();
                    }
                }.start();
            }
        }, 0);

    }

    private void readData() {

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                                //float dd= dataSet.getDataPoints().get(0).getValue(Field.FIELD_DISTANCE).asFloat();


                                Log.i(TAG, "Total steps: " + total + " : Time: " + CommonFunctions.getFormatedDate(System.currentTimeMillis()) + " : distance: " + 00);

                                stepCounterPref.edit().putLong(Constants.STEP_COUNTER_LAST_COUNT, stepCounterPref.getLong(Constants.STEP_COUNTER_COUNT, 0)).apply();
                                stepCounterPref.edit().putLong(Constants.STEP_COUNTER_LAST_TIMESTAMP, stepCounterPref.getLong(Constants.STEP_COUNTER_TIMESTAMP, System.currentTimeMillis())).apply();

                                stepCounterPref.edit().putLong(Constants.STEP_COUNTER_COUNT, total).apply();
                                stepCounterPref.edit().putString(Constants.STEP_COUNTER_TIME, CommonFunctions.getFormatedDate(System.currentTimeMillis())).apply();
                                stepCounterPref.edit().putLong(Constants.STEP_COUNTER_TIMESTAMP, System.currentTimeMillis()).apply();

                                StepCount stepCount=new StepCount();
                                stepCount.setStepCountTimestamp(System.currentTimeMillis());
                                stepCount.setSteps(stepCounterPref.getLong(Constants.STEP_COUNTER_COUNT, 0) - stepCounterPref.getLong(Constants.STEP_COUNTER_LAST_COUNT, 0));
                                stepCount.setTotalSteps(stepCounterPref.getLong(Constants.STEP_COUNTER_COUNT,0));
                                String minDiff=CommonFunctions.getDateDifference(stepCounterPref.getLong(Constants.STEP_COUNTER_TIMESTAMP,System.currentTimeMillis()),stepCounterPref.getLong(Constants.STEP_COUNTER_LAST_TIMESTAMP,System.currentTimeMillis()));
                                stepCount.setStepsTaken(minDiff);
                                stepsCountLogDAO.insertRecord(stepCount);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LayerDrawable stepCountIcon = (LayerDrawable) item_stepcount.getIcon();
                                        CommonFunctions.setBadgeCount(DashboardActivity.this, stepCountIcon, "" + stepCounterPref.getLong(Constants.STEP_COUNTER_COUNT, 0), R.id.ic_stepcounter_badge);
                                        invalidateOptionsMenu();
                                    }
                                });


                                if (stepCounterPref.getLong(Constants.STEP_COUNTER_COUNT, 0) - stepCounterPref.getLong(Constants.STEP_COUNTER_LAST_COUNT, 0) > 0)
                                    stepCounterPref.edit().putInt(Constants.STEP_COUNTER_BUZZ_COUNTER, 0).apply();

                                //Toast.makeText(DashboardActivity.this, buzzCounter+"",Toast.LENGTH_SHORT).show();
                                //if (stepCounterPref.getInt(Constants.STEP_COUNTER_BUZZ_COUNTER, 0) < 3)

                                if (stepCounterPref.getInt(Constants.STEP_COUNTER_BUZZ_COUNTER, 0) < (stepCounterPref.getInt(Constants.STEP_COUNTER_BUZZ_TIMER, 10)))
                                {
                                    stepCounterPref.edit().putInt(Constants.STEP_COUNTER_BUZZ_COUNTER, (stepCounterPref.getInt(Constants.STEP_COUNTER_BUZZ_COUNTER, 0) + 1)).apply();
                                    System.out.println("=========================================================steps + BuzzCounter: " + stepCounterPref.getInt(Constants.STEP_COUNTER_BUZZ_COUNTER,0));
                                } else {
                                    stepCounterPref.edit().putInt(Constants.STEP_COUNTER_BUZZ_COUNTER, 0).apply();
                                    if (stepCounterPref.getLong(Constants.STEP_COUNTER_COUNT, 0) - stepCounterPref.getLong(Constants.STEP_COUNTER_LAST_COUNT, 0) == 0) {
                                        stepsCountLogDAO.deletRecords();

                                        long currTimeStamp=System.currentTimeMillis();

                                        stepCounterPref.edit().putLong(Constants.STEP_COUNTER_ID,currTimeStamp).apply();

                                        EventLogInsertion eventLogInsertion = new EventLogInsertion(DashboardActivity.this);
                                        eventLogInsertion.addBuzzerStepCountEvent("CAPTCHA", "Captcha", "Event Type", "",currTimeStamp);

                                        int captcType=Integer.parseInt(sharedPreferences.getString(Constants.SETTING_CAPTCHA_TYPE,"0"));
                                        switch (captcType)
                                        {
                                            case 0://captcha
                                                Intent ii = new Intent(DashboardActivity.this, StepBuzzerDialogActivity.class);
                                                ii.putExtra("Allow", 0);
                                                ii.putExtra("Activity", "BUZZER");
                                                ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(ii);
                                                break;
                                            case 1://nfc
                                                if(isNfcSupported()) {
                                                    Intent nfcIntent = new Intent(DashboardActivity.this, NFCCodeReaderActivity.class);
                                                    nfcIntent.putExtra("BUZZER", "NFC");
                                                    startActivity(nfcIntent);
                                                }
                                                break;
                                            case 2://qr code
                                                Intent qrIntent= new Intent(DashboardActivity.this, CaptureActivity.class);
                                                qrIntent.putExtra("BUZZER","QR");
                                                startActivity(qrIntent);
                                                break;
                                        }

                                    } else {
                                        stepCounterPref.edit().putInt(Constants.STEP_COUNTER_BUZZ_COUNTER, 0).apply();
                                        System.out.println("steps difference is non zero");
                                    }

                                    //System.out.println("=========================================================steps reset BuzzCounter: "+stepCounterPref.getInt(Constants.STEP_COUNTER_BUZZ_COUNTER,0));
                                    /*long sDate = CommonFunctions.getParse24HrsDate(stepCounterPref.getString(Constants.STEP_COUNTER_START_DATE, CommonFunctions.getFromToDate1()) + " " + stepCounterPref.getString(Constants.STEP_COUNTER_START_TIME, "00:00:00"));
                                    long eDate = CommonFunctions.getParse24HrsDate(stepCounterPref.getString(Constants.STEP_COUNTER_END_DATE, CommonFunctions.getFromToDate1()) + " " + stepCounterPref.getString(Constants.STEP_COUNTER_END_TIME, "23:59:09"));
                                    long curTime = System.currentTimeMillis();

                                    if (curTime >= sDate && curTime <= eDate) {
                                        if (stepCounterPref.getLong(Constants.STEP_COUNTER_COUNT, 0) - stepCounterPref.getLong(Constants.STEP_COUNTER_LAST_COUNT, 0) == 0) {
                                            stepsCountLogDAO.deletRecords();
                                            int captcType=Integer.parseInt(sharedPreferences.getString(Constants.SETTING_CAPTCHA_TYPE,"0"));
                                            switch (captcType)
                                            {
                                                case 0://captcha
                                                    Intent ii = new Intent(DashboardActivity.this, StepBuzzerDialogActivity.class);
                                                    ii.putExtra("Allow", 0);
                                                    ii.putExtra("Activity", "BUZZER");
                                                    ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(ii);
                                                    break;
                                                case 1://nfc
                                                    if(isNfcSupported()) {
                                                        Intent nfcIntent = new Intent(DashboardActivity.this, NFCCodeReaderActivity.class);
                                                        nfcIntent.putExtra("BUZZER", "NFC");
                                                        startActivity(nfcIntent);
                                                    }
                                                    break;
                                                case 2://qr code
                                                    Intent qrIntent= new Intent(DashboardActivity.this,CaptureActivity.class);
                                                    qrIntent.putExtra("BUZZER","QR");
                                                    startActivity(qrIntent);
                                                    break;
                                            }

                                        } else {
                                            stepCounterPref.edit().putInt(Constants.STEP_COUNTER_BUZZ_COUNTER, 0).apply();
                                            System.out.println("steps difference is non zero");
                                        }
                                    }
                                    else {
                                        System.out.println("Time slot elapsed");
                                    }*/
                                }


                                EventLogInsertion eventLogInsertion = new EventLogInsertion(DashboardActivity.this);
                                eventLogInsertion.addStepCountEvent("STEPCOUNT", "Stepcount", "Event Type");

                                 /*if(stepCounterPref.getLong(Constants.STEP_COUNTER_COUNT,0) > stepCounterPref.getLong(Constants.STEP_COUNTER_LAST_COUNT,0)) {
                                     EventLogInsertion eventLogInsertion = new EventLogInsertion(DashboardActivity.this);
                                     eventLogInsertion.addStepCountEvent("STEPCOUNT", "Stepcount", "Event Type");
                                     //eventLogInsertion.addStepCountEvent("TRACKING", "Tracking", "Event Type");
                                 }*/

                            }

                            private void startActivity(Intent nfcIntent) {
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
    }

    private boolean isNfcSupported()
    {
        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            return true;
        }
        return false;
    }

    /*public void getCallDetails() {

        StringBuffer sb = null;
        Cursor managedCursor = null;
        try {
            sb = new StringBuffer();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
                int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
                int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
                int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
                int nLable=managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int via_number=managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                int via_name=managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME);
                sb.append( "Call Details :");
                while ( managedCursor.moveToNext() ) {
                    String phNumber = managedCursor.getString( number );
                    String callType = managedCursor.getString( type );
                    String callDate = managedCursor.getString( date );
                    //Date callDayTime = new Date(Long.valueOf(callDate));
                    String callDuration = managedCursor.getString( duration );
                    //String numberLable=managedCursor.getString(nLable);
                    //String viaNumber=managedCursor.getString(via_number);
                    //String viaName=managedCursor.getString(via_name);
                    String dir = null;
                    int dircode = Integer.parseInt( callType );
                    switch( dircode ) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            break;

                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            break;

                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            break;
                    }
                    sb.append("|"+phNumber+"|"+dir+"|"+callDate+"|"+callDuration+"|\n");
                    CommonFunctions.writeCallLog(sb.toString());
                *//*sb.append( "\nPhone Number:--- "+phNumber +" \nCall Type:--- "+dir+" \nCall Date:--- "+callDayTime+" " +
                        "\nCall duration in sec :--- "+callDuration +" \n AccountID: "+viaNumber+" \n Account name: "+viaName+" \n Label: "+numberLable);
                sb.append("\n----------------------------------");
                    System.out.println("Call Log: "+sb.toString());*//*
                }

                return;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {

        }


    }*/

}
