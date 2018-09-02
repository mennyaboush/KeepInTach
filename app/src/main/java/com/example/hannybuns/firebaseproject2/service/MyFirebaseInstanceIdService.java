package com.example.hannybuns.firebaseproject2.service;

import android.util.Log;

import com.example.hannybuns.firebaseproject2.helper.MyfirebaseHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import com.example.hannybuns.firebaseproject2.helper.MyfirebaseHelper;



public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG ="MyFirebaseInstanceIDSer" ;


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if(MyfirebaseHelper.getInstance().getUser()!=null) {
            MyfirebaseHelper.getInstance().updateToken(MyfirebaseHelper.getInstance().getUser().getUid(), refreshedToken);
        }
    }
}


