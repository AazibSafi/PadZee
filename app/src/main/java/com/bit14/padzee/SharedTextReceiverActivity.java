/*
*	Developer: Aazib Safi Patoli
*	January 2017
*/

package com.bit14.padzee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SharedTextReceiverActivity extends Activity {

    Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_text_receiver);

        String receivedText = getContent();

        helper = new Helper(this);
        helper.setSharedText(receivedText);

        Log.d("SharedTextReceiver","Launched with Text: "+receivedText);

        if(helper.isNetworkAvailable(this))
            helper.connectionAvailable();
        else
            helper.connectionUnAvailable();

        finish();
    }
    String getContent(){

        String receivedText = "";

        //get the received intent
        Intent receivedIntent = getIntent();

        //get the action
        String receivedAction = receivedIntent.getAction();

        //find out what we are dealing with
        String receivedType = receivedIntent.getType();

        //make sure it's an action and type we can handle
        if (receivedAction.equals(Intent.ACTION_SEND)) {
            Log.d("Recieved Type",receivedIntent.getType());

            if (receivedType.equals("text/plain")) {
                receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
            }
            else {
                Toast.makeText(this, "Only Text can be shared.", Toast.LENGTH_LONG).show();
                receivedText = "";
            }
        }

        //app has been launched directly, not from share list
        else if(receivedAction.equals(Intent.ACTION_MAIN))
            receivedText = "";

        return receivedText;
    }
}
