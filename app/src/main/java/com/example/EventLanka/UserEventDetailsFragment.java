package com.example.EventLanka;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.EventLanka.Admin.ErrorAlert;
import com.example.EventLanka.Admin.Loading;
import com.example.EventLanka.Admin.SuccessAlert;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserEventDetailsFragment extends Fragment {

    private String eventID;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Loading loading = new Loading();
        loading.showLoading(requireContext(), "Loading...");

        View view = inflater.inflate(R.layout.fragment_user_event_details, container, false);
        eventID = getArguments().getString("eventID");

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // UI References
        TextView eventName = view.findViewById(R.id.title);
        TextView eventLocation = view.findViewById(R.id.location);
        EditText eventDate = view.findViewById(R.id.date);
        EditText eventTime = view.findViewById(R.id.time);
        TextView eventDescription = view.findViewById(R.id.discription);
        ImageView eventImage = view.findViewById(R.id.card_image);
        TextView eventType = view.findViewById(R.id.type);
        AppCompatButton updateButton = view.findViewById(R.id.addTicket);
        AppCompatButton addCart = view.findViewById(R.id.addCart);

        // Fetch Data from Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("event").document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        eventName.setText(documentSnapshot.getString("title"));
                        eventLocation.setText(documentSnapshot.getString("location"));
                        eventDate.setText(documentSnapshot.getString("date"));
                        eventTime.setText(documentSnapshot.getString("time"));
                        eventDescription.setText(documentSnapshot.getString("description"));
                        eventType.setText(documentSnapshot.getString("type"));

                        Glide.with(requireContext())
                                .load(documentSnapshot.getString("imageUrl"))
                                .apply(new RequestOptions().transform(new RoundedCorners(50)))
                                .error(R.drawable.image_slider_4) // Default image if loading fails
                                .into(eventImage);
                    } else {
                        Toast.makeText(getContext(), "Event not found!", Toast.LENGTH_SHORT).show();
                    }
                    loading.Stop();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.Stop();
                });

        // Buy Ticket Button
        updateButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BuyTicketActivity.class);
            intent.putExtra("eventID", eventID);
            startActivity(intent);
        });

        // Add to Cart Button
        addCart.setOnClickListener(v -> {
            String cartName = eventName.getText().toString();
            String cartDate = eventDate.getText().toString();
            String cartTime = eventTime.getText().toString();

            if (cartName.isEmpty() || cartDate.isEmpty() || cartTime.isEmpty()) {
                Toast.makeText(getContext(), "Event details are missing!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = databaseHelper.addToCart(cartName, cartDate, cartTime);
            if (isInserted) {
                new SuccessAlert().showCustomAlert(
                        getContext(),            // Or requireContext()
                        "Success..",
                        "Event added to cart!",
                        "Success",
                        null
                );
            } else {
                new ErrorAlert().showCustomAlert(
                        getContext(),            // Or requireContext()
                        "Oops..",
                        "Failed to add event to cart",
                        "error",
                        null
                );
            }
        });

        return view;
    }
}
