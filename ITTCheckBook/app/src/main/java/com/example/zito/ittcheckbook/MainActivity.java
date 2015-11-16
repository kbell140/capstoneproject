package com.example.zito.ittcheckbook;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Julio C. on 10/13/2015  *** Updated 11/01/2015.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText acctNumber, firstName, lastName, bankName, bankBalance, acctNotes;
    Button btnAdd, btnUpdate, btnDelete;
    TextView runBalance;

    Zacct_Helper db;
    SQLiteDatabase zb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // *** Display the Account Info screen  *****
        acctNumber = (EditText) findViewById(R.id.acctNumber);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        bankName = (EditText) findViewById(R.id.bankName);
        bankBalance = (EditText) findViewById(R.id.bankBalance);
        runBalance = (TextView) findViewById(R.id.runBalance);

        TextView acctDate = (TextView) findViewById(R.id.acctDate);
        //****** Sets the current date - Date Dialog **********
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");  //current date
        acctDate.setText(sdf.format(new Date()));
        acctDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(v);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "Date Picker");
            }

        });

        acctNotes = (EditText) findViewById(R.id.acctNotes);
        // *** Button on the screen
        btnAdd = (Button) findViewById(R.id.btnAdd);
        //btnView = (Button) findViewById(R.id.btnView);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
       // btnList = (Button) findViewById(R.id.btnList);

        db = new Zacct_Helper(this);
        zb = db.getWritableDatabase();

        // *** Calling Listners  *****
        btnAdd.setOnClickListener(this);
       // btnView.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
      //  btnList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //***** Add Records *****
        if (v == btnAdd) {
            insert();
            clearText();

        }

        //****** UPDATE Record ********
        if (v == btnUpdate) {
            TextView acctDate = (TextView) findViewById(R.id.acctDate);
            if (acctNumber.getText().toString().trim().length() == 0) {
                Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
                zMessage("Error ! - Invalid Account #", "Please, Enter a Valid ACCT #", zicon);
                return;
            }

            Cursor z = zb.rawQuery("select * from account where acctNumber ='" + acctNumber.getText() + "'", null);
            if (z.moveToFirst()) {
                zb.execSQL("UPDATE account SET firstName='" + firstName.getText()
                        + "',lastName='" + lastName.getText() + "',bankName='" + bankName.getText()
                        + "',bankBalance='" + bankBalance.getText() + "',acctDate='" + acctDate.getText()
                        + "',runBalance='" + runBalance.getText() + "',acctNotes='" + acctNotes.getText()
                        + "' WHERE acctNumber='" + acctNumber.getText() + "'");

                Toast.makeText(this, "Account Was UPDATED !!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Invalid Account Number !!", Toast.LENGTH_SHORT).show();

            clearText();
        }

        //***** DELETE Record *****
        if (v == btnDelete) {
            if (acctNumber.getText().toString().trim().length() == 0) {
                Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
                zMessage("Error ! - Invalid Account #", "Please, Enter a Valid ACCT #", zicon);
                return;
            }

            Cursor z = zb.rawQuery("select * from account where acctNumber ='" + acctNumber.getText() + "'", null);
            if (z.moveToFirst()) {
                zb.execSQL("DELETE FROM account WHERE acctNumber='" + acctNumber.getText() + "'");

                Toast.makeText(this, "Account Was DELETED !!", Toast.LENGTH_SHORT).show();
                clearText();
            } else {
                Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
                zMessage("Error ! - Account Does Not Exist", "Please, Enter a Valid ACCT #", zicon);
                clearText();
            }
        }

    }  //** End of OnClick View

    //******To Transactions OnClick Button *****
 /*   public void btnTransactions(View v) {

        if (acctNumber.getText().toString().trim().length() == 0) {
            Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            zMessage("Error ! - No Record Found", "Please enter an Account Number", zicon);
            return;
        }
        /*//***** Check database before transactions *****
        Cursor z = zb.rawQuery("SELECT * FROM account WHERE acctNumber ='" + acctNumber.getText() + "'", null);
        if (z.moveToFirst()) {

            Intent intent = new Intent(MainActivity.this, Transactions.class);

            intent.putExtra("xaccount", acctNumber.getText().toString());
            intent.putExtra("xfname", firstName.getText().toString());
            intent.putExtra("xbalan", runBalance.getText().toString());

            startActivity(intent);

        //    zb.execSQL("DELETE FROM account WHERE acctNumber='" + acctNumber.getText() + "'");

         //   Toast.makeText(this, "Account Was DELETED !!", Toast.LENGTH_SHORT).show();
         //   clearText();
        } else {
            Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            zMessage("Error ! - Enter a valid ACCT #.", ">>> Then Click VIEW Button", zicon);

 //           Toast.makeText(this, "Invalid Account Number !!", Toast.LENGTH_SHORT).show();
            clearText();
 //           return;
        }

    }
*/
    //***** ADD Records *****
    private void insert() {
        if (acctNumber.getText().toString().trim().length() == 0) {
            Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            zMessage("Error ! - Invalid Info.", "Please enter values for all fields", zicon);
            return;
        }

        TextView acctDate = (TextView) findViewById(R.id.acctDate);

        String actnumber = acctNumber.getText().toString().trim();
        String fname = firstName.getText().toString().trim();
        String lname = lastName.getText().toString().trim();
        String bkname = bankName.getText().toString().trim();
        String bkbalance = bankBalance.getText().toString().trim();
        String rr = bkbalance;
        String rnbalance = rr;
        String actdate = acctDate.getText().toString().trim();
       // String rnbalance = runBalance.getText().toString().trim();
        String actnotes = acctNotes.getText().toString().trim();
        db.addAccount(actnumber, fname, lname, bkname, bkbalance, actdate, rnbalance, actnotes);
        Toast.makeText(this, "The Account was created !", Toast.LENGTH_SHORT).show();
        acctDate.setText("");
    }

    //***** Clear fields from screen  *****
    public void clearText() {
        acctNumber.setText("");
        firstName.setText("");
        lastName.setText("");
        bankName.setText("");
        bankBalance.setText("");
        acctNotes.setText("");
        runBalance.setText("");
        acctNumber.requestFocus();
    }

    //***** List of Records *****
    public void zList(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setIcon(getResources().getDrawable(R.drawable.zitt));
        builder.setMessage(message);
        builder.show();
    }

    //***** My Defined Function - Display Alert message box  *******
    public void zMessage(String title, String message, Drawable zicon)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.zapp:
                zabout();
                break;
            case R.id.action_settings:
                jtransa();
                break;
            case R.id.viewrec:
                jviewRec();
                break;
            case R.id.action_list:
                jlistaccts();
                break;
            case R.id.zblank:
                clearText();
                break;
            case R.id.barabout:
                zabout();
                break;
        }
        return true;

    }

    private void zabout() {
        Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.zxitt, null);
        zMessage("Captstone * Sep - Dec 2015", "** Created by: Julio Casachagua **", zicon);
        return;
    }

    private void jviewRec() {
        TextView acctDate = (TextView) findViewById(R.id.acctDate);
        if (acctNumber.getText().toString().trim().length() == 0) {
            Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            zMessage("Error ! - No Record Found", "Please enter a valid Account #", zicon);
            //       Toast.makeText(this, "Please enter an Account Number !", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor z = zb.rawQuery("SELECT * FROM account WHERE acctNumber ='" + acctNumber.getText() + "'", null);
        if (z.moveToFirst()) {
            firstName.setText(z.getString(1));
            lastName.setText(z.getString(2));
            bankName.setText(z.getString(3));
            bankBalance.setText(z.getString(4));
            acctDate.setText(z.getString(5));
            runBalance.setText(z.getString(6));
            acctNotes.setText(z.getString(7));

        } else {
            Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            zMessage("Error ! - No Record Found", "Please enter a valid Account #", zicon);
            //  Toast.makeText(this, "Account Not Found !", Toast.LENGTH_SHORT).show();
            clearText();
        }

    }

    private void jtransa() {

        if (acctNumber.getText().toString().trim().length() == 0 ||
                firstName.getText().toString().trim().length() == 0 ||
                lastName.getText().toString().trim().length() == 0 ||
                bankName.getText().toString().trim().length() == 0 ) {
            Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.warning, null);
            zMessage("Alert ! - Missing Values", "Please enter All Values", zicon);
            return;
        }
        //***** Check database before transactions *****
        Cursor z = zb.rawQuery("SELECT * FROM account WHERE acctNumber ='" + acctNumber.getText() + "'", null);
        if (z.moveToFirst()) {

            Intent intent = new Intent(MainActivity.this, Transactions.class);

            intent.putExtra("xaccount", acctNumber.getText().toString());
            intent.putExtra("xfname", firstName.getText().toString());
            intent.putExtra("xbalan", runBalance.getText().toString());

            startActivity(intent);

            //    zb.execSQL("DELETE FROM account WHERE acctNumber='" + acctNumber.getText() + "'");

            //   Toast.makeText(this, "Account Was DELETED !!", Toast.LENGTH_SHORT).show();
            //   clearText();
        } else {
            Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            zMessage("Error ! - Enter a valid ACCT #.", ">>> Then Click VIEW Button", zicon);

            //           Toast.makeText(this, "Invalid Account Number !!", Toast.LENGTH_SHORT).show();
            clearText();
            //           return;
        }
    }

    private void jlistaccts() {
        Cursor z = zb.rawQuery("SELECT * FROM account", null);
        if (z.getCount() == 0) {
            Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            zMessage("Error ! - No Records", "Database has NO Records", zicon);
            return;
        }

        StringBuilder buffer = new StringBuilder();
        while (z.moveToNext())

        {
            buffer.append("Acct.Number: ").append(z.getString(0)).append("\n");
            buffer.append("Name: ").append(z.getString(1)).append(" ").append(z.getString(2)).append("\n");
            buffer.append("Bank Name: ").append(z.getString(3)).append("\n");
            buffer.append("Starting Balance: ").append(z.getString(4)).append("\n");
            buffer.append("Date: ").append(z.getString(5)).append("\n");
            buffer.append("Running  Balance: ").append(z.getString(6)).append("\n");
            buffer.append("Notes: ").append(z.getString(7)).append("\n\n");
        }

        zList("Accounts Detail", buffer.toString());
        clearText();

    }

}  //End of Program
