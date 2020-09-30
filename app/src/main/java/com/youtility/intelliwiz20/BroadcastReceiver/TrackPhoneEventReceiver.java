package com.youtility.intelliwiz20.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.EventLogInsertion;

public class TrackPhoneEventReceiver extends BroadcastReceiver {
    private Context context;
    private String eventMessge="";
    private String eventValue="";
    private EventLogInsertion eventLogInsertion;

    //deviceid, eventvalue, gpslocation, accuracy, altitude, batterylevel, signalstrength, availextmemory, availintmemory,
    // cdtz, mdtz, isdeleted, cuser, eventtype, muser, peoplecode, signalbandwidth
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        eventLogInsertion=new EventLogInsertion(context);

        /*if(intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED))
        {
            boolean isEnabled = Settings.System.getInt(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0) == 1;
            if(isEnabled==true) {
                eventMessge = context.getResources().getString(R.string.phoneevent_airplaneon);
                eventValue="AIRPLANEMODEON";
            }
            else {
                eventMessge = context.getResources().getString(R.string.phoneevent_airplaneoff);
                eventValue="AIRPLANEMODEOFF ";
            }
        }
        else */
        if(intent.getAction().equals(Intent.ACTION_SHUTDOWN))
        {
            eventMessge=context.getResources().getString(R.string.phoneevent_phoneoff);
            eventValue="PHONESWITCHEDOFF";
        }
        else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            eventMessge=context.getResources().getString(R.string.phoneevent_phoneon);
            eventValue="PHONESWITCHEDON";
        }
        else if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW))
        {
            eventMessge=context.getResources().getString(R.string.phoneevent_batterylow);
            eventValue="BATTERYLOW";
        }
        /*else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            final NetworkInfo networkInfo =intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if((networkInfo.getType() == ConnectivityManager.TYPE_MOBILE))
            {
                if ((networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) && networkInfo.isConnected()) {
                    eventMessge = context.getResources().getString(R.string.phoneevent_mobiledataenable);
                    eventValue="MOBILEDATAENABLE";
                }

                if (!networkInfo.isConnected() && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                    eventMessge = context.getResources().getString(R.string.phoneevent_mobiledatadisable);
                    eventValue="MOBILEDATADISABLE";
                }
            }
            if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && networkInfo.isConnected())
            {
                eventMessge=context.getResources().getString(R.string.phoneevent_wifienable);
                eventValue="WIFIENABLE";
            }
            else if (!networkInfo.isConnected() && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                eventMessge = context.getResources().getString(R.string.phoneevent_wifidisable);
                eventValue="WIFIDISABLE";
            }
        }*/
        else if(intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
        {
            LocationManager manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE );
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            System.out.println("statusOfGPS: "+statusOfGPS);

            if(statusOfGPS)
            {
                eventMessge = context.getResources().getString(R.string.phoneevent_gpson);
                eventValue="GPSSWITCHEDON";
            }
            else
            {
                eventMessge = context.getResources().getString(R.string.phoneevent_gpsoff);
                eventValue="GPSSWITCHEDOFF";
            }
        }

        /*System.out.println("eventMessge: "+eventMessge);
        Toast.makeText(context,eventMessge,  Toast.LENGTH_SHORT).show();*/

        if(eventMessge!=null && eventValue!=null)
            eventLogInsertion.addDeviceEvent(eventValue,eventMessge,"Event Type");

    }
}
