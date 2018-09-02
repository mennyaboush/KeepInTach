package com.example.hannybuns.firebaseproject2.activity;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hannybuns.firebaseproject2.R;
import com.example.hannybuns.firebaseproject2.objects.ContactUser;
import com.example.hannybuns.firebaseproject2.objects.UserInformation;

import java.util.ArrayList;

public class NearbyUsersFragment extends android.support.v4.app.ListFragment {

    private ArrayList<UserInformation> nearbyUsers;
    NearbyUsersArrayAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nearbyUsers =(ArrayList<UserInformation>)getArguments().getSerializable("Nearby Users");
//        nearbyUsers=new ArrayList<>();
//        nearbyUsers.add(new UserInformation("hanny", "hanny@gmail", "09"));
        adapter = new NearbyUsersArrayAdapter(getActivity(), R.layout.list_nearby_users, nearbyUsers);
        setListAdapter(adapter);
//        nearbyUsers = (ArrayList<UserInformation>)getArguments().getSerializable("Nearby Users");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nearby_users, container, false );
    }

    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {

        Toast.makeText(getActivity(),
                getListView().getItemAtPosition(position).toString(),
                Toast.LENGTH_LONG).show();
    }



}
