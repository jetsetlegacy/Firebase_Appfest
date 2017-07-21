
package appfest.fire.ka.firebase_appfest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private ImageView imageView;
    private TextView email;
    private View Add;
    private TextView userId;
    private TextView curr_Balance;
    private EditText amo;
    private ProgressDialog progressDialog,progressDialog1;
    private String wallet;
    private Wallet w;
    //// TODO: 07/06/17 wallet offline
    private static class Walletdatabase extends SQLiteOpenHelper {
        private static final String DB_NAME = "payJioDatabase.db";
        private static final int DB_VERSION = 2;

        public Walletdatabase(Context context) {
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
                db.execSQL("DROP TABLE IF EXISTS " + "walley");
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        email = (TextView) findViewById(R.id.email_field);
        Add = findViewById(R.id.Add);
        userId = (TextView) findViewById(R.id.user_id);
        curr_Balance = (TextView) findViewById(R.id.Amount3);
        imageView = (ImageView) findViewById(R.id.user_photo);
        amo = (EditText)findViewById(R.id.Amount2);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

        showProcessDialog1();
        final String mUserId = user1.getUid();
        mDatabase.child("users").child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    w = dataSnapshot.getValue(Wallet.class);
                    if (w != null) {
                        wallet = w.getAmount().toString();
                        Double curr = w.getAmount();
                        Log.e("Wallet", wallet);
                        curr_Balance.setText("Current Balance Rs. " + wallet);
                        Walletdatabase k = new Walletdatabase(MainActivity.this);
                        SQLiteDatabase db;
                        try {
                            db = k.getWritableDatabase();
                        } catch (SQLiteException e) {
                            db = null;
                        }
                        db.execSQL("DELETE FROM " + "walley");
                        db.execSQL("INSERT INTO " + "walley" + "("
                                + "Amount"
                                + ") Values" + "('" + curr
                                + "');");

                    }
                    progressDialog1.dismiss();
                }





            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog1.dismiss();


            }
        });

        //add a auth listener
        authListener = new FirebaseAuth.AuthStateListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("MainActivity", "onAuthStateChanged");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setDataToView(user);

                    //loading image by Picasso
                    if (user.getPhotoUrl() != null) {
                        Log.d("MainActivity", "photoURL: " + user.getPhotoUrl());
                        Picasso.with(MainActivity.this).load(user.getPhotoUrl()).into(imageView);
                    }
                } else {
                    //user auth state is not existed or closed, return to Login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        //Signing out
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

                showProcessDialog1();
                final String mUserId = user1.getUid();
                mDatabase.child("users").child(mUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            w = dataSnapshot.getValue(Wallet.class);
                            if (w != null) {
                                wallet = w.getAmount().toString();
                                Log.e("Wallet", wallet);
                                curr_Balance.setText("Current Balance Rs. " + wallet);

                            }
                            progressDialog1.dismiss();
                        }




                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog1.dismiss();


                    }
                });
                if (wallet != null) {
                    double prev_balance = Double.valueOf(wallet);
                    showProcessDialog();
                    String Money = amo.getText().toString().trim();
                    double new_amount = Double.valueOf(Money);
                    double final_balance = prev_balance + new_amount;
                    List<Transaction> tran = w.getTr();
                    Date d = new Date();
                    String date = d.toString();
                    final Transaction t = new Transaction(date,"SELF","WALLET",Money);
                    final Integer f = tran.size();
                    String f2 = f.toString();
                    Log.e("ttt",f2);


                    String email = user.getEmail();


                    mDatabase.child("users").child(mUserId).child("amount").setValue(final_balance).addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            showProcessDialog();
                            mDatabase.child("users").child(mUserId).child("tr").child(f.toString()).setValue(t).addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    Intent i = new Intent(MainActivity.this,MainActivity.class);
                                    startActivity(i);

                                }
                            });

                        }
                    });



                }
                else {
                    showProcessDialog();
                    String Money = amo.getText().toString().trim();
                    Double first = Double.parseDouble(Money);
                    Date d = new Date();
                    String date = d.toString();
                    Transaction t = new Transaction(date,"SELF","WALLET",Money);
                    List<Transaction> ta = new ArrayList<Transaction>();
                    ta.add(t);
                    String email = user.getEmail();
                    Wallet insert = new Wallet(first,ta,email);
                    mDatabase.child("users").child(mUserId).setValue(insert).addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            Intent i = new Intent(MainActivity.this,MainActivity.class);
                            startActivity(i);


                        }
                    });


                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // your code.
        Intent d = new Intent(MainActivity.this,StartScreen.class);
        startActivity(d);
    }
    @SuppressLint("SetTextI18n")
    private void setDataToView(FirebaseUser user) {
        email.setText("User Email: " + user.getEmail());
        userId.setText("User id: " + user.getUid());



    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
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
}