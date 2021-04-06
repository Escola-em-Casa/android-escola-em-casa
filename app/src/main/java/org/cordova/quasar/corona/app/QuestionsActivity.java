package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class QuestionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_questions);

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setSelectedItemId(R.id.questions);

        navigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.classroom:
                            startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                                    .putExtra("url", "https://classroom.google.com/a/estudante.se.df.gov.br"));
                            overridePendingTransition(0, 0);
                            navigationView.getMenu().getItem(0).setChecked(true);
                            return true;
                        case R.id.wikipedia:
                            startActivity(new Intent(getApplicationContext(), WikipediaActivity.class)
                                    .putExtra("url", "https://pt.wikipedia.org/"));
                            overridePendingTransition(0, 0);
                            navigationView.getMenu().getItem(1).setChecked(true);
                            return true;
                        case R.id.questions:
                            startActivity(new Intent(getApplicationContext(), QuestionsActivity.class));
                            overridePendingTransition(0, 0);

                            navigationView.getMenu().getItem(2).setChecked(true);

                            return true;
                        case R.id.about:
                            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                            overridePendingTransition(0, 0);
                            navigationView.getMenu().getItem(3).setChecked(true);
                            return true;
                    }
                    return false;
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.getMenu().getItem(2).setChecked(true);
    }
}
