package org.cordova.quasar.corona.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int ActivityWebviewLayout = R.layout.activity_webview;
        setContentView(ActivityWebviewLayout);

        checkCameraPermissions();

        setupNavigationView();

        setupProgressBar();

        setupFabButton();

        setupMyWebView();

        loadUrlOnWebView();

    }

    private void loadUrlOnWebView() {
        Locale brLocale = new Locale("pt", "BR");
        Locale.setDefault(brLocale);
        url = getIntent().getStringExtra("url");
        myWebView.loadUrl(url);
    }

    private void setupFabButton() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> myWebView.goBack());
    }

    private void setupMyWebView() {
        int myWebviewId = R.id.web_view;
        myWebView = findViewById(myWebviewId);

        MyWebViewClient webViewClient = new MyWebViewClient();
        myWebView.setWebViewClient(webViewClient);

        ChromeClient chromeClient = new ChromeClient();
        myWebView.setWebChromeClient(chromeClient);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.OFF);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.supportZoom();
    }

    private void setupProgressBar() {
        int progressBar1Id = R.id.progressBar1;
        spinner = findViewById(progressBar1Id);
    }

    private void setupNavigationView() {
        int navigationId = R.id.navigation;
        BottomNavigationView navigationView = findViewById(navigationId);

        final int classroomId = R.id.classroom;
        navigationView.setSelectedItemId(classroomId);

        navigationView.setOnNavigationItemSelectedListener(
                item -> {
                    final int wikipediaId = R.id.wikipedia;
                    int selectedItemId = item.getItemId();

                    Context applicationContext = getApplicationContext();

                    Intent webviewActivityIntent = new Intent(applicationContext, WebviewActivity.class);

                    overridePendingTransition(0, 0);
                    final int questionsId = R.id.questions;
                    final int aboutId = R.id.about;
                    switch (selectedItemId) {
                        case classroomId: {
                            String classroomUrl = "https://classroom.google.com/a/estudante.se.df.gov.br";
                            boolean isUrlEqualsClassroomUrl = url.equals(classroomUrl);
                            if (isUrlEqualsClassroomUrl) {
                                return true;
                            }
                            Intent activityWithUrlIntent = webviewActivityIntent.putExtra("url", classroomUrl);
                            startActivity(activityWithUrlIntent);
                            return true;
                        }
                        case wikipediaId: {
                            String wikipediaUrl = "https://pt.wikipedia.org/";
                            boolean isUrlEqualsWikipediaUrl = url.equals(wikipediaUrl);
                            if (isUrlEqualsWikipediaUrl) {
                                return true;
                            }
                            Intent activityWithUrlIntent = webviewActivityIntent.putExtra("url", wikipediaUrl);
                            startActivity(activityWithUrlIntent);
                            return true;
                        }
                        case questionsId: {
                            Intent questionsActivityIntent = new Intent(applicationContext, QuestionsActivity.class);
                            startActivity(questionsActivityIntent);
                            return true;
                        }
                        case aboutId: {
                            Intent aboutActivityIntent = new Intent(applicationContext, AboutActivity.class);
                            startActivity(aboutActivityIntent);
                            return true;
                        }
                    }
                    navigationView.getMenu().getItem(selectedItemId).setChecked(true);
                    return false;
                }
        );
    }

    private void checkCameraPermissions() {
        WebviewActivity webViewActivityContext = WebviewActivity.this;

        int sumGrantedPermissions = ContextCompat.checkSelfPermission(webViewActivityContext, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(webViewActivityContext, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(webViewActivityContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean allPermissionsWereGranted = sumGrantedPermissions == PackageManager.PERMISSION_GRANTED;
        if (allPermissionsWereGranted) {
            String onSuccessText = "Permissões já concedidas";
            Toast.makeText(webViewActivityContext, onSuccessText, Toast.LENGTH_SHORT);
        } else {
            requestCameraPermission();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean dataStringIsEmptyButHasData = data != null && data.getDataString() == null;
        if(dataStringIsEmptyButHasData) {
            data = null;
        }
        boolean isSdkVersionEqualsOrNewerThanLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        boolean isDataEmpty = data == null;
        if (isSdkVersionEqualsOrNewerThanLollipop) {
            boolean isRequestCodeDifferentThanInputFileRequestOrEmptyFilePathCallback = requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null;
            if (isRequestCodeDifferentThanInputFileRequestOrEmptyFilePathCallback) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            boolean isResultCodeOK = resultCode == Activity.RESULT_OK;
            if (isResultCodeOK) {
                if (isDataEmpty) {
                    boolean isCameraPhotoPathEmpty = mCameraPhotoPath != null;
                    if (isCameraPhotoPathEmpty) {
                        Uri parsedCameraPhotoPath = Uri.parse(mCameraPhotoPath);
                        results = new Uri[]{parsedCameraPhotoPath};
                    }
                } else {
                    String dataString = data.getDataString();
                    boolean isDataStringFilled = dataString != null;
                    if (isDataStringFilled) {
                        Uri parsedDataString = Uri.parse(dataString);
                        results = new Uri[]{parsedDataString};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        }
        else {
            boolean isSdkVersionEqualsOrNewerThanKitkat = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
            if (isSdkVersionEqualsOrNewerThanKitkat) {
                boolean isRequestCodeDifferentThanFileChooserOrEmptyUploadMessage = requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null;
                if (isRequestCodeDifferentThanFileChooserOrEmptyUploadMessage) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }

                boolean isRequestCodeEqualsFileChooserResultCode = requestCode == FILECHOOSER_RESULTCODE;
                if (isRequestCodeEqualsFileChooserResultCode) {
                    boolean isUploadMessageEmpty = this.mUploadMessage == null;
                    if (isUploadMessageEmpty) {
                        return;
                    }

                    Uri result = null;
                    try {
                        boolean isResultCodeABadResult = resultCode != RESULT_OK;
                        if (isResultCodeABadResult) {
                            result = null;
                        } else {
                            if(isDataEmpty){
                                result = mCapturedImageURI;
                            }else{
                                result = data.getData();
                            }
                        }
                    } catch (Exception e) {
                        String onFailureText = "activity :" + e;
                        Toast.makeText(getApplicationContext(), onFailureText, Toast.LENGTH_LONG).show();
                    }

                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;

                }
            }
        }
    }

    private void requestCameraPermission() {
        final WebviewActivity webviewActivity = WebviewActivity.this;
        boolean hasAnyDeniedPermission = ActivityCompat.shouldShowRequestPermissionRationale(webviewActivity, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(webviewActivity, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(webviewActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        String[] permissionsString = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (hasAnyDeniedPermission) {
            String permissionsDeniedText = "Permissões Negadas";
            String explanationText = "Para o funcionamento correto do Google Sala de Aula, por favor aceite as permissões necessárias.";
            String positiveButtonText = "Ok";
            String cancellButtonText = "Cancelar";
            new AlertDialog.Builder(webviewActivity).setTitle(permissionsDeniedText).setMessage(explanationText)
                    .setPositiveButton(positiveButtonText, (dialogInterface, i) -> ActivityCompat.requestPermissions(webviewActivity, permissionsString, PERMISSION_CODE)).setNegativeButton(cancellButtonText, (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        } else {
            ActivityCompat.requestPermissions(this, permissionsString, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && (grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                String grantedPermissionText = "Permissão concedida";
                Toast.makeText(this, grantedPermissionText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        BottomNavigationView navigationView = findViewById(R.id.navigation);

        final String classroomUrl = "https://classroom.google.com/a/estudante.se.df.gov.br";
        final String wikipediaUrl = "https://pt.wikipedia.org/";

        switch (url) {
            case classroomUrl: {
                int classroomId = 0;
                navigationView.getMenu().getItem(classroomId).setChecked(true);
                break;
            }
            case wikipediaUrl: {
                int wikipediaId = 1;
                navigationView.getMenu().getItem(wikipediaId).setChecked(true);
                break;
            }
            default: {
                int defaultId = 2;
                navigationView.getMenu().getItem(defaultId).setChecked(true);
                break;
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyPressedEqualsBack = keyCode == KeyEvent.KEYCODE_BACK;
        boolean isKeyBackPressedAndCanThisGoBack = isKeyPressedEqualsBack && myWebView.canGoBack();
        if (isKeyBackPressedAndCanThisGoBack) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView webview, String url, Bitmap favicon) {
            boolean isThisFirstUse = isFirstUse();
            boolean isSeDfGovUrl = url.equals("http://www.se.df.gov.br/");
            boolean isUrlDifferentThanGovUrl = !isSeDfGovUrl;
            boolean isFirstUseAndDifferentUrlThanGovUrl = isThisFirstUse && isUrlDifferentThanGovUrl;
            if (isFirstUseAndDifferentUrlThanGovUrl) {
                webview.setVisibility(webview.INVISIBLE);
            }
            if (isSeDfGovUrl) {
                spinner.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setNotFirstUse();
            spinner.setVisibility(View.GONE);

            view.setVisibility(myWebView.VISIBLE);
            super.onPageFinished(view, url);

            String firstJSUrl = "javascript:(function f(e) {" +
                    "var email = document.getElementsByName('identifier');" +

                    "email[0].oninput = function(value) {" +
                    "if(!/^\\w?([\\.-]?\\w+)*(@)?((e(d(u)?)?)?|(e(s(t(u(d(a(n(t(e)?)?)?)?)?)?)?)?)?)?(\\.)?(s(e(\\.(d(f(\\.(g(o(v(\\.(b(r)?)?)?)?)?)?)?)?)?)?)?)?$/.test(email[0].value)){" +
                    "email[0].value = email[0].value.split('@')[0];" +
                    "alert('São permitidos apenas emails com domínio: @edu.se.df.gov.br ou @estudante.se.df.gov.br ou @se.df.gov.br');" +
                    "return false;" +
                    "}" +
                    "}" +
                    "})()";
            view.loadUrl(firstJSUrl);

            String secondJSUrl = "javascript:(function f() {" +
                    "document.getElementsByClassName('OIPlvf')[0].style.display='none'; " +

                    "document.getElementsByClassName('Y4dIwd')[0].innerHTML = 'Use sua conta Google Sala De Aula (@edu.se.df.gov.br ou @estudante.se.df.gov.br ou @se.df.gov.br)'" +
                    "})()";

            view.loadUrl(secondJSUrl);

            String thirdJSUrl = "javascript:(function f() {" +
                    "document.getElementsByClassName('docs-ml-header-item docs-ml-header-drive-link')[0].style.display='none'; " +
                    "})()";
            view.loadUrl(thirdJSUrl);
            
            String fourthJSUrl = "javascript:(function f() {" +
                    "document.getElementById('p-donation').style.display='none'; " +
                    "})()";
            view.loadUrl(fourthJSUrl);
        }

        private String processYoutubeLink(WebView view, String urlParameter) {
            final String regexYouTube = "^.*((youtu.be\\/)|(v\\/)|(\\/u\\/\\w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#&?]*).*";
            String url;
            WebBackForwardList mWebBackForwardList = view.copyBackForwardList();
            String historyUrl = "";

            int currentIndex = mWebBackForwardList.getCurrentIndex();
            boolean isCurrentIndexGreaterThanZero = currentIndex > 0;
            if (isCurrentIndexGreaterThanZero) {
                historyUrl = mWebBackForwardList.getItemAtIndex(currentIndex).getUrl();
            }

            boolean historyUrlMatchsWithtYoutubeRegex = historyUrl.matches(regexYouTube);
            if (historyUrlMatchsWithtYoutubeRegex)
                return "";

            boolean urlParamaterMatchsWithYoutubeRegex = urlParameter.matches(regexYouTube);
            boolean urlParamaterHasNoEmbedString = !urlParameter.matches("embed");
            boolean urlParamaterContainsDotYoutube = urlParameter.contains(".youtube");
            boolean urlParamaterIsFormatedAsExpected = urlParamaterMatchsWithYoutubeRegex && urlParamaterHasNoEmbedString && urlParamaterContainsDotYoutube;
            if (urlParamaterIsFormatedAsExpected) {
                Pattern compiledPattern = Pattern.compile(regexYouTube);
                Matcher matcher = compiledPattern.matcher(urlParameter);

                boolean urlParamaterWasFoundInRegex = matcher.find();
                if (urlParamaterWasFoundInRegex) {
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
        public String fixFormShortLink(WebView webView, String url){
            String formShortLinkRegex = "forms.gle/[\\w]{10}\\w*.*browser_fallback_url=(.*/viewform)";
            Pattern pattern = Pattern.compile(formShortLinkRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(url);
            String fixed_url;
            boolean urlMatchsWithRegex = matcher.find();
            if(urlMatchsWithRegex) {
                fixed_url = getFirstGroupOnRegex(matcher);
                webView.loadUrl(fixed_url);
            }else{
                fixed_url = url;
            }
            return fixed_url;
        }

        private String getFirstGroupOnRegex(Matcher matcher) {
            return matcher.group(1);
        }

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
            String url = this.processYoutubeLink(webView, urlParameter);
            url = this.fixFormShortLink(webView, url);
            boolean urlStartsWithJavascript = url.startsWith("javascript");
            boolean urlStartsWithMailto = url.startsWith("mailto:");
            try {
                if (urlStartsWithJavascript)
                    return false;
                if (urlStartsWithMailto) {
                    Intent actionViewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    webView.getContext().startActivity(actionViewIntent);
                    return true;
                }

                boolean urlStartsWithHttp = url.startsWith("http");
                boolean urlStartsWithHttps = url.startsWith("https");
                boolean urlStartsWithHttpOrHttps = urlStartsWithHttp || urlStartsWithHttps;
                if (urlStartsWithHttpOrHttps) {
                    boolean isSdAvailable = MyApplication.sdState == SdState.SD_AVAILABLE;
                    if (isSdAvailable) {
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
                            boolean isUrlAllowed = urlEntrada.getAuthority().contains(urlsPermitidas.get(i));
                            if (isUrlAllowed) {
                                return false;
                            }
                        }

                        Log.d("ControleAcesso", "Acesso negado a " + url);

                        int duration = Toast.LENGTH_LONG;
                        String deniedAcessText = "Acesso negado: " + url;
                        Toast toast = Toast.makeText(getApplicationContext(), deniedAcessText, duration);
                        toast.show();

                        return true;
                    } else {
                        return false;
                    }
                }

                boolean urlStartsWithIntent = url.startsWith("intent://");
                if (urlStartsWithIntent) {
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
                                String concatMarketWithPackage = "market://details?id=" + intent.getPackage();
                                Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(
                                        Uri.parse(concatMarketWithPackage));

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

    private boolean isFirstUse() {
        return ShowOrHideWebViewInitialUse.equals("show");
    }

    private void setNotFirstUse() {
        ShowOrHideWebViewInitialUse = "hide";
    }


    private class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;
        
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            boolean isThereAnyCallback = mFilePathCallback != null;
            if (isThereAnyCallback) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            boolean isTakePictureIntentFilled = takePictureIntent.resolveActivity(getPackageManager()) != null;
            if (isTakePictureIntentFilled) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    Log.e("ErrorCreatingFile", "Unable to create Image File", ex);
                }
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
            boolean isTakePictureFilled = takePictureIntent != null;
            if (isTakePictureFilled) {
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

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");

            boolean imageDirectoryDoesNotExists = !imageStorageDir.exists();
            if (imageDirectoryDoesNotExists) {
                imageStorageDir.mkdirs();
            }

            String currentTimeString = String.valueOf(System.currentTimeMillis());
            String concatPathnameWithFileNameAndExtension = imageStorageDir + File.separator + "IMG_" + currentTimeString + ".jpg";
            File file = new File(concatPathnameWithFileNameAndExtension);
            mCapturedImageURI = Uri.fromFile(file);
            
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            
            Intent chooserIntent = Intent.createChooser(i, "Upload De Imagem");
            
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[] { captureIntent });
            
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg, acceptType);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog dialog = new AlertDialog.Builder(view.getContext()).
                    setMessage(message).
                    setPositiveButton("OK", (dialog1, which) -> {
                    }).create();
            dialog.show();
            result.confirm();
            return true;
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null)
                return null;
            int defaultId = 2130837573;
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), defaultId);
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

    private File createImageFile() throws IOException {
        String dateFormat = "yyyyMMdd_HHmmss";
        Locale locale = Locale.US;
        Date currentDate = new Date();
        String timeStamp = new SimpleDateFormat(dateFormat, locale).format(currentDate);
        String imageFormatPrefix = "JPEG_";
        String concatImageFormatTimeStamp = imageFormatPrefix + timeStamp + "_";
        String picturesDirectory = Environment.DIRECTORY_PICTURES;
        File storageDir = getExternalFilesDir(picturesDirectory);

        String imageFormatSuffix = ".jpg";
        File imageFile = File.createTempFile(
                concatImageFormatTimeStamp,
                imageFormatSuffix,
                storageDir
        );
        return imageFile;
    }
}