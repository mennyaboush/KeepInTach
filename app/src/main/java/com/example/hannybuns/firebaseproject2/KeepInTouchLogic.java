package com.example.hannybuns.firebaseproject2;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.hannybuns.firebaseproject2.helper.MyLocationHelper;
import com.example.hannybuns.firebaseproject2.helper.MyfirebaseHelper;
import com.example.hannybuns.firebaseproject2.objects.UserInformation;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by HannyBuns on 8/25/2018.
 */

public class KeepInTouchLogic {
    public static final String TAG = "KeepInTouchLogic";

    private static KeepInTouchLogic singletonhLogic = null;
    MyfirebaseHelper MyfirebaseHelperRef;
    MyLocationHelper myLocation;
    UserInformation lestUser;
    ArrayList<UserInformation> nearbyFriends;
    double[] oldLocation;
    Context context;
    String token;

    public UserInformation getLestUser() {
        return lestUser;
    }

    public void setLestUser(UserInformation lestUser) {
        this.lestUser = lestUser;
    }

    public KeepInTouchLogic() {

        this.MyfirebaseHelperRef = new MyfirebaseHelper();
        nearbyFriends = new ArrayList<>();
    }
    public static synchronized KeepInTouchLogic getInstance() {
        if (singletonhLogic == null)
            singletonhLogic = new KeepInTouchLogic();
        return singletonhLogic;
    }

    public UserInformation getInformationUserFromDB(DataSnapshot ds) {
        String email = (String) ds.child("email").getValue();
        String name = (String) ds.child("name").getValue();
        String phone = (String) ds.child("phone").getValue();
        String token = (String) ds.child(Keys.TOKEN).getValue();
        String uid =  (String) ds.child("uid").getValue();
        String latitude = (String) ds.child(Keys.LOCATION).child(Keys.LATITUDE).getValue();
        String longitude = (String) ds.child(Keys.LOCATION).child(Keys.LONGITUDE).getValue();
        if (email != null) {
            UserInformation ui = new UserInformation(name, email, phone, uid);
            ui.setToken(token);
            if (longitude != null)
                ui.setLongitude(Double.parseDouble(longitude));
            if (latitude != null)
                ui.setLatitude(Double.parseDouble(latitude));
            return ui;
        }
        return  null;
    }

    public void search(final MyCallback myCallback) {
        DatabaseReference ref;
        final ArrayList<String> children_location = new ArrayList<>();
        final ArrayList<String> children_contacts = new ArrayList<>();
        final String latitudePath = MyfirebaseHelperRef.getPathLocation(Keys.LATITUDE, 15);
        final String longitudePath = MyfirebaseHelperRef.getPathLocation(Keys.LONGITUDE, 15);
        ref = FirebaseDatabase.getInstance().getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nearbyFriends.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().contains(Keys.LATITUDE))
                        children_location.add(ds.child(latitudePath).toString());
                    if(ds.getKey().contains(Keys.LONGITUDE))
                        children_location.add(ds.child(longitudePath).toString());
                    if(ds.getKey().contains(Keys.USERS)) {
                        for (DataSnapshot dsUser : ds.getChildren())
                            if (dsUser.getKey().contains(getMyfirebaseHelperRef().getUser().getUid()))
                                for (DataSnapshot dsItem : dsUser.getChildren())
                                    if (dsItem.getKey().contains(Keys.CONTACT_SELECTED_USERS))
                                        for (DataSnapshot dsContact : dsItem.getChildren())
                                            children_contacts.add((String) dsContact.
                                                    child(Keys.PHONE_NUMBER).getValue());
                        break;
                    }
                }
                UserInformation my_ui = null;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().contains(Keys.USERS)){
                        for(DataSnapshot dsUser:ds.getChildren()) {
                            String tempUserId = dsUser.getKey();
                            if(children_location.get(0).contains(tempUserId) &&
                                    children_location.get(1).contains(tempUserId)){
                                UserInformation ui = getInformationUserFromDB(dsUser);
                                if(ui!= null)
                                    if(ui.getUid().contains(getMyfirebaseHelperRef().getUser().getUid()))
                                        my_ui=ui;
                                    else {
                                        for (String phoneNumber : children_contacts)
                                            if (phoneNumber.contains(ui.getPhone()))
                                                nearbyFriends.add(getInformationUserFromDB(dsUser));
                                    }
                            }
                        }
                    }
                }
                if(nearbyFriends.size()>0)
                    sendNotification(my_ui);
                myCallback.onCallback(nearbyFriends);
                nearbyFriends.clear();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
    public void sendNotification(UserInformation my_ui) {
        for (UserInformation nearbyUser : nearbyFriends) {
            MyfirebaseHelperRef.getUserInformationToFirebase(Keys.NOTIFICATIONS+"/"+
                    Keys.USERS_TOKENS+ "/"+ nearbyUser.getToken()+"/", my_ui);
        }
    }

    public MyfirebaseHelper getMyfirebaseHelperRef() {
        return MyfirebaseHelperRef;
    }

    public void setMyLocation(Context context) {
        this.context=context;
        this.myLocation = new MyLocationHelper(context);
    }
    public MyLocationHelper getMyLocationHelper() {
        return myLocation;
    }
    public Location getMyLocation() {
        myLocation.setCurrentLocation();
        return myLocation.getCurrentLocation();
    }


    public interface MyCallback<T> {
        void onCallback(T value);
    }


}
