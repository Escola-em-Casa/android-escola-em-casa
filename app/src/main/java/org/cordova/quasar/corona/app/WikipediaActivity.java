package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datami.smi.SdState;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WikipediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wikipedia);

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setSelectedItemId(R.id.wikipedia);
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.classroom: {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                overridePendingTransition(0, 0);
                                return true;
                            }
                            case R.id.wikipedia:
                            case R.id.about:{
                                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                                overridePendingTransition(0, 0);
                                return true;
                            }
                        }
                        return false;
                    }
                }
        );

        WebView myWebView = (WebView) findViewById(R.id.web_view_wiki);
        myWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Locale.setDefault(new Locale("pt", "BR"));

        myWebView.loadDataWithBaseURL(null, "<script" +
                "      src=\"https://code.jquery.com/jquery-3.5.0.min.js\"" +
                "      integrity=\"sha256-xNzN2a4ltkB44Mc/Jz3pT4iU1cmeR0FkXs4pru/JxaQ=\"" +
                "      crossorigin=\"anonymous\"" +
                "    ></script>", "text/html", "utf-8", null);
        myWebView.loadUrl("https://pt.wikipedia.org/");
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl(
                    "javascript:(function f() {" +
                            "var email = document.getElementsByName('identifier');" +
                            "email[0].oninput = function(value) {" +
                            "if(!/^\\w+([\\.-]?\\w+)*(@)?((e(d(u(c(a(d(o(r)?)?)?)?)?)?)?)?|(a(l(u(n(o)?)?)?)?))?(\\.)?(e(d(u(\\.(e(s(\\.(g(o(v(\\.(b(r)?)?)?)?)?)?)?)?)?)?)?)?)?$/.test(email[0].value)){" +
                            "email[0].value = '';" +
                            "email.parentNode.parentNode.parentNode.insertAdjacentHTML('afterend', 'Apenas domínio edu.es.gov.br!');" +
                            "return false;" +
                            "}" +
                            "}" +
                            "})()");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            try {
                if(url.startsWith("javascript"))
                    return false;

                if (url.startsWith("http") || url.startsWith("https"))
                {
                    if(MyApplication.sdState == SdState.SD_AVAILABLE)
                    {
                        URL urlEntrada = null;
                        urlEntrada = new URL(url);
                        List<String> urlsPermitidas = new ArrayList<String>(25);
                        urlsPermitidas.add("pt.wikipedia.org");
                        urlsPermitidas.add("en.wikipedia.org");
                        urlsPermitidas.add("wikipedia.org");

                        //TODO: fazer um filtro inteligente de URLs
                        for (int i = 0; i <= urlsPermitidas.size() -1; i++)
                        {
                            if(urlEntrada.getAuthority().contains(urlsPermitidas.get(i))) {
                                return false;
                            }
                        }
                        Log.d("ControleAcesso", "Acesso negado a " + url);
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(getApplicationContext(), "Acesso negado.", duration);
                        toast.show();
                        return true;
                    }
                    else
                        return false;
                }

                if (url.startsWith("intent://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        PackageManager packageManager = webView.getContext().getPackageManager();
                        if (intent != null) {
                            webView.stopLoading();
                            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            if (info != null) {
                                webView.getContext().startActivity(intent);
                            } else {
                                Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(
                                        Uri.parse("market://details?id=" + intent.getPackage()));
                                if (marketIntent.resolveActivity(packageManager) != null) {
                                    getApplicationContext().startActivity(marketIntent);
                                    return true;
                                }
                            }
                            return true;
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
            return true;
        }

    }
}