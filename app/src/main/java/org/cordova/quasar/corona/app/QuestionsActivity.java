package org.cordova.quasar.corona.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class QuestionsActivity extends AppCompatActivity {

    List<TextView> textViews = new ArrayList<>();
    List<ImageView> imageViews = new ArrayList<>();


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

        textViews.add(0,(TextView) findViewById(R.id.sponsored_data_answer));
        textViews.get(0).setVisibility(View.GONE);
        textViews.add(1,(TextView) findViewById(R.id.meet_answer));
        textViews.get(1).setVisibility(View.GONE);
        textViews.add(2,(TextView) findViewById(R.id.route_answer));
        textViews.get(2).setVisibility(View.GONE);

        imageViews.add(0, (ImageView) findViewById(R.id.sponsored_data_icon));
        imageViews.add(1, (ImageView) findViewById(R.id.meet_icon));
        imageViews.add(2, (ImageView) findViewById(R.id.route_icon));
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.getMenu().getItem(2).setChecked(true);
    }

    //TODO: refactor slide functions
    void slide(Context ctx, View v, Boolean down) {
        Animation a;
        if(down) a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        else a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public void toggle_contents(View v) {
        int id = Integer.parseInt((String) v.getTag());
        TextView answerView = textViews.get(id);


        if (answerView.isShown()) {
            slide(this, answerView, false);
            answerView.setVisibility(View.GONE);
            imageViews.get(id).setImageResource(R.drawable.baseline_expand_more_24);
        } else {
            answerView.setVisibility(View.VISIBLE);
            slide(this, answerView, true);
            imageViews.get(id).setImageResource(R.drawable.baseline_expand_less_24);
        }
    }
}
