
package appfest.fire.ka.firebase_appfest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by mac on 09/05/17.
 */

public class StartScreen extends Activity {
    Button btn1,btn2,btn3,btn4,btn5,btn6;
    String id,amount,id2,date;
    private Wallet w1,w2;
    private String from,to;
    private List<Transaction> t1,t2;
    private String wallet,wallet2;
    private ProgressDialog progressDialog,progressDialog1;
    private int temp = 0;
    private int temp1 = 0;
    private int temp22 = 0;
    private double am1,am2;
    public Cursor cursor;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        if(connected==true) {
            SQLiteDatabase db;
            Database ka = new Database(StartScreen.this);
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
                    Toast.makeText(StartScreen.this, "NO PENDING TRANSACTION", Toast.LENGTH_LONG);

                    if (cursor.getCount() > 0) {
                        showProcessDialog1();


                        Log.e("Query", "here");

                        cursor.moveToFirst();
                        Double amounti = cursor.getDouble(1);
                        amount=amounti.toString();
                        id = cursor.getString(3);
                        id2 = cursor.getString(4);
                        date = cursor.getString(2);
                        final int p_key = cursor.getInt(0);
                        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        // showProcessDialog1();
                        mDatabase.child("users").child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                w1 = dataSnapshot.getValue(Wallet.class);
                                t1 = w1.getTr();
                                to = w1.getName();
                                wallet = w1.getAmount().toString();
                                temp1=1;
                                mDatabase.child("users").child(id2).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        w2 = dataSnapshot.getValue(Wallet.class);
                                        wallet2 = w2.getAmount().toString();
                                        from = w2.getName();
                                        t2 = w2.getTr();
                                        Log.e("Wallet2",wallet2);
                                        temp22=1;
                                        StartScreen.Database k = new Database(StartScreen.this);

                                        SQLiteDatabase db;

                                        try {
                                            db = k.getWritableDatabase();
                                        } catch (SQLiteException e) {
                                            db = null;
                                        }
                                        Log.e("KUTTE KAMINEY","DELETE");

                                        db.execSQL("DELETE FROM " + "iiiiii"
                                                + " WHERE _ID = " + p_key );
                                        processTransaction();







                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        progressDialog1.dismiss();


                                    }
                                });



                                Log.e("Wallet",wallet);



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressDialog1.dismiss();


                            }
                        });



                    }
                }
            }

        }


        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text",messageText);
                if(messageText.contains("==")) {
                    Toast.makeText(StartScreen.this, "Message: " + messageText, Toast.LENGTH_LONG).show();
                    String Decoded = "";
                    //System.out.println("INDEX555:"+text1);
                    String text1 = messageText;
                    int split = text1.indexOf("==");
                    System.out.println("INDEX:" + split);
                    int k = text1.length();
                    String hexString = text1.substring(0, split + 2);
                    System.out.println("INDEX444:" + hexString);


                    String rest = text1.substring(split + 3, k);
                    //byte[] yourBytes = new BigInteger(hexString, 16).toByteArray();*/

                    byte[] yourBytes = Base64.decode(hexString, Base64.DEFAULT);
                    Log.e("GGG", yourBytes.toString());

                    byte[] yourBytes2 = Base64.decode(rest, Base64.DEFAULT);

                    //byte[] yourBytes2 = new BigInteger(rest, 16).toByteArray();
                    SecretKey originalKey = new SecretKeySpec(yourBytes, 0, yourBytes.length, "AES");
                    try {
                        AESEncryption K = new AESEncryption();
                        String decryptedText = K.decryptText(yourBytes2, originalKey);

                        System.out.println("Descrypted Text LAST SCANNED:" + decryptedText);

                        Decoded = decryptedText;


                    } catch (Exception e) {

                    }
                    Log.e("DECODED", Decoded);
                    int index = Decoded.indexOf(",");
                    int length = Decoded.length();
                    id = Decoded.substring(0, index);
                    Log.e("ID", id);
                    String temp = Decoded.substring(index + 1, length);
                    index = temp.indexOf(",");
                    length = temp.length();
                    amount = temp.substring(0, index);
                    Log.e("AMOUNT", amount);
                    String temp2 = temp.substring(index + 1, length);
                    Log.e("temp2", temp2);
                    int index1 = temp2.indexOf(",");
                    int length1 = temp2.length();
                    id2 = temp2.substring(0, index1);
                    Log.e("ID2", id2);
                    date = temp2.substring(index1 + 1, length1);
                    Log.e("DATE", date);
                    /*Intent process = new Intent(StartScreen.this, OfflineProcessing.class);
                    process.putExtra("from_id", id);
                    process.putExtra("amount", amount);
                    process.putExtra("to_id", id2);
                    process.putExtra("date", date);
                    //startActivity(process);*/
                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    showProcessDialog1();
                    mDatabase.child("users").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            w1 = dataSnapshot.getValue(Wallet.class);
                            t1 = w1.getTr();
                            to = w1.getName();
                            wallet = w1.getAmount().toString();
                            temp1=1;
                            showProcessDialog1();
                            mDatabase.child("users").child(id2).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    w2 = dataSnapshot.getValue(Wallet.class);
                                    wallet2 = w2.getAmount().toString();
                                    from = w2.getName();
                                    t2 = w2.getTr();
                                    Log.e("Wallet2",wallet2);
                                    temp22=1;
                                    progressDialog1.dismiss();
                                    processTransaction();







                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    progressDialog1.dismiss();


                                }
                            });



                            Log.e("Wallet",wallet);



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog1.dismiss();


                        }
                    });



















                }



            }
        });
        //// TODO: 07/06/17

        setContentView(R.layout.activity_launch);
        btn1 = (Button)findViewById(R.id.button1);
        btn2 = (Button)findViewById(R.id.button2);
        btn3 = (Button)findViewById(R.id.button3);
        btn4 = (Button)findViewById(R.id.button4);
        btn5 = (Button)findViewById(R.id.button5);
        btn6 = (Button)findViewById(R.id.button6);




    }
    @Override
    public void onBackPressed() {
        // your code.
        Intent d = new Intent(StartScreen.this,StartScreen.class);
        startActivity(d);
    }

    public void onClick(View v){

        if(v.getId() == R.id.button1){
            Intent intent = new Intent(StartScreen.this, Generator.class);
            startActivity(intent);

        }else if(v.getId() == R.id.button2){
            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            }
            else
                connected = false;
            if(connected==true) {
                Intent intent = new Intent(StartScreen.this, Reader.class);
                startActivity(intent);
            }
            else{
                //todo check network access
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(StartScreen.this);

                // Setting Dialog Title
                alertDialog.setTitle("Transaction");

                // Setting Dialog Message
                alertDialog.setMessage("Complete Via");

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("SMS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        // Write your code here to invoke YES event
                        Toast.makeText(getApplicationContext(), "Your Transaction Will Be Completed Via SMS soon", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StartScreen.this, ReaderOffline.class);
                        startActivity(intent);

                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("OFFLINE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        Toast.makeText(getApplicationContext(), "Pending Transactions (To Be Completed Soon)", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StartScreen.this, NoNetwork.class);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();

            }
        }else if(v.getId() == R.id.button3){
            FirebaseAuth auth;
            auth = FirebaseAuth.getInstance();
            auth.signOut();
            Intent intent = new Intent(StartScreen.this, Cameratest.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.button4){
            Intent intent = new Intent(StartScreen.this, MainActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.button5){
            //// TODO: 07/06/17  
            Intent intent = new Intent(StartScreen.this, PendingTransactionsList.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.button6){
            Intent intent = new Intent(StartScreen.this, ListViewAndroidExample.class);
            startActivity(intent);
        }

    }
    private void showProcessDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Money is being added to your wallet...");
        progressDialog.show();
    }
    private void showProcessDialog1() {
        progressDialog1 = new ProgressDialog(this);
        progressDialog1.setTitle("Please Wait");
        progressDialog1.setMessage("Loading...");
        progressDialog1.show();
    }
    private void processTransaction(){
        showProcessDialog();
        double Merchant = Double.valueOf(wallet2);
        double User = Double.valueOf(wallet);
        double price = Double.valueOf(amount);
        final double user_amount = User-price;
        final double Merchant_amount = Merchant+price;
        am1 = user_amount;
        am2 = Merchant_amount;
        //// TODO: 07/06/17
        if(user_amount<0||Merchant_amount<0){
            Log.e("INSUFFICIENT BALANCE!","SEND MESSAGE");
            progressDialog.dismiss();
            Toast.makeText(this,"Insufficient balance",Toast.LENGTH_LONG);
        }
        else if(temp==1){
            progressDialog.dismiss();
            Log.e("KUTTE KAMINEY","SEND MESSAGE");
            Intent i = new Intent(StartScreen.this,MainActivity.class);
            startActivity(i);

        }
        else {
            temp=1;
            Date d = new Date();
            String date = d.toString();
            final Transaction tran = new Transaction(date,to,from,amount);
            final Integer f = t1.size();
            t1.add(0,tran);
            w1 = new Wallet(Merchant_amount,t1,to);
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            showProcessDialog();
            mDatabase.child("users").child(id).child("amount").setValue(am1).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDatabase.child("users").child(id).child("tr").child(f.toString()).setValue(tran).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Transaction Successful",Toast.LENGTH_LONG);
                            final Integer f2 = t2.size();
                            t2.add(0,tran);
                            w2 = new Wallet(user_amount,t2,from);

                            mDatabase.child("users").child(id2).child("amount").setValue(am2).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDatabase.child("users").child(id2).child("tr").child(f2.toString()).setValue(tran).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(),"Transaction Successful",Toast.LENGTH_LONG);

                                            progressDialog.dismiss();


                                        }
                                    });

                                }
                            });



                        }
                    });

                }
            });

        }







    }
    private void processTransaction2(){
        double Merchant = Double.valueOf(wallet2);
        double User = Double.valueOf(wallet);
        double price = Double.valueOf(amount);
        final double user_amount = User-price;
        final double Merchant_amount = Merchant+price;
        am1 = user_amount;
        am2 = Merchant_amount;
        //// TODO: 07/06/17
        if(user_amount<0||Merchant_amount<0){
            Log.e("INSUFFICIENT BALANCE!","SEND MESSAGE");
            progressDialog.dismiss();
            Toast.makeText(this,"Insufficient balance",Toast.LENGTH_LONG);
        }
        else if(temp==1){
            progressDialog.dismiss();
            Log.e("KUTTE KAMINEY","SEND MESSAGE");
            Intent i = new Intent(StartScreen.this,StartScreen.class);
            startActivity(i);
            finish();

        }
        else {
            temp=1;
            Date d = new Date();
            final Transaction tran = new Transaction(date,to,from,amount);
            final Integer f = t1.size();
            t1.add(0,tran);
            w1 = new Wallet(Merchant_amount,t1,to);
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            showProcessDialog();
            mDatabase.child("users").child(id).child("amount").setValue(am1).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDatabase.child("users").child(id).child("tr").child(f.toString()).setValue(tran).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Transaction Successful",Toast.LENGTH_LONG);
                            final Integer f2 = t2.size();
                            t2.add(0,tran);
                            w2 = new Wallet(user_amount,t2,from);

                            mDatabase.child("users").child(id2).child("amount").setValue(am2).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDatabase.child("users").child(id2).child("tr").child(f2.toString()).setValue(tran).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(),"Transaction Successful",Toast.LENGTH_LONG);
                                            progressDialog.dismiss();


                                        }
                                    });

                                }
                            });



                        }
                    });

                }
            });

        }







    }
