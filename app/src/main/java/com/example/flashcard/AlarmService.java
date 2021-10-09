package com.example.flashcard;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class AlarmService extends Service {

    Random r;

    @Override
    public void onCreate() {
        super.onCreate();
        r = new Random();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "FlashCard_CHANNEL")
                .setSmallIcon(R.drawable.thumb_selctor)
                .setContentTitle("Time to learn!")
                .setContentText("Time to learn!!!!!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true) // Set to true to auto remove the notification
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        startForeground(1, notification);
        notificationManager.cancelAll();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e("InfiniteService", "Service started");
        if (intent.getAction() != null && intent.getAction().equals("STOP FOREGROUND")) {
            stopForeground(false);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}