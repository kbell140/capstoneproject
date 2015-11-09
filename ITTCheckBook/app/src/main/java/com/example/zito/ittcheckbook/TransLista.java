package com.example.zito.ittcheckbook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Julio C. on 11/7/2015.
 */
public class TransLista extends AppCompatActivity {
    Button btnAdd;
    ListView listAccts;

    Zacct_Helper db;
    SQLiteDatabase zb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tran);

        db = new Zacct_Helper(this);
        zb = db.getWritableDatabase();

        Intent intent = getIntent();
        final String vAcct = intent.getStringExtra("xaccount");
        final String vfname = intent.getStringExtra("xfname");
        final String vBalan = intent.getStringExtra("xbalan");

        btnAdd = (Button) findViewById(R.id.btnAdd);
        listAccts = (ListView) findViewById(R.id.listAccts);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iput = new Intent(TransLista.this, Transactions.class);
                iput.putExtra("xaccount", vAcct);
                iput.putExtra("xfname", vfname);
                iput.putExtra("xbalan", vBalan);
                startActivity(iput);
            }
        });

        //***** Get Data for List View ****
        Cursor cur = db.readData();
        String[] from = new String[]{db.TRAN_ID, db.TRAN_ACTN, db.TRAN_TYPE, db.TRAN_AMOUNT,
                db.TRAN_DATE, db.TRAN_NOTES};

        int[] to = new int[]{
                R.id.z_id, R.id.z_tacct, R.id.z_ttype, R.id.z_tamount, R.id.z_tdate, R.id.z_tnotes};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                TransLista.this, R.layout.list_format, cur, from, to);

        adapter.notifyDataSetChanged();
        listAccts.setAdapter(adapter);

    }
}
