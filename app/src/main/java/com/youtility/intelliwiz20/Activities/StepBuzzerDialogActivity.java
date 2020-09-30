package com.youtility.intelliwiz20.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Services.GetStepCounterService;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.EventLogInsertion;

import java.util.Random;

public class StepBuzzerDialogActivity extends Activity implements View.OnClickListener {
    private TextView dialog_header;
    private TextView dialog_event_name;
    private TextView dialog_event_time;
    private EditText captchaEditText;
    private Button dialog_ok_button;
    private String intentMessge=null;
    private String[] eventMsg;
    private String fromActivity=null;
    private Vibrator vibrator;
    private Ringtone ringtone;
    SharedPreferences sharedPreferences;
    private String alertType="0";
    private JobNeed jobNeed;
    private TypeAssistDAO typeAssistDAO;

    private PowerManager mPowerManager;
    private WindowManager mWindowManager;
    private PowerManager.WakeLock mWakeLock;

    private SharedPreferences loginPref;
    private String generatedCaptchaString=null;
    private SharedPreferences stepCounterPref;

    private final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";


    private String getRandomString(int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD );
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);


        setContentView(R.layout.activity_stepbuzzer_dialog);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(StepBuzzerDialogActivity.this);
        alertType=sharedPreferences.getString(Constants.SETTING_ALERT_TYPE,"0");
        stepCounterPref=getSharedPreferences(Constants.STEP_COUNTER_PREF, MODE_PRIVATE);

        typeAssistDAO=new TypeAssistDAO(StepBuzzerDialogActivity.this);

        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);

        dialog_header=(TextView)findViewById(R.id.dialogHeader);
        dialog_event_name=(TextView)findViewById(R.id.dialog_event_name);
        dialog_event_time=(TextView)findViewById(R.id.dialog_event_captcha);
        dialog_ok_button=(Button)findViewById(R.id.dialog_ok_button);
        captchaEditText=(EditText)findViewById(R.id.captchaEditText);

        dialog_ok_button.setOnClickListener(this);
        dialog_event_name.setOnClickListener(this);

        Intent getVal=getIntent();
        fromActivity=getVal.getStringExtra("Activity");
        dialog_header.setText(getResources().getString(R.string.buzzer_dialog_title));

        //dialog_event_name.setText(eventMsg[0]);
        generatedCaptchaString=getRandomString(4);
        dialog_event_time.setText(generatedCaptchaString);

        switch(Integer.parseInt(alertType))
        {
            case 0:
                playSoundAndVibrate();
                break;
            case 1:
                playSound();
                break;
            case 2:
                playVibrate();
                break;
        }

        try {
            mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.getDefaultDisplay();

            // Create a bright wake lock
            mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());

            mWakeLock.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onRestart() {
        System.out.println("buzzer restart");
        super.onRestart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.dialog_ok_button:
                if(loginPref.getBoolean(Constants.IS_LOGIN_DONE,false)) {
                    if (fromActivity.equalsIgnoreCase("BUZZER")) {
                        if(captchaEditText.getText().toString().trim().length()>0 )
                        {
                            if(generatedCaptchaString.equalsIgnoreCase(captchaEditText.getText().toString().trim())) {
                                if (mWakeLock != null)
                                    mWakeLock.release();

                                if (vibrator != null)
                                    vibrator.cancel();
                                if (ringtone != null)
                                    ringtone.stop();

                                /*EventLogInsertion eventLogInsertion = new EventLogInsertion(StepBuzzerDialogActivity.this);
                                eventLogInsertion.addBuzzerStepCountEvent("CAPTCHA", "Captcha", "Event Type", captchaEditText.getText().toString().trim());*/
                                Intent stepCountService = new Intent(StepBuzzerDialogActivity.this, GetStepCounterService.class);
                                startService(stepCountService);
                                finish();
                            }
                            else
                            {
                                captchaEditText.setError(getResources().getString(R.string.buzzer_error_captchanotmatched));
                            }
                        }
                        else
                            captchaEditText.setError(getResources().getString(R.string.buzzer_error_captchanotentered));
                    }
                }
                break;
            case R.id.dialog_event_name:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        generatedCaptchaString=getRandomString(4);
                        dialog_event_time.setText(generatedCaptchaString);
                    }
                });
                break;
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        /*if(mWakeLock!=null)
            mWakeLock.release();

        if(vibrator!=null)
            vibrator.cancel();
        if(ringtone!=null)
            ringtone.stop();*/
        //finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mWakeLock.release();
        /*if(vibrator!=null)
            vibrator.cancel();
        if(ringtone!=null)
            ringtone.stop();

        finish();*/
    }

    private void playSound()
    {
        Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(StepBuzzerDialogActivity.this, alarm);
        ringtone.play();
    }

    private void playSoundAndVibrate()
    {
        playSound();
        playVibrate();
    }

    private void playVibrate()
    {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = { 0, 500, 500 };
        vibrator.vibrate(pattern, 0);
    }

    @Override
    protected void onResume() {
        System.out.println("buzzer onresume");
        super.onResume();
        //mWakeLock.acquire();
    }

    @Override
    protected void onStop() {
        System.out.println("buzzer onstop");
        int val=-1;
        EventLogInsertion eventLogInsertion=new EventLogInsertion(StepBuzzerDialogActivity.this);
        String captValue=captchaEditText.getText().toString().trim();
        if(captValue!=null && captValue.length()>0)
            val=eventLogInsertion.editBuzzerStepCountEvent(stepCounterPref.getLong(Constants.STEP_COUNTER_ID,-1),captchaEditText.getText().toString().trim());
        else
            val=eventLogInsertion.editBuzzerStepCountEvent(stepCounterPref.getLong(Constants.STEP_COUNTER_ID,-1),"SKIP CAPTCHA CODE");

        System.out.println("Deviceeventlog updated value: "+val);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.out.println("buzzer ondestory");
        super.onDestroy();
    }
}
