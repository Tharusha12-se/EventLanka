package com.example.EventLanka.Admin;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.EventLanka.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ActiveUserFragment extends Fragment {

    private Loading loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate this fragment's layout
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);



        // Show loading
        loading = new Loading();
        loading.showLoading(requireContext(), "Loading...");

        // The linear layout where user cards are added
        LinearLayout view1 = view.findViewById(R.id.loadUser);

        // Example corner radius for profile images
        int cornerRadius = 50;

        // Fetch from Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("user")
                .where(Filter.equalTo("status", "active"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String fname = document.getString("fname");
                            String lname = document.getString("lname");
                            String email = document.getString("email");
                            String image = document.getString("profileImageUrl");
                            String userID = document.getId();

                            // Inflate each user "card"
                            View userView = getLayoutInflater().inflate(R.layout.fragment_user, view1, false);

                            TextView nameText = userView.findViewById(R.id.userName);
                            TextView emailText = userView.findViewById(R.id.userEmail);
                            ShapeableImageView imageView = userView.findViewById(R.id.userImage);

                            nameText.setText(fname + " " + lname);
                            emailText.setText(email);

                            if (image != null && !image.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(image)
                                        .apply(new RequestOptions().transform(new RoundedCorners(cornerRadius)))
                                        .error(R.drawable.profile_png)
                                        .into(imageView);
                            } else {
                                imageView.setImageResource(R.drawable.profile_png);
                            }

                            // On click, hide this fragment and add the detail fragment
                            userView.setOnClickListener(v -> {
                                Fragment userDetailFragment = new ActiveUserDetailsFragment();
                                Bundle args = new Bundle();
                                args.putString("userID", userID);
                                userDetailFragment.setArguments(args);

                                FragmentTransaction transaction = requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction();

                                // Hide this fragment
                                transaction.hide(ActiveUserFragment.this);

                                // Add the detail fragment to your activity container (NOT the linear layout)
                                // Make sure R.id.mainFragmentContainer is in your activity layout
                                transaction.add(R.id.mainScrillView, userDetailFragment);

                                // Add to back stack so pressing BACK will remove detail and unhide this
                                transaction.addToBackStack(null);

                                // Commit
                                transaction.commit();
                            });

                            view1.addView(userView);
                        }

                        // Stop loading after items are loaded
                        loading.Stop();
                    }
                });

        return view;

    }
}