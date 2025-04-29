package com.example.EventLanka.Admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.EventLanka.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class ActiveUserDetailsFragment extends Fragment {

    private String userID; // Stores the userID

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve userID from arguments
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);

        SuccessAlert successAlert = new SuccessAlert();

        // Find UI components
        TextView userName = view.findViewById(R.id.pfname);
        TextView userEmail = view.findViewById(R.id.pemail);
        TextView userMobile = view.findViewById(R.id.pmobile);
        ShapeableImageView userImage = view.findViewById(R.id.userImage);
        Button inactiveButton = view.findViewById(R.id.inactive_button);

        // Fetch user details from Firestore
        if (userID != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user").document(userID).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Extract user details
                    String fname = documentSnapshot.getString("fname");
                    String lname = documentSnapshot.getString("lname");
                    String email = documentSnapshot.getString("email");
                    String mobile = documentSnapshot.getString("mobile");
                    String image = documentSnapshot.getString("profileImageUrl");

                    // Update UI fields
                    if (fname != null && lname != null) {
                        userName.setText(fname + " " + lname);
                    }
                    if (email != null) {
                        userEmail.setText(email);
                    }
                    if (mobile != null) {
                        userMobile.setText(mobile);
                    }

                    // Load profile image using Glide
                    if (image != null && !image.isEmpty()) {
                        Glide.with(requireContext())
                                .load(image)
                                .apply(new RequestOptions().transform(new RoundedCorners(50)))
                                .error(R.drawable.profile_png) // Default image if loading fails
                                .into(userImage);
                    } else {
                        userImage.setImageResource(R.drawable.profile_png); // Default image
                    }
                }
            });
        }

        // Handle "Inactive" button click
        inactiveButton.setOnClickListener(v -> {
            // Inactivate user in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("user").document(userID);

            userRef.update("status", "inactive").addOnSuccessListener(aVoid -> {
                // Show Success Alert and handle "OK" click
                new SuccessAlert().showCustomAlert(
                        getContext(),
                        "Success..",
                        "User Inactivated Successfully",
                        "success",
                        null);

                // Go back to UserManagementFragment
                goBackToUserManagement();
            });
        });

        return view;
    }

    private void goBackToUserManagement() {
        // Get FragmentManager
        FragmentManager fragmentManager = getParentFragmentManager();

        // Pop the back stack (removes UserDetailsFragment & returns to UserManagementFragment)
        fragmentManager.popBackStack();

        // Find UserManagementFragment in the fragment manager
        UserManagementFragment userManagementFragment = (UserManagementFragment)
                fragmentManager.findFragmentByTag("UserManagementFragment");

        // If it exists in memory, trigger its loadUser() method to refresh the data
        if (userManagementFragment != null) {
            userManagementFragment.loadUser();
        }
    }
}
