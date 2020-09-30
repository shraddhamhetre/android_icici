package com.youtility.intelliwiz20.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.Model.ResponseClientUrlData;
import com.youtility.intelliwiz20.Model.UploadServerSelectionParam;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.EncryptionDescryptionData;
import com.youtility.intelliwiz20.Utils.RetrofitClientURL;
import com.youtility.intelliwiz20.Utils.RetrofitServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerSelectionActivity extends Activity implements View.OnClickListener {
    private EditText domainNameEdittext;
    private Button serverNextButton;
    private SharedPreferences applicationMainPref;
    private SharedPreferences loginPref;

    private ImageView imageView;
    private String serverName=null;
    SharedPreferences sharedPreferences;
    Context context = ServerSelectionActivity.this;

    boolean isAppInstalled = false;
    EncryptionDescryptionData encryptionDescryptionData;
    //final String secretKey = "MyCoNameMyDOB#11";
    final String secretKey = "MyCoNameMyDOB#";
    int []imageArray={R.drawable.youtility_logo,R.drawable.splashscreen1,R.drawable.youtility};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_selection);

        System.out.println("on Create server");

        System.out.println("called onCreate");
        applicationMainPref=getSharedPreferences(Constants.APPLICATION_MAIN_PREF,MODE_PRIVATE);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        System.out.println("login check==="+ loginPref.getBoolean(Constants.IS_LOGIN_DONE,false));

        if(loginPref.getBoolean(Constants.IS_LOGIN_DONE,false) == true){
            System.out.println("logged in "+applicationMainPref.getString(Constants.APPLICATION_IMAGE_SERVER_URL,""));

        }else {

            SharedPreferences.Editor editor1 = applicationMainPref.edit();
            editor1.clear();
            editor1.apply();
            editor1.commit();
        }


        domainNameEdittext=(EditText)findViewById(R.id.domainNameEdittext);
        serverNextButton=(Button)findViewById(R.id.serverNextButton);
        serverNextButton.setOnClickListener(this);
        imageView=(ImageView)findViewById(R.id.imageView);

        System.out.println("data url"+applicationMainPref.getString(Constants.APPLICATION_DATA_SERVER_URL,""));
        getAccess();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.INTERNET
                    },0);
        }
        else
        {
            if(applicationMainPref.getString(Constants.APPLICATION_DATA_SERVER_URL,"").trim().length()!=0 &&
                    applicationMainPref.getString(Constants.APPLICATION_IMAGE_SERVER_URL,"").trim().length()!=0)
            {

                System.out.println("on splashscreen");
                CommonFunctions.setServerNameFromResponse(ServerSelectionActivity.this,applicationMainPref.getString(Constants.APPLICATION_IMAGE_SERVER_URL,"").trim());
                Intent ii = new Intent(ServerSelectionActivity.this, SplashScreenActivity.class);
                startActivity(ii);
                finish();
            }
        }

        //addShortcutSOSIconOnHomeScreen();
        /*sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isAppInstalled = sharedPreferences.getBoolean("isAppInstalled", false);

        if (!isAppInstalled) {
            //createPinnedShortcut();
            if (Build.VERSION.SDK_INT <= 26) {
                System.out.println("Build.VERSION.SDK_INT-"+Build.VERSION.SDK_INT);
                System.out.println("Build.VERSION_CODES.DONUT-"+Build.VERSION_CODES.DONUT);
            }else {
                createPinnedShortcut();
            }
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putBoolean("isAppInstalled", true);
            editor1.apply();
        }*/



        /*new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(domainNameEdittext, "Client Code",
                                getString(R.string.serverselection_provideclientcode))
                                .cancelable(false).transparentTarget(true).outerCircleColor(R.color.button_background).targetRadius(50),
                        TapTarget.forView(serverNextButton, "Save",
                                "Save client code")
                                .cancelable(false).transparentTarget(true).outerCircleColor(R.color.button_background).targetRadius(50)
                ).start();*/


        /*final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i=0;
            public void run() {
                imageView.setImageResource(imageArray[i]);
                i++;
                if(i>imageArray.length-1)
                {
                    i=0;
                }
                handler.postDelayed(this, 3000);  //for interval...
            }
        };
        handler.postDelayed(runnable, 2000); //for initial delay..*/


    /*loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);
    encryptionDescryptionData=new EncryptionDescryptionData();*/

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },0);
        }
        else
        {
            String ss=CommonFunctions.readDomainName();
            System.out.println("SS: "+ss);

            if(ss!=null && ss.length()>0)
            {
                try {
                    serverName=EncryptionDescryptionData.decrypt(ss);
                    System.out.println("Domain Name: "+serverName);
                    if(serverName!=null && serverName.length()>0)
                    {
                        domainNameEdittext.setText(serverName);
                        CommonFunctions.setServerName(serverName);
                        loginPref.edit().putString(Constants.SERVER_NAME, serverName).apply();
                        Intent ii = new Intent(ServerSelectionActivity.this, SplashScreenActivity.class);
                        startActivity(ii);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }*/



    }

    private void getAccess() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isAppInstalled = sharedPreferences.getBoolean("isAppInstalled", false);
        System.out.println("pinned called"+isAppInstalled);

        if (isAppInstalled) {
            //createPinnedShortcut();
            if (Build.VERSION.SDK_INT <= 26) {
                System.out.println("Build.VERSION.SDK_INT-"+Build.VERSION.SDK_INT);
                System.out.println("Build.VERSION_CODES.DONUT-"+Build.VERSION_CODES.DONUT);
            }else {
                createPinnedShortcut();
                System.out.println("Build.VERSION.SDK_INT----"+Build.VERSION.SDK_INT);

            }
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putBoolean("isAppInstalled", true);
            editor1.apply();
        }
    }

    private void addShortcutSOSIconOnHomeScreen()
    {
        //shortcutPref.edit().putBoolean(ConstantVariables.IS_SHORTCUT_CREATED, true).commit();

        Intent shortcutIntent = new Intent(getApplicationContext(), SOSActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        //addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.shortcut_panic));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "SOS");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(getApplicationContext(),R.drawable.sos));

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }


    @SuppressLint("NewApi")
    private void createPinnedShortcut() {

        System.out.println("pinned");
        //ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        ShortcutManager shortcutManager = (ShortcutManager)getSystemService(SHORTCUT_SERVICE);



        if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()) {
            ShortcutInfo pinShortcutInfo =
                    new ShortcutInfo.Builder(context, "my-shortcut").build();

            Intent pinnedShortcutCallbackIntent =
                    shortcutManager.createShortcutResultIntent(pinShortcutInfo);

            pinnedShortcutCallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


            PendingIntent successCallback = PendingIntent.getBroadcast(
                    context,
                    0,
                    pinnedShortcutCallbackIntent,
                    0);

            shortcutManager.requestPinShortcut(pinShortcutInfo,
                    successCallback.getIntentSender());
        }

        //getDefaultSmsApp();
    }

    private void getDefaultSmsApp() {


        Intent setSmsAppIntent;
        setSmsAppIntent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        setSmsAppIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                getPackageName());
        startActivityForResult(setSmsAppIntent, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==0)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                if(applicationMainPref.getString(Constants.APPLICATION_DATA_SERVER_URL,"").trim().length()!=0 &&
                        applicationMainPref.getString(Constants.APPLICATION_IMAGE_SERVER_URL,"").trim().length()!=0)
                {
                    Intent ii = new Intent(ServerSelectionActivity.this, SplashScreenActivity.class);
                    startActivity(ii);
                    finish();
                }
            }

            /*if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED)
            {
                if(CommonFunctions.readDomainName()!=null && CommonFunctions.readDomainName().length()>0)
                {
                    try {
                        serverName=EncryptionDescryptionData.decrypt(CommonFunctions.readDomainName());
                        if(serverName!=null && serverName.length()>0)
                        {
                            CommonFunctions.setServerName(serverName);
                            loginPref.edit().putString(Constants.SERVER_NAME, serverName).apply();
                            Intent ii = new Intent(ServerSelectionActivity.this, SplashScreenActivity.class);
                            startActivity(ii);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }*/
        }
    }

    @Override
    public void onClick(View v) {

        /*if(domainNameEdittext.getText().toString().trim().length()>0)
        {

            final ProgressDialog progressDialog = new ProgressDialog(ServerSelectionActivity.this);
            progressDialog.setCancelable(false); // set cancelable to false
            progressDialog.setMessage(getResources().getString(R.string.please_wait)); // set message
            progressDialog.show(); // show progress dialog

            ERP erp=new ERP();
            erp.setBu_name(domainNameEdittext.getText().toString().trim());

            ArrayList<ERP>erpArrayList=new ArrayList<>();
            erpArrayList.add(erp);

            Gson gson = new Gson();
            String upData = gson.toJson(erpArrayList);
            System.out.println("Domain Name upData: "+upData);
            CommonFunctions.UploadLog("\n <Server URL > \n"+upData+"\n");

            RetrofitServices retrofitServices= RetrofitClientURL.getClient().create(RetrofitServices.class);
            *//*String ss="[{'bu_name': '"+domainNameEdittext.getText().toString().trim()+"'}]";
            System.out.println("ss: "+gson.toJson(ss));*//*

            Call<ResponseClientUrlData> call=retrofitServices.getNotice(upData);
            call.enqueue(new Callback<ResponseClientUrlData>() {
                @Override
                public void onResponse(Call<ResponseClientUrlData> call, Response<ResponseClientUrlData> response) {
                    progressDialog.dismiss();
                    if(response.isSuccessful() && response.body()!=null)
                    {
                        System.out.println("response.body().getRc(): "+response.body().getRc());
                        if(response.body().getRc()==0)
                        {

                        }
                        else
                        {
                            Snackbar.make(domainNameEdittext,getResources().getString(R.string.serverselection_notablefetchinfo),Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Snackbar.make(domainNameEdittext,getResources().getString(R.string.serverselection_notablefetchinfo),Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseClientUrlData> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        }
        else
        {
            Snackbar.make(domainNameEdittext,getResources().getString(R.string.serverselection_provideclientcode),Snackbar.LENGTH_LONG).show();
        }*/

        if(domainNameEdittext.getText().toString().trim().length()>0)
        {

            final ProgressDialog progressDialog = new ProgressDialog(ServerSelectionActivity.this);
            progressDialog.setCancelable(false); // set cancelable to false
            progressDialog.setMessage(getResources().getString(R.string.please_wait)); // set message
            progressDialog.show(); // show progress dialog

            UploadServerSelectionParam uploadParameters=new UploadServerSelectionParam();
            uploadParameters.setServicename(Constants.SERVICE_CLIENTURL);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setClientcode(domainNameEdittext.getText().toString().trim());

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("Domain Name upData: "+upData);
            CommonFunctions.UploadLog("\n <Server URL > \n"+upData+"\n");

            RetrofitServices retrofitServices= RetrofitClientURL.getClient().create(RetrofitServices.class);
            Call<ResponseClientUrlData> call=retrofitServices.getServerUrl(Constants.SERVICE_CLIENTURL,uploadParameters);
            call.enqueue(new Callback<ResponseClientUrlData>() {
                @Override
                public void onResponse(Call<ResponseClientUrlData> call, Response<ResponseClientUrlData> response) {
                    progressDialog.dismiss();
                    if(response.isSuccessful() && response.body()!=null)
                    {
                        System.out.println("response.body().getRc(): "+response.body().getRc());
                        System.out.println("response.body().getClienturl(): "+response.body().getClienturl());

                        if(response.body().getRc()==0)
                        {

                            //applicationMainPref.edit().putString(Constants.APPLICATION_DATA_SERVER_URL,"").apply();
                            //applicationMainPref.edit().putString(Constants.APPLICATION_IMAGE_SERVER_URL,"").apply();
                            applicationMainPref.edit().putString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE, domainNameEdittext.getText().toString().trim()).apply();
                            CommonFunctions.setServerNameFromResponse(ServerSelectionActivity.this,response.body().getClienturl().toString().trim());
                            Intent ii = new Intent(ServerSelectionActivity.this, SplashScreenActivity.class);
                            startActivity(ii);
                            finish();
                        }
                        else
                        {
                            Snackbar.make(domainNameEdittext,getResources().getString(R.string.serverselection_notablefetchinfo),Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Snackbar.make(domainNameEdittext,getResources().getString(R.string.serverselection_notablefetchinfo),Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseClientUrlData> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        }
        else
        {
            Snackbar.make(domainNameEdittext,getResources().getString(R.string.serverselection_provideclientcode),Snackbar.LENGTH_LONG).show();
        }

    }
}
