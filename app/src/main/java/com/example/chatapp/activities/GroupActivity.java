package com.example.chatapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.chatapp.Listeners.UsersListener;
import com.example.chatapp.R;
import com.example.chatapp.adapters.UsersAdapter;
import com.example.chatapp.databinding.ActivityGroupBinding;
import com.example.chatapp.databinding.ActivitySignUpBinding;
import com.example.chatapp.models.User;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends BaseActivity implements UsersListener {

    private ActivityGroupBinding binding;
    private PreferenceManager preferenceManager;
    private User Member;
    private String encodedImage;
    ArrayList<User> userArrayList;
    List<User> MembersList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LoadMemberDetails();
        preferenceManager = new PreferenceManager(this);
        setListeners();
    }
    private void LoadMemberDetails() {
        if (getIntent().hasExtra(Constants.KEY_USER)) {
            Member = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
            MembersList.add(Member);
            // Check if MembersList has members before
            if (!MembersList.isEmpty() && MembersList.size() != 0) {
                userArrayList = new ArrayList<>();  // Initialize userArrayList
                UsersAdapter usersAdapter = new UsersAdapter(MembersList, this);
                binding.usersRecyclerView.setAdapter(usersAdapter);
                binding.usersRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }


    //Customize it
    private  void setListeners(){
        //on click add friend show the friends list

        //picking up image profile for group
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.layoutImageAdd.setOnClickListener(v -> {
            if(MembersList.isEmpty()){
                startActivity(new Intent(getApplicationContext(), friendlistsAcitivity.class));
            }else{
                Toast.makeText(this, "Start with one member", Toast.LENGTH_SHORT).show();
            }
        });
        binding.imageProfile2.setOnClickListener(v -> {
            if(MembersList.isEmpty()){
                startActivity(new Intent(getApplicationContext(), friendlistsAcitivity.class));
            }else{
                Toast.makeText(this, "Start with one member", Toast.LENGTH_SHORT).show();
            }
        });
        binding.textAdd.setOnClickListener(v -> {
            if(MembersList.isEmpty()){
                startActivity(new Intent(getApplicationContext(), friendlistsAcitivity.class));
            }else{
                Toast.makeText(this, "Start with one member", Toast.LENGTH_SHORT).show();
            }
        });
        //for signing up
        binding.buttonSaveGroup.setOnClickListener(v -> {
            if (isValidGroupDetails()) {
                AddGroup();
            }
        });

    }

    private void showToast(String Message){
        Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_SHORT).show();
    }

    private void AddGroup() {
        //save the group as
        //change the screen to chat activity
        //
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

    private Boolean isValidGroupDetails(){
        if (encodedImage == null){
            showToast("Select profile image for the group");
            return false;
        } else if (binding.inputGroupName.getText().toString().trim().isEmpty()) {
            showToast("Enter Name for the group");
            return false;
        } else if (userArrayList.isEmpty()) {
            showToast("Add a member");
            return false;
        }else {
            return true;
        }
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.buttonSaveGroup.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSaveGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {

    }
}