package com.example.nfcproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreateUser extends AppCompatDialogFragment {
    Database db;
    MainActivity main = new MainActivity();
    private EditText Xnumber_form;
    private EditText FName_form;
    private EditText LName_form;
    private EditText Email_form;
    DatabaseReference ref;
    long counter=0;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        db = new Database(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.createuser_fragment, null);
        builder.setView(view);
        builder.setTitle("Create new record");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference db = database.getReference("Users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    counter = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User newUser = new User(Xnumber_form.getText().toString(), FName_form.getText().toString(), LName_form.getText().toString(), Email_form.getText().toString());
                db.child(String.valueOf(counter+1)).setValue(newUser);
                dialog.cancel();
            }
        });
            Xnumber_form = view.findViewById(R.id.Xnumber_form);
            FName_form = view.findViewById(R.id.FName_form);
            LName_form = view.findViewById(R.id.LName_form);
            Email_form = view.findViewById(R.id.Email_form);
            return builder.create();
        }






    }

