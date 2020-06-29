package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                .putExtra("url", "http://classroom.google.com/a/edu.se.df.gov.br"));
        overridePendingTransition(0, 0);
    }
}