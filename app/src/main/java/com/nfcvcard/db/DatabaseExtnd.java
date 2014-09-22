package com.nfcvcard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by K on 9/6/2014.
 */
public class DatabaseExtnd extends SQLiteOpenHelper{
    private static final String DB_NAME="NfcVc.db";
    public static final String CONTACTS_TABLE_NAME = "nfc_contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String LOGO_URI = "logouri";
    public static final String CONTATS_PIC_URI = "picuri";
    public static final String CONTACTS_COLUMN_PHONE = "phone";



    public DatabaseExtnd(Context context) {
         super(context,DB_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table if not exists nfc_contacts " +
                        "(id integer primary key, name text,phone text,email text, logouri text,picuri text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS nfc_contacts");
        onCreate(db);
    }

    public boolean insertContact  (String name, String phone, String email, Uri logo,Uri picuri)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        if (null!= logo)
        contentValues.put("logouri", logo.toString());
        if (null!=picuri)
        contentValues.put("picuri",picuri.toString());

        db.insert("nfc_contacts", null, contentValues);
        return true;
    }
    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from nfc_contacts where id="+id+"", null );
        return res;
    }
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }
    public boolean updateContact (Integer id, String name, String phone, String email,Uri logo,Uri picuri)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("logouri", logo.toString());
        contentValues.put("picuri", picuri.toString());
        db.update("nfc_contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("nfc_contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }
    public ArrayList getAllCotacts()
    {
        ArrayList array_list = new ArrayList();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from nfc_contacts", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}
