package com.example.hannybuns.firebaseproject2.service;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.hannybuns.firebaseproject2.KeepInTouchLogic;
import com.example.hannybuns.firebaseproject2.Keys;
import com.example.hannybuns.firebaseproject2.MyApplication;
import com.example.hannybuns.firebaseproject2.activity.KeepInTouchActivity;
import com.example.hannybuns.firebaseproject2.helper.MyLocationHelper;
import com.example.hannybuns.firebaseproject2.helper.NotificationGenerator;
import com.example.hannybuns.firebaseproject2.objects.UserInformation;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.JobParameters;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

/**
 * Created by HannyBuns on 8/27/2018.
 */

public class MyService extends JobService {
    private static final String TAG = "MyService";
    private static String token;
    BackgroundTask backgroundTask;
    KeepInTouchLogic keepInTouchLogic = KeepInTouchLogic.getInstance();
    ArrayList<String> emails = new ArrayList<>();


    @Override
    public boolean onStartJob(final JobParameters job) {
        getNewToken();
        backgroundTask = new BackgroundTask()
        {
            @Override
            protected void onPostExecute(ArrayList<UserInformation> userInformation){
                if(userInformation.size()>0) {
                    userInformation.add(KeepInTouchLogic.getInstance().getLestUser());
                    NotificationGenerator.openActivityNotification(getApplicationContext(), userInformation);
                    clearUserInformation();
                }
                jobFinished(job, false);
            }
        };
        backgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void getNewToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token= task.getResult().getToken();
                    }
                });
    }
    public static String getToken() {
        return token;
    }

    public static class BackgroundTask extends AsyncTask<Void, Void, ArrayList<UserInformation>>
    {
        private static ArrayList<UserInformation> userInformation=new ArrayList<>();

        public static void clearUserInformation() {
            userInformation.clear();
        }

        @Override
        protected ArrayList<UserInformation> doInBackground(Void... voids){
            DatabaseReference ref;
            ref = FirebaseDatabase.getInstance()
                    .getReference(Keys.NOTIFICATIONS+"/"+ Keys.USERS_TOKENS+ "/");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String token = getToken();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.getKey().contains(getToken())) {
                            String email = (String) ds.child("email").getValue();
                            String name = (String) ds.child("name").getValue();
                            String phone = (String) ds.child("phone").getValue();
                            Double latitude = (Double)ds.child(Keys.LATITUDE).getValue();
                            Double longitude = (Double) ds.child(Keys.LONGITUDE).getValue();
                            if(email !=null) {
                                UserInformation temp = new UserInformation(name, email, phone, token);
                                temp.setToken(token);
                                temp.setLongitude(longitude);
                                temp.setLatitude(latitude);
                                userInformation.add(temp);
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "onCancelled", databaseError.toException());
                }
            });
            return userInformation;
        }


    }



}
