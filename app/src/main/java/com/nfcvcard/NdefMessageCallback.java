package com.nfcvcard;

import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by K on 9/20/2014.
 */
public class NdefMessageCallback implements NfcAdapter.CreateNdefMessageCallback {
    private Bundle bundleData;
    private File externalFilesDir;
    private Uri fileUri;

    public NdefMessageCallback() {
    }
  /*  */public NdefMessageCallback(Bundle bundleData,File externalFilesDir) {
        this.bundleData = bundleData;
        this.externalFilesDir = externalFilesDir;
    }
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis());


        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { NdefRecord.createMime(
                        "application/com.nfcvcard", text.getBytes()) ,
                        NdefRecord.createUri(createBeamUris())

                });
        return msg;
    }
    public Uri createBeamUris() {
          /*
         * Create a list of URIs, get a File,
         * and set its permissions
         */
        Uri[] mFileUris = new Uri[10];
        String transferFile = "CvPicImage.jpg";
        File extDir = externalFilesDir;
        File requestFile = new File(extDir, transferFile);
        requestFile.setReadable(true, false);

        // Get a URI for the File and add it to the list of URIs
        fileUri = Uri.fromFile(requestFile);


        return fileUri;
    }
}
