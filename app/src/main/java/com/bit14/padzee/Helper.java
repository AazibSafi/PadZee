/*
*	Developer: Aazib Safi Patoli
*	January 2017
*/

package com.bit14.padzee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by aazib.patoli on 17-Jan-17.
 */

public class Helper {

    public String URL = "http://padzee.com/";
    public String APP_SUPPORT_REFERENCE = ".androidAppSupport"; //Class defined in web Page Textarea
    public String APP_SUPPORT_DOWNLOAD_BUTTON = ".androidAppSupport_DownloadApp"; //Class defined in web Page on Download App bUTTON

    Context AppContext;
    private String SharedText;

    public Helper(Context context){
        AppContext = context;
    }

    public void setSharedText(String sharedText) {
        SharedText = sharedText;
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void connectionAvailable(){
        Log.i("Network","Available");
        Intent webSiteView = new Intent(AppContext, WebView_Activity.class);
        webSiteView.putExtra("sharedContent",SharedText);
        AppContext.startActivity(webSiteView);
    }

    public void connectionUnAvailable(){
        Log.e("Network","Not Available");
        Intent androidView = new Intent(AppContext, Retry_Activity.class);
        AppContext.startActivity(androidView);
    }

    public boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
