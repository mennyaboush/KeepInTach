package com.example.hannybuns.firebaseproject2.helper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.hannybuns.firebaseproject2.Keys;
import com.example.hannybuns.firebaseproject2.R;
import com.example.hannybuns.firebaseproject2.activity.MainActivity;
import com.example.hannybuns.firebaseproject2.activity.MyMapActitvity;
import com.example.hannybuns.firebaseproject2.objects.UserInformation;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HannyBuns on 8/27/2018.
 */

public class NotificationGenerator {

    private static final int NOTIFICATION_ID_OPEN_ACTIVITY = 9;

    public static void openActivityNotification(Context context, ArrayList<UserInformation> userInformation) {
        NotificationCompat.Builder nc = new NotificationCompat.Builder(context, "notify_001");
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(context, MyMapActitvity.class);
        notifyIntent.putExtra(Keys.NEATBY_USERS, (Serializable) userInformation);

        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        nc.setContentIntent(pendingIntent);
        nc.setSmallIcon(R.mipmap.ic_launcher);
        nc.setAutoCancel(true);
        nc.setContentTitle("notification");
        nc.setContentText(userInformation.get(userInformation.size()-1).getName()+"\nIs nearby and wants to meet you ");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
        }
        nm.notify(NOTIFICATION_ID_OPEN_ACTIVITY , nc.build());

    }
}
