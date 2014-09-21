
package com.nfcvcard;

import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.NfcA;
import android.util.Log;

import java.io.File;

/**
 * Created by K on 9/18/2014.
 */
public class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {
    private Uri fileUri;
    private File externalFilesDir;

    public FileUriCallback() {
    }

    public FileUriCallback(File externalFilesDir) {
        this.externalFilesDir = externalFilesDir;
    }

    @Override
    public Uri[] createBeamUris(NfcEvent event) {
          /*
         * Create a list of URIs, get a File,
         * and set its permissions
         */
         Uri[] mFileUris = new Uri[10];
        String transferFile = "kubipic.jpg";
        File extDir = externalFilesDir;
        File requestFile = new File(extDir, transferFile);
        requestFile.setReadable(true, false);
        // Get a URI for the File and add it to the list of URIs
        fileUri = Uri.fromFile(requestFile);

        if (null != fileUri) {
            mFileUris[0] = fileUri;
        } else {
            Log.e("My Activity", "No File URI available for file.");
        }
        return mFileUris;
    }

}
