package com.example.EventLanka.Admin;

import android.content.Intent;
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

public class TicketManagementFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Loading loading = new Loading();
        loading.showLoading(requireContext(), "Loading...");

        View view = inflater.inflate(R.layout.fragment_ticket_management, container, false);


        LinearLayout adminTicketView = view.findViewById(R.id.ticketView);
        int cornerRadius = 50;

        AppCompatButton addTicket = view.findViewById(R.id.InactiveTicketButton);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("ticket")
                .where(Filter.equalTo("Status", "Available"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots){

                            View ticketCard = getLayoutInflater().inflate(R.layout.ticket,null);

                            ImageView cardImage = ticketCard.findViewById(R.id.timage);
                            TextView cardTitle = ticketCard.findViewById(R.id.tname);
                            TextView cardDate = ticketCard.findViewById(R.id.tdate);
                            TextView cardTime = ticketCard.findViewById(R.id.ttime);
                            TextView cardPrice = ticketCard.findViewById(R.id.tprice);
                            TextView cardEndTime = ticketCard.findViewById(R.id.tEndDate);

                            cardImage.setImageResource(R.drawable.image_slider_4);
                            cardTitle.setText(document.getString("ticketName"));
                            cardDate.setText(document.getString("ticketDate"));
                            cardTime.setText(document.getString("ticketTime"));
                             String price = document.getString("ticketPrice");
                            cardPrice.setText(price+" "+"LKR");
                            cardEndTime.setText(document.getString("ticketBookingDate"));

                            String ticketID = document.getId();

                            ticketCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(requireContext(), InactiveTicket.class);
                                    intent.putExtra("ticketID", ticketID); // Pass eventID to the activity
                                    startActivity(intent);
                                }
                            });
//
//                            // Retrieve event image URL
//                            String profileImageUrl = document.getString("imageUrl");
//                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
//                                Glide.with(requireContext())
//                                        .load(profileImageUrl)
//                                        .apply(new RequestOptions().transform(new RoundedCorners(cornerRadius)))
//                                        .error(R.drawable.image_slider_1)
//                                        .placeholder(R.drawable.image_slider_1)
//                                        .into(cardImage);
//                            }

                      //       On click, hide this fragment and add the detail fragment
//                            ticketCard.setOnClickListener(v -> {
//                                Fragment InactiveTicketFragment = new InactiveTicketFragment();
//                                Bundle args = new Bundle();
//                                args.putString("ticketID", ticketID);
//                                InactiveTicketFragment.setArguments(args);
//
//                                FragmentTransaction transaction = requireActivity()
//                                        .getSupportFragmentManager()
//                                        .beginTransaction();
//
//                                // Hide this fragment
//                                transaction.hide(Ticke.this);
//
//                                // Add the detail fragment to your activity container (NOT the linear layout)
//                                // Make sure R.id.mainFragmentContainer is in your activity layout
//                                transaction.add(R.id.mainScrillView, InactiveTicketFragment);
//
//                                // Add to back stack so pressing BACK will remove detail and unhide this
//                                transaction.addToBackStack(null);
//
//                                // Commit
//                                transaction.commit();
//
//                            });

//                            viewButton.setOnClickListener(v -> {
//                                Fragment viewInactiveFragment = new ViewInactiveFragment();
//
//                                FragmentTransaction transaction = requireActivity()
//                                        .getSupportFragmentManager()
//                                        .beginTransaction();
//
//                                // Hide this fragment
//                                transaction.hide(EventManagementFragment.this);
//
//                                // Add the detail fragment to your activity container (NOT the linear layout)
//                                // Make sure R.id.mainFragmentContainer is in your activity layout
//                                transaction.add(R.id.mainScrillView, viewInactiveFragment);
//
//                                // Add to back stack so pressing BACK will remove detail and unhide this
//                                transaction.addToBackStack(null);
//
//                                // Commit
//                                transaction.commit();
//
//                            });


                            loading.Stop();
                            adminTicketView.addView(ticketCard);
                        }
                    }
                });



        return view;
    }
}