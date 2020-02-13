/*
*	Developer: Aazib Safi Patoli
*	January 2017
*/

package com.bit14.padzee;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class Retry_Activity extends AppCompatActivity {

    Helper helper;

    ImageView splashimg;
    Button tryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_view);

        // Allows the user to use the Activity only in Portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        helper = new Helper(this);
        helper.setSharedText("");

        splashimg = (ImageView) findViewById(R.id.splashLogo);
        splashimg.setImageResource(R.mipmap.app_icon);

        tryAgain = (Button) findViewById(R.id.tryAgain);

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helper.isNetworkAvailable(getBaseContext())) {
                    Log.i("Network", "Available");
                    helper.connectionAvailable();
                }
                else
                    Log.e("Network","Not Available");
            }
        });
    }
}