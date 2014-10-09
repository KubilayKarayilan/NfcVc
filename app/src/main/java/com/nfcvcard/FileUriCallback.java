
package com.nfcvcard;

import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.provider.MediaStore;
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


        String path= getFilePathFromUri(uris.get(0));
        File requestFile = new File(path);
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

    private String getFilePathFromUri(Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = myActivity.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();
            return cursor.getString(0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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
