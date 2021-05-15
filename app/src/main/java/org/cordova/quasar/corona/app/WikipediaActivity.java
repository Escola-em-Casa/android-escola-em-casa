package org.cordova.quasar.corona.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.datami.smi.SdState;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikipediaActivity extends NavigationBarActivity {
    private WebView myWebView;
    private String url;
    private ProgressBar spinner;
    String ShowOrHideWebViewInitialUse = "show";
    private int PERMISSION_CODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wikipedia);

        super.setupNavigationBar(R.id.navigation, R.id.wikipedia);

        myWebView = findViewById(R.id.web_view);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.setWebChromeClient(new ChromeClient());

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.OFF);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.supportZoom();

        Locale.setDefault(new Locale("pt", "BR"));

        url = getIntent().getStringExtra("url");
        myWebView.loadUrl(url);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myWebView.goBack();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null && data.getDataString() == null)
            data = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    Log.d("sdadsa", "onActivityResult: "+ dataString);
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        }
        else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            if (requestCode == FILECHOOSER_RESULTCODE) {

                if (null == this.mUploadMessage) {
                    return;

                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {

                        result = null;

                    } else {

                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && (grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        BottomNavigationView navigationView = findViewById(R.id.navigation);

        Log.d("URL", "onResume: " + url);

        switch (url) {
            case "https://classroom.google.com/a/estudante.se.df.gov.br": {
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

                            "email[0].oninput = function(value) {" +
                            "if(!/^\\w?([\\.-]?\\w+)*(@)?((e(d(u)?)?)?|(e(s(t(u(d(a(n(t(e)?)?)?)?)?)?)?)?)?)?(\\.)?(s(e(\\.(d(f(\\.(g(o(v(\\.(b(r)?)?)?)?)?)?)?)?)?)?)?)?$/.test(email[0].value)){" +
                            "email[0].value = email[0].value.split('@')[0];" +
                            "alert('São permitidos apenas emails com domínio: @edu.se.df.gov.br ou @estudante.se.df.gov.br ou @se.df.gov.br');" +
                            "return false;" +
                            "}" +
                            "}" +
                            "})()");

            view.loadUrl(
                    "javascript:(function f() {" +
                            "document.getElementsByClassName('OIPlvf')[0].style.display='none'; " +

                            "document.getElementsByClassName('Y4dIwd')[0].innerHTML = 'Use sua conta Google Sala De Aula (@edu.se.df.gov.br ou @estudante.se.df.gov.br ou @se.df.gov.br)'" +
                            "})()");
            view.loadUrl(
                    "javascript:(function f() {" +
                            "document.getElementsByClassName('docs-ml-header-item docs-ml-header-drive-link')[0].style.display='none'; " +
                            "})()");
            view.loadUrl(
                    "javascript:(function f() {" +
                            "document.getElementById('p-donation').style.display='none'; " +
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
        public String formShortLinkFixer(WebView webView, String url){
            Pattern pattern = Pattern.compile("forms.gle/[\\w]{10}\\w*.*browser_fallback_url=(.*/viewform)",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(url);
            String fixed_url;
            if(matcher.find()) {
                fixed_url = matcher.group(1);
                webView.loadUrl(fixed_url);
            }else{
                fixed_url = url;
            }
            return fixed_url;
        };

        public void setBackFloatButtonVisibilty(WebView view){
            if(view.canGoBack()){
                fab.setVisibility(View.VISIBLE);
            }else{
                fab.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            setBackFloatButtonVisibilty(view);
            super.onLoadResource(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String urlParameter) {
            Log.d("URL: ", urlParameter);
            String url = this.youtubeProtect(webView, urlParameter);
            url = this.formShortLinkFixer(webView, url);
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

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard

            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");

            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }

            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");

            mCapturedImageURI = Uri.fromFile(file);

            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");

            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Upload De Imagem");

            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[]{captureIntent});

            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);


        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {

            openFileChooser(uploadMsg, acceptType);
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
    }

}