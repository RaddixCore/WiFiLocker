package com.raddixcore.android.wifilocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

public class ConnectivityReceiver extends BroadcastReceiver {


    WifiManager wifiManager;

    @Override
    public void onReceive(final Context context, Intent intent) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        boolean prevWifiStatus = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("wifiStatus", false);
        if(!prevWifiStatus && wifiManager.isWifiEnabled()){
            //wifi turned on
            Intent i = new Intent(context, LockActivity.class);
            i.putExtra("prevState", false);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if(prevWifiStatus && !wifiManager.isWifiEnabled()){
            //wifi turned off
            Intent i = new Intent(context, LockActivity.class);
            i.putExtra("prevState", true);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}