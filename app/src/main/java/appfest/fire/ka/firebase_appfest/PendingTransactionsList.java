package appfest.fire.ka.firebase_appfest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by mac on 07/06/17.
 */

public class PendingTransactionsList extends Activity {

    private static class Database extends SQLiteOpenHelper {
        private static final String DB_NAME = "payJioDatabase.db";
        private static final int DB_VERSION = 2;

        public Database(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            Log.e("in constructor","!!");

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            updateDatabase(db, 0, DB_VERSION);
            Log.e("createee","database");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            updateDatabase(db, oldVersion, newVersion);
        }


        private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 2) {
                db.execSQL("DROP TABLE IF EXISTS " + "iiiiii");
            }
            db.execSQL("CREATE TABLE IF NOT EXISTS " + "iiiiii" + " ( "
                    + "_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "Amount REAL, "
                    + "Date TEXT, "
                    + "Fro TEXT, "
                    + "Tom TEXT "
                    +")" );
            db.execSQL("CREATE TABLE IF NOT EXISTS " + "walley " + " ( "
                    + "Amount REAL "
                    +" )" );
            Log.e("create table","done");


        }
    }

    ListView listView ;
    String[] popup;
    Integer i=0;
    ProgressDialog progressDialog1,progressDialog;
    public Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liviewtransactions);
        listView = (ListView) findViewById(R.id.list);

        SQLiteDatabase db;
        PendingTransactionsList.Database ka = new Database(PendingTransactionsList.this);
        try {
            db = ka.getWritableDatabase();
        } catch (SQLiteException e) {
            db = null;
        }
        if (db != null) {


            cursor = db.rawQuery("SELECT " +
                            "_ID, " +
                            "Amount, " +
                            "Date, " +
                            "Fro, " +
                            "Tom " +
                            "FROM " + "iiiiii"
                    //		+ " ORDER BY _ID DESC LIMIT " + mMaxSearchHistorySize
                    , null);

                if (cursor != null) {
                    showProcessDialog1();
                }

                    if (cursor.getCount() > 0) {
                        int k = cursor.getCount();
                        popup = new String[k];
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            Double am = cursor.getDouble(1);
                            popup[i]=cursor.getString(2)+"\n"+am.toString();
                            cursor.moveToNext();
                            i++;
                        }
                        make();
                        progressDialog1.dismiss();

                    } else {
                        //no pending transactions
                        progressDialog1.dismiss();

                    }
                    cursor.close();
                }


        // Get ListView object from xml


        // Defined Array values to show in ListView

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        if(popup!=null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, popup);


            // Assign adapter to ListView
            listView.setAdapter(adapter);
        }

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();

            }

        });
    }
    @Override
    public void onBackPressed() {
        // your code.
        Intent d = new Intent(PendingTransactionsList.this,StartScreen.class);
        startActivity(d);
    }
    private void showProcessDialog1() {
        progressDialog1 = new ProgressDialog(this);
        progressDialog1.setTitle("Please Wait");
        progressDialog1.setMessage("Loading...");
        progressDialog1.show();
    }
    public void make(){
        if(popup!=null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, popup);


            // Assign adapter to ListView
            listView.setAdapter(adapter);
        }

    }


}
