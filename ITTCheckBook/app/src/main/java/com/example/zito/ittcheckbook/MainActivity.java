package com.example.zito.ittcheckbook;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
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
public class MainActivity extends Activity implements View.OnClickListener {

    EditText acctNumber, firstName, lastName, bankName, bankBalance, acctNotes;
    Button btnAdd, btnView, btnUpdate, btnList, btnDelete;

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
        btnView = (Button) findViewById(R.id.btnView);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnList = (Button) findViewById(R.id.btnList);

        db = new Zacct_Helper(this);
        zb = db.getWritableDatabase();

        // *** Calling Listners  *****
        btnAdd.setOnClickListener(this);
        btnView.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //***** Add Records *****
        if (v == btnAdd) {
            insert();
            clearText();

        }

        //***** VIEW One Record Details *****
        if (v == btnView) {
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
                acctNotes.setText(z.getString(6));

            } else {

                Toast.makeText(this, "Account Not Found !", Toast.LENGTH_SHORT).show();
                clearText();
            }
        }

        //****** UPDATE Record ********
        if (v == btnUpdate) {
            TextView acctDate = (TextView) findViewById(R.id.acctDate);
            if (acctNumber.getText().toString().trim().length() == 0) {
                Toast.makeText(this, "Account Not Found !!", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor z = zb.rawQuery("select * from account where acctNumber ='" + acctNumber.getText() + "'", null);
            if (z.moveToFirst()) {
                zb.execSQL("UPDATE account SET firstName='" + firstName.getText()
                        + "',lastName='" + lastName.getText() + "',bankName='" + bankName.getText()
                        + "',bankBalance='" + bankBalance.getText() + "',acctDate='" + acctDate.getText()
                        + "',acctNotes='" + acctNotes.getText()
                        + "' WHERE acctNumber='" + acctNumber.getText() + "'");

                Toast.makeText(this, "Account Was UPDATED !!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Invalid Account Number !!", Toast.LENGTH_SHORT).show();

            clearText();
        }

        //***** DELETE Record *****
        if (v == btnDelete) {
            if (acctNumber.getText().toString().trim().length() == 0) {
                Toast.makeText(this, "Account Not Found !!", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor z = zb.rawQuery("select * from account where acctNumber ='" + acctNumber.getText() + "'", null);
            if (z.moveToFirst()) {
                zb.execSQL("DELETE FROM account WHERE acctNumber='" + acctNumber.getText() + "'");

                Toast.makeText(this, "Account Was DELETED !!", Toast.LENGTH_SHORT).show();
                clearText();
            } else {
                Toast.makeText(this, "Invalid Account Number !!", Toast.LENGTH_SHORT).show();
                clearText();
            }
        }

        //***** LIST All Records *****
        if (v == btnList) {
            Cursor z = zb.rawQuery("SELECT * FROM account", null);
            if (z.getCount() == 0) {
                //  showMessage("Error - View All  Records", "No records were found...");
                Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
                zMessage("Error ! - No Records", "Database has NO Records", zicon);
               // Toast.makeText(this, "NO Records Found !!", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuilder buffer = new StringBuilder();
            while (z.moveToNext())

            {
                buffer.append("Acct.Number: ").append(z.getString(0)).append("\n");
                buffer.append("Name: ").append(z.getString(1)).append(" ").append(z.getString(2)).append("\n");
                buffer.append("Bank Name: ").append(z.getString(3)).append("\n");
                buffer.append("Bank Balance: ").append(z.getString(4)).append("\n\n");
            }

            zList("Accounts Detail", buffer.toString());
            clearText();
        }

    }  //** End of OnClick View

    //******To Transactions OnClick Button *****
    public void btnTransactions(View v) {

        TextView acctDate = (TextView) findViewById(R.id.acctDate);
        if (acctNumber.getText().toString().trim().length() == 0) {
            Drawable zicon = ResourcesCompat.getDrawable(getResources(), R.drawable.error, null);
            zMessage("Error ! - No Record Found", "Please enter an Account Number", zicon);
         //   Toast.makeText(this, "Please enter an Account Number !", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MainActivity.this, Transactions.class);

        intent.putExtra("xaccount", acctNumber.getText().toString());
        intent.putExtra("xfname", firstName.getText().toString());
        intent.putExtra("xbalan", bankBalance.getText().toString());

        startActivity(intent);
    }

    //***** ADD Records *****
    private void insert() {
        TextView acctDate = (TextView) findViewById(R.id.acctDate);
        String actnumber = acctNumber.getText().toString().trim();
        String fname = firstName.getText().toString().trim();
        String lname = lastName.getText().toString().trim();
        String bkname = bankName.getText().toString().trim();
        String bkbalance = bankBalance.getText().toString().trim();
        String actdate = acctDate.getText().toString().trim();
        String actnotes = acctNotes.getText().toString().trim();
        db.addAccount(actnumber, fname, lname, bkname, bkbalance, actdate, actnotes);
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
        //acctDate.setText("");
        acctNotes.setText("");
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

    //***** My Defined Function - Display Aler message box  *******
    public void zMessage(String title, String message, Drawable zicon)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(zicon);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        //return;
    }

}  //End of Program

// *****  Code below do not apply for the current project **********
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
*/
