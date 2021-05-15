package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.os.Bundle;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class QuestionsActivity extends NavigationBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_questions);
        super.setupNavigationBar(R.id.navigation, R.id.questions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.getMenu().getItem(2).setChecked(true);
    }
    public void StartActivity(){
        startActivity(new Intent(getApplicationContext(), this.getClass()));
    }
}