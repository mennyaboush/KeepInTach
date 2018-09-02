package com.example.hannybuns.firebaseproject2.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hannybuns.firebaseproject2.KeepInTouchLogic;
import com.example.hannybuns.firebaseproject2.Keys;
import com.example.hannybuns.firebaseproject2.R;
import com.example.hannybuns.firebaseproject2.helper.MyLocationHelper;
import com.example.hannybuns.firebaseproject2.helper.MyfirebaseHelper;
import com.example.hannybuns.firebaseproject2.objects.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

public class KeepInTouchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "KeepInTouchActivity";
    private TextView textViewEmail;
    private Button buttonTokenInformation, buttonMakeContectList, buttonLogout, buttonNearbyFriends;
    MyLocationHelper myLocation;
    KeepInTouchLogic keepInTouchLogic;
    public UserInformation currentUserInformation;
    private FirebaseAuth firebaseAuth;
    MyfirebaseHelper databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_in_touch);
        keepInTouchLogic = KeepInTouchLogic.getInstance();
        getCurrentUserInformationFromFB(new KeepInTouchLogic.MyCallback() {
            @Override
            public void onCallback(Object userInformation) {
                startKeepInTouch((UserInformation) userInformation);
            }
        });
    }

    private void startKeepInTouch(UserInformation userInformation) {
        currentUserInformation = userInformation;
        keepInTouchLogic.setLestUser(currentUserInformation);
        getToken();
        keepInTouchLogic.setMyLocation(this);
        keepInTouchLogic.getMyLocation();
        textViewEmail = findViewById(R.id.emailUser);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonMakeContectList = findViewById(R.id.makeContectList);
        buttonNearbyFriends = findViewById(R.id.buttonSearch);
        textViewEmail.setText("Welcome " + currentUserInformation.getName());

        buttonLogout.setOnClickListener(this);
        buttonMakeContectList.setOnClickListener(this);
        buttonNearbyFriends.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogout) {
            keepInTouchLogic.getMyfirebaseHelperRef().
                    removeUserLocation(currentUserInformation.getLongitude(),
                            currentUserInformation.getLatitude());
            keepInTouchLogic.getMyfirebaseHelperRef().signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));

        }
        if (view == buttonMakeContectList) {
            startActivity(new Intent(this, MakeContectListActivity.class));
        }
        if (view == buttonNearbyFriends) {
            KeepInTouchLogic.getInstance().search(new KeepInTouchLogic.MyCallback() {
                @Override
                public void onCallback(Object nearbyFriends) {
                    if(((ArrayList<UserInformation>)nearbyFriends).size()>0) {
                        Toast.makeText(KeepInTouchActivity.this,
                                ((ArrayList<UserInformation>) nearbyFriends).get(0).getName()+" is nearby you", Toast.LENGTH_SHORT).show();
                        startMapFragment((ArrayList<UserInformation>) nearbyFriends);
                    }
                    else
                        Toast.makeText(KeepInTouchActivity.this, "no nearby frinds", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void startMapFragment(ArrayList<UserInformation> nearbyFriends) {
        MyMapsFragment mapFragment = new MyMapsFragment();
        Bundle bundle = new Bundle(2);
        bundle.putSerializable(Keys.NEATBY_USERS1, nearbyFriends.get(0));
        bundle.putSerializable(Keys.NEATBY_USERS2, currentUserInformation);
        mapFragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragmentKeepInTouch, mapFragment, mapFragment.getTag()).commit();
    }

    public void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        currentUserInformation.setToken(token);
                        String pathUser = Keys.USERS + "/" + currentUserInformation.getUid() + "/"+ Keys.TOKEN;
                        String pathNotification = Keys.NOTIFICATIONS + "/" + Keys.USERS_TOKENS + "/"+ token;
                        KeepInTouchLogic.getInstance().getMyfirebaseHelperRef().addToFirebase(pathUser, token);
                        KeepInTouchLogic.getInstance().getMyfirebaseHelperRef().addToFirebase(pathNotification, "1");
                    }
                });
    }

    private void getCurrentUserInformationFromFB(final KeepInTouchLogic.MyCallback myCallback) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(Keys.USERS+"/");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().contains(keepInTouchLogic.getMyfirebaseHelperRef().getUser().getUid())) {
                        UserInformation userInformation = keepInTouchLogic.getInformationUserFromDB(ds);
                        if(userInformation.getLongitude() != 0)
                            keepInTouchLogic.getMyfirebaseHelperRef().
                                    removeUserLocation(userInformation.getLongitude(), userInformation.getLatitude());
                        myCallback.onCallback(userInformation);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });

    }

}


