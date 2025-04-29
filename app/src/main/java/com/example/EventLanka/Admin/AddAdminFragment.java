package com.example.EventLanka.Admin;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide; // Import Glide for Image Loading
import com.example.EventLanka.Admin.ErrorAlert;
import com.example.EventLanka.Admin.Loading;
import com.example.EventLanka.Admin.SuccessAlert;
import com.example.EventLanka.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class AddAdminFragment extends Fragment {

    private ShapeableImageView profileImageView;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_admin, container, false);




        profileImageView = view.findViewById(R.id.userImage);
        Button saveButton = view.findViewById(R.id.active_button);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();


        // Image picker initialization
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            selectedImageUri = uri;
                            profileImageView.setImageURI(uri);
                        }
                    }
                });

        // Open gallery when clicking profile image
        profileImageView.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(pickIntent);
        });

        // Save button click listener
        saveButton.setOnClickListener(v -> {

            EditText firstName = view.findViewById(R.id.pfname);
            EditText lastName = view.findViewById(R.id.plname);
            EditText email = view.findViewById(R.id.pemail);

            Loading loading = new Loading();
            loading.showLoading(requireContext(), "Loading...");

            // Get user input
            String fname = firstName.getText().toString().trim();
            String lname = lastName.getText().toString().trim();
            String emailText = email.getText().toString().trim();

            if (fname.isEmpty() || lname.isEmpty() || emailText.isEmpty()) {
                loading.Stop();
                requireActivity().runOnUiThread(() -> {
                    new ErrorAlert().showCustomAlert(
                            getContext(),
                            "Oops..",
                            "All fields must be filled",
                            "error",
                            null
                    );
                });
                return;
            }

            // Create a HashMap to store updated values
            HashMap<String, Object> userPmap = new HashMap<>();
            userPmap.put("fname", fname);
            userPmap.put("lname", lname);
            userPmap.put("email", emailText);
            userPmap.put("status", "active");
            userPmap.put("type", "admin");
            userPmap.put("mobile", "");
            userPmap.put("password", "EventLanka123");


            firestore.collection("user")
                    .add(userPmap)
                    .addOnSuccessListener(aVoid -> {
                        loading.Stop();
                         new SuccessAlert().showCustomAlert(getContext(), "Success", "Profile updated successfully", "error", null);
                    })
                    .addOnFailureListener(e -> {
                        loading.Stop();
                        new ErrorAlert().showCustomAlert(getContext(), "Oops..", "Update failed", "error", null);
                    });

           loading.Stop();

        });



        return view;
    }


}
