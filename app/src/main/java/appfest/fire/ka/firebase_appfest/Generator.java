
package appfest.fire.ka.firebase_appfest;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.EnumMap;
import java.util.Map;

import javax.crypto.SecretKey;

public class Generator extends AppCompatActivity {
    private final String tag = "QRCGEN";
    private final int REQUEST_PERMISSION = 0xf0;

    private Generator self;
    private Snackbar snackbar;
    private Bitmap qrImage;

    private EditText amount;
    private TextView txtSaveHint;
    private Button btnGenerate, btnReset;
    private ImageView imgResult;
    private ProgressBar loader;
    private Cursor cursor;
    private double wallamo;
    private double reqamo;

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



//todo kaam


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generator_layout);
        Walletdatabase k = new Walletdatabase(Generator.this);

        SQLiteDatabase db;

        try {
            db = k.getWritableDatabase();
        } catch (SQLiteException e) {
            db = null;
        }
        cursor = db.rawQuery("SELECT " +
                        "Amount " +
                        "FROM " + "walley"
                //		+ " ORDER BY _ID DESC LIMIT " + mMaxSearchHistorySize
                , null);

        if (cursor != null) {

            if (cursor.getCount() > 0) {
                Log.e("Query", "here");

                cursor.moveToFirst();
                wallamo = cursor.getDouble(0);




            }
        }
        self = this;

        amount = (EditText) findViewById(R.id.Amount);

        txtSaveHint = (TextView) findViewById(R.id.txtSaveHint);
        btnGenerate = (Button) findViewById(R.id.btnGenerate);
        btnReset = (Button) findViewById(R.id.btnReset);
        imgResult = (ImageView) findViewById(R.id.imgResult);
        loader = (ProgressBar) findViewById(R.id.loader);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.generateImage();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.reset();
            }
        });
    }
    @Override
    public void onBackPressed() {
        // your code.
        Intent d = new Intent(Generator.this,StartScreen.class);
        startActivity(d);
    }

    private void alert(String message){
        AlertDialog dlg = new AlertDialog.Builder(self)
                .setTitle("QRCode Generator")
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dlg.show();
    }

    private void snackbar(String msg) {
        if (self.snackbar != null) {
            self.snackbar.dismiss();
        }

        self.snackbar = Snackbar.make(
                findViewById(R.id.mainBody),
                msg, Snackbar.LENGTH_SHORT);

        self.snackbar.show();
    }

    private void endEditing(){

        amount.clearFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    private void generateImage(){
        FirebaseUser mFuser = FirebaseAuth.getInstance().getCurrentUser();
        String Test = mFuser.getUid();
        String text1 = Test+","+amount.getText().toString();
        AESEncryption K = new AESEncryption();
        reqamo = Double.parseDouble(amount.getText().toString());
        if((wallamo-reqamo)>0) {
            try {
                Double curr = wallamo-reqamo;
                SecretKey secKey = K.getSecretEncryptionKey();
                byte[] yourBytes = Base64.decode(bytesToHex(secKey.getEncoded()), Base64.DEFAULT);
                Log.e("GGG", yourBytes.toString());
                byte[] cipherText = K.encryptText(text1, secKey);
                String decryptedText = K.decryptText(cipherText, secKey);
                System.out.println("Original Text:" + text1);
                System.out.println("AES Key (Hex Form):" + bytesToHex(secKey.getEncoded()));
                System.out.println("Encrypted Text (Hex Form):" + bytesToHex(cipherText));
                System.out.println("Descrypted Text:" + decryptedText);
                text1 = bytesToHex(cipherText);
                text1 = bytesToHex(secKey.getEncoded()) + bytesToHex(cipherText);
                final String text = text1;
                if(text.trim().isEmpty()){
                    alert("No ID or Amount");
                    return;
                }
                Walletdatabase k = new Walletdatabase(Generator.this);
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

                endEditing();
                showLoadingVisible(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int size = imgResult.getMeasuredWidth();
                        if( size > 1){
                            Log.e(tag, "size is set manually");
                            size = 260;
                        }

                        Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
                        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                        hintMap.put(EncodeHintType.MARGIN, 1);
                        QRCodeWriter qrCodeWriter = new QRCodeWriter();
                        try {
                            BitMatrix byteMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size,
                                    size, hintMap);
                            int height = byteMatrix.getHeight();
                            int width = byteMatrix.getWidth();
                            self.qrImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                            for (int x = 0; x < width; x++){
                                for (int y = 0; y < height; y++){
                                    qrImage.setPixel(x, y, byteMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                                }
                            }

                            self.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    self.showImage(self.qrImage);
                                    self.showLoadingVisible(false);
                                    self.snackbar("QRCode generated");
                                }
                            });
                        } catch (WriterException e) {
                            e.printStackTrace();
                            alert(e.getMessage());
                        }
                    }
                }).start();

                //alert("FINAL DATA:"+text1);


            } catch (Exception e) {

            }
        }
        else {
            alert("Insufficient Balance!");
            amount.setText("");
            showImage(null);
            endEditing();
        }
        /*
        System.out.println("INDEX555:"+text1);

        int split = text1.indexOf("==");
        System.out.println("INDEX:"+split);
        int k = text1.length();
        String hexString = text1.substring(0,split+2);
        System.out.println("INDEX444:"+hexString);


        String rest = text1.substring(split+3,k);
        //byte[] yourBytes = new BigInteger(hexString, 16).toByteArray();*/
/*
        byte[] yourBytes     = Base64.decode(hexString,Base64.DEFAULT);
        Log.e("GGG",yourBytes.toString());

        byte[] yourBytes2     = Base64.decode(rest, Base64.DEFAULT);

        //byte[] yourBytes2 = new BigInteger(rest, 16).toByteArray();
        SecretKey originalKey = new SecretKeySpec(yourBytes, 0, yourBytes.length, "AES");
        try{
            String decryptedText = K.decryptText(yourBytes2, originalKey);

            System.out.println("Descrypted Text LAST:"+decryptedText);




        }


        catch (Exception e){

        }



*/



    }

    private void showLoadingVisible(boolean visible){
        if(visible){
            showImage(null);
        }

        loader.setVisibility(
                (visible) ? View.VISIBLE : View.GONE
        );
    }

    private void reset(){
        amount.setText("");
        showImage(null);
        endEditing();
    }

    private void showImage(Bitmap bitmap) {
        if (bitmap == null) {
            imgResult.setImageResource(android.R.color.transparent);
            qrImage = null;
            txtSaveHint.setVisibility(View.GONE);
        } else {
            imgResult.setImageBitmap(bitmap);
            txtSaveHint.setVisibility(View.VISIBLE);
        }
    }
    private static String  bytesToHex(byte[] hash) {
        return Base64.encodeToString(hash, Base64.DEFAULT);
    }
}
