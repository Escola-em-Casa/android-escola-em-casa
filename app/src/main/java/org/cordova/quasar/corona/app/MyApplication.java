package org.cordova.quasar.corona.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.datami.smi.SdState;
import com.datami.smi.SdStateChangeListener;
import com.datami.smi.SmiResult;
import com.datami.smi.SmiVpnSdk;
import com.datami.smi.internal.MessagingType;

public class MyApplication extends Application implements SdStateChangeListener {
    private static final String TAG = MainActivity.class.getName();
    public static SdState sdState;
    private Toast toast;
    private Context context;
    private int duration;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        duration = Toast.LENGTH_LONG;
        String mySdkKey = "ak-ec97df48-57ce-4311-bf98-cde273ad0329"; //Use the SDK API access key given by Datami.
        SmiVpnSdk.initSponsoredData(mySdkKey, this, R.drawable.ic_launcher_background, MessagingType.NONE);
    }

    @Override
    public void onChange(SmiResult currentSmiResult) {
        sdState = currentSmiResult.getSdState();
        Log.d(TAG, "sponsored data state : " + sdState);
        CharSequence text = "";
        if (sdState == SdState.SD_AVAILABLE) {
            text = "Seu acesso a esse site é gratuito.";
        } else if (sdState == SdState.SD_NOT_AVAILABLE) {
            text = "Seu acesso a esse site poderá acarretar cobranças em seu plano de dados.";
            Log.d(TAG, " - reason: " + currentSmiResult.getSdReason());
        } else if (sdState == SdState.WIFI) {
            // device is in wifi
            text = "Acesso via wifi.";
            Log.d(TAG, "wifi - reason: " + currentSmiResult.getSdReason());
        }
    }
}
