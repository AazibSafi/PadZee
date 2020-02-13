/*
*	Developer: Aazib Safi Patoli
*	January 2017
*/

package com.bit14.padzee;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity","Launched");

        helper = new Helper(this);
        helper.setSharedText("");

        if(helper.isNetworkAvailable(this))
            helper.connectionAvailable();
        else
            helper.connectionUnAvailable();

        finish();
    }
}
