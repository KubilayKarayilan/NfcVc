package com.nfcvcard.tasks;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;

import com.nfcvcard.MyActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by K on 9/12/2014.
 */
public class NdefWriterTask extends AsyncTask<Tag, Void, String>  {

    @Override
    protected String doInBackground(Tag... params) {
        Tag tag = params[0];

      //  Ndef ndef = Ndef.get(tag);


         

        /*if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }*/

        return null;
    }



    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Log.i("frag","****************************************"+ result);
        }
    }
}
