package com.youtility.intelliwiz20.Services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

/**
 * Created by youtility on 31/8/18.
 */

public class BackgroundSoundService extends Service {

    private MediaPlayer player;
    TextToSpeech textToSpeech;

    @Override
    public void onCreate() {
        super.onCreate();
        //player=MediaPlayer.create(getApplicationContext(), R.raw.camera);
        /*textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1.5f);
            }
        });*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int key=intent.getExtras().getInt("key");

        //player=MediaPlayer.create(getApplicationContext(), R.raw.camera);
        System.out.println("player3 "+ player);
        if(key == 101){
            player=MediaPlayer.create(getApplicationContext(), R.raw.camera);

        }else if(key == 102){


			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View v=inflater.inflate(R.layout.custom_toast_layout, null);
            TextView qrresult= (TextView) v.findViewById(R.id.qrresult);
            TextView punchTimestamp=(TextView)v.findViewById(R.id.timestamp);
            SharedPreferences qrPref=getSharedPreferences(Constants.SITE_ATTENDANCE_PREF, MODE_PRIVATE);
            qrresult.setText("ID No : " +qrPref.getString(Constants.SITE_ATTENDANCE_QR_RESULT, "000000"));
            punchTimestamp.setText(CommonFunctions.getFormatedDate(System.currentTimeMillis()));
            qrPref.edit().putString(Constants.SITE_ATTENDANCE_QR_RESULT, "").commit();
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);

			/*if(land){
				//v.setRotation(90f);
				v.setRotationX(90f);
			}*/

            toast.setView(v);
            toast.show();

			/*Toast myToast = new Toast(getApplicationContext());
			// Creating our custom text view, and setting text/rotation

			// Setting duration and displaying the toast
			myToast.setDuration(Toast.LENGTH_SHORT);
			myToast.show();*/

            //player=MediaPlayer.create(getApplicationContext(), R.raw.attendance);
            System.out.println("Scan Name: "+qrPref.getString(Constants.SITE_ATTENDANCE_QR_RESULT_NAME,""));
            //textToSpeech.speak(getResources().getString(R.string.attendance_registered)+qrPref.getString(Constants.SITE_ATTENDANCE_QR_RESULT_NAME,""),TextToSpeech.QUEUE_FLUSH,null);
            //textToSpeech.speak(getResources().getString(R.string.attendance_registered), TextToSpeech.QUEUE_FLUSH,null);
        }

        if(player!=null) {
            player.setLooping(false);
            player.setVolume(100, 100);
            player.start();
        }
        else
        {
            System.out.println("key:"+key);
            player=MediaPlayer.create(getApplicationContext(),R.raw.camera);
            player.setLooping(false);
            player.setVolume(100, 100);
            player.start();
        }

        System.out.println("Service Start");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
