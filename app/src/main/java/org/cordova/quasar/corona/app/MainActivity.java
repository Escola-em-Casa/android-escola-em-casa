package org.cordova.quasar.corona.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static FirstRun first_run;

    public static FirstRun getFirstRun() {
        return first_run;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        first_run = new FirstRun(getApplicationContext());
        if(first_run.isFirstTime()){
            startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
            overridePendingTransition(0, 0);
        }else{
            startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                    .putExtra("url", "https://classroom.google.com/a/estudante.se.df.gov.br"));
            overridePendingTransition(0, 0);
        }
    }
}
class FirstRun
{
    SharedPreferences sharedPreferences;
    public FirstRun(Context context)
    {
        sharedPreferences = context.getSharedPreferences("myAppData", 0);
    }

    public boolean isFirstTime()
    {
        return sharedPreferences.getBoolean("first", true);
    }

    public void setFirstTime(boolean b)
    {
        sharedPreferences.edit().putBoolean("first", b).commit();
    }

}