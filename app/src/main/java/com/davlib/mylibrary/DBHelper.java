/*
 * Copyright (c) 2015. @WadehraArshpreet
 *
 * Created By Arshpreet Singh Wadehra 9/2/15 5:57 PM
 */

package com.davlib.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Arshpreet on 8/31/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "myLibrary";
    private static final int DB_VERSION = 1;
    private String TABLE_NAME = "user_details";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + "(user_id varchar(20), password varchar(30),email varchar(30),wishList TEXT,issuedBooks TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long onInsert(String user_id,String password,String wishlist,String issuedBooks)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id",user_id);
        cv.put("password",password);
        if(wishlist==null)
            wishlist="";
        if(issuedBooks==null)
            issuedBooks="";
        cv.put("wishlist", wishlist);
        cv.put("issuedBooks", issuedBooks);
        return db.insert(TABLE_NAME,null,cv);
    }
    public int onUpdate(String cols,String val)
    {   SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(cols,val);
        db.update(TABLE_NAME, cv, "1=1", null);
        return 1;
    }
    public int onDelete()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"1=1",null);
    }

    public String[] getDetails()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr;
        cr = db.rawQuery("Select * FROM " + TABLE_NAME,null);
        if(cr.moveToFirst() && cr != null)
        {
            if(cr.getString(0) == null)
                return null;
            return new String[] {cr.getString(0),cr.getString(1),cr.getString(2),cr.getString(3)};
        }
        else
            return null;
    }
}
