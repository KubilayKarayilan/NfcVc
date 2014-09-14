package com.nfcvcard.tasks;

import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by K on 9/14/2014.
 */
public class NdefGeneralTasks extends AsyncTask<String,Void,String> {

    private static final String TAG = "NFCTASK";
    private NfcAdapter mNfcAdapter;

    @Override
    protected String doInBackground(String... params) {
        String state="";
       if (params[0].equals("turnOnNfc"))
           state=turnNfcOn(true);
        if (params[0].equals("turnOffNfc"))
           state= turnNfcOn(false);
        return state;
    }

    public NdefGeneralTasks setmNfcAdapter(NfcAdapter mNfcAdapter) {
        this.mNfcAdapter = mNfcAdapter;
        return this;
    }

    private String turnNfcOn(boolean desiredState) {


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i(TAG,"postExec: "+ s);
    }
}
