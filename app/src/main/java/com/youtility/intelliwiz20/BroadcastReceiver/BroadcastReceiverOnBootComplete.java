package com.youtility.intelliwiz20.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.youtility.intelliwiz20.Activities.ServerSelectionActivity;

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            System.out.println("OnBootStartupReceiver");
            Intent loginIntent = new Intent(context, ServerSelectionActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loginIntent.putExtra("FROM", 1);
            this.context.startActivity(loginIntent);
        }
    }


}
