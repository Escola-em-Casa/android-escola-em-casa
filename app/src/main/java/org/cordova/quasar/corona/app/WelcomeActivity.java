package org.cordova.quasar.corona.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WelcomeActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        String url = "https://escolaemcasa.se.df.gov.br/index.php/politica-de-privacidade/";
        webView = (WebView) findViewById(R.id.web_view);
        webView.loadUrl(url);
    }
}