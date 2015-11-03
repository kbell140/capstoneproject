package com.example.zito.ittcheckbook;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Zito on 10/27/2015.
 */
public class Transactions extends Activity implements View.OnClickListener {

    EditText trId, trDate, trType, trAmount, trNotes;
    Button btnAdd, btnView, btnUpdate, btnDelete, btnExit;

    String zvAcct = "";
    String zvBalan = "";
    private int zId = 0;

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
        TextView vBalan = (TextView)findViewById(R.id.trBalance);
        vBalan.setText(getIntent().getExtras().getString("xbalan"));

        TextView vTrId = (TextView) findViewById(R.id.trId);
        zvAcct = vAcct.getText().toString().trim();
        zvBalan = vBalan.getText().toString().trim();

        trDate = (EditText) findViewById(R.id.trDate);
        trType = (EditText) findViewById(R.id.trType);
        trAmount = (EditText) findViewById(R.id.trAmount);
        trNotes = (EditText) findViewById(R.id.trNotes);

        trDate.requestFocus();
        // *** Button on the screen
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnView = (Button) findViewById(R.id.btnView);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnExit = (Button) findViewById(R.id.btnExit);

        // *** Calling Listners  *****
        btnAdd.setOnClickListener(this);
        btnView.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == btnAdd) {
                 tranInsert();
        }

    } //***** End of onClick Listener *****


    public void tranInsert() {

        String ztrAcct = zvAcct;
        String ztrDate = trDate.getText().toString().trim();
        String ztrType = trType.getText().toString().trim();
        String ztrAmount = trAmount.getText().toString().trim();
        String ztrNotes = trNotes.getText().toString().trim();
        db.addtrans(ztrAcct, ztrType, ztrAmount, ztrDate, ztrNotes);
        Toast.makeText(this, "Transaction created !", Toast.LENGTH_SHORT).show();
    }
}
