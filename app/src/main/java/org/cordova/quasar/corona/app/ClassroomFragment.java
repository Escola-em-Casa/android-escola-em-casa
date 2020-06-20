package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.datami.smi.SdState;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.net.URISyntaxException;


public class ClassroomFragment extends Fragment {
    WebView myWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle   savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_classroom, container, false);
        myWebView = (WebView) view.findViewById(R.id.web_view);

        myWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Locale.setDefault(new Locale("pt", "BR"));

        myWebView.loadDataWithBaseURL(null, "<script" +
                "      src=\"https://code.jquery.com/jquery-3.5.0.min.js\"" +
                "      integrity=\"sha256-xNzN2a4ltkB44Mc/Jz3pT4iU1cmeR0FkXs4pru/JxaQ=\"" +
                "      crossorigin=\"anonymous\"" +
                "    ></script>", "text/html", "utf-8", null);
        myWebView.loadUrl("https://classroom.google.com/?emr=0");
        return view;
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
                if (url.startsWith("javascript"))
                    return false;

                if (url.startsWith("http") || url.startsWith("https")) {
                    if (MyApplication.sdState == SdState.SD_AVAILABLE) {
                        URL urlEntrada = null;
                        urlEntrada = new URL(url);
                        List<String> urlsPermitidas = new ArrayList<String>(25);
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
                        urlsPermitidas.add("youtube.com");

                        //TODO: fazer um filtro inteligente de URLs
                        for (int i = 0; i <= urlsPermitidas.size() - 1; i++) {
                            if (urlEntrada.getAuthority().contains(urlsPermitidas.get(i))) {
                                return false;
                            }
                        }
                        Log.d("ControleAcesso", "Acesso negado a " + url);
                        int duration = Toast.LENGTH_LONG;
                        return true;
                    } else
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
    public static ClassroomFragment newInstance() {
        return new ClassroomFragment();
    }
}