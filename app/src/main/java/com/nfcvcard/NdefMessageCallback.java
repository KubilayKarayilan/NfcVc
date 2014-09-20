package com.nfcvcard;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;

/**
 * Created by K on 9/20/2014.
 */
public class NdefMessageCallback implements NfcAdapter.CreateNdefMessageCallback {
    private Bundle bundleData;

    public NdefMessageCallback() {
    }
    public NdefMessageCallback(Bundle bundleData) {
        this.bundleData = bundleData;
    }
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Bundle bundle = bundleData;
        String text = ("Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis());
        String text2;
        if (null!=bundle) 
         text2 = bundle.getString("name");
            else
             text2 = "No name came!******************";

        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{NdefRecord.createMime(
                        "text/plain", text.getBytes()), NdefRecord.createMime(
                        "text/plain", text2.getBytes())});
        return msg;
    }
}
