package com.example.EventLanka.Admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.EventLanka.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;

public class AddTicket extends AppCompatActivity {

    private String eventID;
    private TextView ticketBookingDate;
    private ImageView ticketImage;
    private Uri selectedImageUri; // Stores selected image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ticket);

        // Retrieve eventID from intent
        eventID = getIntent().getStringExtra("eventID");

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        TextView ticketName = findViewById(R.id.ticketName);
        TextView ticketPrice = findViewById(R.id.ticketPrice);
        TextView ticketLocation = findViewById(R.id.ticketLocation);
        TextView ticketDate = findViewById(R.id.ticketDate);
        TextView ticketTime = findViewById(R.id.ticketTime);
        ticketBookingDate = findViewById(R.id.ticketBookingDate);
        TextView ticketQuantity = findViewById(R.id.ticketQuantity);
        ticketImage = findViewById(R.id.ticketImage);
        AppCompatButton addButton = findViewById(R.id.add_button);

        // Load event details from Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("event").document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    ticketName.setText(documentSnapshot.getString("title"));
                    ticketLocation.setText(documentSnapshot.getString("location"));
                    ticketDate.setText(documentSnapshot.getString("date"));
                    ticketTime.setText(documentSnapshot.getString("time"));

                    // Load event image using Glide
                    String imageUrl = documentSnapshot.getString("imageUrl");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(imageUrl)
                                .apply(new RequestOptions())
                                .error(R.drawable.image_slider_4)
                                .into(ticketImage);
                    }
                });

        // Handle Image Selection from Gallery
        ticketImage.setOnClickListener(v -> pickImageFromGallery());

        // Add Ticket Button Click
        addButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImageToFirebaseStorage(ticketName, ticketPrice, ticketLocation, ticketDate, ticketTime, ticketBookingDate, ticketQuantity);
            } else {
                saveTicketToFirestore(null, ticketName, ticketPrice, ticketLocation, ticketDate, ticketTime, ticketBookingDate, ticketQuantity);
            }
        });

        // Set Date Picker for ticketBookingDate
        ticketBookingDate.setOnClickListener(v -> showDatePickerDialog(ticketBookingDate));
    }

    /**
     * Opens a Date Picker Dialog and sets the selected date in the given TextView
     */
    private void showDatePickerDialog(TextView dateField) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(AddTicket.this, (view, year, month, dayOfMonth) -> {
            String selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year;
            dateField.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Opens the gallery to select an image
     */
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    /**
     * Handles the result of image selection
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100 && data != null) {
            selectedImageUri = data.getData();
            ticketImage.setImageURI(selectedImageUri);
        }
    }

    /**
     * Uploads selected image to Firebase Storage and then saves ticket details
     */
    private void uploadImageToFirebaseStorage(TextView ticketName, TextView ticketPrice, TextView ticketLocation,
                                              TextView ticketDate, TextView ticketTime, TextView ticketBookingDate, TextView ticketQuantity) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("ticket_images/" + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = storageRef.putFile(selectedImageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            saveTicketToFirestore(imageUrl, ticketName, ticketPrice, ticketLocation, ticketDate, ticketTime, ticketBookingDate, ticketQuantity);
        })).addOnFailureListener(e -> {
            new ErrorAlert().showCustomAlert(AddTicket.this, "Oops..", "Failed to upload image", "error", null);
        });
    }

    /**
     * Saves ticket details to Firestore
     */
    private void saveTicketToFirestore(String imageUrl, TextView ticketName, TextView ticketPrice, TextView ticketLocation,
                                       TextView ticketDate, TextView ticketTime, TextView ticketBookingDate, TextView ticketQuantity) {

        // Create ticket details HashMap
        HashMap<String, Object> ticket = new HashMap<>();
        ticket.put("ticketName", ticketName.getText().toString());
        ticket.put("ticketPrice", ticketPrice.getText().toString());
        ticket.put("ticketLocation", ticketLocation.getText().toString());
        ticket.put("ticketDate", ticketDate.getText().toString());
        ticket.put("ticketTime", ticketTime.getText().toString());
        ticket.put("ticketBookingDate", ticketBookingDate.getText().toString());
        ticket.put("ticketQuantity", ticketQuantity.getText().toString());
        ticket.put("eventID", eventID);
        ticket.put("Status", "Available");

        if (imageUrl != null) {
            ticket.put("ticketImage", imageUrl);
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("ticket")
                .add(ticket)
                .addOnSuccessListener(documentReference -> {
                    new SuccessAlert().showCustomAlert(AddTicket.this, "Success", "Ticket added successfully", "success", null);
                    clearFields(ticketName, ticketPrice, ticketLocation, ticketDate, ticketTime, ticketBookingDate, ticketQuantity);
                })
                .addOnFailureListener(e -> {
                    new ErrorAlert().showCustomAlert(AddTicket.this, "Oops..", "Failed to add ticket", "error", null);
                });
    }

    /**
     * Clears all input fields after ticket is added
     */
    private void clearFields(TextView ticketName, TextView ticketPrice, TextView ticketLocation,
                             TextView ticketDate, TextView ticketTime, TextView ticketBookingDate, TextView ticketQuantity) {
        ticketName.setText("");
        ticketPrice.setText("");
        ticketLocation.setText("");
        ticketDate.setText("");
        ticketTime.setText("");
        ticketBookingDate.setText("");
        ticketQuantity.setText("");
        ticketImage.setImageResource(R.drawable.image_slider_4);
        selectedImageUri = null;
    }
}
