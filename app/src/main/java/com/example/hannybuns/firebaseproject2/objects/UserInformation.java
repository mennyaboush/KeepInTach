package com.example.hannybuns.firebaseproject2.objects;

import com.example.hannybuns.firebaseproject2.KeepInTouchLogic;
import com.example.hannybuns.firebaseproject2.Keys;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HannyBuns on 7/28/2018.
 */

public class UserInformation implements Serializable {
    private String name;
    private String email;
    private String phone;
    private String uid;
    private ArrayList<ContactUser> contactSelectedUsers;
    double longitude;
    double latitude;
    String token;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<ContactUser> getContactSelectedUsers() {
        return contactSelectedUsers;
    }

    public void setContactSelectedUsers(ArrayList<ContactUser> contactSelectedUsers) {
        this.contactSelectedUsers = contactSelectedUsers;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInformation(String userName, String userEmail, String userPhone, String uid) {
        this.name = userName;
        this.email = userEmail;
        this.phone = userPhone;
        this.uid = uid;
        this.latitude = 0;
        this.longitude = 0;
        this.token = null;
        contactSelectedUsers = new ArrayList<>();

    }
}
