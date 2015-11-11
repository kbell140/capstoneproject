package com.example.zito.ittcheckbook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Julio C. on 11/7/2015.
 */
public class TransLista extends AppCompatActivity {
    Button btnAdd;
    ListView listAccts;
    TextView jtid, jtacct, jttype, jtamount, jtdate;
    String zvAcct, zvBalan;

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

        //****** If List item is cliked *******
        ///***=============================**
        listAccts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

             //   TextView jtacct = (TextView) findViewById(R.id.z_tacct);
              //  jtacct.setText(getIntent().getExtras().getString("xaccount"));
              //  TextView vfname = (TextView) findViewById(R.id.zOwner);
             //   vfname.setText(getIntent().getExtras().getString("xfname"));
              //  TextView vBalan = (TextView) findViewById(R.id.trBalance);
              //  vBalan.setText(getIntent().getExtras().getString("xbalan"));
/*

                zvAcct = jtacct.getText().toString().trim();
                zvBalan = vBalan.getText().toString().trim();

                Double xBalan = Double.parseDouble(zvBalan);
                DecimalFormat zcur = new DecimalFormat("$###,###.##");
                String zBal = zcur.format(xBalan);
                vBalan.setText(zBal);
*/

                Intent iput = new Intent(TransLista.this, TransUpdel.class);
                iput.putExtra("xaccount", vAcct);
                iput.putExtra("xfname", vfname);
                iput.putExtra("xbalan", vBalan);
                startActivity(iput);


            }
        });
    }
}