/*
    private void processTransaction2(){
        showProcessDialog();
        double Merchant = Double.valueOf(wallet2);
        double User = Double.valueOf(wallet);
        double price = Double.valueOf(amount);
        final double user_amount = User-price;
        final double Merchant_amount = Merchant+price;
        am1 = user_amount;
        am2 = Merchant_amount;
        if(user_amount<0||Merchant_amount<0){
            Log.e("INSUFFICIENT BALANCE!","SEND MESSAGE");
            progressDialog.dismiss();
            Toast.makeText(this,"Insufficient balance",Toast.LENGTH_LONG);
        }
 /*       else if(temp==1){
            progressDialog.dismiss();
            Log.e("KUTTE KAMINEY","SEND MESSAGE");
            Intent i = new Intent(StartScreen.this,MainActivity.class);
            startActivity(i);

        }*//*
        else {
            final Transaction tran = new Transaction(date,to,from,amount);
            final Integer f = t1.size();
            t1.add(0,tran);
            w1 = new Wallet(Merchant_amount,t1,to);
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            showProcessDialog();
            mDatabase.child("users").child(id).child("amount").setValue(am1).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDatabase.child("users").child(id).child("tr").child(f.toString()).setValue(tran).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Transaction Successful",Toast.LENGTH_LONG);
                            final Integer f2 = t2.size();
                            t2.add(0,tran);
                            w2 = new Wallet(user_amount,t2,from);

                            mDatabase.child("users").child(id2).child("amount").setValue(am2).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDatabase.child("users").child(id2).child("tr").child(f2.toString()).setValue(tran).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(),"Transaction Successful",Toast.LENGTH_LONG);

                                            cursor.moveToNext();
                                            if(!cursor.isAfterLast()){

                                                StartScreen.Database k = new Database(StartScreen.this);

                                                SQLiteDatabase db;

                                                try {
                                                    db = k.getWritableDatabase();
                                                } catch (SQLiteException e) {
                                                    db = null;
                                                }
                                                Log.e("KUTTE KAMINEY","DELETE");

                                                db.execSQL("DELETE FROM " + "iiiiii"
                                                        + " WHERE Date = " + date );
                                            Intent i=getIntent();
                                                finish();
                                                startActivity(i);



                                            }
                                            else {
                                                temp =1;
                                                //  processTransaction2();
                                                progressDialog.dismiss();
                                                Log.e("KUTTE KAMINEY","SEND MESSAGE");
                                                Intent i = new Intent(StartScreen.this,MainActivity.class);
                                                startActivity(i);
                                            }

                                            progressDialog.dismiss();


                                        }
                                    });

                                }
                            });



                        }
                    });

                }
            });

        }

    }
    */
        /*

    private void processTransaction3(){
        showProcessDialog();
        double Merchant = Double.valueOf(wallet2);
        double User = Double.valueOf(wallet);
        double price = Double.valueOf(amount);
        final double user_amount = User-price;
        final double Merchant_amount = Merchant+price;
        am1 = user_amount;
        am2 = Merchant_amount;
        if(user_amount<0||Merchant_amount<0){
            Log.e("INSUFFICIENT BALANCE!","SEND MESSAGE");
            progressDialog.dismiss();
            Toast.makeText(this,"Insufficient balance",Toast.LENGTH_LONG);
        }
        else if(temp==1){
            progressDialog.dismiss();
            Log.e("KUTTE KAMINEY","SEND MESSAGE");
            Intent i = new Intent(StartScreen.this,MainActivity.class);
            startActivity(i);

        }
        else {
            final Transaction tran = new Transaction(date,to,from,amount);
            final Integer f = t1.size();
            t1.add(0,tran);
            w1 = new Wallet(Merchant_amount,t1,to);
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            showProcessDialog();
            mDatabase.child("users").child(id).child("amount").setValue(am1).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDatabase.child("users").child(id).child("tr").child(f.toString()).setValue(tran).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Transaction Successful",Toast.LENGTH_LONG);
                            final Integer f2 = t2.size();
                            t2.add(0,tran);
                            w2 = new Wallet(user_amount,t2,from);

                            mDatabase.child("users").child(id2).child("amount").setValue(am2).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDatabase.child("users").child(id2).child("tr").child(f2.toString()).setValue(tran).addOnCompleteListener(StartScreen.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(),"Transaction Successful",Toast.LENGTH_LONG);
                                            cursor.moveToNext();
                                            if(!cursor.isAfterLast()){
                                                Double amounti = cursor.getDouble(1);
                                                amount=amounti.toString();
                                                id = cursor.getString(3);
                                                id2 = cursor.getString(4);
                                                date = cursor.getString(2);
                                                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                                showProcessDialog1();
                                                mDatabase.child("users").child(id).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        w1 = dataSnapshot.getValue(Wallet.class);
                                                        t1 = w1.getTr();
                                                        to = w1.getName();
                                                        wallet = w1.getAmount().toString();
                                                        temp1=1;
                                                        showProcessDialog1();
                                                        mDatabase.child("users").child(id2).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                w2 = dataSnapshot.getValue(Wallet.class);
                                                                wallet2 = w2.getAmount().toString();
                                                                from = w2.getName();
                                                                t2 = w2.getTr();
                                                                Log.e("Wallet2",wallet2);
                                                                temp22=1;
                                                                progressDialog1.dismiss();
                                                                processTransaction2();







                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                progressDialog1.dismiss();


                                                            }
                                                        });



                                                        Log.e("Wallet",wallet);



                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        progressDialog1.dismiss();


                                                    }
                                                });





                                            }
                                            else {
                                                temp =1;
                                              //  processTransaction2();
                                                progressDialog.dismiss();
                                                Log.e("KUTTE KAMINEY","SEND MESSAGE");
                                                Intent i = new Intent(StartScreen.this,MainActivity.class);
                                                startActivity(i);
                                            }

                                            progressDialog.dismiss();


                                        }
                                    });

                                }
                            });



                        }
                    });

                }
            });

        }







    }
    */
}
