package com.example.EventLanka.Admin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.EventLanka.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class InactiveTicket extends AppCompatActivity {

    private String ticketID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inactive_ticket);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve ticketID from Intent
        ticketID = getIntent().getStringExtra("ticketID");

        // Initialize TextViews
        TextView ticketNameView = findViewById(R.id.tname);
        TextView ticketPriceView = findViewById(R.id.tprice);
        TextView ticketDateView = findViewById(R.id.tdate);
        TextView ticketTimeView = findViewById(R.id.ttime);
        TextView ticketBookingDateView = findViewById(R.id.tEndDate);
        AppCompatButton cancelButton = findViewById(R.id.cancleButton);

        // Ensure ticketID is not null before fetching data
        if (ticketID != null && !ticketID.isEmpty()) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("ticket")
                    .document(ticketID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Get values from Firestore document
                            String ticketName = documentSnapshot.getString("ticketName");
                            String ticketPrice = documentSnapshot.getString("ticketPrice");
                            String ticketDate = documentSnapshot.getString("ticketDate");
                            String ticketTime = documentSnapshot.getString("ticketTime");
                            String ticketBookingDate = documentSnapshot.getString("ticketBookingDate");

                            // Set text in TextViews
                            ticketNameView.setText(ticketName);
                            ticketPriceView.setText(ticketPrice);
                            ticketDateView.setText(ticketDate);
                            ticketTimeView.setText(ticketTime);
                            ticketBookingDateView.setText(ticketBookingDate);
                        } else {
                            ticketNameView.setText("Ticket not found");
                        }
                    })
                    .addOnFailureListener(e -> {
                        ticketNameView.setText("Error loading ticket");
                    });



        } else {
            ticketNameView.setText("Invalid Ticket ID");
        }

        cancelButton.setOnClickListener(v -> {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("ticket")
                    .document(ticketID)
                    .update("Status", "Unavailable")
                    .addOnSuccessListener(aVoid -> {
                        // Handle ticket deletion success

                        new SuccessAlert().showCustomAlert(InactiveTicket.this,
                                "Success",
                                "Ticket cancle successfully",
                                "success", null);
                    })
                    .addOnFailureListener(e -> {
                        // Handle ticket deletion failure
                    });
            // Handle cancel button click
            finish();
        });
    }
}
