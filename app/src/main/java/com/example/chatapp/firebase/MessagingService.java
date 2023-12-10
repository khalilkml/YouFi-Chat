package com.example.chatapp.firebase;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.chatapp.R;
import com.example.chatapp.activities.ChatActivity;
import com.example.chatapp.models.User;
import com.example.chatapp.utilities.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    // Method invoked when a new FCM token is generated for the device
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token); // Call the parent method
        Log.d("FCM", "onNewToken: " + token); // Log the new FCM token
    }

    // Method invoked when a new FCM message is received
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage); // Call the parent method
        User user = new User();
        user.id = remoteMessage.getData().get(Constants.KEY_USER_ID);
        user.name = remoteMessage.getData().get(Constants.KEY_NAME);
        user.token = remoteMessage.getData().get(Constants.KEY_FCM_TOKEN);

        int notificationId = new Random().nextInt();
        String channelId = "chat_message";

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.KEY_USER, user);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);// or PendingIntent.FLAG_MUTABLE

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_notifications);
        builder.setContentTitle(user.name);
        builder.setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                remoteMessage.getData().get(Constants.KEY_MESSAGE)
        ));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Chat Message";
            String channelDescription = "This notification channel is used for chat message notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // You may want to explain why you need this permission before redirecting the user to the app settings
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }

        notificationManagerCompat.notify(notificationId, builder.build());

    }
}
