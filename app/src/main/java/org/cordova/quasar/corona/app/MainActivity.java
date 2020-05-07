package org.cordova.quasar.corona.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.datami.smi.SdState;
import com.datami.smi.SdStateChangeListener;
import com.datami.smi.SmiResult;
import com.datami.smi.SmiVpnSdk;
import com.datami.smi.internal.MessagingType;
import com.datami.webview.SmiWebView;

public class MainActivity extends AppCompatActivity implements SdStateChangeListener {
    private WebView webView;
    // private SmiWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String sdkKey = "";
        SmiVpnSdk.initSponsoredData(sdkKey, this, R.drawable.ic_launcher_background, MessagingType.BOTH);
        SmiVpnSdk.startSponsoredData();



//        WebView = (SmiWebView) findViewById(R.id.sponsorWebView);
//        WebView.setWebViewClient( new WebViewClient());
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient( new WebViewClient());


//        WebSettings webSettings = myWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        WebView.setWebChromeClient(new WebChromeClient());
//        WebView.loadUrl("https://quintuplegeneral.htmlpasta.com/");
        webView.loadUrl("https://classroom.google.com");
    }

    @Override
    public void onChange(SmiResult currentSmiResult) {
        SdState sdState = currentSmiResult.getSdState();
        Log.d("Datami", "sponsored data state : " + sdState);
        if (sdState == SdState.SD_AVAILABLE) {
            // TODO: show a banner or message to user, indicating that
//            the data usage is sponsored and charges do not apply to user data plan
        } else if (sdState == SdState.SD_NOT_AVAILABLE) {
            // TODO: show a banner or message to user, indicating that
//            the data usage is NOT sponsored and charges apply to user data plan
            Log.d("Datami", " - reason: " + currentSmiResult.getSdReason());
        } else if (sdState == SdState.WIFI) {
            //
        }
    }
}
