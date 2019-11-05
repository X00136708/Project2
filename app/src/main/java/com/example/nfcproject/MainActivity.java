package com.example.nfcproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Placeholder;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Database db;
    TextView nfcShow;
    EditText XnumberInput;
    Button createRecord;
    Button deleteRecord;
    Button deleteAll;
    Button loadfirebase;
    NfcAdapter nfcAdapter;
    DatabaseReference ref;
    ArrayList<User> arrayOfData;
    ArrayList<String> arrayOfXnumbers;
    public MainActivity(){

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new Database(this);
        nfcShow = (TextView)findViewById(R.id.nfcShow);
        XnumberInput = (EditText)findViewById(R.id.XnumberInput);
        createRecord = (Button)findViewById(R.id.createRecord);
        deleteRecord = (Button)findViewById(R.id.deleteRecord);
        deleteAll = (Button)findViewById(R.id.deleteAll);
        loadfirebase = (Button)findViewById(R.id.loadfirebase);
        createRecord();
        deleteData();
        deleteAllData();
        loadFirebase();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC available!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "NFC Not available!", Toast.LENGTH_LONG).show();
        }
    }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)


        @Override
        protected void onNewIntent(Intent intent) {

            super.onNewIntent(intent);
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareClassic tag = MifareClassic.get(tagFromIntent);

            try {
                //Variables
                int sectorCount = tag.getSectorCount();
                int tagSize = tag.getSize();
                int bCount;
                int bIndex;
                boolean auth;
                byte[] data;
                //Keys
                byte[] defaultKeys = new byte[]{};
                defaultKeys = MifareClassic.KEY_DEFAULT;
                //Connecting to tag
                tag.connect();
                //auth = true
                auth = tag.authenticateSectorWithKeyA(2, defaultKeys);


                if (auth) {
                    // Read the block
                    data = tag.readBlock(9);
                    // xNumberString = 16 bytes with X00136708 inside somewhere(SX00136708 #####)
                    String xNumberString = new String(data, "UTF-8");
                    // These are the letters (and numbers) I want to find and extract from xNumberString
                    char[] letters = new char[11];
                    // This is where the extracted letters & numbers get stored
                    char[] output = new char[10];
                    letters[0] = 'X';
                    letters[1] = '0';
                    letters[2] = '1';
                    letters[3] = '2';
                    letters[4] = '3';
                    letters[5] = '4';
                    letters[6] = '5';
                    letters[7] = '6';
                    letters[8] = '7';
                    letters[9] = '8';
                    letters[10] = '9';
                    for (int i = 0; i < xNumberString.length(); i++) {
                        for (int j = 0; j < letters.length; j++) {
                            if (xNumberString.charAt(i) == letters[j]) {
                                output[i] = xNumberString.charAt(i);
                            }
                        }

                    }

                    // Output of "X00136708"
                    String xNumberNearly = new String(output);
                    char output2[] = new char[9];

                    // Length of variable is actually 9 but it shows 10 below

                    for(int i=1; i<xNumberNearly.length();i++){
                        output2[i-1] = xNumberNearly.charAt(i);
                    }
                    String xNumber = new String(output2);
                    nfcShow.setText(" XNUMBER:" + xNumber);
//                    db.insertData(xNumber, "Ronan", "Doran", "doranronan14@gmail.com");
//                    loopRecords(xNumber);
                    xNumber = null;
                } else {
                    Log.e("a", "Auth Failed");
                }

            } catch (Exception e) {

            }
        }
