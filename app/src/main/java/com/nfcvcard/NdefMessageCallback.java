package com.nfcvcard;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by K on 9/20/2014.
 */
public class NdefMessageCallback implements NfcAdapter.CreateNdefMessageCallback {
    private Bundle bundleData;
    private File externalFilesDir;
    private Uri fileUri;
    private MyActivity myActivity;

    public NdefMessageCallback() {
    }
  /*  */public NdefMessageCallback(File externalFilesDir,MyActivity myActivity) {
        this.myActivity= myActivity;
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
        Bundle bundle=myActivity.getSavedData(1);
        ArrayList<Uri> uris= bundle.getParcelableArrayList("imageUri");
       /* if (uris.size()>0)
            return uris.get(0);*/

        Uri[] mFileUris = new Uri[10];
        String transferFile = "CvPicImage.jpg";
        File extDir = externalFilesDir;
        File requestFile = new File(extDir, transferFile);
        requestFile.setReadable(true, false);

        // Get a URI for the File and add it to the list of URIs
        fileUri = Uri.fromFile(requestFile);
        return fileUri;

    }
    public String getFileUri(  Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = myActivity.getContentResolver().query(contentUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();
            return cursor.getString(0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String getRealPathFromURI(  Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = myActivity.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
