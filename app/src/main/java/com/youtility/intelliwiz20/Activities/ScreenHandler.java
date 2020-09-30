package com.youtility.intelliwiz20.Activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenHandler extends BroadcastReceiver {

    public static boolean screenOff;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
        }
        System.out.println("ScreenHandler.screenOff: "+screenOff);
        Intent i = new Intent(context, DashboardActivity.class);
        i.putExtra("screen_state", screenOff);
        context.startService(i);
    }
}