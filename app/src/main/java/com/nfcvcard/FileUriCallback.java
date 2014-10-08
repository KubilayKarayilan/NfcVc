
package com.nfcvcard;

import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by K on 9/18/2014.
 */
public class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {
    private Uri fileUri;
    private File externalFilesDir;
     MyActivity myActivity;
    public FileUriCallback() {
    }

    public FileUriCallback(File externalFilesDir,MyActivity myActivity) {
        this.myActivity=myActivity;
        this.externalFilesDir = externalFilesDir;
    }

    @Override
    public Uri[] createBeamUris(NfcEvent event) {
          /*
         * Create a list of URIs, get a File,
         * and set its permissions
         */
        Bundle bundle=myActivity.getSavedData(1);
        ArrayList<Uri> uris= bundle.getParcelableArrayList("imageUri");
        Uri[] mFileUris = new Uri[1];

        /*
        mFileUris[0]=uris.get(0);*/
       String transferFile = "CvPicImage.jpg";
        File extDir = externalFilesDir;
        File requestFile = new File(extDir, transferFile);
        requestFile.setReadable(true, false);
        // Get a URI for the File and add it to the list of URIs
        fileUri = Uri.fromFile(requestFile);

        if (null != fileUri) {
            mFileUris[0] = fileUri;
        } else {
            Log.e("My Activity", "No File URI available for file.");
        } /**/

        myActivity.nfcAdapter.setNdefPushMessage(setNdfMessage(fileUri),myActivity);
        return mFileUris;
    }

    private NdefMessage setNdfMessage(Uri fileUri) {
        String text = ("Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis());

        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { NdefRecord.createMime(
                        "application/com.nfcvcard", text.getBytes()) ,
                        NdefRecord.createUri(fileUri)
                });
        return msg;
    }


}
