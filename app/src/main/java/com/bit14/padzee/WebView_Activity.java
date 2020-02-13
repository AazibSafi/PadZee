/*
*	Developer: Aazib Safi Patoli
*	January 2017
*/

package com.bit14.padzee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebView_Activity extends Activity {

    WebView webView;
    Helper helper;
    String sharedContent;

    private ProgressBar mProgress;

    long startTime = 0;

    private boolean onPageFinished_LoadedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_site_view);

        // Allows the user to Rotate in any direction
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);

        helper = new Helper(this);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        if (!data.isEmpty())
            sharedContent = data.getString("sharedContent");
        else
            sharedContent = "";

        openPadzeeApp();
    }
    void openPadzeeApp() {

        webView = (WebView) findViewById(R.id.webView);
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        enableHTML5AppCache();
//        disableHTML5AppCache();

        webView.setOnTouchListener(new View.OnTouchListener() {
            Toast toast;

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1){
                // If Network Turns off during webview Running
                // Following Disables the Touch on screen
                // And opens the Keyboard so that user can enter text offline

                if(toast != null)
                    toast.cancel();

                if(helper.isNetworkAvailable(getBaseContext()))
                    return false;
                else {
                    toast = Toast.makeText(getBaseContext(),"Check Your Internet Connection",Toast.LENGTH_SHORT);
                    toast.show();

                    //Show Keyboard
                    InputMethodManager keyboard=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(arg0,InputMethodManager.SHOW_IMPLICIT);

                    Log.e("Internet","Not Working");
                    return true;
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.i("onJsAlert","JavaScript/JQuery Request");
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int progress){
                Log.i("Loading Progress",""+progress);

                // onProgressChanged calls before onPageStart,
                // therefore we start time from here
                startTime = System.currentTimeMillis();

                // Check if Page Loading time exceeds to "NetworkSlowTime"
                int NetworkSlowTime = 5000;
                checkSlowNetworkConnection(NetworkSlowTime);
                checkSlowNetworkConnection(NetworkSlowTime*2);

                // Show the ProgressLoader until PageHas not been Loaded
                if( !getOnPageFinished_LoadedOnce() ) {
                    mProgress.setVisibility(View.VISIBLE);
                    mProgress.setProgress(progress);
                }

                if (progress == 100)
                    Log.d("Web Page Loaded","Successfuly");
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            JS_Injection injectJS;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i("Page Started", "onPageStarted");

                injectJS = new JS_Injection();
                injectJS.setHelper(helper);

                setOnPageFinished_LoadedOnce(false);


                setTitle("Custom Title");
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.i("onLoadResource", "sharedContent: " + sharedContent);

                if(injectJS != null) {

                    //// We can Add More scripts below and defining their function in JS_Injection Class /////
                    if (sharedContent != null && !sharedContent.equals("")) {
                        injectJS.putSharedText(sharedContent);
                        sharedContent = "";
                    }

                    injectJS.hideDownloadAppButton();
                    //// We can Add More scripts above and defining their function in JS_Injection Class /////


                    //// Load the Scripts to WebView if you have Added them above /////
                    if (!injectJS.isEmpty()) {
                        String javascript = injectJS.getScriptToLoad();
                        view.loadUrl(javascript);
                        Log.d("javascript", javascript);
                    }
                    //// Load the Scripts to WebView if you have Added them above /////
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("OverrideUrlLoading", url);
                boolean success = false;

                if(url != null) {
                    try {
                        // Facebook and linkedin Sharing will be done through web browser
                        // twitter is using web browser if App is not installed

                        if( url.contains("twitter.com") || url.startsWith("mailto:") ) {
                            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            success = true;
                        }
                        else if(url.startsWith("whatsapp://")) {

                            if(helper.isAppInstalled(getBaseContext(),"com.whatsapp")) {
                                Log.i("WhatsApp", "Installed");
                                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            }
                            else {
                                Log.e("WhatsApp", "Not installed");
                                Toast.makeText(getBaseContext(), "WhatsApp not installed in your Device",
                                        Toast.LENGTH_SHORT).show();
                            }
                            success = true;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                return success;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("Page Finish", "onPageFinished");
                sharedContent = "";

                mProgress.clearAnimation();
                mProgress.setVisibility(View.GONE);

                Log.i("Page Load Time",calculateTime()+" ms");
                startTime = 0;

                setOnPageFinished_LoadedOnce(true);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("onReceivedError", "Description: "+description+"\n: FailingUrl"+failingUrl);
            }
        });

        webView.loadUrl(helper.URL);
    }

    private void enableHTML5AppCache() {
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webView.getSettings().setAppCacheMaxSize(1024*1024*8);
//        webView.getSettings().setAppCachePath(getApplication().getCacheDir().toString());
//        webView.getSettings().setAllowFileAccess(true);
    }

    private void disableHTML5AppCache() {
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    void checkSlowNetworkConnection(final int NetworkSlowTime){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if( startTime > 0 && !getOnPageFinished_LoadedOnce() ) {

                    long elapsedTime = calculateTime();

                    if (elapsedTime >= NetworkSlowTime) {
                        Toast.makeText(getBaseContext(), "Low Network Speed", Toast.LENGTH_SHORT).show();
                        Log.e("ProgressTime", elapsedTime + " ms, Low Network Speed");
                    }
                }
            }
        }, NetworkSlowTime);
    }

    public long calculateTime(){
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public void onBackPressed() {
        if(webView != null && webView.canGoBack())
            webView.goBack();
        else {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            exitApp();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    public void exitApp(){
        super.onBackPressed();
    }

    public void setOnPageFinished_LoadedOnce(boolean onPageFinished_LoadedOnce) {
        this.onPageFinished_LoadedOnce = onPageFinished_LoadedOnce;
    }

    public boolean getOnPageFinished_LoadedOnce() {
        return onPageFinished_LoadedOnce;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

/* This Function Prevents the activity Reload on Screen Rotation */

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Orientation","LANDSCAPE");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d("Orientation","PORTRAIT");
        }

        // Hide the Keyboard if opened, Bcz it disturbs the Layout
        try{
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e){
            Log.e("KeyBoardUtil", e.toString(), e);
        }
    }
}