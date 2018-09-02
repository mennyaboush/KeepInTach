package com.example.hannybuns.firebaseproject2.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hannybuns.firebaseproject2.Keys;
import com.example.hannybuns.firebaseproject2.R;
import com.example.hannybuns.firebaseproject2.objects.UserInformation;

import java.util.ArrayList;


public class MyMapActitvity extends AppCompatActivity {
    ArrayList<UserInformation> nearbyFrind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_map);
        nearbyFrind = new ArrayList<>();
        Intent i = getIntent();
        nearbyFrind = (ArrayList<UserInformation>)getIntent().getSerializableExtra(Keys.NEATBY_USERS);

        MyMapsFragment mapFragment = new MyMapsFragment();
        Bundle bundle = new Bundle(2);
        bundle.putSerializable(Keys.NEATBY_USERS1, nearbyFrind.get(0));
        bundle.putSerializable(Keys.NEATBY_USERS2, nearbyFrind.get(1));
        mapFragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.notificationFragment,
                mapFragment, mapFragment.getTag()).commit();

    }

}
