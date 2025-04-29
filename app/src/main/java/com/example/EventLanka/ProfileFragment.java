package com.example.EventLanka;

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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private ShapeableImageView profileImageView;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Loading loading = new Loading();
        loading.showLoading(requireContext(), "Loading...");

        EditText firstName = view.findViewById(R.id.pfname);
        EditText lastName = view.findViewById(R.id.plname);
        EditText email = view.findViewById(R.id.pemail);
        EditText mobile = view.findViewById(R.id.pmobile);
        profileImageView = view.findViewById(R.id.userImage);
        Button saveButton = view.findViewById(R.id.add_button);
        Button logOutButton = view.findViewById(R.id.logOutButton);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Retrieve userId from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginData", MODE_PRIVATE);
        userId = sharedPreferences.getString("Logged_userId", "");

        if (userId.isEmpty()) {
            loading.Stop();
            new ErrorAlert().showCustomAlert(getContext(), "Oops..", "User ID not found. Please log in again.", "error", null);
            return view;
        }

        // Fetch user data from Firestore
        firestore.collection("user").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        firstName.setText(documentSnapshot.getString("fname"));
                        lastName.setText(documentSnapshot.getString("lname"));
                        email.setText(documentSnapshot.getString("email"));
                        mobile.setText(documentSnapshot.getString("mobile"));

                        // Retrieve profile image URL
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.profile_png)
                                    .error(R.drawable.profile_png)
                                    .into(profileImageView);
                        }
                    }
                    loading.Stop();
                }) .addOnFailureListener(e -> {
                    loading.Stop();


                    requireActivity().runOnUiThread(() -> {
                        new ErrorAlert().showCustomAlert(
                                getContext(),            // Or requireContext()
                                "Oops..",
                                "Failed to load profile data",
                                "error",
                                null
                        );
                    });
                });

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
            loading.showLoading(getContext(), "Loading...");
            // Get user input
            String fname = firstName.getText().toString().trim();
            String lname = lastName.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String mobileText = mobile.getText().toString().trim();

            if (fname.isEmpty() || lname.isEmpty() || emailText.isEmpty() || mobileText.isEmpty()) {
                loading.Stop();
                requireActivity().runOnUiThread(() -> {
                    new ErrorAlert().showCustomAlert(
                            getContext(),            // Or requireContext()
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
            userPmap.put("mobile", mobileText);

            // Update Firestore document
            firestore.collection("user").document(userId)
                    .update(userPmap)
                    .addOnSuccessListener(aVoid -> {
                        loading.Stop();
                       // new SuccessAlert().showCustomAlert(getContext(), "Success", "Profile updated successfully", "error", null);
                    })
                    .addOnFailureListener(e -> {
                        loading.Stop();
                        //new ErrorAlert().showCustomAlert(getContext(), "Oops..", "Update failed", "error", null);
                    });

            // Upload profile image if selected
            if (selectedImageUri != null) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReference("profile_images/" + UUID.randomUUID().toString() + ".jpg");
                UploadTask uploadTask = storageReference.putFile(selectedImageUri);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        userPmap.put("profileImageUrl", uri.toString());
                        saveUserProfile(firestore, userPmap);
                    });
                }).addOnFailureListener(e -> {
                    loading.Stop();
                    requireActivity().runOnUiThread(() -> {
                        new ErrorAlert().showCustomAlert(
                                getContext(),            // Or requireContext()
                                "Oops..",
                                "Image Upload Failed",
                                "error",
                                null
                        );
                    });
                });
            } else {
                saveUserProfile(firestore, userPmap);
            }
            loading.Stop();

        });

        // Logout button click listener
        logOutButton.setOnClickListener(v -> {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.clear();
//            editor.apply();

            Intent intent = new Intent(requireActivity(), MainActivity.class);
          //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            loading.Stop();
            requireActivity().runOnUiThread(() -> {
                new SuccessAlert().showCustomAlert(
                        getContext(),            // Or requireContext()
                        "Success..",
                        "Logout Successful",
                        "error",
                        null
                );
            });
        });

        return view;
    }

    /**
     * Helper method to save the user profile data to Firestore.
     */
    private void saveUserProfile(FirebaseFirestore firestore, HashMap<String, Object> userPmap) {
        firestore.collection("user")
                .document(userId)
                .update(userPmap) // Use update to preserve existing fields
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        requireActivity().runOnUiThread(() -> {
                            new SuccessAlert().showCustomAlert(
                                    getContext(),            // Or requireContext()
                                    "Success..",
                                    "Profile Updated",
                                    "error",
                                    null
                            );
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            new ErrorAlert().showCustomAlert(
                                    getContext(),            // Or requireContext()
                                    "Oops..",
                                    "Profile Update Failed",
                                    "error",
                                    null
                            );
                        });
                    }
                });
    }
}
