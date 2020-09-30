package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.youtility.intelliwiz20.Adapters.ApplicationGridViewAdapter;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.Arrays;
import java.util.List;

public class ApplicationAccessViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ActionBar actionBar;
    private TextView loginUserNameTV, loginUserDesignationTV, loginUserDefaultSiteTV;
    private GridView applicationGrivView;
    private List<String> applicationList;
    private ApplicationGridViewAdapter appGridViewAdapter;
    private SharedPreferences applicationPref;
    private SharedPreferences loginPref;
    private SharedPreferences siteRelatedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_access_view);
        actionBar=getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.applist_actionbar_title));

        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        //siteRelatedPref=getSharedPreferences(Constants.SITE_RELATED_PREF, MODE_PRIVATE);
        applicationPref=getSharedPreferences(Constants.APPLICATION_PREF, Context.MODE_PRIVATE);

        if(applicationPref.getBoolean(Constants.IS_APPLICATION_SELECTED,false))
        {
            Intent dashBoardIntent=new Intent(ApplicationAccessViewActivity.this, DashboardActivity.class);
            dashBoardIntent.putExtra("AppName", applicationPref.getString(Constants.APPLICATION_SELECTED_NAME,getResources().getString(R.string.guardtour)));
            startActivity(dashBoardIntent);
            finish();
        }

        componentInitialise();

        applicationList= Arrays.asList(getResources().getStringArray(R.array.appName_array));
        appGridViewAdapter=new ApplicationGridViewAdapter(ApplicationAccessViewActivity.this, applicationList);
        applicationGrivView.setAdapter(appGridViewAdapter);
        applicationGrivView.setOnItemClickListener(this);


    }

    private void componentInitialise()
    {
        loginUserNameTV=(TextView)findViewById(R.id.userName);
        loginUserDesignationTV=(TextView)findViewById(R.id.userDesgination);
        loginUserDefaultSiteTV=(TextView)findViewById(R.id.userSiteName);
        applicationGrivView=(GridView)findViewById(R.id.gridView);

        loginUserNameTV.setText(loginPref.getString(Constants.LOGIN_USER_ID,""));
        //loginUserDefaultSiteTV.setText(siteRelatedPref.getString(Constants.SITE_CODE,""));

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent dashBoardIntent=new Intent(ApplicationAccessViewActivity.this, DashboardActivity.class);
        dashBoardIntent.putExtra("AppName", applicationList.get(position));
        System.out.println("AppName: "+ applicationList.get(position));
        applicationPref.edit().putString(Constants.APPLICATION_SELECTED_NAME, applicationList.get(position)).commit();
        applicationPref.edit().putBoolean(Constants.IS_APPLICATION_SELECTED,true).commit();
        startActivity(dashBoardIntent);
        finish();
    }
}
