package com.nfcvcard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.widget.Toast;

import com.nfcvcard.MyActivity;

/**
 * Created by K on 9/12/2014.
 */
public class BroadCastNfc extends BroadcastReceiver {
    Intent myActivity;
    @Override
    public void onReceive(Context context, Intent intent) {
        myActivity = new Intent(context, MyActivity.class);
        final String action = intent.getAction();

        if (action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {

                    Toast.makeText(context,"Tech Discovered",Toast.LENGTH_LONG).show();
                    context.startActivity(myActivity);
        }else  if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            context.startActivity(myActivity);
            Toast.makeText(context,"Tag Discovered",Toast.LENGTH_LONG).show();

        }else  if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            context.startActivity(myActivity);
            Toast.makeText(context,"Ndf Discovered",Toast.LENGTH_LONG).show();

        }
    }
}
