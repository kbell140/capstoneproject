package com.example.zito.ittcheckbook;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Julio C. on 10/27/2015
 */
public class Transactions extends AppCompatActivity implements View.OnClickListener {

    EditText trAmount, trNotes;
    Button btnAdd;
    Spinner tr_tipos;

    String zvAcct = "";
    String zvBalan = "";
    String jjBalan = "";

    Zacct_Helper db;
    SQLiteDatabase zb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);

        db = new Zacct_Helper(this);
        zb = db.getWritableDatabase();

        // *** Display the Account Info screen  *****

        TextView vAcct = (TextView) findViewById(R.id.zAcct);
        vAcct.setText(getIntent().getExtras().getString("xaccount"));
        TextView vfname = (TextView) findViewById(R.id.zOwner);
        vfname.setText(getIntent().getExtras().getString("xfname"));
        TextView vBalan = (TextView) findViewById(R.id.trBalance);
        vBalan.setText(getIntent().getExtras().getString("xbalan"));

        zvAcct = vAcct.getText().toString().trim();
        zvBalan = vBalan.getText().toString().trim();
        zvBalan = zvBalan.replace(",","");

        Double xBalan = Double.parseDouble(zvBalan);
        DecimalFormat zcur = new DecimalFormat("$###,###.##");
        String zBal = zcur.format(xBalan);
        vBalan.setText(zBal);

        tr_tipos = (Spinner) findViewById(R.id.tipos);

        ArrayAdapter adap = ArrayAdapter.createFromResource(this, R.array.trans_tipos, android.R.layout.simple_spinner_dropdown_item);
        tr_tipos.setAdapter(adap);

        TextView trDate = (TextView) findViewById(R.id.trDate);
        //****** Sets the current date - Date Dialog **********
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");  //current date
        trDate.setText(sdf.format(new Date()));
        trDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(v);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "Date Picker");
            }

        });

        trAmount = (EditText) findViewById(R.id.trAmount);
        trNotes = (EditText) findViewById(R.id.trNotes);

        // *** Button on the screen
        btnAdd = (Button) findViewById(R.id.btnAdd);

        // *** Calling Button to add transactions  *****
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == btnAdd) {
            tranInsert();
        }

