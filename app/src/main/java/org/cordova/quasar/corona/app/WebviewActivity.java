package org.cordova.quasar.corona.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.datami.smi.SdState;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebviewActivity extends AppCompatActivity {
    private WebView myWebView;
    private String url;
    private ProgressBar spinner;
    String ShowOrHideWebViewInitialUse = "show";
    private int PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        if (ContextCompat.checkSelfPermission(WebviewActivity.this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(WebviewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(WebviewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(WebviewActivity.this, "Permissões já concedidas", Toast.LENGTH_SHORT);
        } else {
            requestCameraPermission();
        }

        BottomNavigationView navigationView = findViewById(R.id.navigation);

        String datamilessToastMessage = "A internet utilizada ainda não está sendo paga pelo GDF.";

        Toast.makeText(
            getApplicationContext(), 
            datamilessToastMessage, 
            Toast.LENGTH_LONG)
        .show();

        navigationView.setSelectedItemId(R.id.classroom);
        navigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.classroom: {
                            if (url.equals("https://classroom.google.com/?emr=0")) {
                                return true;
                            }
                            startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                                    .putExtra("url",
                                            "https://classroom.google.com/?emr=0"));
                            overridePendingTransition(0, 0);
                            navigationView.getMenu().getItem(0).setChecked(true);

                            return true;
                        }
                        case R.id.wikipedia: {
                            if (url.equals("https://pt.wikipedia.org/")) {
                                return true;
                            }
                            startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                                    .putExtra("url",
                                            "https://pt.wikipedia.org/"));
                            overridePendingTransition(0, 0);
                            navigationView.getMenu().getItem(1).setChecked(true);

                            return true;
                        }
                        case R.id.about: {
                            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                            overridePendingTransition(0, 0);
                            navigationView.getMenu().getItem(2).setChecked(true);

                            return true;
                        }
                    }

                    return false;
                }
        );

        myWebView = findViewById(R.id.web_view);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.setWebChromeClient(new ChromeClient());

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Locale.setDefault(new Locale("pt", "BR"));

        url = getIntent().getStringExtra("url");
        myWebView.loadUrl(url);
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(WebviewActivity.this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(WebviewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(WebviewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(WebviewActivity.this).setTitle("Permissões Negadas").setMessage("Para o funcionamento correto do Google Sala de Aula, por favor aceite as permissões necessárias.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(WebviewActivity.this, new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && (grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        BottomNavigationView navigationView = findViewById(R.id.navigation);

        Log.d("URL", "onResume: " + url);

        switch (url) {
            case "https://classroom.google.com/?emr=0": {
                navigationView.getMenu().getItem(0).setChecked(true);
                break;
            }
            case "https://pt.wikipedia.org/": {
                navigationView.getMenu().getItem(1).setChecked(true);

                break;
            }
            default: {
                navigationView.getMenu().getItem(2).setChecked(true);

                break;
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView webview, String url, Bitmap favicon) {
            // only make it invisible the FIRST time the app is run
            if (ShowOrHideWebViewInitialUse.equals("show") && !url.equals("http://www.se.df.gov.br/")) {
                webview.setVisibility(webview.INVISIBLE);
            }
            if (url.equals("http://www.se.df.gov.br/")) {
                spinner.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            ShowOrHideWebViewInitialUse = "hide";
            spinner.setVisibility(View.GONE);

            view.setVisibility(myWebView.VISIBLE);
            super.onPageFinished(view, url);

            view.loadUrl(
                "javascript:(function f(e) {" +
                    "var email = document.getElementsByName('identifier');" +
                    "var submitBtn = document.getElementsByClassName('VfPpkd-LgbsSe VfPpkd-LgbsSe-OWXEXe-k8QpJ VfPpkd-LgbsSe-OWXEXe-dgl2Hf nCP5yc AjY5Oe DuMIQc qIypjc TrZEUc')[0];" +

                    "email[0].oninput = function(value) {" +
                        "if(!/^\\w?([\\.-]?\\w+)*(@)?((e(d(u)?)?)?|(e(s(t(u(d(a(n(t(e)?)?)?)?)?)?)?)?)?)?(\\.)?(s(e(\\.(d(f(\\.(g(o(v(\\.(b(r)?)?)?)?)?)?)?)?)?)?)?)?$/.test(email[0].value)){" +
                            "email[0].value = email[0].value.split('@')[0];" +
                            "submitBtn.disabled = true;" +
                            "alert('São permitidos apenas emails com domínio: @edu.se.df.gov.br ou @estudante.se.df.gov.br ou @se.df.gov.br');" +
                            "return false;" +
                        "}" +

                        "if(!email[0].value.split('@')[1]) {" +
                            "submitBtn.disabled = true;" +
                        "}" +

                        "else {" +
                            "submitBtn.disabled = false;" +
                        "}" +
                    "}" +
                "})()");

            view.loadUrl(
                "javascript:(function f() {" +
                    "document.getElementsByClassName('OIPlvf')[0].style.display='none'; " +

                    "document.getElementsByClassName('Y4dIwd')[0].innerHTML = 'Use sua conta Google Sala De Aula'" +
                "})()");

        }

        private String youtubeProtect(WebView view, String urlParameter) {
            final String regexYouTube = "^.*((youtu.be\\/)|(v\\/)|(\\/u\\/\\w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#&?]*).*";
            String url = "";
            WebBackForwardList mWebBackForwardList = view.copyBackForwardList();
            String historyUrl = "";

            if (mWebBackForwardList.getCurrentIndex() > 0) {
                historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList
                        .getCurrentIndex())
                        .getUrl();
            }

            if (historyUrl.matches(regexYouTube))
                return "";

            if (urlParameter.matches(regexYouTube) && !urlParameter.matches("embed")
                    && urlParameter.contains(".youtube")) {
                Pattern compiledPattern = Pattern.compile(regexYouTube);
                Matcher matcher = compiledPattern.matcher(urlParameter);

                if (matcher.find()) {
                    url = "https://www.youtube-nocookie.com/embed/" + matcher.group(7) + "?rel=0";
                    view.loadUrl(url);

                    return "";
                } else {
                    url = urlParameter;
                }
            } else {
                url = urlParameter;
            }

            return url;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String urlParameter) {
            Log.d("URL: ", urlParameter);
            String url = this.youtubeProtect(webView, urlParameter);

            try {
                Log.d("URL: ", url);
                if (url.startsWith("javascript"))
                    return false;
                if (url.startsWith("mailto:")) {
                    webView.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }

                if (url.startsWith("http") || url.startsWith("https")) {
                    if (MyApplication.sdState == SdState.SD_AVAILABLE) {
                        URL urlEntrada = null;
                        List<String> urlsPermitidas = new ArrayList<String>(25);

                        urlEntrada = new URL(url);

                        urlsPermitidas.add("se.df.gov.br");
                        urlsPermitidas.add("escolaemcasa.se.df.gov.br");
                        urlsPermitidas.add("pt.wikipedia.org");
                        urlsPermitidas.add("en.wikipedia.org");
                        urlsPermitidas.add("wikipedia.org");
                        urlsPermitidas.add("classroom.google.com");
                        urlsPermitidas.add("accounts.google.com");
                        urlsPermitidas.add("googledrive.com");
                        urlsPermitidas.add("drive.google.com");
                        urlsPermitidas.add("docs.google.com");
                        urlsPermitidas.add("c.docs.google.com");
                        urlsPermitidas.add("sheets.google.com");
                        urlsPermitidas.add("slides.google.com");
                        urlsPermitidas.add("takeout.google.com");
                        urlsPermitidas.add("gg.google.com");
                        urlsPermitidas.add("script.google.com");
                        urlsPermitidas.add("ssl.google-analytics.com");
                        urlsPermitidas.add("video.google.com");
                        urlsPermitidas.add("s.ytimg.com");
                        urlsPermitidas.add("apis.google.com");
                        urlsPermitidas.add("googleapis.com");
                        urlsPermitidas.add("googleusercontent.com");
                        urlsPermitidas.add("gstatic.com");
                        urlsPermitidas.add("gvt1.com");
                        urlsPermitidas.add("edu.google.com");
                        urlsPermitidas.add("accounts.youtube.com");
                        urlsPermitidas.add("myaccount.google.com");
                        urlsPermitidas.add("forms.gle");
                        urlsPermitidas.add("google.com");

                        //TODO: fazer um filtro inteligente de URLs
                        for (int i = 0; i <= urlsPermitidas.size() - 1; i++) {
                            if (urlEntrada.getAuthority().contains(urlsPermitidas.get(i))) {
                                return false;
                            }
                        }

                        Log.d("ControleAcesso", "Acesso negado a " + url);

                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Acesso negado: " + url,
                                duration);

                        toast.show();

                        return true;
                    } else {
                        return false;
                    }
                }

                if (url.startsWith("intent://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        PackageManager packageManager = webView.getContext().getPackageManager();

                        if (intent != null) {
                            webView.stopLoading();
                            ResolveInfo info = packageManager.resolveActivity(intent,
                                    PackageManager.MATCH_DEFAULT_ONLY);

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


    private class ChromeClient extends WebChromeClient {
        protected FrameLayout mFullscreenContainer;
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog dialog = new AlertDialog.Builder(view.getContext()).
                    setMessage(message).
                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    }).create();
            dialog.show();
            result.confirm();
            return true;
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null)
                return null;

            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);

            this.mCustomView = null;

            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);

            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView,
                                     WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();

                return;
            }

            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;

            ((FrameLayout) getWindow().getDecorView()).addView(
                    this.mCustomView,
                    new FrameLayout.LayoutParams(-1, -1));

            getWindow().getDecorView().setSystemUiVisibility(3846
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

}