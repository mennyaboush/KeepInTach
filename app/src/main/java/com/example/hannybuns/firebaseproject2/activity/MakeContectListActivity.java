package com.example.hannybuns.firebaseproject2.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.hannybuns.firebaseproject2.Keys;
import com.example.hannybuns.firebaseproject2.objects.ContactUser;
import com.example.hannybuns.firebaseproject2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MakeContectListActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private Button SaveSelctedContacts, backButton, showListButton;
    boolean checkPermission;
    MyListFragment listFragment;
    ArrayList<ContactUser> contactSelectedUsers;
    public static Context contextOfApplication;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_contect_list);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        SaveSelctedContacts = findViewById(R.id.buttonSaveSelctedContacts);
        backButton = findViewById(R.id.buttonBack);
        showListButton = findViewById(R.id.buttonShowList);

        SaveSelctedContacts.setOnClickListener(this);
        backButton.setOnClickListener(this);
        showListButton.setOnClickListener(this);

        SaveSelctedContacts.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.INVISIBLE);

        contactSelectedUsers = new ArrayList<ContactUser>();
        contextOfApplication = getApplicationContext();
        checkPermission = checkPermission();
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }
    public boolean checkPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED & checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }
    private void saveUserInformation() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.getRef().child(Keys.USERS).child(user.getUid()).
                child(Keys.CONTACT_SELECTED_USERS).setValue(contactSelectedUsers);
    }
    public void onClick(View view) {
        if(view == showListButton){
            if (checkPermission) {
                SaveSelctedContacts.setVisibility(View.VISIBLE);
                listFragment = new MyListFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.contactsFragment, listFragment, listFragment.getTag()).commit();
            }
        }
        else if(view == SaveSelctedContacts){
            backButton.setVisibility(View.VISIBLE);
            contactSelectedUsers = listFragment.setSelectedUsers();
        }
        else if(view == backButton){
            saveUserInformation();
            finish();
        }
    }
}

