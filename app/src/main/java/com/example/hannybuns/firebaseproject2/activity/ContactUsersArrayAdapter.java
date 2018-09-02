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

import java.util.List;

public class ContactUsersArrayAdapter extends ArrayAdapter<ContactUser> implements CompoundButton.OnCheckedChangeListener {
    SparseBooleanArray mCheckStates;
    private final Context context;
    private final List<ContactUser> users;

    public ContactUsersArrayAdapter(Context context, int resource, List<ContactUser> users) {
        super(context,resource, users);
        this.context = context;
        this.users = users;
        mCheckStates = new SparseBooleanArray(users.size());

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_contacts, parent, false);
        AppInfoHolder holder;

        TextView textName = rowView.findViewById(R.id.textName);
        TextView textPhone = rowView.findViewById(R.id.textPhone);
        CheckBox chkSelect = rowView.findViewById(R.id.checkBox);
        holder = new AppInfoHolder(textName, textPhone, chkSelect);
        rowView.setTag(holder);

        holder.textName.setText(users.get(position).getName());
        holder.textPhone.setText(users.get(position).getPhoneNumber());
        holder.chkSelect.setTag(position);
        holder.chkSelect.setChecked(mCheckStates.get(position, false));
        holder.chkSelect.setOnCheckedChangeListener(this);

        return rowView;
    }
    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);

    }

    public void toggle(int position) {
        setChecked(position, !isChecked(position));

    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {

        mCheckStates.put((Integer) buttonView.getTag(), isChecked);

    }

    static class AppInfoHolder
    {
        TextView textName;
        TextView textPhone;
        CheckBox chkSelect;

        public AppInfoHolder(TextView textName, TextView textPhone, CheckBox chkSelect) {
            this.textName = textName;
            this.textPhone = textPhone;
            this.chkSelect = chkSelect;

        }
    }
}