package com.example.EventLanka;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.EventLanka.Admin.AdminEventDetailsFragment;
import com.example.EventLanka.Admin.EventManagementFragment;
import com.example.EventLanka.Admin.Loading;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class HomeFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Loading loading = new Loading();
        loading.showLoading(requireContext(), "Loading...");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageSlider imageSlider = view.findViewById(R.id.image_slider);

        ArrayList<SlideModel> slideImages = new ArrayList<>();
        slideImages.add(new SlideModel(R.drawable.image_slider_1, ScaleTypes.CENTER_CROP));
        slideImages.add(new SlideModel(R.drawable.image_slider_2, ScaleTypes.CENTER_CROP));
        slideImages.add(new SlideModel(R.drawable.image_slider_3, ScaleTypes.CENTER_CROP));
        slideImages.add(new SlideModel(R.drawable.image_slider_4, ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideImages);

        LinearLayout view4 = view.findViewById(R.id.view4);
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
                                        .placeholder(R.drawable.image_slider_1)
                                        .error(R.drawable.image_slider_1)
                                        .into(cardImage);
                            }

                            // On click, hide this fragment and add the detail fragment
                            eventCard.setOnClickListener(v -> {
                                Fragment userEventDetailsFragment = new UserEventDetailsFragment();
                                Bundle args = new Bundle();
                                args.putString("eventID", eventID);
                                userEventDetailsFragment.setArguments(args);

                                FragmentTransaction transaction = requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction();

                                // Hide this fragment
                                transaction.hide(HomeFragment.this);

                                // Add the detail fragment to your activity container (NOT the linear layout)
                                // Make sure R.id.mainFragmentContainer is in your activity layout
                                transaction.add(R.id.mainScrillView, userEventDetailsFragment);

                                // Add to back stack so pressing BACK will remove detail and unhide this
                                transaction.addToBackStack(null);

                                // Commit
                                transaction.commit();

                            });

                            loading.Stop();

                            view4.addView(eventCard);


                        }
                    }
                });



        return  view;

    }
}