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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class friendlistsAcitivity extends BaseActivity implements UsersListener {

    private ActivityFriendlistsAcitivityBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendlistsAcitivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getFriendsList();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());

        binding.searchforfriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                loading(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideErrorMessage();
                // Call getUsers() with the entered text to filter users
                FindFriend(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                loading(true);
            }
        });

    }

    private void FindFriend(String string) {
    }

    private void getFriendsList() {
        loading(true);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        firebaseFirestore.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> friendIds = (List<String>) documentSnapshot.get(Constants.KEY_USER_FRIENDS);
                        if (friendIds != null && !friendIds.isEmpty()) {
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

    // Fetch details of friends based on their IDs
    private void fetchFriendsDetails(List<String> friendIds) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        List<User> friendsList = new ArrayList<>();

        for (String friendId : friendIds) {
            firebaseFirestore.collection("users").document(friendId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Parse friend's details and add to friendsList
                            User friend = documentSnapshot.toObject(User.class);
                            if (friend != null) {
                                loading(false);
                                friendsList.add(friend);
                                displayFriends(friendsList);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        showErrorMessage();
                    });
        }
    }

    private void displayFriends(List<User> friendsList) {
        hideErrorMessage();
        UsersAdapter usersAdapter = new UsersAdapter(friendsList, this);
        binding.usersRecyclerView.setAdapter(usersAdapter);
        binding.usersRecyclerView.setVisibility(View.VISIBLE);
    }

    // Function to filter and display friends based on search input
    private void displayfoundedFriends(List<User> friendsList, String searchText) {
        List<User> filteredFriends = new ArrayList<>();

        String searchLowerCase = searchText.toLowerCase();

        for (User friend : friendsList) {
            String friendName = friend.name.toLowerCase();
            if (friendName.contains(searchLowerCase)) {
                filteredFriends.add(friend);
            }
        }

        // Update the UI or adapter with filteredFriends
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