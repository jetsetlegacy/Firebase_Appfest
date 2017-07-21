
package appfest.fire.ka.firebase_appfest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListViewAndroidExample extends Activity {
    ListView listView ;
    private ProgressDialog progressDialog1;
    private Wallet w1;
    List<Transaction> t1;
    String amount;
    String[] popup;
    Integer i=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liviewtransactions);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

        final String mUserId = user1.getUid();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mUserId).child("tr");

        showProcessDialog1();

        Log.e("muser",mUserId);
        t1=new ArrayList<>();
        i=0;
        mDatabase.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                long num = snapshot.getChildrenCount();
                int k = (int)num;
                popup = new String[k];


                for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                    Transaction university = postSnapshot.getValue(Transaction.class);
                    t1.add(university);
                    Integer s = t1.size();
                    s=s-1;
                    mDatabase.child(s.toString()).child("data").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot2: dataSnapshot.getChildren()) {
                                String date = postSnapshot2.getValue(String.class);
                                Log.e("e",date);
                                if(popup[i]!=null) {
                                    popup[i] = popup[i] + "\n" + date;
                                }
                                else
                                    popup[i]=date;
                                Log.e("v",i.toString());
                                make();
                                progressDialog1.dismiss();

                            }
                            i++;

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Log.e("E",s.toString());

                    // here you can access to name property like university.name

                }
            }

            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


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
        Intent d = new Intent(ListViewAndroidExample.this,StartScreen.class);
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