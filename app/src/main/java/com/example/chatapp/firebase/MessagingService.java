package com.example.chatapp.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    // Method invoked when a new FCM token is generated for the device
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token); // Call the parent method
        Log.d("FCM", "onNewToken: " + token); // Log the new FCM token
    }

    // Method invoked when a new FCM message is received
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message); // Call the parent method
        // Message handling logic can be implemented here
        // Currently, this method does not have specific message processing
    }
}
