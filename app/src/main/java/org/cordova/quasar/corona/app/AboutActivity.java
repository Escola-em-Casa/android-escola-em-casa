package org.cordova.quasar.corona.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setSelectedItemId(R.id.about);
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.classroom: {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                overridePendingTransition(0, 0);
                                return true;
                            }
                            case R.id.wikipedia: {
                                startActivity(new Intent(getApplicationContext(), WikipediaActivity.class));
                                overridePendingTransition(0, 0);
                                return true;
                            }
                            case R.id.about:
                                return true;
                        }
                        return false;
                    }
                }
        );
    }
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.getMenu().getItem(2).setChecked(true);
    }

}