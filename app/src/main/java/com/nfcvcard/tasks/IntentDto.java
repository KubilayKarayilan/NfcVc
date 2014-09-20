package com.nfcvcard.tasks;

import android.content.Context;
import android.nfc.Tag;
import android.os.Parcelable;

/**
 * Created by K on 9/20/2014.
 */
public class IntentDto {
    private Tag tag;
    private Parcelable[] rawMsgs;
    private Context ctx;

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Parcelable[] getRawMsgs() {
        return rawMsgs;
    }

    public void setRawMsgs(Parcelable[] rawMsgs) {
        this.rawMsgs = rawMsgs;
    }
}
