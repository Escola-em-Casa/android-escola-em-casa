package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    String CLASSROOM = "https://classroom.google.com/a/estudante.se.df.gov.br";
    int enterAnim = 0;
    int exitAnim = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                .putExtra("url", CLASSROOM));
        overridePendingTransition(enterAnim, exitAnim);
    }
}