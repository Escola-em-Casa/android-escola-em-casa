package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

import android.content.SharedPreferences;

public class AboutActivity extends AppCompatActivity {
  private String about_tutorial = "Esta aba serve para acessar informações do aplicativo.\n\nAqui se encontram alguns links importantes, caso tenha dúvidas de como acessar o ambiente de sala de aula, basta clicar no link 'Como acessar o Google Sala de Aula', caso tenha dúvida de como utilizar o aplicativo, clique no botão 'Tutorial interativo'";  
    
    private void checkFirstRun() {
      final String PREFS_NAME = "about_first_run";
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
            .setTitle("Sobre")
            .setContentText(about_tutorial)
            .setDismissType(DismissType.anywhere)
            .setTargetView(findViewById(R.id.about))
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

        setContentView(R.layout.activity_about);

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setSelectedItemId(R.id.about);

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

    public void myOnClick(View view) {
        String link = view.getResources().getResourceEntryName(view.getId());

        switch (link) {
            case "escola_em_casa_btn":
                startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                        .putExtra("url", "https://escolaemcasa.se.df.gov.br/"));
                overridePendingTransition(0, 0);
                break;
            case "como_acessar_btn":
                startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                        .putExtra("url",
                                "https://escolaemcasa.se.df.gov.br/index.php/como-acessar/"));
                overridePendingTransition(0, 0);
                break;
            case "secretaria_site_btn":
                startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                        .putExtra("url", "http://www.se.df.gov.br/"));
                overridePendingTransition(0, 0);
                break;
            case "tutorial_btn":
                getSharedPreferences("classroom_first_run", MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences("wikipedia_first_run", MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences("questions_first_run", MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences("about_first_run", MODE_PRIVATE).edit().clear().apply();
                startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                        .putExtra("url", "https://classroom.google.com/a/estudante.se.df.gov.br"));
                overridePendingTransition(0, 0);
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.getMenu().getItem(3).setChecked(true);
    }
}