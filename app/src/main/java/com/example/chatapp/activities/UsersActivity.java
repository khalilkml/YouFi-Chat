package com.example.chatapp.activities;



import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Listeners.UsersListener;
import com.example.chatapp.adapters.UsersAdapter;
import com.example.chatapp.databinding.ActivityUsersBinding;
import com.example.chatapp.models.User;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UsersListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
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
                getUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                loading(true);
            }
        });

    }

    private void getUsers(String searchText) {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if (!searchText.isEmpty()) {
            String searchLowerCase = searchText.toLowerCase(); // Convert search query to lowercase
            String searchUpperCase = searchText.toUpperCase(); // Convert search query to uppercase
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        loading(false);
                        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if (task.isSuccessful() && task.getResult() != null) {
                            hideErrorMessage();
                            List<User> users = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                // Skip the current user info
                                if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                    continue;
                                }
                                User user = new User();
                                user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                                user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                user.id = queryDocumentSnapshot.getId();

                                String userName = user.name.toLowerCase(); // Convert user's name to lowercase
                                if (userName.startsWith(searchLowerCase) || userName.startsWith(searchUpperCase)) {
                                    // Compare lowercase/uppercase versions for case-insensitive search
                                    users.add(user);
                                }
                            }
                            if (users.size() > 0) {
                                hideErrorMessage();
                                UsersAdapter usersAdapter = new UsersAdapter(users, this);
                                binding.usersRecyclerView.setAdapter(usersAdapter);
                                binding.usersRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                showErrorMessage();
                            }
                        } else {
                            showErrorMessage();
                        }
                    });
        } else {
            // If search text is empty, clear the RecyclerView
            List<User> emptyList = new ArrayList<>();
            UsersAdapter usersAdapter = new UsersAdapter(emptyList, this);
            binding.usersRecyclerView.setAdapter(usersAdapter);
            binding.usersRecyclerView.setVisibility(View.GONE);
            loading(false);
        }
    }




    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
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
        Intent intent =new Intent(getApplicationContext(), ChatActivity.class);
        preferenceManager.addFriend(Constants.KEY_USER_FRIENDS,user.id);
        updatefriendslistfirebase(preferenceManager.getFriendArrayList(Constants.KEY_USER_FRIENDS),user);
        //receiver user details
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }

    public void updatefriendslistfirebase(ArrayList<String> updatedFriendList,User user){
        // Get the current user's ID
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        // Assume `firebaseFirestore` is your instance of FirebaseFirestore
         firebaseFirestore.collection("users").document(currentUserId)
                .update(Constants.KEY_USER_FRIENDS, updatedFriendList)
                .addOnSuccessListener(aVoid -> {
                    if(!updatedFriendList.contains(user.id)){
                        Toast.makeText(this, "friend added", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(this, "he is already a friend", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "friend not added", Toast.LENGTH_SHORT).show();
                });

    }
}