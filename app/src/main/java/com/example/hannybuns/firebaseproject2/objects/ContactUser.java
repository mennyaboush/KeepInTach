package com.example.hannybuns.firebaseproject2.objects;

/**
 * Created by HannyBuns on 8/4/2018.
 */

public class ContactUser {
    private  String name;
    private  String phoneNumber;

    public ContactUser(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
