package com.example.hannybuns.firebaseproject2.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.example.hannybuns.firebaseproject2.R;
import com.example.hannybuns.firebaseproject2.activity.ContactUsersArrayAdapter;
import com.example.hannybuns.firebaseproject2.activity.MakeContectListActivity;
import com.example.hannybuns.firebaseproject2.objects.ContactUser;

public class MyListFragment extends ListFragment {
    List<ContactUser> allContactUser;
    ContactUsersArrayAdapter adapter;

    public MyListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allContactUser = new ArrayList<ContactUser>();
        getContactUsers();
        adapter = new ContactUsersArrayAdapter(getActivity(),
                R.layout.list_contacts,
                allContactUser);
        setListAdapter(adapter);
    }

    private void getContactUsers() {
        Context applicationContext = MakeContectListActivity.getContextOfApplication();
        ContentResolver cr = applicationContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        allContactUser.add(new ContactUser(name, phoneNo));
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts_fragment, container, false);
        return view;
    }

    public ArrayList<ContactUser> setSelectedUsers() {
        ArrayList<ContactUser> contactSelectedUsers = new ArrayList<ContactUser>();
        for (int i = 0; i < allContactUser.size(); i++) {
            if (adapter.mCheckStates.get(i) == true) {
                ContactUser temp = allContactUser.get(i);
                temp.setName(temp.getName().replace("-","")
                        .replace("+", "").replace("972", ""));
                contactSelectedUsers.add(allContactUser.get(i));
            }
        }
        return  contactSelectedUsers;
    }
}


