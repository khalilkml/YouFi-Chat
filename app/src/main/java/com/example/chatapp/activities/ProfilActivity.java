package com.example.chatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.databinding.ActivityProfilBinding;
import com.example.chatapp.models.User;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfilActivity extends AppCompatActivity {

    private ActivityProfilBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;

    private User user ;
    DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(this);
        userRef = FirebaseFirestore.getInstance().collection("users").document(preferenceManager.getString(Constants.KEY_USER_ID));
        loadUserDetails();
        setListeners();
        LoadUserDetails();
    }

    private void loadUserDetails(){
        binding.inputName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.inputEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        binding.inputPassword.setText(preferenceManager.getString(Constants.KEY_PASSWORD));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void SignOut(){
        showToast("Signing out...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }

    private  void setListeners(){
        binding.buttonSingOut.setOnClickListener(v ->
                SignOut());
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.updateInfo.setOnClickListener(v -> UpdateUserInfo());
        binding.imageProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
            //call the function that change the profile and the function that update the profile in fire store db
        });
        //add the icons that will be used to update user info
    }

    private void UpdateUserInfo() {
        if(!binding.inputName.getText().toString().equals(preferenceManager.getString(Constants.KEY_NAME))){
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", binding.inputName.getText().toString());

            userRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        preferenceManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());

                        showToast("Profile updated successfully");
                    })
                    .addOnFailureListener(e -> {
                        showToast("Profile didn't updated successfully");
                    });
        }

        if (!binding.inputPassword.getText().toString().equals(preferenceManager.getString(Constants.KEY_PASSWORD))){
            Map<String, Object> updates = new HashMap<>();
            updates.put("password", binding.inputPassword.getText().toString());

            userRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        preferenceManager.putString(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());

                        showToast("Profile updated successfully");
                    })
                    .addOnFailureListener(e -> {
                        showToast("Profile didn't updated successfully");
                    });
        }

        if (!binding.inputEmail.getText().toString().equals(preferenceManager.getString(Constants.KEY_EMAIL))){
            Map<String, Object> updates = new HashMap<>();
            updates.put("email", binding.inputEmail.getText().toString());

            userRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());

                        showToast("Profile updated successfully");
                    })
                    .addOnFailureListener(e -> {
                        showToast("Profile didn't updated successfully");
                    });
        }
    }


    private void LoadUserDetails() {
        if (getIntent().hasExtra(Constants.KEY_USER)) {
            user = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
            // Check if there is a user Data before
            if (user != null) {
                binding.imageProfile.setImageBitmap(getBitmapFromEncodedString(user.image));
            }
        }
    }


    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth/bitmap.getWidth();
        Bitmap previwBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight, false);
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
        previwBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayInputStream);
        byte[] bytes = byteArrayInputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        }else {
            return null;
        }
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}