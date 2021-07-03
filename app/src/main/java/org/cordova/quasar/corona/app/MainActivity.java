package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000; // 1000 = 1s

    // Method to send request each second
    private void startHandler(){
        handler.postDelayed(runnable = () -> {
            handler.postDelayed(runnable, delay);
            //send httpRequest here
            Log.w("Request", "1 sec"); // log msg to test loop
        }, delay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                .putExtra("url", "https://classroom.google.com/a/estudante.se.df.gov.br"));
        overridePendingTransition(0, 0);
        startHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler
    }
}