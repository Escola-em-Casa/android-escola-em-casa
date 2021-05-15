package org.cordova.quasar.corona.app;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationBarActivity extends AppCompatActivity {
    BottomNavigationView navigationView;
    public void setupNavigationBar(int navigation, int selected_item){
        navigationView = findViewById(navigation);
        navigationView.setSelectedItemId(selected_item);
        navigationView.setOnNavigationItemSelectedListener(
                item -> {
                    if(notSelectedYet(selected_item, item)) {
                        switch (item.getItemId()) {
                            case R.id.classroom:
                                System.out.println("Classroom "+R.id.classroom);
                                startActivity(new Intent(getApplicationContext(), ClassroomActivity.class)
                                        .putExtra("url", "https://classroom.google.com/a/estudante.se.df.gov.br"));
                                navigationView.getMenu().getItem(0).setChecked(true);
                                break;
                            case R.id.wikipedia:
                                startActivity(new Intent(getApplicationContext(), WikipediaActivity.class)
                                        .putExtra("url", "https://pt.wikipedia.org/"));
                                navigationView.getMenu().getItem(1).setChecked(true);
                                break;
                            case R.id.questions:
                                startActivity(new Intent(getApplicationContext(), QuestionsActivity.class));
                                navigationView.getMenu().getItem(2).setChecked(true);
                                break;
                            case R.id.about:
                                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                                navigationView.getMenu().getItem(3).setChecked(true);
                                break;
                        }
                        overridePendingTransition(0, 0);
                        return true;
                    }
                    return false;
                }
        );
    }

    private boolean notSelectedYet(int selected_item, android.view.MenuItem item) {
        return item.getItemId() != selected_item;
    }
}
