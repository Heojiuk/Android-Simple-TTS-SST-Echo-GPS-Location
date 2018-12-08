package kr.ac.mokwon.ice.testandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PhonCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            String sPhonNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Toast.makeText(context,"Phone # = " + sPhonNum,Toast.LENGTH_LONG).show();

        }else if(action.equals(Intent.ACTION_CALL_BUTTON)){
String sKey = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
Toast.makeText(context,"Call Button : " + sKey,Toast.LENGTH_LONG ).show();
        }
    }
}
