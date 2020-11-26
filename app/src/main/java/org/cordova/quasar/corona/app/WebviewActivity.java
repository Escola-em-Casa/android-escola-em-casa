package org.cordova.quasar.corona.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.datami.smi.SdState;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

import android.content.SharedPreferences;

public class WebviewActivity extends AppCompatActivity {
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
    private String classrom_tutorial = "Esta aba serve para acessar sua conta do Google Sala de Aula.\n\nSeu email de acesso é composto pelo primeiro nome junto com o código de estudante, acrescido de @estudante.se.df.gov.br\n\nPara saber como obter o primeiro acesso, verifique a aba 'sobre', no link, 'como acessar o Google Sala de Aula'.";
    private String wikipedia_tutorial = "Esta aba serve para acessar a wikipédia.\n\nUtilizando o ícone da lupa é possivel fazer buscas dentro da wikipédia";

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    private void checkClassroomFirstRun() {
        final String PREFS_NAME = "classroom_first_run";
        final String PREF_VERSION_CODE_KEY = "1.0";
        final int DOESNT_EXIST = -1;
    
        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;
    
        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
    
        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
    
            // This is just a normal run
            return;
    
        } else if (savedVersionCode == DOESNT_EXIST) {
            new GuideView.Builder(this)
              .setTitle("Google Sala de Aula")
              .setContentText(classrom_tutorial)
              .setDismissType(DismissType.targetView)
              .setTargetView(findViewById(R.id.classroom))
              .setContentTextSize(14)
              .setTitleTextSize(16)
              .build()
              .show();    
        } else if (currentVersionCode > savedVersionCode) {
            // TODO This is an upgrade
        }
    
        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    private void checkWikipediaFirstRun() {
      final String PREFS_NAME = "wikipedia_first_run";
      final String PREF_VERSION_CODE_KEY = "1.0";
      final int DOESNT_EXIST = -1;
  
      // Get current version code
      int currentVersionCode = BuildConfig.VERSION_CODE;
  
      // Get saved version code
      SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
      int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
  
      // Check for first run or upgrade
      if (currentVersionCode == savedVersionCode) {
  
          // This is just a normal run
          return;
  
      } else if (savedVersionCode == DOESNT_EXIST) {
          new GuideView.Builder(this)
              .setTitle("Wikipédia")
              .setContentText(wikipedia_tutorial)
              .setDismissType(DismissType.anywhere)
              .setTargetView(findViewById(R.id.wikipedia))
              .setContentTextSize(14)
              .setTitleTextSize(16)
              .build()
              .show();  
      } else if (currentVersionCode > savedVersionCode) {
  
          // TODO This is an upgrade
      }
  
      // Update the shared preferences with the current version code
      prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
  }

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

        navigationView.setSelectedItemId(R.id.classroom);
        navigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.classroom: {
                            if (url.equals("https://classroom.google.com/a/estudante.se.df.gov.br")) {
                                return true;
                            }
                            startActivity(new Intent(getApplicationContext(), WebviewActivity.class)
                                    .putExtra("url",
                                            "https://classroom.google.com/a/estudante.se.df.gov.br"));
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
                        case R.id.questions: {
                            startActivity(new Intent(getApplicationContext(), QuestionsActivity.class));
                            overridePendingTransition(0, 0);

                            navigationView.getMenu().getItem(2).setChecked(true);

                            return true;
                        }
                        case R.id.about: {
                            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                            overridePendingTransition(0, 0);
                            navigationView.getMenu().getItem(3).setChecked(true);

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
      
        if (url.equals("https://classroom.google.com/a/estudante.se.df.gov.br")) {
          checkClassroomFirstRun();
        } else if (url.equals("https://pt.wikipedia.org/")) {
          checkWikipediaFirstRun();
        }
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

            view.loadUrl(getString(R.string.email_checker));

            view.loadUrl(getString(R.string.email_information));

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

        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e("ErrorCreatingFile", "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Upload De Arquivo");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

            return true;

        }

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
                    , new Parcelable[] { captureIntent });

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