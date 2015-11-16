package com.example.zito.ittcheckbook;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zito on 11/9/2015.
 */
public class TransUpdel extends Activity implements View.OnClickListener {

    EditText jAmount, jNotes;
    Button btnUpdate, btnDelete;

    Long jz_id;
    int zbig;

    String zvAcct = "";
    String zvBalan = "";
    String zvamount = "";
    String zzid = "";
    String ztipo = "";

    Zacct_Helper db;
    SQLiteDatabase zb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_updel);

        db = new Zacct_Helper(this);
        zb = db.getWritableDatabase();

        // *** Display the Transaction  *****
        TextView vid = (TextView) findViewById(R.id.jid);
        vid.setText(getIntent().getExtras().getString("xid"));
        TextView vAcct = (TextView) findViewById(R.id.jAcct);
        vAcct.setText(getIntent().getExtras().getString("xaccount"));
        TextView vfname = (TextView) findViewById(R.id.jOwner);
        vfname.setText(getIntent().getExtras().getString("xfname"));
        TextView vBalan = (TextView) findViewById(R.id.jBalance);
        vBalan.setText(getIntent().getExtras().getString("xbalan"));

        TextView vtipo = (TextView) findViewById(R.id.jtipos);
        vtipo.setText(getIntent().getExtras().getString("xtype"));

        TextView vamount = (TextView) findViewById(R.id.jAmount);
        vamount.setText(getIntent().getExtras().getString("xamount"));

        TextView vnotes = (TextView) findViewById(R.id.jNotes);
        vnotes.setText(getIntent().getExtras().getString("xnotes"));

        zzid = vid.getText().toString().trim();
        jz_id = Long.parseLong(zzid);
        zvAcct = vAcct.getText().toString().trim();
        zvamount = vamount.getText().toString().trim();
        ztipo = vtipo.getText().toString().trim();
        zvBalan = vBalan.getText().toString().trim();
        zvBalan = zvBalan.replace(",","");  //**** Removes commas

        Double xBalan = Double.parseDouble(zvBalan);
        DecimalFormat zcur = new DecimalFormat("$###,###.##");
        String zBal = zcur.format(xBalan);
        vBalan.setText(zBal);

        // TextView jrDate = (TextView) findViewById(R.id.jDate);
        TextView vdate = (TextView) findViewById(R.id.jDate);
        vdate.setText(getIntent().getExtras().getString("xdate"));
        //****** Sets the current date - Date Dialog **********
        vdate.setOnClickListener(new View.OnClickListener(){
            TextView vdate = (TextView) findViewById(R.id.jDate);
            String dd = vdate.getText().toString().trim();
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat(dd);  //current date
                vdate.setText(sdf.format(new Date()));
                vdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateDialog dialog = new DateDialog(v);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialog.show(ft, "Date Picker");
                    }
                });
            }
        });

        jAmount = (EditText) findViewById(R.id.jAmount);
        jNotes = (EditText) findViewById(R.id.jNotes);

        // *** Buttons Update/Delete
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        // *** Calling Buttons Listners  *****
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //***** UPDATE Transaction *******
        if (v == btnUpdate) {

            switch (ztipo) {
                case "CREDIT":
                    uCredit();
                    zClear();
                    break;
                case "DEBIT":
                    uDebit();
                    zClear();

                    break;
                default:
                    Toast.makeText(this, "Invalid Transaction Type !", Toast.LENGTH_SHORT).show();
            }
        }

        //***** DELETE Transaction *******
        if (v == btnDelete) {

            switch (ztipo) {
                case "CREDIT":
                    zDebit();
                    zClear();
                    break;
                case "DEBIT":
                    zCredit();
                    zClear();

                    break;
                default:
                    Toast.makeText(this, "Invalid Transaction Type !", Toast.LENGTH_SHORT).show();
            }

        }

    } //***** End on Create

    //************ UPDATES / DELETES **********
    public void uCredit() {

    }

    public void uDebit() {
        TextView vAcct = (TextView) findViewById(R.id.jAcct);
        TextView vfname = (TextView) findViewById(R.id.jOwner);
        TextView vBalan = (TextView) findViewById(R.id.jBalance);
        TextView vdate = (TextView) findViewById(R.id.jDate);
        zvBalan = vBalan.getText().toString().trim();
        String jbal = zvBalan.substring(1); //*** Removes $ sign

        if (jAmount.getText().toString().trim().length() == 0) {
            //    Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            //    zMessage("Error ! - Transaction Amount", "Please, Enter a Valid Amount", zicon);
            Toast.makeText(this, "Transaction Amount is INVALID !!", Toast.LENGTH_SHORT).show();
            return;
        }

        String ztrDate = vdate.getText().toString().trim();
        String ztrAmount = jAmount.getText().toString().trim();
        String ztrNotes = jNotes.getText().toString().trim();


        Cursor z = zb.rawQuery("SELECT * FROM transactions WHERE _id ='" + jz_id + "'", null);
        if (z.moveToFirst()) {
            zb.execSQL("UPDATE transactions SET tranAmount='" + ztrAmount
                    + "',tranDate='" + ztrDate + "',tranNotes='" + ztrNotes
                    + "' WHERE _id='" + jz_id + "'");

            Toast.makeText(this, "Transaction Was UPDATED !!", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "Invalid Transaction !!", Toast.LENGTH_SHORT).show();


        //  Double xAmount = Double.parseDouble(ztrAmount);  //Convert to Double
        BigDecimal b1 = new BigDecimal(jbal.replaceAll(",",""));
        BigDecimal b2 = new BigDecimal(ztrAmount);
        BigDecimal b3 = new BigDecimal(zvamount);
        zbig = b2.compareTo(b3);
        if (zbig == 0) {
            BigDecimal b5 = b1.setScale(2, BigDecimal.ROUND_UP);  //*** b2 and b3 are equal
        }
        if (zbig == 1) {
                BigDecimal b4 = b2.subtract(b3); //*** b2 greater than b3
                b1 = b1.subtract(b4);
        }

        if (zbig == -1) {
            BigDecimal b4 = b3.subtract(b2);  //*** b3 greater than b2
            b1 = b1.add(b4);
        }

        BigDecimal b5 = b1.setScale(2, BigDecimal.ROUND_UP);

        String runBalance = String.valueOf(b5); //convert to String

        Cursor zup = zb.rawQuery("SELECT runBalance FROM account WHERE acctNumber ='" + vAcct.getText() + "'", null);
        if (zup.moveToFirst()) {
            zb.execSQL("UPDATE account SET runBalance='" + runBalance + "' WHERE acctNumber='" + vAcct.getText() + "'");
            //   Toast.makeText(this, "Run Balance Was UPDATED ! " + zBal + " " + runBalance, Toast.LENGTH_LONG).show();
        }

        //***** To Transactions List ********
        Intent dlist = new Intent(TransUpdel.this, TransLista.class);
        dlist.putExtra("xaccount", vAcct.getText().toString());
        dlist.putExtra("xfname", vfname.getText().toString());
        dlist.putExtra("xbalan", runBalance);
        startActivity(dlist);
        finish();
    }

    public void zCredit() {

        TextView vAcct = (TextView) findViewById(R.id.jAcct);
        TextView vfname = (TextView) findViewById(R.id.jOwner);
        TextView vBalan = (TextView) findViewById(R.id.jBalance);
        zvBalan = vBalan.getText().toString().trim();
        String jbal = zvBalan.substring(1); //*** Removes $ sign


        Cursor z = zb.rawQuery("SELECT * FROM transactions WHERE _id ='" + jz_id + "'", null);
        if (z.moveToFirst()) {
            zb.execSQL("DELETE FROM transactions WHERE _id='" + jz_id + "'");

            Toast.makeText(this, "Transaction DELETED !!", Toast.LENGTH_SHORT).show();
        } else {
            //     Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            //   zMessage("Error ! - Account Does Not Exist", "Please, Enter a Valid ACCT #", zicon);
            Toast.makeText(this, "Transaction NOT EXIST !!", Toast.LENGTH_SHORT).show();
        }

        //***** Update account balance - adding deleted trans amount ******

        String ztrAmount = jAmount.getText().toString().trim();
        //  Double xAmount = Double.parseDouble(ztrAmount);  //Convert to Double

        if (jAmount.getText().toString().trim().length() == 0) {
            //    Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            //    zMessage("Error ! - Transaction Amount", "Please, Enter a Valid Amount", zicon);
            Toast.makeText(this, "Transaction Amount is INVALID !!", Toast.LENGTH_SHORT).show();
            return;
        }

        // BigDecimal b1 = new BigDecimal(jbal);
        BigDecimal b1 = new BigDecimal(jbal.replaceAll(",",""));
        BigDecimal b2 = new BigDecimal(ztrAmount);
        b1 = b1.add(b2);
        BigDecimal b3 = b1.setScale(2, BigDecimal.ROUND_UP);

        String runBalance = String.valueOf(b3); //convert to String


        Cursor zup = zb.rawQuery("SELECT runBalance FROM account WHERE acctNumber ='" + vAcct.getText() + "'", null);
        if (zup.moveToFirst()) {
            zb.execSQL("UPDATE account SET runBalance='" + runBalance + "' WHERE acctNumber='" + vAcct.getText() + "'");
            //   Toast.makeText(this, "Run Balance Was UPDATED ! " + zBal + " " + runBalance, Toast.LENGTH_LONG).show();
        }


        //***** To Transactions List ********
        Intent dlist = new Intent(TransUpdel.this, TransLista.class);
        dlist.putExtra("xaccount", vAcct.getText().toString());
        dlist.putExtra("xfname", vfname.getText().toString());
        dlist.putExtra("xbalan", runBalance);
        startActivity(dlist);
        finish();

    } //*** End CREDIT from Delete button ******

    public void zDebit() {
        TextView vAcct = (TextView) findViewById(R.id.jAcct);
        TextView vfname = (TextView) findViewById(R.id.jOwner);
        TextView vBalan = (TextView) findViewById(R.id.jBalance);
        zvBalan = vBalan.getText().toString().trim();
        String jbal = zvBalan.substring(1); //*** Removes $ sign


        Cursor z = zb.rawQuery("SELECT * FROM transactions WHERE _id ='" + jz_id + "'", null);
        if (z.moveToFirst()) {

            zb.execSQL("DELETE FROM transactions WHERE _id='" + jz_id + "'");

            Toast.makeText(this, "Transaction DELETED !!", Toast.LENGTH_SHORT).show();
        } else {
            //     Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            //   zMessage("Error ! - Account Does Not Exist", "Please, Enter a Valid ACCT #", zicon);
            Toast.makeText(this, "Transaction NOT EXIST !!", Toast.LENGTH_SHORT).show();
        }

        //***** Update account balance - adding deleted trans amount ******

        String ztrAmount = jAmount.getText().toString().trim();
        //  Double xAmount = Double.parseDouble(ztrAmount);  //Convert to Double

        if (jAmount.getText().toString().trim().length() == 0) {
            //    Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            //    zMessage("Error ! - Transaction Amount", "Please, Enter a Valid Amount", zicon);
            Toast.makeText(this, "Transaction Amount is INVALID !!", Toast.LENGTH_SHORT).show();
            return;
        }

        // BigDecimal b1 = new BigDecimal(jbal);
        BigDecimal b1 = new BigDecimal(jbal.replaceAll(",",""));
        BigDecimal b2 = new BigDecimal(ztrAmount);
        b1 = b1.subtract(b2);
        BigDecimal b3 = b1.setScale(2, BigDecimal.ROUND_UP);

        String runBalance = String.valueOf(b3); //convert to String


        Cursor zup = zb.rawQuery("SELECT runBalance FROM account WHERE acctNumber ='" + vAcct.getText() + "'", null);
        if (zup.moveToFirst()) {
            zb.execSQL("UPDATE account SET runBalance='" + runBalance + "' WHERE acctNumber='" + vAcct.getText() + "'");
            //   Toast.makeText(this, "Run Balance Was UPDATED ! " + zBal + " " + runBalance, Toast.LENGTH_LONG).show();
        }

        //***** To Transactions List ********
        Intent dlist = new Intent(TransUpdel.this, TransLista.class);
        dlist.putExtra("xaccount", vAcct.getText().toString());
        dlist.putExtra("xfname", vfname.getText().toString());
        dlist.putExtra("xbalan", runBalance);
        startActivity(dlist);
        finish();

    } //*** End DEBIT from Delete button ******

    public void zClear() {
        jAmount.setText("");
        jNotes.setText("");
    }


} //**** End Transaction Update/delete Class
