package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(getApplicationContext(), ClassroomActivity.class)
                .putExtra("url", "https://classroom.google.com/a/estudante.se.df.gov.br"));
        overridePendingTransition(0, 0);
    }
}