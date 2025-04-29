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

public class ViewInactiveFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Loading loading = new Loading();
        loading.showLoading(requireContext(), "Loading...");

        View view = inflater.inflate(R.layout.fragment_view_inactive, container, false);


        LinearLayout adminEventView = view.findViewById(R.id.adminInactiveEvent);
        int cornerRadius = 50;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("event")
                .where(Filter.equalTo("status", "inactive"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots){

                            View InactiveEventCard = getLayoutInflater().inflate(R.layout.main_event_home,null);

                            ImageView cardImage = InactiveEventCard.findViewById(R.id.card_image);
                            TextView cardTitle = InactiveEventCard.findViewById(R.id.title);
                            TextView cardLocation = InactiveEventCard.findViewById(R.id.location);
                            TextView cardDate = InactiveEventCard.findViewById(R.id.date);

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
                            InactiveEventCard.setOnClickListener(v -> {
                                Fragment viewInactiveDetailsFragment = new InactiveEventDetailsFragment();
                                Bundle args = new Bundle();
                                args.putString("eventID", eventID);
                                viewInactiveDetailsFragment.setArguments(args);

                                FragmentTransaction transaction = requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction();

                                // Hide this fragment
                                transaction.hide(ViewInactiveFragment.this);

                                // Add the detail fragment to your activity container (NOT the linear layout)
                                // Make sure R.id.mainFragmentContainer is in your activity layout
                                transaction.add(R.id.mainScrillView, viewInactiveDetailsFragment);

                                // Add to back stack so pressing BACK will remove detail and unhide this
                                transaction.addToBackStack(null);

                                // Commit
                                transaction.commit();

                            });

                            loading.Stop();
                            adminEventView.addView(InactiveEventCard);
                        }
                    }
                });



        return view;
    }
}