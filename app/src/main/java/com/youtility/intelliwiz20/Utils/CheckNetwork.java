package com.youtility.intelliwiz20.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by PrashantD on 04/08/17.
 *
 * Check user's mobile or device having internet connection available or not
 */

public class CheckNetwork {
    private Context context;
    private ConnectivityManager connectivityManager;

    public CheckNetwork(Context context)
    {
        this.context=context;
    }


    /*public boolean isNetworkConnectionAvailable()
    {
        connectivityManager=((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return (connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected());
    }*/

    public boolean isNetworkConnectionAvailable()
    {
        connectivityManager=((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if(connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected())
        {
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            {
                return true;
            }
            else return false;
        }
        return false;
    }
}
