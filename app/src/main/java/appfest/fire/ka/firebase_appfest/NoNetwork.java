
package appfest.fire.ka.firebase_appfest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class NoNetwork extends AppCompatActivity {
    // UI
    private TextView text;

    // QREader
    private SurfaceView mySurfaceView;
    private QREader qrEader;
    private Wallet w1,w2;
    private String from,to;
    private List<Transaction> t1,t2;
    private String wallet;
    private ProgressDialog progressDialog,progressDialog1;
    private String id,amount,wallet2;
    private int temp =0;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 10001;



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


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,
                appfest.fire.ka.firebase_appfest.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            Log.e("permission", "Camera permission granted");

            ActivityCompat.requestPermissions(this,
                    new String[]{appfest.fire.ka.firebase_appfest.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

        }
        setContentView(R.layout.reader_main);

        text = (TextView) findViewById(R.id.code_info);

        final Button stateBtn = (Button) findViewById(R.id.btn_start_stop);
        stateBtn.setText("Continue");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        final String mUserId = user1.getUid();
        Log.e("muser",mUserId);


 /*       mDatabase.child("users").child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                w1 = dataSnapshot.getValue(Wallet.class);
                t1 = w1.getTr();
                to = w1.getName();
                wallet = w1.getAmount().toString();
                progressDialog1.dismiss();

                Log.e("Wallet",wallet);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog1.dismiss();


            }
        });

*/

        stateBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (qrEader.isCameraRunning()) {
                                                Database k = new Database(NoNetwork.this);

                                                SQLiteDatabase db;

                                                try {
                                                    db = k.getWritableDatabase();
                                                } catch (SQLiteException e) {
                                                    db = null;
                                                }
                                                String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                                db.execSQL("INSERT INTO " + "iiiiii" + "("
                                                        + "Amount, "
                                                        + "Date, "
                                                       + "Fro, "
                                                        + "Tom"
                                                        + ") Values" + "('" + amount
                                                       + "', '" + mydate
                                                        + "', '" + id
                                                        + "', '" + mUserId
                                          //              + "',''" + id + "','" + mUserId
                                                        + "');");
                                                Intent ka = new Intent(NoNetwork.this,PendingTransactionsList.class);
                                                startActivity(ka);
                                            }
                                            SQLiteDatabase db;
                                            Database k = new Database(NoNetwork.this);
                                            try {
                                                db = k.getWritableDatabase();
                                            } catch (SQLiteException e) {
                                                db = null;
                                            }
                                            Cursor cursor = db.rawQuery("SELECT " +
                                                            "Amount " +
                                                            "FROM " + "walley"
                                                    //		+ " ORDER BY _ID DESC LIMIT " + mMaxSearchHistorySize
                                                    , null);

                                            if (cursor != null) {

                                                if (cursor.getCount() > 0) {
                                                    Log.e("Query", "here");

                                                    cursor.moveToFirst();
                                                    Double wallamo = cursor.getDouble(0);
                                                    Double add_amo = Double.parseDouble(amount);
                                                    Double new_amount = wallamo+add_amo;
                                                    try {
                                                        db = k.getWritableDatabase();
                                                    } catch (SQLiteException e) {
                                                        db = null;
                                                    }
                                                    db.execSQL("DELETE FROM " + "walley");
                                                    db.execSQL("INSERT INTO " + "walley" + "("
                                                            + "Amount"
                                                            + ") Values" + "('" + new_amount
                                                            + "');");






                                                }
                                            }
                                        }
                                    });


/*

        // change of reader state in dynamic
        stateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qrEader.isCameraRunning()) {
                    showProcessDialog1();
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            w2 = dataSnapshot.getValue(Wallet.class);
                            wallet2 = w2.getAmount().toString();
                            from = w2.getName();
                            t2 = w2.getTr();

                            progressDialog1.dismiss();
                            Log.e("Wallet2",wallet2);
                            processTransaction();




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog1.dismiss();


                        }
                    });
                    stateBtn.setText("Start QREader");
                    qrEader.stop();
                }
                else {
                    stateBtn.setText("Stop QREader");
                    qrEader.start();
                }


            }
        });
        */

        stateBtn.setVisibility(View.VISIBLE);

        Button restartbtn = (Button) findViewById(R.id.btn_restart_activity);
        restartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoNetwork.this, Reader.class));
                finish();
            }
        });


        // Setup SurfaceView
        // -----------------
        // Init QREader
        // ------------
        mySurfaceView = (SurfaceView) findViewById(R.id.camera_view);

        // Init QREader

        qrEader = new QREader.Builder(NoNetwork.this, mySurfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                Log.d("QREader", "Value : " + data);
                String Decoded="";
                //System.out.println("INDEX555:"+text1);
                String text1 = data;
                int split = text1.indexOf("==");
                System.out.println("INDEX:"+split);
                int k = text1.length();
                String hexString = text1.substring(0,split+2);
                System.out.println("INDEX444:"+hexString);


                String rest = text1.substring(split+3,k);
                //byte[] yourBytes = new BigInteger(hexString, 16).toByteArray();*/

                byte[] yourBytes     = Base64.decode(hexString,Base64.DEFAULT);
                Log.e("GGG",yourBytes.toString());

                byte[] yourBytes2     = Base64.decode(rest, Base64.DEFAULT);

                //byte[] yourBytes2 = new BigInteger(rest, 16).toByteArray();
                SecretKey originalKey = new SecretKeySpec(yourBytes, 0, yourBytes.length, "AES");
                try{
                    AESEncryption K = new AESEncryption();
                    String decryptedText = K.decryptText(yourBytes2, originalKey);

                    System.out.println("Descrypted Text LAST SCANNED:"+decryptedText);

                    Decoded = decryptedText;


                }


                catch (Exception e){

                }









                int index = Decoded.indexOf(",");
                int length = Decoded.length();
                id = Decoded.substring(0,index);
                Log.e("ID",id);
                //Toast.makeText(Reader.this,id,Toast.LENGTH_SHORT);
                amount = Decoded.substring(index+1,length);
                Log.e("AMOUNT",amount);

                text.post(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(amount);
                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(mySurfaceView.getHeight())
                .width(mySurfaceView.getWidth())
                .build();

    }


    @Override
    public void onBackPressed() {
        // your code.
        Intent d = new Intent(NoNetwork.this,StartScreen.class);
        startActivity(d);
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Init and Start with SurfaceView
        // -------------------------------
        qrEader.initAndStart(mySurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Cleanup in onPause()
        // --------------------
        qrEader.releaseAndCleanup();
    }
     }









