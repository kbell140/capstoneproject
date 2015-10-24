package com.example.zito.ittcheckbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zito on 10/13/2015.
 */
public class Zacct_Helper extends SQLiteOpenHelper {

    public static final String DB_NAME = "account_DB";
    public static final String TABLE_NAME = "account";
    public static final String ACCT_NUMBER = "acctNumber";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String BANK_NAME = "bankName";
    public static final String BANK_BALANCE = "bankBalance";
    public static final String ACCT_DATE = "acctDate";
    public static final String ACCT_NOTES = "acctNotes";

    private static final int DB_VERSION = 1;

    public Zacct_Helper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Calling the constructor
    public Zacct_Helper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Creating the account table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + ACCT_NUMBER + " INTEGER PRIMARY KEY,"
                + FIRST_NAME + " TEXT," + LAST_NAME + " TEXT," + BANK_NAME + " TEXT,"
                + BANK_BALANCE + " DOUBLE," + ACCT_DATE + " TEXT," + ACCT_NOTES + " TEXT);";

        db.execSQL(sql);

    }

    // Delete the table and create a new table - if needed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = ("DROP TABLE IF EXISTS account");
        db.execSQL(sql);
        onCreate(db);
    }

        // Enter records
    public boolean addAccount(String actnumber, String fname, String lname, String bkname, String bkbalance, String actdate, String actnotes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCT_NUMBER, actnumber);
        contentValues.put(FIRST_NAME, fname);
        contentValues.put(LAST_NAME, lname);
        contentValues.put(BANK_NAME, bkname);
        contentValues.put(BANK_BALANCE, bkbalance);
        contentValues.put(ACCT_DATE, actdate);
        contentValues.put(ACCT_NOTES, actnotes);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    //Position cursor to start reading records ---> For view records process

    public Cursor getAccount() {
     /*   SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + TABLE_NAME + " where " + ACCT_NUMBER + " =?" + ACCT_NUMBER;
        Cursor c = db.rawQuery(selectQuery,null);
 */



        String zQuery = "select * from " + TABLE_NAME + " where " + ACCT_NUMBER + " =?" + ACCT_NUMBER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(zQuery,null);
        if (c != null)
            c.moveToFirst();
        return c;

    }
}
