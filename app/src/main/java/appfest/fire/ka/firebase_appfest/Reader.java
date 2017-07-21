
package appfest.fire.ka.firebase_appfest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Reader extends AppCompatActivity {
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

        showProcessDialog1();
        final String mUserId = user1.getUid();
        Log.e("muser",mUserId);
        mDatabase.child("users").child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                w1 = dataSnapshot.getValue(Wallet.class);
                if(w1!=null) {
                    t1 = w1.getTr();
                    to = w1.getName();
                    wallet = w1.getAmount().toString();
                    Log.e("Wallet",wallet);
                }
                progressDialog1.dismiss();





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog1.dismiss();


            }
        });






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

        stateBtn.setVisibility(View.VISIBLE);

        Button restartbtn = (Button) findViewById(R.id.btn_restart_activity);
        restartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Reader.this, Reader.class));
                finish();
            }
        });


        // Setup SurfaceView
        // -----------------
        // Init QREader
        // ------------
        mySurfaceView = (SurfaceView) findViewById(R.id.camera_view);

        // Init QREader

        qrEader = new QREader.Builder(this, mySurfaceView, new QRDataListener() {
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
        Intent d = new Intent(Reader.this,StartScreen.class);
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
        double Merchant = Double.valueOf(wallet);
        double User = Double.valueOf(wallet2);
        double price = Double.valueOf(amount);
        final double user_amount = User-price;
        double Merchant_amount = Merchant+price;
        if(user_amount<0||Merchant_amount<0){
            text.setText("INSUFFICIENT BALANCE!");
            progressDialog.dismiss();
            Toast.makeText(this,"Insufficient balance",Toast.LENGTH_LONG);
        }
        else if(temp==1){
            progressDialog.dismiss();
            Intent j = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(j);
        }
        else {
            temp=1;
            Date d = new Date();
            String date = d.toString();
            final Transaction tran = new Transaction(date,from,to,amount);
            final Integer f = t1.size();
            t1.add(0,tran);
            w1 = new Wallet(Merchant_amount,t1,to);
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
            final String mUserId = user1.getUid();
            showProcessDialog();
            mDatabase.child("users").child(mUserId).child("amount").setValue(Merchant_amount).addOnCompleteListener(Reader.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    showProcessDialog();
                    mDatabase.child("users").child(mUserId).child("tr").child(f.toString()).setValue(tran).addOnCompleteListener(Reader.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Transaction Successful",Toast.LENGTH_LONG);
                            showProcessDialog();
                            final Integer f2 = t2.size();
                            w2 = new Wallet(user_amount,t2,from);
                            mDatabase.child("users").child(id).child("amount").setValue(user_amount).addOnCompleteListener(Reader.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    showProcessDialog();
                                    mDatabase.child("users").child(id).child("tr").child(f2.toString()).setValue(tran).addOnCompleteListener(Reader.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(),"Transaction Successful",Toast.LENGTH_LONG);
                                            progressDialog.dismiss();
                                            Intent j = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(j);

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
}
