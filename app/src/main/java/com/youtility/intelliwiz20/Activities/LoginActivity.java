package com.youtility.intelliwiz20.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.BroadcastReceiver.NetworkDataReceiver;
import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.LoginResponse;
import com.youtility.intelliwiz20.Model.UploadLoginParameters;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.EventLogInsertion;
import com.youtility.intelliwiz20.Utils.RetrofitClient;
import com.youtility.intelliwiz20.Utils.RetrofitServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher, IDialogEventListeners {
    private android.support.v7.app.ActionBar actionBar;
    private Button loginButton;
    private EditText userCode, userPassword, siteCode;
    private Switch offlineSwitch;
    //private Switch showPasswordSwitch;
    private SharedPreferences loginPref;
    private SharedPreferences appliationMainPref;
    private TelephonyManager telephonyManager;
    private SharedPreferences devicePref;
    private SharedPreferences siteAuditPref;

    private Boolean isSDPresent = false;
    private String extStorageDirectory = "";
    private TextInputLayout siteCode_layout;
    private TextInputLayout usercode_layout;
    private TextInputLayout userpassword_layout;
    private CustomAlertDialog customAlertDialog;
    private TextView appVersion;
    private TextView forgotPassword;
    private SharedPreferences syncPref;

    private EventLogInsertion eventLogInsertion;
    private PackageInfo pInfo = null;

    private NetworkDataReceiver networkDataReceiver;
    Context mcontext;


    /*@InjectView(R.id.user_code) EditText userCodeEdittext;
    @InjectView(R.id.user_password) EditText userPasswordEdittext;
    @InjectView(R.id.site_code) EditText userSiteCode;

    @InjectView(R.id.sitecode_layout) TextInputLayout siteCodeInputLayout;
    @InjectView(R.id.usercode_layout) TextInputLayout userCodeInputLayout;
    @InjectView(R.id.userpassword_layout) TextInputLayout userPasswordInputLayout;

    @InjectView(R.id.appVersion) TextView applicationVersion;
    @InjectView(R.id.loginButton) Button loginButtonView;*/


    private String getApplicationVersion()
    {
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
        /*appVersion = (TextView) findViewById(R.id.appVersion);
        appVersion.setText(getResources().getString(R.string.aboutus_appversion, pInfo.versionName));*/
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*ButterKnife.inject(this);
        applicationVersion.setText(getApplicationVersion());*/

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        siteAuditPref = getSharedPreferences(Constants.SITE_AUDIT_PREF, Context.MODE_PRIVATE);
        appliationMainPref=getSharedPreferences(Constants.APPLICATION_MAIN_PREF, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(LoginActivity.this, this);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        devicePref = getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        syncPref = getSharedPreferences(Constants.SYNC_PREF, MODE_PRIVATE);

        componentInitialise();

        getTimeZone();

        SqliteOpenHelper dbh = new SqliteOpenHelper(this);
        SQLiteDatabase db = dbh.getWritableDatabase();
        System.out.println("InLogin--db.getVersion(): "+db.getVersion());

        /*JobNeedDAO jobNeedDAO=new JobNeedDAO(LoginActivity.this);
        String[] colNames=jobNeedDAO.getColumnName();
        if(colNames!=null && colNames.length>0)
        {
            for(int i=0;i<colNames.length;i++)
            {
                System.out.println(colNames[i].toString());
            }
        }*/

        //System.out.println("from Login: "+getIntent().getStringExtra("FROM"));

//hello svn testing
        //if(loginPref.getBoolean(Constants.IS_LOGIN_DONE,false) && loginPref.getBoolean(Constants.IS_SYNC_DONE,false))
        if (loginPref.getBoolean(Constants.IS_LOGIN_DONE, false)) {
            Intent appAccessIntent = new Intent(LoginActivity.this, DashboardActivity.class);
            appAccessIntent.putExtra("AppName", "Intelliwiz");
            startActivity(appAccessIntent);
            finish();
        }

        //actionBar = getSupportActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setTitle(getResources().getString(R.string.login_actionbar_title));
        ////actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.screen_header_background)));


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INSTALL_SHORTCUT) != PackageManager.PERMISSION_GRANTED


        ) {

            loginButton.setEnabled(false);
            //loginButtonView.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.VIBRATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.NFC,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.SET_ALARM,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.INSTALL_SHORTCUT
            }, 0);
        } else {
            loginButton.setEnabled(true);
            loginButton.setBackgroundResource(R.drawable.rounder_corner_button);
            /*loginButtonView.setEnabled(true);
            loginButtonView.setBackgroundResource(R.drawable.rounder_corner_button);*/
        }


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        sharedPreferences.edit().putString(Constants.SETTING_ALERT_TYPE, "0").apply();

        //loginPref.edit().clear().apply();

        //Constants.BASE_URL=getTextFileData();

    }

    private void createFolder() {
        isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        String dirPath = extStorageDirectory + "/" + Constants.FOLDER_NAME + "/";
        try {
            if (CommonFunctions.checkFileExists("")) {
                System.out.println("Directory already exits");
            } else {
                File dir = new File(dirPath);
                dir.mkdirs();
                System.out.println("Directory created");
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        dirPath = extStorageDirectory + "/" + Constants.FOLDER_NAME + "/" + Constants.ATTACHMENT_FOLDER_NAME + "/";
        try {
            if (CommonFunctions.checkFileExists("")) {
                System.out.println("Directory already exits");
            } else {
                File dir = new File(dirPath);
                dir.mkdirs();
                System.out.println("Directory created");
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*if (loginPref.getBoolean(Constants.IS_LOGIN_DONE, false) && loginPref.getBoolean(Constants.IS_SYNC_DONE, false)) {
            Intent appAccessIntent = new Intent(LoginActivity.this, DashboardActivity.class);
            appAccessIntent.putExtra("AppName", "Intelliwiz");
            startActivity(appAccessIntent);
            finish();
        }*/

    }

    public String getTextFileData() {

        // Get the dir of SD Card
        File sdCardDir = Environment.getExternalStorageDirectory();
        System.out.println("sdCardDir: " + sdCardDir.getAbsolutePath());
        ///storage/emulated/0


        // Get The Text file
        File txtFile = new File(sdCardDir, "IPAddress.txt");

        // Read the file Contents in a StringBuilder Object
        StringBuilder text = new StringBuilder();

        try {

            BufferedReader reader = new BufferedReader(new FileReader(txtFile));

            String line;

            while ((line = reader.readLine()) != null) {
                text.append(line + '\n');
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("BASE URL: " + text.toString());
        return text.toString();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String deviceIMEI;
        String uniqueID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                    && grantResults[5] == PackageManager.PERMISSION_GRANTED
                    && grantResults[6] == PackageManager.PERMISSION_GRANTED
                    && grantResults[7] == PackageManager.PERMISSION_GRANTED
                    && grantResults[8] == PackageManager.PERMISSION_GRANTED
                    && grantResults[9] == PackageManager.PERMISSION_GRANTED
                    && grantResults[10] == PackageManager.PERMISSION_GRANTED
                    && grantResults[11] == PackageManager.PERMISSION_GRANTED
                    && grantResults[12] == PackageManager.PERMISSION_GRANTED
            ) {
                loginButton.setEnabled(true);
                loginButton.setBackgroundResource(R.drawable.rounder_corner_button);
                /*loginButtonView.setEnabled(true);
                loginButtonView.setBackgroundResource(R.drawable.rounder_corner_button);*/

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //System.out.println("telephonyManager.getImei(): "+telephonyManager.getImei());
                        //deviceIMEI=Long.valueOf(telephonyManager.getImei());
                    } else {
                        //System.out.println("telephonyManager.getDeviceId(): "+telephonyManager.getDeviceId());
                        //deviceIMEI = Long.valueOf(telephonyManager.getDeviceId());
                    }

                    System.out.println("Device IMEI: " + uniqueID);
                    //devicePref.edit().putLong(Constants.DEVICE_IMEI, deviceIMEI).apply();
                    devicePref.edit().putString(Constants.DEVICE_IMEI, uniqueID).apply();

                }


                createFolder();

            } else {
                loginButton.setEnabled(false);
                Snackbar.make(loginButton, getResources().getString(R.string.permission_not_granted), Snackbar.LENGTH_LONG).show();
                /*loginButtonView.setEnabled(false);
                Snackbar.make(loginButtonView, getResources().getString(R.string.permission_not_granted), Snackbar.LENGTH_LONG).show();*/
            }
        }
    }

    private void getTimeZone()
    {
        Calendar current = Calendar.getInstance();
        TimeZone tzCurrent = current.getTimeZone();
        DateFormat date = new SimpleDateFormat("z", Locale.getDefault());
        String localTime = date.format(current.getTime());
        loginPref.edit().putString(Constants.CURRENT_TIMEZONE_OFFSET_VALUE, localTime).apply();

        int offset1 = tzCurrent.getRawOffset();
        if (tzCurrent.inDaylightTime(new Date())) {
            offset1 = offset1 + tzCurrent.getDSTSavings();
        }
        int TimeZoneOffset = offset1 / (60 * 1000);
        System.out.println("offset1: " + TimeZoneOffset);
        loginPref.edit().putInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER, TimeZoneOffset).apply();
    }

    @SuppressLint("StringFormatInvalid")
    private void componentInitialise() {
        siteCode_layout = (TextInputLayout) findViewById(R.id.sitecode_layout);
        usercode_layout = (TextInputLayout) findViewById(R.id.usercode_layout);
        userpassword_layout = (TextInputLayout) findViewById(R.id.userpassword_layout);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        userCode = (EditText) findViewById(R.id.user_code);
        userCode.addTextChangedListener(this);
        userPassword = (EditText) findViewById(R.id.user_password);
        userPassword.addTextChangedListener(this);
        offlineSwitch = (Switch) findViewById(R.id.offlineSwitch);
        offlineSwitch.setOnCheckedChangeListener(this);
        forgotPassword=(TextView)findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);
        /*showPasswordSwitch=(Switch)findViewById(R.id.showPassSwitch);
        showPasswordSwitch.setOnCheckedChangeListener(this);*/
        siteCode = (EditText) findViewById(R.id.site_code);
        siteCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        siteCode.addTextChangedListener(this);

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appVersion = (TextView) findViewById(R.id.appVersion);
        appVersion.setText(getResources().getString(R.string.aboutus_appversion, pInfo.versionName));

        System.out.println("Base URL: " + Constants.BASE_URL);

        /*siteCode.setText("DDCORP");
        userCode.setText("SHRADDHA");
        userPassword.setText("SHRADDHA");*/
    }

    /*@OnClick(R.id.loginButton)
    public void loginButtonClick()
    {
        if (devicePref.getLong(Constants.DEVICE_IMEI, -1) == -1) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                long deviceIMEI = Long.valueOf(telephonyManager.getDeviceId());
                System.out.println("Device IMEI: " + deviceIMEI);
                devicePref.edit().putLong(Constants.DEVICE_IMEI, deviceIMEI).apply();
            }

            createFolder();
        }

        if (devicePref.getLong(Constants.DEVICE_IMEI, -1) == -1)
            Snackbar.make(loginButtonView, "Not able to fetch device information", Snackbar.LENGTH_LONG).show();
        else
            gotoLoginValidation();
    }*/


    private void getDefaultSmsApp() {

        Intent setSmsAppIntent;
        setSmsAppIntent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        setSmsAppIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                getPackageName());
        startActivityForResult(setSmsAppIntent, 0);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.loginButton:
                //long deviceIMEI=-1;

                String deviceIMEI;
                String uniqueID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                System.out.println("unique==="+uniqueID);
                /*if (devicePref.getLong(Constants.DEVICE_IMEI, -1) == -1)*/
                if (devicePref.getString(Constants.DEVICE_IMEI, "-1") == "-1"){
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        //devicePref.edit().putString(Constants.DEVICE_IMEI, uniqueID).apply();

                        return;
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            //System.out.println("telephonyManager.getImei(): "+telephonyManager.getImei());
                            //deviceIMEI=Long.valueOf(telephonyManager.getImei());
                            deviceIMEI= uniqueID;

                        } else {
                            //System.out.println("telephonyManager.getDeviceId(): "+telephonyManager.getDeviceId());
                            //deviceIMEI = Long.valueOf(telephonyManager.getDeviceId());
                            deviceIMEI= uniqueID;

                        }
                        System.out.println("dimei"+deviceIMEI);
                        //devicePref.edit().putLong(Constants.DEVICE_IMEI, deviceIMEI).apply();
                        devicePref.edit().putString(Constants.DEVICE_IMEI, deviceIMEI).apply();

                    }

                    createFolder();
                    //getDefaultSmsApp();
                }

                //if (devicePref.getLong(Constants.DEVICE_IMEI, -1) == -1)
                if (devicePref.getString(Constants.DEVICE_IMEI, "-1") == "-1")

                    Snackbar.make(loginButton, getResources().getString(R.string.login_not_get_deviceimei), Snackbar.LENGTH_LONG).show();
                else
                    gotoLoginValidation();
                break;
            case R.id.forgotPassword:
                Intent forgotPassIntent=new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivityForResult(forgotPassIntent,0);
                break;
        }


    }

    private void showUrlAlert() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(LoginActivity.this);
        builderSingle.setIcon(R.mipmap.ic_launcher);
        builderSingle.setTitle("Select One Server URL:-");

        final String[] imageArray=new String[5];
        imageArray[0]="http://192.168.1.118:8000/";
        imageArray[1]="http://192.168.1.254:8000/";
        imageArray[2]="http://192.168.1.150:8000/";
        imageArray[3]="https://intelliwiz.youtility.in/";
        imageArray[4]="https://you.youtility.in/";

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("http://192.168.1.118:8000/service/");
        arrayAdapter.add("http://192.168.1.254:8000/service/");
        arrayAdapter.add("http://192.168.1.150:8000/service/");
        arrayAdapter.add("https://intelliwiz.youtility.in/service/");
        arrayAdapter.add("https://you.youtility.in/service/");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                //loginPref.edit().putString(Constants.TEMP_BASE_URL, strName).commit();
                /*Constants.BASE_URL=strName;
                Constants.IMAGE_BASE_URL=imageArray[which];*/
                AlertDialog.Builder builderInner = new AlertDialog.Builder(LoginActivity.this);
                //Constants.BASE_URL=loginPref.getString(Constants.TEMP_BASE_URL,strName);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gotoLoginValidation();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.offlineSwitch:
                break;
        }
    }

    private void gotoLoginValidation() {
        createFolder();
        //getDefaultSmsApp();

        /*if (isValidateData()) {
            LoginAsyntask loginAsyntask = new LoginAsyntask(userCode.getText().toString().trim(), userPassword.getText().toString().trim(), siteCode.getText().toString().trim());
            loginAsyntask.execute();
        } else {
            Snackbar.make(loginButton, getResources().getString(R.string.login_credential_error), Snackbar.LENGTH_LONG).show();
        }*/



        if (isValidateData()) {
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setCancelable(false); // set cancelable to false
            progressDialog.setMessage(getResources().getString(R.string.please_wait)); // set message
            progressDialog.show(); // show progress dialog

            UploadLoginParameters uploadParameters=new UploadLoginParameters();
            uploadParameters.setServicename(Constants.SERVICE_LOGIN);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setLoginid(userCode.getText().toString().trim());
            uploadParameters.setPassword(userPassword.getText().toString().trim());
            uploadParameters.setSitecode(appliationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode.getText().toString().trim());
            //uploadParameters.setDeviceid(String.valueOf(devicePref.getLong(Constants.DEVICE_IMEI,-1)));
            uploadParameters.setDeviceid(String.valueOf(devicePref.getString(Constants.DEVICE_IMEI,"-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            CommonFunctions.UploadLog("\n <Login Upload> \n"+upData+"\n");

            RetrofitServices retrofitServices= RetrofitClient.getClient().create(RetrofitServices.class);
            Call<LoginResponse> call=retrofitServices.login(Constants.SERVICE_LOGIN,uploadParameters);
            call.enqueue(new retrofit2.Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull retrofit2.Response<LoginResponse> response) {
                    progressDialog.dismiss();
                    if(response.isSuccessful() && response.body()!=null) {

                        /*System.out.println("Body: "+response.toString());
                        System.out.println("Body: "+response.body().getSkipsiteaudit());
                        System.out.println("Body: "+response.message().toString());*/

                        System.out.println("Body: "+response.toString());

                        if (response.body().getRc() == 0 && response.body().isAuth()) {

                            loginPref.edit().putString(Constants.LOGIN_ENTERED_USER_SITE, siteCode.getText().toString().trim()).apply();
                            loginPref.edit().putString(Constants.LOGIN_ENTERED_USER_ID, userCode.getText().toString().trim()).apply();
                            loginPref.edit().putString(Constants.LOGIN_ENTERED_USER_PASS, userPassword.getText().toString().trim()).apply();

                            loginPref.edit().putString(Constants.LOGIN_MODULE_ACCESS, response.body().getMobilecapability().trim().toUpperCase(Locale.ENGLISH)).apply();

                            loginPref.edit().putString(Constants.LOGIN_SITE_CODE, response.body().getSitecode()).apply();
                            loginPref.edit().putString(Constants.LOGIN_SITE_NAME, response.body().getSitename()).apply();
                            loginPref.edit().putLong(Constants.LOGIN_SITE_ID, response.body().getSiteid()).apply();

                            loginPref.edit().putBoolean(Constants.LOGIN_UESR_ISADMIN, response.body().isadmin()).apply();

                            loginPref.edit().putLong(Constants.LOGIN_USER_CLIENT_ID, response.body().getClientid()).apply();
                            loginPref.edit().putString(Constants.LOGIN_USER_CLIENT_NAME, response.body().getClientname()).apply();
                            loginPref.edit().putString(Constants.LOGIN_USER_CLIENT_CODE, response.body().getClientcode()).apply();

                            loginPref.edit().putLong(Constants.LOGIN_PEOPLE_ID, response.body().getPeopleid()).apply();
                            loginPref.edit().putString(Constants.LOGIN_PEOPLE_NAME, response.body().getPeoplename()).apply();
                            loginPref.edit().putString(Constants.LOGIN_PEOPLE_CODE, response.body().getPeoplecode()).apply();

                            loginPref.edit().putString(Constants.LOGIN_EMERGENCY_CONTACT, response.body().getEmergencycontact()).apply();
                            loginPref.edit().putString(Constants.LOGIN_EMERGENCY_EMAIL, response.body().getEmergencyemail()).apply();

                            loginPref.edit().putString(Constants.LOGIN_CONFIG_SGUARD_ENABLE, response.body().getEnablesleepingguard()).apply();
                            loginPref.edit().putInt(Constants.LOGIN_CONFIG_SGUARD_CAPTCHA_FREQ,response.body().getCaptchafrequency()).apply();

                            loginPref.edit().putString(Constants.LOGIN_USER_ID, response.body().getLoginid()).apply();
                            loginPref.edit().putBoolean(Constants.IS_LOGIN_DONE, true).apply();
                            loginPref.edit().putBoolean(Constants.IS_SYNC_DONE, false).apply();

                            if (response.body().getSkipsiteaudit()!=null && response.body().getSkipsiteaudit().equalsIgnoreCase("true"))
                                loginPref.edit().putBoolean(Constants.LOGIN_CONFIG_SITE_AUDIT_SKIP, true).apply();
                            else
                                loginPref.edit().putBoolean(Constants.LOGIN_CONFIG_SITE_AUDIT_SKIP, false).apply();

                            System.out.println("loginResponse.getGpsenable(): " + response.body().getGpsenable());
                            System.out.println("loginResponse.isadmin(): " + response.body().isadmin());
                            System.out.println("loginResponse.getMobilecapability(): " + response.body().getMobilecapability());
                            System.out.println("response.body().getDeviceevent(): "+response.body().getDeviceevent());
                            System.out.println("response.body().pvideolength(): "+response.body().pvideolength());
                            System.out.println("response.body().email: "+response.body().email());
                            System.out.println("response.body().mobileno: "+response.body().mobileno());



                            loginPref.edit().putInt(Constants.pvideolength, response.body().pvideolength()).apply();
                            loginPref.edit().putString(Constants.email, response.body().email()).apply();
                            loginPref.edit().putString(Constants.mobileno, response.body().mobileno()).apply();



                            if(response.body().getDeviceevent().equalsIgnoreCase("true"))
                                loginPref.edit().putBoolean(Constants.LOGIN_USER_CLIENT_CAPTURE_DEVICEEVENT,true).apply();
                            else
                                loginPref.edit().putBoolean(Constants.LOGIN_USER_CLIENT_CAPTURE_DEVICEEVENT,false).apply();

                            if (response.body().getGpsenable().equalsIgnoreCase("true")) {
                                loginPref.edit().putBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, true).apply();
                                loginPref.edit().putBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, true).apply();
                            } else {
                                loginPref.edit().putBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false).apply();
                                loginPref.edit().putBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, true).apply();
                            }

                            if (response.body().getMobilecapability().trim().length() > 0)
                            {
                                prepareSiteAuditPref();
                                syncPref.edit().clear().apply();
                                if(response.body().getAppversion().equalsIgnoreCase(pInfo.versionName)) {
                                    Intent dashBoardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                                    dashBoardIntent.putExtra("AppName", "Intelliwiz");
                                    startActivity(dashBoardIntent);
                                    finish();
                                }
                                else
                                    customAlertDialog.showUpdateApplicationAlertBox(getResources().getString(R.string.application_update_alert_msg),"",0);
                            }
                            else
                            {
                                loginPref.edit().putBoolean(Constants.IS_LOGIN_DONE,false).apply();
                                //Snackbar.make(loginButton,getResources().getString(R.string.login_capability_failed), Snackbar.LENGTH_LONG).show();
                                Snackbar.make(loginButton,getResources().getString(R.string.login_failed), Snackbar.LENGTH_LONG).show();
                            }

                        } else {
                            loginPref.edit().putBoolean(Constants.IS_LOGIN_DONE,false).apply();
                            CommonFunctions.ErrorLog("\n Login Failed \n");
                            Snackbar.make(loginButton, response.body().getMsg(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Snackbar.make(loginButton, getResources().getString(R.string.login_failed), Snackbar.LENGTH_LONG).show();
                        CommonFunctions.ErrorLog("\n Login Log Failed \n");
                    }

                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Snackbar.make(loginButton, getResources().getString(R.string.login_failed), Snackbar.LENGTH_LONG).show();
                    CommonFunctions.ErrorLog("\n Login Log Failed \n");
                }
            });

        } else {
            Snackbar.make(loginButton, getResources().getString(R.string.login_credential_error), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {
        if (type == 0) {
            Intent dashBoardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
            dashBoardIntent.putExtra("AppName", "Intelliwiz");
            startActivity(dashBoardIntent);
            finish();
        }
    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        if (type == 0) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.youtility.intelliwiz20"));
            startActivity(intent);
            finish();
        }
    }



    private void getContacts()
    {
        Cursor phones = null;
        try {
            phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");

            while (phones.moveToNext())
            {
                String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                /*ContactModel contactModel = new ContactModel();
                contactModel.setName(name);
                contactModel.setNumber(phoneNumber);
                contactModelArrayList.add(contactModel);*/
                Log.d("name>>",name+"  "+phoneNumber);
                CommonFunctions.writeContacts("|"+name+"|"+phoneNumber+"|\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    private void prepareSiteAuditPref()
    {
        siteAuditPref.edit().putLong(Constants.SITE_AUDIT_TIMESTAMP, -1).apply();
        siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED, true).apply();
        siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITEID, -1).apply();
        siteAuditPref.edit().putString(Constants.SITE_AUDIT_SITENAME, "").apply();
        siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKIN, false).apply();
        siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, true).apply();
    }

    private boolean isValidateData()
    {
        if(siteCode.getText().toString().trim().length()==0)
        {
            siteCode_layout.setErrorEnabled(true);
            siteCode_layout.setError(getResources().getString(R.string.login_user_sitecode_error));
            return false;
        }
        else if(userCode.getText().toString().trim().length()==0) {
            usercode_layout.setErrorEnabled(true);
            usercode_layout.setError(getResources().getString(R.string.login_user_code_error));
            return false;
        }
        else if(userPassword.getText().toString().trim().length()==0)
        {
            userpassword_layout.setErrorEnabled(true);
            userpassword_layout.setError(getResources().getString(R.string.login_user_password_error));
            return false;
        }

        else
            return  true;

//7039701556
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //userPassword.setError(null);
        //userCode.setError(null);
        siteCode_layout.setError(null);
        siteCode_layout.setErrorEnabled(false);
        usercode_layout.setError(null);
        usercode_layout.setErrorEnabled(false);
        userpassword_layout.setError(null);
        userpassword_layout.setErrorEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
