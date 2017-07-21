
package appfest.fire.ka.firebase_appfest;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class ReaderOffline extends AppCompatActivity {
    // UI
    private TextView text;

    // QREader
    private SurfaceView mySurfaceView;
    private QREader qrEader;
    private String wallet;
    private ProgressDialog progressDialog,progressDialog1;
    private String M_id,id,amount,wallet2;
    private String Message;
    private int temp =0;
    private static final int MY_PERMISSIONS = 10002;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndRequestPermissions();
        setContentView(R.layout.reader_main);

        text = (TextView) findViewById(R.id.code_info);

        final Button stateBtn = (Button) findViewById(R.id.btn_start_stop);
        stateBtn.setText("Continue");
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

        M_id = user1.getUid();

        // change of reader state in dynamic
        stateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qrEader.isCameraRunning()) {
                    showProcessDialog1();
                    //// TODO: 19/05/17 sms code
                    if(Message!=null) {
                        String no = "8373944042";


                        //Getting intent and PendingIntent instance
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                        //Get the SmsManager instance and call the sendTextMessage method to send message
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(no, null, Message, pi, null);

                        Intent go = new Intent(ReaderOffline.this,StartScreen.class);
                        startActivity(go);


                        Toast.makeText(getApplicationContext(), "Message Sent successfully!",
                                Toast.LENGTH_LONG).show();
                    }
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
                startActivity(new Intent(ReaderOffline.this, ReaderOffline.class));
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
                String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Message = id+","+amount+","+M_id+","+mydate;


                text.post(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(amount);
                    }
                });





                AESEncryption K = new AESEncryption();
                try{
                    SecretKey secKey = K.getSecretEncryptionKey();
                    byte[] cipherText = K.encryptText(Message, secKey);
                    System.out.println("Original Text:" + text1);
                    System.out.println("AES Key (Hex Form):"+bytesToHex(secKey.getEncoded()));
                    System.out.println("Encrypted Text (Hex Form):"+bytesToHex(cipherText));
                    Message=bytesToHex(cipherText);
                    Message= bytesToHex(secKey.getEncoded())+bytesToHex(cipherText);

                    //alert("FINAL DATA:"+text1);




                }


                catch (Exception e){

                }
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
        Intent d = new Intent(ReaderOffline.this,StartScreen.class);
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
    private void showProcessDialog1() {
        progressDialog1 = new ProgressDialog(this);
        progressDialog1.setTitle("Please Wait");
        progressDialog1.setMessage("Loading...");
        progressDialog1.show();
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MY_PERMISSIONS);
            return false;
        }
        return true;
    }
    private static String  bytesToHex(byte[] hash) {
        return Base64.encodeToString(hash, Base64.DEFAULT);
    }

}


