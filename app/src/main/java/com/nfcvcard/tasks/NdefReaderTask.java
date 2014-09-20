package com.nfcvcard.tasks;

import android.content.Context;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.nfcvcard.MyActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by K on 9/12/2014.
 */
public class NdefReaderTask extends AsyncTask<IntentDto, Void, String> {
    Context context;

    @Override
    protected String doInBackground(IntentDto... params) {
      IntentDto intentDto= params[0];
        context= intentDto.getCtx();
        return readText(intentDto.getTag(),intentDto.getRawMsgs(),intentDto.getCtx());

    }

    private String readText(Tag tagFromIntent,Parcelable[] rawMsgs,Context ctx)  {
        String incomeMsg="empty";
        String incomeMsg2="empty";
        StringBuilder stringBuilder=new StringBuilder("EMPTY");
        if (null != rawMsgs) {
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            // record 0 contains the MIME type, record 1 is the AAR, if present
            NdefRecord[] records = msg.getRecords();
             incomeMsg = new String(msg.getRecords()[0].getPayload());

        }
       /* if (null != tagFromIntent) {
            String[] techList = tagFromIntent.getTechList();
            stringBuilder = new StringBuilder("");
            Ndef ndef=Ndef.get(tagFromIntent);
            NdefMessage ndefMessage= ndef.getCachedNdefMessage();
            NdefRecord[] records = ndefMessage.getRecords();
            String heh= new String(ndefMessage.getRecords()[0].getPayload());

            for (String s : techList) {
                stringBuilder.append(s + "\n");
            }
            Log.e("MyActivity", stringBuilder.toString());
        }*/
        stringBuilder.append(incomeMsg+ " : "+ incomeMsg2);

    return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Toast.makeText(context, "result: "+result, Toast.LENGTH_LONG).show();
        }
    }
}