//        public void loopRecords(String xnum){
//            Cursor res = db.getAllData();
//            if(res.getCount() == 0){
//                // No records available
//                showMessage("Error", "No data found");
//                return;
//            }
//            else {
//                StringBuffer buffer = new StringBuffer();
//                String id="";
//                while (res.moveToNext()) {
//                    if(res.getString(0).equals(xnum)){
//                        id = res.getString(0);
//                        buffer.append("XNumber " + res.getString(0) + "\n");
//                        buffer.append("FName " + res.getString(1) + "\n");
//                        buffer.append("LName " + res.getString(2) + "\n");
//                        buffer.append("Email " + res.getString(3) + "\n\n");
//                    }
//                }
//                showMessage("Records for User " + id, buffer.toString());
//            }
//        }
//        public void ViewAllRecords() {
//            viewAllRecords.setOnClickListener(
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                                ref= FirebaseDatabase.getInstance().getReference().child("Users");
//                                ref.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        String xnum = dataSnapshot.child("xnum").getValue().toString();
//                                        String fname = dataSnapshot.child("fname").getValue().toString();
//                                        String lname = dataSnapshot.child("lname").getValue().toString();
//                                        String email = dataSnapshot.child("email").getValue().toString();
//                                        Log.i("Names of things", "" + xnum + fname + lname + email);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                                Cursor res = db.getAllData();
//                                if(res.getCount() == 0){
//                                    // No records available
//                                    showMessage("Error", "No data found");
//                                    return;
//                                }
//                                else{
//                                    StringBuffer buffer = new StringBuffer();
//                                    while(res.moveToNext()){
//                                        buffer.append("XNumber " + res.getString(0)+ "\n");
//                                        buffer.append("FName " + res.getString(1)+ "\n");
//                                        buffer.append("LName " + res.getString(2)+ "\n");
//                                        buffer.append("Email " + res.getString(3)+ "\n\n");
//                                    }
//                                    showMessage("Data", buffer.toString());
//
//                                }
//
//                        }
//                    }
//            );
//        }
            public void getID(DataSnapshot snapshot){
                int size = (int) snapshot.getChildrenCount();
                arrayOfXnumbers = new ArrayList<String>(size+5);
                String str = snapshot.getValue().toString();
                Pattern p = Pattern.compile("[{ ]xnum=([^,}]+)[,}]");
                    Matcher m = p.matcher(str);
                    while (m.find()) {
                        String xnum = m.group(1);
                        arrayOfXnumbers.add(xnum);
                    }
            }
            public void loadFirebase(){
                    loadfirebase.setOnClickListener(
                            new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Array being initialised to size 100 each time the read is happening
                                arrayOfData  = new ArrayList<User>(100);
                                ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String xnum="", fname="", lname="", email="";
                                        getID(dataSnapshot);
                                        for(int i=0; i<dataSnapshot.getChildrenCount(); i++) {

//                                            xnum = dataSnapshot.child(String.valueOf(i+1)).child("xnum").getValue().toString();
//                                            fname = dataSnapshot.child(String.valueOf(i+1)).child("fname").getValue().toString();
//                                            lname = dataSnapshot.child(String.valueOf(i+1)).child("lname").getValue().toString();
//                                            email = dataSnapshot.child(String.valueOf(i+1)).child("email").getValue().toString();

                                            xnum = dataSnapshot.child(String.valueOf(arrayOfXnumbers.get(i))).child("xnum").getValue().toString();
                                            fname = dataSnapshot.child(String.valueOf(arrayOfXnumbers.get(i))).child("fname").getValue().toString();
                                            lname = dataSnapshot.child(String.valueOf(arrayOfXnumbers.get(i))).child("lname").getValue().toString();
                                            email = dataSnapshot.child(String.valueOf(arrayOfXnumbers.get(i))).child("email").getValue().toString();
                                            arrayOfData.add(new User(xnum,fname,lname,email));

                                        }
                                        passRowBack(arrayOfData);


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                    });
            }

            public void deleteData(){
                deleteRecord.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final String value = (XnumberInput.getText().toString());
                                ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String xnum="", fname="", lname="", email="";
                                        getID(dataSnapshot);
                                        for(int i=0; i<dataSnapshot.getChildrenCount(); i++) {
                                            xnum = dataSnapshot.child(String.valueOf(arrayOfXnumbers.get(i))).child("xnum").getValue().toString();
                                            fname = dataSnapshot.child(String.valueOf(arrayOfXnumbers.get(i))).child("fname").getValue().toString();
                                            lname = dataSnapshot.child(String.valueOf(arrayOfXnumbers.get(i))).child("lname").getValue().toString();
                                            email = dataSnapshot.child(String.valueOf(arrayOfXnumbers.get(i))).child("email").getValue().toString();
                                            if (xnum.equals(value)){
                                                ref.child(xnum).removeValue();
                                                arrayOfXnumbers.remove(i);
                                                for(int a=0; a<arrayOfXnumbers.size(); a++) {
                                                    Log.i("Array of numbers", "" + arrayOfXnumbers.get(a));
                                                }
                                                return;
                                            }
                                            else{

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        }
                        }
                );
            }
            public void deleteAllData(){
                deleteAll.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.removeValue();
                            }
                        }
                );
            }
            public void showMessage(String title, String message){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle(title);
                builder.setMessage(message);
//                    builder.setPositiveButton("Create new user", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            openDialog();
//                        }
//                    });
//                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
                builder.show();
            }
            public void createRecord(){
            createRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog();
                }
            });
            }
            public void passRowBack(@NonNull ArrayList<User> array){
                String record="";
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                for(int i=0; i<array.size();i++){

                   record += "XNumber: " + array.get(i).xnum + "\nFirst Name: " + array.get(i).fname + "\nSurname: " + array.get(i).lname + "\nEmail: " + array.get(i).email +"\n\n";
                    builder.setMessage(record);
                }

                builder.setCancelable(true);
                builder.setTitle("Data");
                builder.setPositiveButton("Create new user", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openDialog();
                    }
                });
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
            public void openDialog(){
                CreateUser fragment = new CreateUser();
                fragment.show(getSupportFragmentManager(), "Create new record");
            }
            @Override
            protected void onResume() {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                IntentFilter[] intentFilter = new IntentFilter[]{};
                nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);

                super.onResume();
            }

            @Override
            protected void onPause() {
                nfcAdapter.disableForegroundDispatch(this);
                super.onPause();
            }

}
