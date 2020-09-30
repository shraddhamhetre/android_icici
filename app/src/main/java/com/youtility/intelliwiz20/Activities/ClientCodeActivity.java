package com.youtility.intelliwiz20.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Services.GetLocationService;
import com.youtility.intelliwiz20.Utils.Constants;

import java.io.File;

public class ClientCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private ActionBar actionBar;
    private Button saveButton;
    private EditText clientCodeEditText;
    private SharedPreferences siteRelatedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_code);
        actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        //siteRelatedPref=getSharedPreferences(Constants.SITE_RELATED_PREF, MODE_PRIVATE);


        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/"+ Constants.FOLDER_NAME);
        try{
            if(dir.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory existed");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        componentInitialise();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,Manifest.permission.VIBRATE)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.NFC)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM)!=PackageManager.PERMISSION_GRANTED)
        {
            saveButton.setEnabled(false);
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
                    Manifest.permission.SET_ALARM
            },0);
        }

        /*if(siteRelatedPref.getBoolean(Constants.IS_SITE_CODE_AVAILABLE, false))
        {
            Intent loginIntent=new Intent(ClientCodeActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==0)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED
                    && grantResults[2]==PackageManager.PERMISSION_GRANTED
                    && grantResults[3]==PackageManager.PERMISSION_GRANTED
                    && grantResults[4]==PackageManager.PERMISSION_GRANTED
                    && grantResults[5]==PackageManager.PERMISSION_GRANTED
                    && grantResults[6]==PackageManager.PERMISSION_GRANTED
                    && grantResults[7]==PackageManager.PERMISSION_GRANTED
                    && grantResults[8]==PackageManager.PERMISSION_GRANTED
                    && grantResults[9]==PackageManager.PERMISSION_GRANTED
                    && grantResults[10]==PackageManager.PERMISSION_GRANTED
                    && grantResults[11]==PackageManager.PERMISSION_GRANTED)
            {
                saveButton.setEnabled(true);

            }
            else
            {
                Snackbar.make(saveButton,getResources().getString(R.string.permission_not_granted), Snackbar.LENGTH_LONG).show();
            }
        }
    }


    private void componentInitialise()
    {
        saveButton=(Button)findViewById(R.id.saveButton);
        clientCodeEditText=(EditText)findViewById(R.id.client_code);
        clientCodeEditText.setText(getResources().getString(R.string.company_name));
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        //if(isValidateData())
        if(true)
        {
            //siteRelatedPref.edit().putBoolean(Constants.IS_SITE_CODE_AVAILABLE, true).commit();
            //siteRelatedPref.edit().putString(Constants.SITE_CODE, clientCodeEditText.getText().toString().trim()).commit();
            Intent loginIntent=new Intent(ClientCodeActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            Intent locationService=new Intent(ClientCodeActivity.this, GetLocationService.class);
            startService(locationService);

            /*Intent loginIntent=new Intent(ClientCodeActivity.this, LoginActivity.class);
            startActivity(loginIntent);*/
            finish();
        }
        else
        {
            //siteRelatedPref.edit().putBoolean(Constants.IS_SITE_CODE_AVAILABLE, false).commit();
            Snackbar.make(saveButton,getResources().getString(R.string.entersitecode_error), Snackbar.LENGTH_LONG).setAction("Action",null).show();
        }
    }


    private boolean isValidateData()
    {
        if(clientCodeEditText.getText().toString().trim().length()>0)
            return true;
        else
            return false;
    }


}
