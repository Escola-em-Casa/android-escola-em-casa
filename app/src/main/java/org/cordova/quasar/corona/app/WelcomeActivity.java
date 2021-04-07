package org.cordova.quasar.corona.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;

public class WelcomeActivity extends AppCompatActivity {
    WebView webView;
    Button button;
    CheckBox checkbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        String url = "https://escolaemcasa.se.df.gov.br/index.php/politica-de-privacidade/";
        webView = (WebView) findViewById(R.id.web_view);
        webView.loadUrl(url);

        button = (Button) findViewById(R.id.button);
        checkbox = (CheckBox) findViewById(R.id.checkBox);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                        .putExtra("url", "https://classroom.google.com/a/estudante.se.df.gov.br"));
                overridePendingTransition(0, 0);
            }
        });

    }
    @SuppressLint("ResourceAsColor")
    public void itemClicked(View v) {
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()){
            button.setEnabled(true);
            button.setBackgroundColor(Color.rgb(32,146,229));
        }else{
            button.setEnabled(false);
            button.setBackgroundColor(Color.GRAY);
        }
    }
}