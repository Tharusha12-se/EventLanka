package com.example.EventLanka;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.EventLanka.Admin.Loading;
import com.example.EventLanka.R;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ViewTicketFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Show loading animation
        Loading loading = new Loading();
        loading.showLoading(requireContext(), "Loading...");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_ticket, container, false);

        // Correct way to access SharedPreferences inside a Fragment
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginData", MODE_PRIVATE);
        String userID = sharedPreferences.getString("Logged_userId", null);

        // Reference to layout where dynamic ticket views will be added
        LinearLayout adminTicketView = view.findViewById(R.id.ticketView);

        View view1 = inflater.inflate(R.layout.activity_home, container, false);

        ImageView ticket = view1.findViewById(R.id.imageView15);


        // Initialize Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("buyTicket")
                .where(Filter.equalTo("userID", userID))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // If no tickets found, stop loading
                    if (queryDocumentSnapshots.isEmpty()) {
                        loading.Stop();
                        return;
                    }

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Inflate ticket card layout dynamically
                        View ticketCard = getLayoutInflater().inflate(R.layout.ticket, null);

                        // Get UI elements from ticket layout
                        ImageView cardImage = ticketCard.findViewById(R.id.timage);
                        TextView cardTitle = ticketCard.findViewById(R.id.tname);
                        TextView cardDate = ticketCard.findViewById(R.id.tdate);
                        TextView cardTime = ticketCard.findViewById(R.id.ttime);
                        TextView cardPrice = ticketCard.findViewById(R.id.tprice);
                        TextView cardEndTime = ticketCard.findViewById(R.id.tEndDate);

                        // Set ticket details
                        Glide.with(requireContext())
                                .load(R.drawable.image_slider_4) // Placeholder image
                                .into(cardImage);
                        cardTitle.setText(document.getString("ticketName"));
                        cardDate.setText(document.getString("ticketDate"));
                        cardTime.setText(document.getString("ticketTime"));
                        String price = document.getString("ticketPrice");
                        cardPrice.setText(price + " LKR");
                        cardEndTime.setText(document.getString("ticketBookingDate"));



                        // Stop loading and add ticket to the view
                        adminTicketView.addView(ticketCard);
                    }
                    loading.Stop();
                })
                .addOnFailureListener(e -> {
                    // Stop loading and handle failure case
                    loading.Stop();
                });

        return view;
    }
}
