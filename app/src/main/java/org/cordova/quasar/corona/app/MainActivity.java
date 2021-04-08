package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants constants = new Constants();
        boolean hasInternet = constants.checkInternet(getApplicationContext());
        if(hasInternet) {
            startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                    .putExtra("url", "https://classroom.google.com/a/estudante.se.df.gov.br"));
            overridePendingTransition(0, 0);
        }else{
            startActivity(new Intent(getApplicationContext(), NoInternetActivity.class));
            overridePendingTransition(0, 0);
        }
    }
}