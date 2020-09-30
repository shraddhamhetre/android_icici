package com.youtility.intelliwiz20.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.EventLogInsertion;

public class NetworkDataReceiver extends BroadcastReceiver {
    private String eventMessge="";
    private String eventValue="";
    private EventLogInsertion eventLogInsertion;
    private SharedPreferences networkAvailPrevPref;
    @Override
    public void onReceive(Context context, Intent intent) {

        eventLogInsertion=new EventLogInsertion(context);
        networkAvailPrevPref=context.getSharedPreferences(Constants.NETWORK_PREF, Context.MODE_PRIVATE);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork!=null)
        {
            if((activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE))
            {
                if ((activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) && activeNetwork.isConnected()) {
                    eventMessge = context.getResources().getString(R.string.phoneevent_mobiledataenable);
                    eventValue="MOBILEDATAENABLE";
                }

                if (!activeNetwork.isConnected() && (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)) {
                    eventMessge = context.getResources().getString(R.string.phoneevent_mobiledatadisable);
                    eventValue="MOBILEDATADISABLE";
                }
            }
            if ((activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) && activeNetwork.isConnected())
            {
                eventMessge=context.getResources().getString(R.string.phoneevent_wifienable);
                eventValue="WIFIENABLE";
            }
            else if (!activeNetwork.isConnected() && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
                eventMessge = context.getResources().getString(R.string.phoneevent_wifidisable);
                eventValue="WIFIDISABLE";
            }
        }
        else
        {
            eventMessge = context.getResources().getString(R.string.phoneevent_mobiledatadisable);
            eventValue="MOBILEDATADISABLE";
        }



        //System.out.println("eventMessge: "+eventMessge);
        //Toast.makeText(context,eventMessge,  Toast.LENGTH_SHORT).show();

        /*if(eventMessge!=null && eventValue!=null) {
            if(!eventValue.equalsIgnoreCase(networkAvailPrevPref.getString(Constants.NETWORK_STATE_PREVIOUS,""))) {
                eventLogInsertion.addDeviceEvent(eventValue, eventMessge, "Event Type");
                Toast.makeText(context,eventMessge,  Toast.LENGTH_SHORT).show();
                networkAvailPrevPref.edit().putString(Constants.NETWORK_STATE_PREVIOUS, eventValue).apply();
            }
        }*/

    }
}
