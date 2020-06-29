package org.cordova.quasar.corona.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class MyApplication extends Application {
    private static final String TAG = MainActivity.class.getName();
    private Toast toast;
    private Context context;
    private int duration;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        duration = Toast.LENGTH_LONG;
    }
}
