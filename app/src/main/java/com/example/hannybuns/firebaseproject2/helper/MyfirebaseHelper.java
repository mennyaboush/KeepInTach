package com.example.hannybuns.firebaseproject2.helper;

import com.example.hannybuns.firebaseproject2.KeepInTouchLogic;
import com.example.hannybuns.firebaseproject2.Keys;
import com.example.hannybuns.firebaseproject2.objects.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by HannyBuns on 8/9/2018.
 */

public class MyfirebaseHelper {
    private static final String TAG = "MyfirebaseHelper";

    private static MyfirebaseHelper singletonfirebaseHelper = null;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public static synchronized MyfirebaseHelper getInstance( ) {
        if (singletonfirebaseHelper == null)
            singletonfirebaseHelper=new MyfirebaseHelper();
        return singletonfirebaseHelper;
    }
    public MyfirebaseHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void signOut() {
        firebaseAuth.signOut();
    }
    public FirebaseUser getUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void getUserInformationToFirebase(String path, UserInformation userInformation){
        databaseReference.getRef().child(path).setValue(userInformation);
    }

    public void addToFirebase(String path, String data) {
        databaseReference.getRef().child(path).setValue(data);
    }
    public void removeUserLocation(double longitude, double latitude) {
        databaseReference.child(getPath(Keys.LONGITUDE, longitude)).removeValue();
        databaseReference.child(getPath(Keys.LATITUDE, latitude)).removeValue();
    }
    public void addUserLocation(double longitude, double latitude) {
        if(getUser()!= null) {
            addToFirebase(getPath(Keys.LONGITUDE, longitude) + Keys.USER_ID,
                    getUser().getUid());
            addToFirebase(getPath(Keys.LATITUDE, latitude) + Keys.USER_ID,
                    getUser().getUid());
        }
    }
    public String getPathLocation(String point, int size) {
        if (KeepInTouchLogic.getInstance().getMyLocation() == null) return getPath(point, 0);
        if(point.contains(Keys.LATITUDE))
            return getPath(point, KeepInTouchLogic.getInstance().getMyLocation()
                    .getLatitude()).substring(Keys.LATITUDE.length()+1, size);
        else
            return getPath(point, KeepInTouchLogic.getInstance().getMyLocation()
                    .getLongitude()).substring(Keys.LATITUDE.length()+1, size);
    }
    public String getPath(String point, double locationPoint) {
        String location = String.valueOf(locationPoint);
        for (int i = 0; i < location.length(); i++) {
            point += "/";
            if (location.charAt(i) != '.')
                point += location.charAt(i);
            else
                point += '_';
        }
        point += "/";
        return point;
    }

    public void updateToken(String uid, String token) {
        databaseReference.child(Keys.NOTIFICATIONS).child(Keys.USERS_TOKENS).child(uid).child(Keys.FSM_TOKEN).setValue(token);
    }


}
