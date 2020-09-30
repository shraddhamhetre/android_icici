package com.youtility.intelliwiz20.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    private TextView welcomeMsgTextview;
    private Handler handler;
    private SharedPreferences loginPref;
    private SharedPreferences applicationTrackerPref;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        welcomeMsgTextview=(TextView)findViewById(R.id.welComeMsgTextview);
        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        applicationTrackerPref=getSharedPreferences(Constants.APPLICATION_TRACKER_PREF, MODE_PRIVATE);

        if(applicationTrackerPref.getString(Constants.APPLICATION_TRACKER_INSTALLDATE,"").trim().length()==0)
        {
            applicationTrackerPref.edit().putString(Constants.APPLICATION_TRACKER_INSTALLDATE, CommonFunctions.getFormatedDate(System.currentTimeMillis())).apply();
        }
        applicationTrackerPref.edit().putInt(Constants.APPLICATION_TRACKER_OPENED_COUNTER,(applicationTrackerPref.getInt(Constants.APPLICATION_TRACKER_OPENED_COUNTER,0)+1)).apply();
        applicationTrackerPref.edit().putString(Constants.APPLICATION_TRACKER_LASTOPENEDDATE, CommonFunctions.getFormatedDate(System.currentTimeMillis())).apply();

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this);
        String languageCode=sharedPreferences.getString(Constants.SETTING_GENERAL_LANGUAGE,"en");
        System.out.println("languageCode: "+languageCode);

        if(loginPref.getString(Constants.PREVIOUS_SELECTED_LANGUAGE,"").trim().length()==0)
            loginPref.edit().putString(Constants.PREVIOUS_SELECTED_LANGUAGE,languageCode).apply();
        else
        {
            if(loginPref.getString(Constants.PREVIOUS_SELECTED_LANGUAGE,"").trim().equalsIgnoreCase(languageCode))
            {
                loginPref.edit().putBoolean(Constants.SELECTED_LANGUAGE_CHANGE,false).apply();
            }
            else
                loginPref.edit().putBoolean(Constants.SELECTED_LANGUAGE_CHANGE,true).apply();

        }

        changeLanguage(languageCode);

        /*if(Constants.valid==0)
        {
            SharedPreferences.Editor editor=loginPref.edit();
            editor.clear();
            editor.apply();
            Constants.valid=1;
        }*/

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        welcomeMsgTextview.startAnimation(anim);


        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent intent=new Intent(SplashScreenActivity.this,LoginActivity.class);
                Intent intent=new Intent(SplashScreenActivity.this,LoginActivity.class);

                intent.putExtra("FROM",0);
                startActivity(intent);
                finish();
            }
        },1000);

    }


    private void changeLanguage(String lang)
    {
        String seletedLan=lang;
        Locale locale = null;
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

        /*if(!seletedLan.equalsIgnoreCase("en") || !seletedLan.equalsIgnoreCase("hi"))
        {
            String[]contryCode=seletedLan.split("_");
            System.out.println("contryCode[0]: "+contryCode[0]);
            //System.out.println("contryCode[1]: "+contryCode[1]);
            if(contryCode[1].equalsIgnoreCase("CN"))
                locale=Locale.SIMPLIFIED_CHINESE;
            else if(contryCode[1].equalsIgnoreCase("TW"))
                locale=Locale.TRADITIONAL_CHINESE;
        }
        else
        {
            locale = new Locale(sharedPreferences.getString("language_type", "en"));
        }*/
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());


    }


}
