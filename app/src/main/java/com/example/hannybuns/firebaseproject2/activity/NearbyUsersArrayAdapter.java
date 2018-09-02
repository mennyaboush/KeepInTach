package com.example.hannybuns.firebaseproject2.activity;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.hannybuns.firebaseproject2.R;
import com.example.hannybuns.firebaseproject2.objects.ContactUser;
import com.example.hannybuns.firebaseproject2.objects.UserInformation;

import java.util.List;

/**
 * Created by HannyBuns on 9/1/2018.
 */

class NearbyUsersArrayAdapter extends ArrayAdapter<UserInformation> implements CompoundButton.OnCheckedChangeListener {
    private final Context context;
    private final List<UserInformation> users;

    public NearbyUsersArrayAdapter(Context context, int resource, List<UserInformation> users) {
        super(context,resource, users);
        this.context = context;
        this.users = users;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_nearby_users, parent, false);
        NearbyUsersArrayAdapter.AppInfoHolder holder;

        TextView textName = rowView.findViewById(R.id.textNearbyUserName);
        holder = new NearbyUsersArrayAdapter.AppInfoHolder(textName);
        rowView.setTag(holder);

        holder.textName.setText(users.get(position).getName());

        return rowView;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    static class AppInfoHolder
    {
        TextView textName;

        public AppInfoHolder(TextView textName) {
            this.textName = textName;

        }
    }
}
