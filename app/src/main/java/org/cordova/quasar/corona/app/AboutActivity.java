package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setSelectedItemId(R.id.about);

        String CLASSROOMURL = "https://classroom.google.com/a/estudante.se.df.gov.br";
        String WIKPEDIAURL = "https://pt.wikipedia.org/";

        navigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.classroom:
                            startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                                    .putExtra("url", CLASSROOMURL));
                            overridePendingTransition(0, 0);
                            navigationView.getMenu().getItem(0).setChecked(true);
                            return true;
                        case R.id.wikipedia:
                            startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                                    .putExtra("url", WIKPEDIAURL));
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

    public void myOnClick(View view) {
        String link = view.getResources().getResourceEntryName(view.getId());
        String ESCOLAEMCASAURL = "https://escolaemcasa.se.df.gov.br/";
        String ESCOLAEMCASADFURL = "https://escolaemcasa.se.df.gov.br/index.php/como-acessar/";
        String SEDF = "http://www.se.df.gov.br/";

        switch (link) {
            case "escola_em_casa_btn":
                startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                        .putExtra("url", ESCOLAEMCASAURL));
                overridePendingTransition(0, 0);
                break;
            case "como_acessar_btn":
                startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                        .putExtra("url",
                                ESCOLAEMCASADFURL));
                overridePendingTransition(0, 0);
                break;
            case "secretaria_site_btn":
                startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                        .putExtra("url", SEDF));
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