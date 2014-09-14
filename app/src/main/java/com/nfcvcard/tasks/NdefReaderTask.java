package com.nfcvcard.tasks;

import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by K on 9/12/2014.
 */
public class NdefReaderTask extends AsyncTask<Tag, Void, String> {

    @Override
    protected String doInBackground(Tag... params) {

     // readText()
        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
    return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
           Log.i("frag","****************************************"+ result);
        }
    }
}
