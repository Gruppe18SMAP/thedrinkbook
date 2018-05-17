package com.example.thedrinkbook;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * Created with inspiration from material on BlackBoard (WeatherServiceDemo)
 */

// Class to check for network status (is the device connected or not)
public class NetworkChecker {

    // Looks at the currently active network
    public static String getNetworkStatus(Context c){
        ConnectivityManager connectMan = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectMan.getActiveNetworkInfo();
        // Check that there is an active network and connected to it
        if (netInfo != null && netInfo.isConnected()){
            // The device's connected, but we don't see the need to inform the user - ergo, return null
            return null;
        } else{
            // The device's NOT connected - we need to inform the user!
            return c.getResources().getString(R.string.no_connection);
        }

    }
}
