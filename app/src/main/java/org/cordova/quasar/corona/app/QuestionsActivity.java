package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

import android.content.SharedPreferences;

public class QuestionsActivity extends AppCompatActivity {
  private String question_tutorial = "Esta aba serve para acessar as dúvidas frequentes.\n\nCaso não consiga solucionar sua dúvida, verifique a aba 'sobre', no link, 'Escola em Casa DF (site)'.";
    
    private void checkFirstRun() {
      final String PREFS_NAME = "questions_first_run";
      final String PREF_VERSION_CODE_KEY = "1.0";
      final int DOESNT_EXIST = -1;

      // Get current version code
      int currentVersionCode = BuildConfig.VERSION_CODE;

      // Get saved version code
      SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
      int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

      // Check for first run or upgrade
      if (currentVersionCode == savedVersionCode) {

          // This is just a normal run
          return;

      } else if (savedVersionCode == DOESNT_EXIST) {
        new GuideView.Builder(this)
            .setTitle("Dúvidas Frequentes")
            .setContentText(question_tutorial)
            .setDismissType(DismissType.anywhere)
            .setTargetView(findViewById(R.id.questions))
            .setContentTextSize(14)
            .setTitleTextSize(16)
            .build()
            .show();
      } else if (currentVersionCode > savedVersionCode) {

          // TODO This is an upgrade
      }

      // Update the shared preferences with the current version code
      prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

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
                            startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                                    .putExtra("url", "https://pt.wikipedia.org/"));
                            overridePendingTransition(0, 0);
                            navigationView.getMenu().getItem(1).setChecked(true);
                            return true;
                        case R.id.questions:
                            if (navigationView.getSelectedItemId() == R.id.questions) {
                              return true;
                            }
                            startActivity(new Intent(getApplicationContext(), QuestionsActivity.class));
                            overridePendingTransition(0, 0);

                            navigationView.getMenu().getItem(2).setChecked(true);

                            return true;
                        case R.id.about:
                            if (navigationView.getSelectedItemId() == R.id.about) {
                              return true;
                            }
                            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                            overridePendingTransition(0, 0);
                            navigationView.getMenu().getItem(3).setChecked(true);
                            return true;
                    }
                    return false;
                }
        );

        checkFirstRun();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.getMenu().getItem(2).setChecked(true);
    }
}
