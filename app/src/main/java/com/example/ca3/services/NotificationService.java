package com.example.ca3.services;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.ca3.R;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationService {

    private static final String CHANNEL_ID = "memory_notifications";
    private static final String CHANNEL_NAME = "Memory Notifications";
    private static final String CHANNEL_DESC = "Notifications for memory updates";

    private final Context context;

    @Inject
    public NotificationService(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, required for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESC);
            // Register the channel with the system
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void sendNotification(String title, String message) {
        // Check if POST_NOTIFICATIONS permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted; handle accordingly
                // Typically, you would notify the user or disable notification features
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title != null ? title : "New Memory")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // Use a unique notification ID; here, using the current timestamp
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
