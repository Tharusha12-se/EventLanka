package com.example.EventLanka.Admin;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.EventLanka.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class EventManagementFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Loading loading = new Loading();
        loading.showLoading(requireContext(), "Loading...");

        View view = inflater.inflate(R.layout.fragment_event_management, container, false);


        LinearLayout adminEventView = view.findViewById(R.id.adminEvent);
        int cornerRadius = 50;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("event")
                .where(Filter.equalTo("status", "active"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots){

                            View eventCard = getLayoutInflater().inflate(R.layout.main_event_home,null);

                            ImageView cardImage = eventCard.findViewById(R.id.card_image);
                            TextView cardTitle = eventCard.findViewById(R.id.title);
                            TextView cardLocation = eventCard.findViewById(R.id.location);
                            TextView cardDate = eventCard.findViewById(R.id.date);
                            AppCompatButton viewButton = view.findViewById(R.id.viewButton);
                            AppCompatButton addButton = view.findViewById(R.id.addEvent);

                            cardImage.setImageResource(R.drawable.image_slider_1);
                            cardTitle.setText(document.getString("title"));
                            cardLocation.setText(document.getString("location"));
                            cardDate.setText(document.getString("date"));

                            String eventID = document.getId();

                            // Retrieve event image URL
                            String profileImageUrl = document.getString("imageUrl");
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(profileImageUrl)
                                        .apply(new RequestOptions().transform(new RoundedCorners(cornerRadius)))
                                        .error(R.drawable.image_slider_1)
                                        .placeholder(R.drawable.image_slider_1)
                                        .into(cardImage);
                            }

                            // On click, hide this fragment and add the detail fragment
                            eventCard.setOnClickListener(v -> {
                                Fragment adminEventDetailsFragment = new AdminEventDetailsFragment();
                                Bundle args = new Bundle();
                                args.putString("eventID", eventID);
                                adminEventDetailsFragment.setArguments(args);

                                FragmentTransaction transaction = requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction();

                                // Hide this fragment
                                transaction.hide(EventManagementFragment.this);

                                // Add the detail fragment to your activity container (NOT the linear layout)
                                // Make sure R.id.mainFragmentContainer is in your activity layout
                                transaction.add(R.id.mainScrillView, adminEventDetailsFragment);

                                // Add to back stack so pressing BACK will remove detail and unhide this
                                transaction.addToBackStack(null);

                                // Commit
                                transaction.commit();

                            });

                            viewButton.setOnClickListener(v -> {
                                Fragment viewInactiveFragment = new ViewInactiveFragment();

                                FragmentTransaction transaction = requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction();

                                // Hide this fragment
                                transaction.hide(EventManagementFragment.this);

                                // Add the detail fragment to your activity container (NOT the linear layout)
                                // Make sure R.id.mainFragmentContainer is in your activity layout
                                transaction.add(R.id.mainScrillView, viewInactiveFragment);

                                // Add to back stack so pressing BACK will remove detail and unhide this
                                transaction.addToBackStack(null);

                                // Commit
                                transaction.commit();

                            });

                            addButton.setOnClickListener(v -> {
                                Fragment addEventFragment = new EventDetailsFragment();

                                FragmentTransaction transaction = requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction();

                                // Hide this fragment
                                transaction.hide(EventManagementFragment.this);

                                // Add the detail fragment to your activity container (NOT the linear layout)
                                // Make sure R.id.mainFragmentContainer is in your activity layout
                                transaction.add(R.id.mainScrillView, addEventFragment);

                                // Add to back stack so pressing BACK will remove detail and unhide this
                                transaction.addToBackStack(null);

                                // Commit
                                transaction.commit();

                            });


                            loading.Stop();
                            adminEventView.addView(eventCard);
                        }
                    }
                });



        return view;
    }
}