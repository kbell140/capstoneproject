package com.example.zito.ittcheckbook;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    EditText acctNumber, firstName, lastName, bankName, bankBalance, txtDate, txtNotes;
    Button btnAdd, btnDelete, btnUpdate;
    Zacct_Helper db;

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
        txtDate = (EditText) findViewById(R.id.txtDate);
        txtNotes = (EditText) findViewById(R.id.txtNotes);
        // *** Button on the screen
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        db = new Zacct_Helper(this);

        // *** cALL THE LISTENERS  *****
        btnAdd.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == btnAdd) {
            insert();
            clearText();

        }
        if (v==btnUpdate)
        {

        }
    }

    private void insert()
    {
        //String zaccount = acctNumber.getText().toString();
        String actnumber = acctNumber.getText().toString().trim();
        String fname = firstName.getText().toString().trim();
        String lname = lastName.getText().toString().trim();
        String bkname = bankName.getText().toString().trim();
        String bkbalance = bankBalance.getText().toString().trim();
        String actdate = txtDate.getText().toString().trim();
        String actnotes = txtNotes.getText().toString().trim();
        db.addAccount(actnumber, fname, lname, bkname, bkbalance, actdate, actnotes);
        Toast.makeText(this, "The Account was created !", Toast.LENGTH_SHORT).show();

    }

    //** Clear fields on screen
    public void clearText()
    {
        acctNumber.setText("");
        firstName.setText("");
        lastName.setText("");
        bankName.setText("");
        bankBalance.setText("");
        txtDate.setText("");
        txtNotes.setText("");
    }
}


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
