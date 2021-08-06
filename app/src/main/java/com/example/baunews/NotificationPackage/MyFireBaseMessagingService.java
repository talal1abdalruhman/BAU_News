package com.example.baunews.NotificationPackage;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.baunews.MainActivity;
import com.example.baunews.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;


public class MyFireBaseMessagingService extends FirebaseMessagingService {
    String title, message;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        title = remoteMessage.getData().get("Title");
        message = remoteMessage.getData().get("Message");

        SharedPreferences lang = getSharedPreferences("LANGUAGE_PREFERENCE", Context.MODE_PRIVATE);
        String lng = lang.getString("language", "null");
        Log.d("sharedPreferences lang", lng);
        boolean isAR = lng.equals("ar");
        if (title.equals("BAU News")) {
            if (message.equals("general")) {
                if (!isAR) {
                    Log.d("lang_tracker", "here " + isAR + " " + lng);
                    message = "New feeds are added in general.";
                    title = "BAU News";
                } else {
                    Log.d("lang_tracker", "here " + isAR);
                    message = "خبر جديد تمت اضافته في الاخبار العامة.";
                    title = "أخبار جامعة البلقاء";
                }
            } else {
                if (!isAR) {
                    Log.d("lang_tracker", "here " + isAR + " " + lng);
                    message = "New feeds are added in collage.";
                    title = "BAU News";
                } else {
                    Log.d("lang_tracker", "here " + isAR);
                    message = "خبر جديد تمت اضافته في اخبار الكلية.";
                    title = "أخبار جامعة البلقاء";
                }
            }
        }else {
            if (message.equals("G")) {
                if (!isAR) {
                    Log.d("lang_tracker", "here " + isAR + " " + lng);
                    message = "New event is added in general.";
                    title = "BAU Events";
                } else {
                    Log.d("lang_tracker", "here " + isAR);
                    message = "حدث جديد تمت اضافته في الجامعة.";
                    title = "أحداث جامعة البلقاء";
                }
            } else {
                if (!isAR) {
                    Log.d("lang_tracker", "here " + isAR + " " + lng);
                    message = "New event is added in collage.";
                    title = "BAU Events";
                } else {
                    Log.d("lang_tracker", "here " + isAR);
                    message = "حدث جديد تمت اضافته في الكلية.";
                    title = "أحداث جامعة البلقاء";
                }
            }
        }

        Log.d("notification_tracker", "received " + title);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent resIntent = new Intent(this, MainActivity.class);
        PendingIntent resPendingIntent = PendingIntent.getActivity(this, 1, resIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationChannel reqChannel;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            reqChannel = new NotificationChannel("REQUEST_CHANNEL", "request channel", NotificationManager.IMPORTANCE_HIGH);
            reqChannel.setDescription("This is channel for handle details request.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(reqChannel);
            Log.d("notification_tracker", "Channel created " + reqChannel.getId());
        }

        @SuppressLint("WrongConstant")
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "REQUEST_CHANNEL")
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.bau)
                        .setLargeIcon(icon)
                        .setSound(sound)
                        .setBadgeIconType(R.mipmap.ic_launcher_round)
                        .setContentIntent(resPendingIntent);
        Log.d("notification_tracker", "build " + title);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int id = new Random(System.currentTimeMillis()).nextInt(1000);
        manager.notify(id, builder.build());
        Log.d("notification_tracker", "shows " + title);
    }

}