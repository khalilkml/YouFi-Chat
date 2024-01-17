package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.chatapp.Listeners.UsersListener;
import com.example.chatapp.R;
import com.example.chatapp.adapters.UsersAdapter;
import com.example.chatapp.databinding.ActivityFriendlistsAcitivityBinding;
import com.example.chatapp.databinding.ActivityUsersBinding;
import com.example.chatapp.models.User;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class friendlistsAcitivity extends BaseActivity implements UsersListener {

    private ActivityFriendlistsAcitivityBinding binding;
    private PreferenceManager preferenceManager;
    List<String> friendIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendlistsAcitivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(this);
        setListeners();
        getFriendsList();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());

    }


    private void getFriendsList() {
        loading(true);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        firebaseFirestore.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    loading(false);
                    if (documentSnapshot.exists()) {
                        friendIds = (List<String>) documentSnapshot.get(Constants.KEY_USER_FRIENDS);
                        if (friendIds != null && !friendIds.isEmpty()) {
                            hideErrorMessage();
                            // Fetch friends' details based on IDs
                            fetchFriendsDetails(friendIds);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                })
                .addOnFailureListener(e -> {
                    showErrorMessage();
                });
    }


    private void fetchFriendsDetails(List<String> friendIds) {
        loading(true);
        List<User> friendsList = new ArrayList<>();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        // Assuming you have a collection named "users" where each document represents a user
        firebaseFirestore.collection("users")
                .whereIn(FieldPath.documentId(), friendIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    loading(false);
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            // Convert the document data to your UserModel class or use the data as needed
                            User friend = documentSnapshot.toObject(User.class);
                            friendsList.add(friend);
                        }
                    }
                    displayFriends(friendsList);
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    showErrorMessage();
                });
    }


    private void displayFriends(List<User> friendsList) {
        hideErrorMessage();
        UsersAdapter usersAdapter = new UsersAdapter(friendsList, this);
        binding.usersRecyclerView.setAdapter(usersAdapter);
        binding.usersRecyclerView.setVisibility(View.VISIBLE);
        loading(false);
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No friends available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage(){
        binding.textErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent =new Intent(getApplicationContext(), GroupActivity.class);
        //group member user details
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}