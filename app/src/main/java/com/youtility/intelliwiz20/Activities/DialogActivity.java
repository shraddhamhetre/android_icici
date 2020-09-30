package com.youtility.intelliwiz20.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;

public class DialogActivity extends Activity implements View.OnClickListener {
    private TextView dialog_header;
    private TextView dialog_event_name;
    private TextView dialog_event_time;
    private Button dialog_ok_button, dialog_cancel_button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD );
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);


        setContentView(R.layout.activity_dialog);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(DialogActivity.this);
        alertType=sharedPreferences.getString(Constants.SETTING_ALERT_TYPE,"0");

        typeAssistDAO=new TypeAssistDAO(DialogActivity.this);

        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);

        dialog_header=(TextView)findViewById(R.id.dialogHeader);
        dialog_event_name=(TextView)findViewById(R.id.dialog_event_name);
        dialog_event_time=(TextView)findViewById(R.id.dialog_event_time);
        dialog_ok_button=(Button)findViewById(R.id.dialog_ok_button);
        dialog_cancel_button=(Button)findViewById(R.id.dialog_cancel_button);

        dialog_ok_button.setOnClickListener(this);
        dialog_cancel_button.setOnClickListener(this);

        Intent getVal=getIntent();
        fromActivity=getVal.getStringExtra("Activity");
        intentMessge=getVal.getStringExtra("EventName");
        eventMsg=intentMessge.split("~");

        if(fromActivity.equalsIgnoreCase("JOBNEED"))
        {
            dialog_header.setText(getResources().getString(R.string.job_alert_msg_title));
        }
        else if(fromActivity.equalsIgnoreCase("GEOFENCE"))
        {
            dialog_header.setText(getResources().getString(R.string.gf_alert_msg_title));
        }

        dialog_event_name.setText(eventMsg[0]);
        dialog_event_time.setText(eventMsg[1]);

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

        Bundle b=getIntent().getExtras();
        jobNeed=(JobNeed) b.getSerializable("JOB_NEED");

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
    public void onClick(View view) {
        if(mWakeLock!=null)
             mWakeLock.release();

        if(vibrator!=null)
            vibrator.cancel();
        if(ringtone!=null)
            ringtone.stop();

        switch (view.getId())
        {
            case R.id.dialog_cancel_button:
                break;
            case R.id.dialog_ok_button:
                if(loginPref.getBoolean(Constants.IS_LOGIN_DONE,false)) {
                    if (fromActivity.equalsIgnoreCase("JOBNEED")) {
                        if (jobNeed.getIdentifier() == typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TOUR, Constants.IDENTIFIER_JOBNEED)) {
                            Intent nxtActivity = new Intent(DialogActivity.this, CheckpointListActivity.class);
                            nxtActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(nxtActivity);
                        } else if (jobNeed.getIdentifier() == typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TASK, Constants.IDENTIFIER_JOBNEED)) {
                            Intent nxtActivity = new Intent(DialogActivity.this, TaskListActivity.class);
                            nxtActivity.putExtra("FROM", Constants.JOB_NEED_IDENTIFIER_TASK);
                            nxtActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(nxtActivity);
                        } else if (jobNeed.getIdentifier() == typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_PPM, Constants.IDENTIFIER_JOBNEED)) {
                            Intent nxtActivity = new Intent(DialogActivity.this, JOBListActivity.class);
                            nxtActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            nxtActivity.putExtra("FROM", Constants.JOB_NEED_IDENTIFIER_PPM);
                            startActivity(nxtActivity);
                        }
                    }
                }

                break;

        }

        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(mWakeLock!=null)
            mWakeLock.release();

        if(vibrator!=null)
            vibrator.cancel();
        if(ringtone!=null)
            ringtone.stop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mWakeLock.release();
        if(vibrator!=null)
            vibrator.cancel();
        if(ringtone!=null)
            ringtone.stop();

        finish();
    }

    private void gotoJobneedList()
    {
        if(jobNeed.getIdentifier()==typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TASK,Constants.IDENTIFIER_JOBNEED))
        {
            Intent jobNeedIntent = new Intent(DialogActivity.this, JOBListActivity.class);
            startActivity(jobNeedIntent);
        }

        finish();
    }

    private void playSound()
    {
        Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(DialogActivity.this, alarm);
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
        super.onResume();
        //mWakeLock.acquire();
    }
}
