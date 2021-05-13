package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SearchView;

import android.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class QuestionsActivity extends AppCompatActivity {
     List<Questions> questionsList = new ArrayList<>();
     RecAdapter adapter = new RecAdapter(questionsList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

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

        RecyclerView recyclerView = findViewById(R.id.list);

        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setSelectedItemId(R.id.questions);

        EditText editText = findViewById(R.id.edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

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
    }

    private void filter(String text) {
        ArrayList<Questions> filteredList = new ArrayList<>();

        for (Questions q : questionsList) {
            if (q.getQuestion().toLowerCase().contains(text.toLowerCase())
                    || q.getAnswer().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(q);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.getMenu().getItem(2).setChecked(true);
    }
}
