package com.nfcvcard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.widget.Toast;

/**
 * Created by K on 9/12/2014.
 */
public class BroadCastNfc extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();

        if (action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {

                    Toast.makeText(context,"Tech Discovered",Toast.LENGTH_LONG).show();

        }else  if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {

            Toast.makeText(context,"Tag Discovered",Toast.LENGTH_LONG).show();

        }else  if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {

            Toast.makeText(context,"Ndf Discovered",Toast.LENGTH_LONG).show();

        }
    }
}
