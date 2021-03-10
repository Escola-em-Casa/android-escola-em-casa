package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        List<Questions> questionsList = new ArrayList<>();

        questionsList.add(new Questions("Os dados móveis de internet do aplicativo já estão sendo pagos pelo governo?", "Os dados móveis de internet já estão sendo patrocinados (pagos) pelo Governo do Distrito Federal. " +
                "No momento, é possivel fazer uso dos dados utilizando chips ativos das operadoras Claro, Tim e Vivo."));
        questionsList.add(new Questions("O governo pagará pelo uso da plataforma Google Meet?", "Não, pois só é possível utilizá-la através do seu próprio aplicativo. Os dados patrocinados são utilizados somente " +
                "dentro do aplicativo Escola em Casa DF."));
        questionsList.add(new Questions("Se eu rotear a internet do celular para o computador, os dados móveis de internet serão pagos pelo governo?", "Não serão patrocinados (pagos) os dados móveis de internet quando " +
                "roteados, ou compartilhados, com outros dispositivos e/ou computadores."));
        questionsList.add(new Questions("Como alternar entre contas?", "Para alterar entre contas, siga os seguintes passos: 1- Clique no botão superior esquerdo. " +
                "2- Após o menu lateral abrir, clique no seu email. 3- Nessa tela, você verá todas as contas acessadas no aplicativo. Caso queira adicionar alguma, clique em 'Adicionar conta' e siga as instruções."));
        questionsList.add(new Questions("Como Acessar o Google Sala de Aula?", "No menu inferior, clique na primeira opção 'Google Classroom'. Posteriormente, faça o login com um email válido." ));
        questionsList.add(new Questions("O que eu consigo acessar nesse aplicativo?", "Nesse aplicativo você terá acesso apenas às funcionalidades do Google Sala de Aula, pesquisas no Wikipédia, além de acessar aulas" +
                "disponibilizadas no Youtube (apenas com o link direto enviado pelo professor)." ));

        RecAdapter adapter = new RecAdapter(questionsList);

        RecyclerView recyclerView = findViewById(R.id.list);

        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

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

        checkFirstRun();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.getMenu().getItem(2).setChecked(true);
    }
}
