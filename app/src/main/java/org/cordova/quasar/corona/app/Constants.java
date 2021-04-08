package org.cordova.quasar.corona.app;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class Constants {

    public static boolean checkInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo!=null && networkInfo.isConnected();
        }

        Network[] networks = connectivityManager.getAllNetworks();
        boolean hasInternet = false;
        if(networks.length>0){
            for(Network network :networks){
                NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(network);
                if(nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) hasInternet = true;
            }
        }
        return hasInternet;
    }
}