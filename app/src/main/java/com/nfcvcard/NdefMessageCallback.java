package com.nfcvcard;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;

import java.nio.charset.Charset;

/**
 * Created by K on 9/20/2014.
 */
public class NdefMessageCallback implements NfcAdapter.CreateNdefMessageCallback {
    private Bundle bundleData;

    public NdefMessageCallback() {
    }
  /*  public NdefMessageCallback(Bundle bundleData) {
        this.bundleData = bundleData;
    }*/
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis());
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { NdefRecord.createMime(
                        "application/com.nfcvcard", text.getBytes())

                });
        return msg;
    }
}
