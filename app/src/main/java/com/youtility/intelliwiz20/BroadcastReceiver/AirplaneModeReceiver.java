package com.youtility.intelliwiz20.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.EventLogInsertion;

public class AirplaneModeReceiver extends BroadcastReceiver {
    private String eventMessge="";
    private String eventValue="";
    private EventLogInsertion eventLogInsertion;
    @Override
    public void onReceive(Context context, Intent intent) {
        eventLogInsertion=new EventLogInsertion(context);
        boolean isEnabled = Settings.System.getInt(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        if(isEnabled) {
            eventMessge = context.getResources().getString(R.string.phoneevent_airplaneon);
            eventValue="AIRPLANEMODEON";
        }
        else {
            eventMessge = context.getResources().getString(R.string.phoneevent_airplaneoff);
            eventValue="AIRPLANEMODEOFF";
        }

        System.out.println("eventMessge: "+eventMessge);
        Toast.makeText(context,eventMessge,  Toast.LENGTH_SHORT).show();

        if(eventMessge!=null && eventValue!=null)
            eventLogInsertion.addDeviceEvent(eventValue,eventMessge,"Event Type");
    }
}