/*
        if (v == btnView) {
            TextView vAcct = (TextView) findViewById(R.id.zAcct);
            TextView vfname = (TextView) findViewById(R.id.zOwner);
            TextView vBalan = (TextView) findViewById(R.id.trBalance);
            zvBalan = vBalan.getText().toString().trim();
            String jbal = zvBalan.substring(1);

            Intent dlist = new Intent(Transactions.this, TransLista.class);
            dlist.putExtra("xaccount", vAcct.getText().toString());
            dlist.putExtra("xfname", vfname.getText().toString());
            dlist.putExtra("xbalan", jbal);
            startActivity(dlist);
            finish();
        }
*/

    } //***** End of onCreate *****

    //***** Transaction functions *******
    public void tranInsert() {

        // String ztrType = trType.getText().toString().trim();
        String ztrType = tr_tipos.getSelectedItem().toString();
        switch (ztrType) {
            case "CREDIT":
                creditBalance();
                clearTransScrn();
                break;
            case "DEBIT":
                debitBalance();
                clearTransScrn();

                break;
            default:
                Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
                zMessage("Error ! - Invalid Transaction Type", "Please, Select Debit/Credit", zicon);

        }
    }

    public void creditBalance() {
        TextView vAcct = (TextView) findViewById(R.id.zAcct);
        TextView vfname = (TextView) findViewById(R.id.zOwner);
        TextView trDate = (TextView) findViewById(R.id.trDate);

        String ztrAmount = trAmount.getText().toString().trim();
      //  Double xAmount = Double.parseDouble(ztrAmount);  //Convert to Double

        if (trAmount.getText().toString().trim().length() == 0) {
            Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            zMessage("Error ! - Transaction Amount", "Please, Enter a Valid Amount", zicon);
            return;
        }

        BigDecimal b1 = new BigDecimal(zvBalan);
        BigDecimal b2 = new BigDecimal(ztrAmount);
        b1 = b1.add(b2);
        BigDecimal b3 = b1.setScale(2, BigDecimal.ROUND_UP);

        String runBalance = String.valueOf(b3); //convert to String

        String ztrAcct = zvAcct;
        String ztrDate = trDate.getText().toString().trim();
        String ztrType = tr_tipos.getSelectedItem().toString();

        //****** ADD TRANSACTIONS  ************
        String ztrNotes = trNotes.getText().toString().trim();
        db.addtrans(ztrAcct, ztrType, ztrAmount, ztrDate, ztrNotes);

        Toast.makeText(this, "Transaction created !", Toast.LENGTH_SHORT).show();
        trDate.setText("");

        //*** Display updated Balance Amount *****
        TextView vBalan = (TextView) findViewById(R.id.trBalance);

        Double xBalan = Double.parseDouble(runBalance);
        DecimalFormat zcur = new DecimalFormat("$###,###.##");
        String zBal = zcur.format(xBalan);
        vBalan.setText(zBal);

        ///********** ACCOUNT TABLE UPDATE ******

        Cursor z = zb.rawQuery("SELECT runBalance FROM account WHERE acctNumber ='" + vAcct.getText() + "'", null);
        if (z.moveToFirst()) {
            zb.execSQL("UPDATE account SET runBalance='" + runBalance + "' WHERE acctNumber='" + vAcct.getText() + "'");
         //   Toast.makeText(this, "Run Balance Was UPDATED ! " + zBal + " " + runBalance, Toast.LENGTH_LONG).show();
        }

        //********* TO Transactions List *******
        Intent dlist = new Intent(Transactions.this, TransLista.class);
        dlist.putExtra("xaccount", vAcct.getText().toString());
        dlist.putExtra("xfname", vfname.getText().toString());
        dlist.putExtra("xbalan", runBalance);
        startActivity(dlist);

    }

    public void debitBalance() {
        TextView vAcct = (TextView) findViewById(R.id.zAcct);
        TextView vfname = (TextView) findViewById(R.id.zOwner);
        TextView trDate = (TextView) findViewById(R.id.trDate);

        String ztrAmount = trAmount.getText().toString().trim();
        //  Double xAmount = Double.parseDouble(ztrAmount);  //Convert to Double

        if (trAmount.getText().toString().trim().length() == 0) {
            Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            zMessage("Error ! - Transaction Amount", "Please, Enter a Valid Amount", zicon);
            return;
        }

        BigDecimal b1 = new BigDecimal(zvBalan);
        BigDecimal b2 = new BigDecimal(ztrAmount);
        b1 = b1.subtract(b2);
        BigDecimal b3 = b1.setScale(2, BigDecimal.ROUND_UP); //** two decimalplaces

        //Double jBalance = xBalan + xAmount;
        String runBalance = String.valueOf(b3); //convert to String

        String ztrAcct = zvAcct;
        String ztrDate = trDate.getText().toString().trim();
        String ztrType = tr_tipos.getSelectedItem().toString();

        //******* ADD Transactions  *********
        String ztrNotes = trNotes.getText().toString().trim();
        db.addtrans(ztrAcct, ztrType, ztrAmount, ztrDate, ztrNotes);

        Toast.makeText(this, "Transaction created !", Toast.LENGTH_SHORT).show();
        trDate.setText("");

        //*** Display updated Balance Amount *****

        TextView vBalan = (TextView) findViewById(R.id.trBalance);

        Double xBalan = Double.parseDouble(runBalance);
        DecimalFormat zcur = new DecimalFormat("$###,###.##");
        String zBal = zcur.format(xBalan);
        vBalan.setText(zBal);

        ///********** ACCOUNT TABLE UPDATE ******

        Cursor z = zb.rawQuery("SELECT runBalance FROM account WHERE acctNumber ='" + vAcct.getText() + "'", null);
        if (z.moveToFirst()) {
            zb.execSQL("UPDATE account SET runBalance='" + runBalance + "' WHERE acctNumber='" + vAcct.getText() + "'");
       //     Toast.makeText(this, "Balance Was UPDATED ! " + zBal + " " + runBalance, Toast.LENGTH_LONG).show();
        }

        //********* TO Transactions List *******
        Intent dlist = new Intent(Transactions.this, TransLista.class);
        dlist.putExtra("xaccount", vAcct.getText().toString());
        dlist.putExtra("xfname", vfname.getText().toString());
        dlist.putExtra("xbalan", runBalance);
        startActivity(dlist);

    }

 /*   public void btnExit(View v) {
        TextView vAcct = (TextView) findViewById(R.id.zAcct);
        TextView vfname = (TextView) findViewById(R.id.zOwner);
        TextView vBalan = (TextView) findViewById(R.id.trBalance);
        jjBalan = vBalan.getText().toString().trim();
        jjBalan = jjBalan.substring(1); //Removes first character $ from string

        Intent iput = new Intent(Transactions.this, MainActivity.class);
        iput.putExtra("xaccount", vAcct.getText().toString());
        iput.putExtra("xfname", vfname.getText().toString());
        iput.putExtra("xbalan", jjBalan);

        startActivity(iput);
       // finish();
    } /*//*** End of Transactions Class
*/
    public void clearTransScrn() {
        tr_tipos.setSelection(0);
        trAmount.setText("");
        trNotes.setText("");
        trAmount.requestFocus();
    }

    //***** Message Function *******
    public void zMessage(String title, String message, Drawable zicon)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(Transactions.this).create();
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setIcon(zicon);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tran, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.zapp:
                zabout();
                break;
            case R.id.zlistra:
                zlistra();
                break;
            case R.id.zaccts:
                zaccts();
                break;
            case R.id.zblank:
                clearTransScrn();
                break;
            case R.id.barabout:
                zabout();
                break;
        }
        return true;

    }
    private void zabout() {
        Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.zxitt, null);
        zMessage("Captstone Project" + "\n" + "** Sep - Dec 2015 **", "Created by:" + "\n" + "Julio Casachagua", zicon);
        return;
    }

    private void zlistra() {
        TextView vAcct = (TextView) findViewById(R.id.zAcct);
        TextView vfname = (TextView) findViewById(R.id.zOwner);
        TextView vBalan = (TextView) findViewById(R.id.trBalance);
        zvBalan = vBalan.getText().toString().trim();
        String jbal = zvBalan.substring(1);

        Intent dlist = new Intent(Transactions.this, TransLista.class);
        dlist.putExtra("xaccount", vAcct.getText().toString());
        dlist.putExtra("xfname", vfname.getText().toString());
        dlist.putExtra("xbalan", jbal);
        startActivity(dlist);
        finish();

    }

    private void zaccts() {
        TextView vAcct = (TextView) findViewById(R.id.zAcct);
        TextView vfname = (TextView) findViewById(R.id.zOwner);
        TextView vBalan = (TextView) findViewById(R.id.trBalance);
        jjBalan = vBalan.getText().toString().trim();
        jjBalan = jjBalan.substring(1); //Removes first character $ from string

        Intent iput = new Intent(Transactions.this, MainActivity.class);
        iput.putExtra("xaccount", vAcct.getText().toString());
        iput.putExtra("xfname", vfname.getText().toString());
        iput.putExtra("xbalan", jjBalan);

        startActivity(iput);

    }

} //*** End of Program
