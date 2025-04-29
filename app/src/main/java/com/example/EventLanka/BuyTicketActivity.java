package com.example.EventLanka;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.EventLanka.Admin.AddTicket;
import com.example.EventLanka.Admin.ErrorAlert;
import com.example.EventLanka.Admin.Loading;
import com.example.EventLanka.Admin.SuccessAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class BuyTicketActivity extends AppCompatActivity {

    private String eventID;
    private String ticketID;
    private String email;
    private String phone;
    private String fname;
    private String lname;
    private String price;
    private String userID;
    private String buyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        Loading loading = new Loading();
        loading.showLoading(this, "Loading...");

        eventID = getIntent().getStringExtra("eventID");

        setContentView(R.layout.activity_buy_ticket);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });




        createNotificationChannel();

        TextView ticketName = findViewById(R.id.ticketName);
        TextView ticketPrice = findViewById(R.id.ticketPrice);
        TextView ticketLocation = findViewById(R.id.ticketLocation);
        TextView ticketDate = findViewById(R.id.ticketDate);
        TextView ticketTime = findViewById(R.id.ticketTime);
        EditText ticketBookingDate = findViewById(R.id.ticketBookingDate);
        TextView ticketQuantity = findViewById(R.id.ticketQuantity);
        ImageView ticketImage = findViewById(R.id.ticketImage);
        AppCompatButton buyButton = findViewById(R.id.buyButton);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("ticket")
                .where(Filter.equalTo("eventID", eventID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot queryDocumentSnapshots = task.getResult();
                            if (queryDocumentSnapshots != null) {
                                ticketName.setText(queryDocumentSnapshots.getDocuments().get(0).getString("ticketName"));
                                ticketPrice.setText(queryDocumentSnapshots.getDocuments().get(0).getString("ticketPrice"));
                                ticketLocation.setText(queryDocumentSnapshots.getDocuments().get(0).getString("ticketLocation"));
                                ticketDate.setText(queryDocumentSnapshots.getDocuments().get(0).getString("ticketDate"));
                                ticketTime.setText(queryDocumentSnapshots.getDocuments().get(0).getString("ticketTime"));
                                ticketBookingDate.setText(queryDocumentSnapshots.getDocuments().get(0).getString("ticketBookingDate"));
                                ticketID = queryDocumentSnapshots.getDocuments().get(0).getId();

                                price = queryDocumentSnapshots.getDocuments().get(0).getString("ticketPrice");
                                loading.Stop();
                            }
                        } else {
                            loading.Stop();
                            new ErrorAlert().showCustomAlert(BuyTicketActivity.this, "Oops..", "Failed to upload image", "error", null);
                        }
                    }
                });

        FirebaseFirestore firestore1 = FirebaseFirestore.getInstance();
        firestore1.collection("event").document(eventID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(BuyTicketActivity.this)
                                    .load(imageUrl)
                                    .apply(new RequestOptions())
                                    .error(R.drawable.image_slider_4)
                                    .into(ticketImage);
                        }
                        loading.Stop();
                    }
                });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TextView ticketQty = findViewById(R.id.ticketQuantity);

                if (ticketQty.getText().toString().isEmpty()) {
                    new ErrorAlert().showCustomAlert(BuyTicketActivity.this, "Oops..", "Please enter quantity", "error", null);
                    return;
                } else {

                    InitializePayment();

                    FirebaseFirestore firestore1 = FirebaseFirestore.getInstance();

                    SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
                    userID = sharedPreferences.getString("Logged_userId", null);

                    firestore1.collection("user").document(userID).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    email = documentSnapshot.getString("email");
                                    phone = documentSnapshot.getString("phone");
                                    fname = documentSnapshot.getString("fname");
                                    lname = documentSnapshot.getString("lname");

                                }
                            });
                }
                loading.Stop();
            }


        });


    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 110) {
//            if (data == null) {
//                Log.e("PaymentHelper", "Payment Failed: No response received");
//                Toast.makeText(this, "Payment failed: No response received!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            String response = data.getStringExtra(PHConstants.INTENT_EXTRA_RESULT);
//            Log.d("PaymentHelper", "Payment Response: " + response);
//
//            if (resultCode == Activity.RESULT_OK) {
//                Log.d("PaymentHelper", "Payment Approved: " + response);
//                updatePaymentStatus(appointmentId);
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Log.d("PaymentHelper", "Payment Canceled or Failed: " + response);
//                Toast.makeText(this, "Payment was canceled or failed!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void InitializePayment() {

        // Initialize payment
        InitRequest req = new InitRequest();
        req.setMerchantId("1229643");       // Merchant ID
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(Double.parseDouble(price));             // Final Amount to be charged
        req.setOrderId("230000123");        // Unique Reference ID
        req.setItemsDescription("Door bell wireless");  // Item description title
        req.setCustom1("This is the custom message 1");
        req.setCustom2("This is the custom message 2");
        req.getCustomer().setFirstName(fname);
        req.getCustomer().setLastName(lname);
        req.getCustomer().setEmail(email);
        req.getCustomer().setPhone(phone);
        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

//Optional Params
        req.setNotifyUrl("");           // Notifiy Url

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

        // Enable sandbox mode
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        payHereLauncher.launch(intent);

    }


    private final ActivityResultLauncher<Intent> payHereLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                        Serializable serializable = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                        if (serializable instanceof PHResponse) {
                            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) serializable;
                            if (response.isSuccess()) {


                                TextView ticketName = findViewById(R.id.ticketName);
                                TextView ticketPrice = findViewById(R.id.ticketPrice);
                                TextView ticketLocation = findViewById(R.id.ticketLocation);
                                TextView ticketDate = findViewById(R.id.ticketDate);
                                TextView ticketTime = findViewById(R.id.ticketTime);
                                EditText ticketBookingDate = findViewById(R.id.ticketBookingDate);
                                TextView ticketQuantity = findViewById(R.id.ticketQuantity);
                                ImageView ticketImage = findViewById(R.id.ticketImage);
                                AppCompatButton buyButton = findViewById(R.id.buyButton);

                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                HashMap<String, Object> buyTicketData = new HashMap<>();
                                buyTicketData.put("ticketName", ticketName.getText().toString());
                                buyTicketData.put("ticketPrice", ticketPrice.getText().toString());
                                buyTicketData.put("ticketLocation", ticketLocation.getText().toString());
                                buyTicketData.put("ticketDate", ticketDate.getText().toString());
                                buyTicketData.put("ticketTime", ticketTime.getText().toString());
                                buyTicketData.put("ticketBookingDate", ticketBookingDate.getText().toString());
                                buyTicketData.put("ticketQuantity", ticketQuantity.getText().toString());
                                buyTicketData.put("eventID", eventID);
                                buyTicketData.put("ticketID", ticketID);
                                buyTicketData.put("userID", userID);
                                buyTicketData.put("buyDate", buyDate);


                                firestore.collection("buyTicket")
                                        .add(buyTicketData)
                                        .addOnSuccessListener(documentReference -> {

                                            sendNotification(Timestamp.now(), ticketTime.getText().toString()+" "+"Booked Ticket Successfuly");

                                            new SuccessAlert().showCustomAlert(BuyTicketActivity.this, "Success", "Ticket purchased successfully", "success", null);
                                            ticketName.setText("");
                                            ticketPrice.setText("");
                                            ticketLocation.setText("");
                                            ticketDate.setText("");
                                            ticketTime.setText("");
                                            ticketBookingDate.setText("");
                                            ticketQuantity.setText("");
                                            ticketImage.setImageResource(R.drawable.image_slider_4);

                                            Long ticketQty = Long.parseLong(ticketQuantity.getText().toString());

                                          //  updatePaymentStatus(ticketQty.toString());


                                        })
                                        .addOnFailureListener(e -> {
                                            new ErrorAlert().showCustomAlert(BuyTicketActivity.this, "Oops..", "Failed to purchase ticket", "error", null);
                                        });


                            } else {
                                Log.d("PayHere", "PayHere failed");

                                new ErrorAlert().showCustomAlert(BuyTicketActivity.this, "Opps..", "Booking Failed", "error", null);

                            }
                        }
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    Log.d("PayHere", "PayHere canceled");
                }
            });

    private void updatePaymentStatus(String ticketQuatity) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("ticket")
                .document(ticketID)
                .update("ticketQuantity", FieldValue.increment(Long.parseLong(ticketQuatity)))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Ticket", "Ticket quantity updated successfully");
                });
    }



    private void sendNotification(Timestamp appointmentTimestamp, String timeSlot) {
        Context context = BuyTicketActivity.this;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if NotificationManager is available
        if (notificationManager == null) {
            Log.e("NotificationDebug", "NotificationManager is null. Cannot send notification.");
            return;
        }

        // Convert timestamp to a readable date format
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
         buyDate = sdf.format(appointmentTimestamp.toDate());



        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "booking_channel")
                .setSmallIcon(R.drawable.logo_e) // Replace with your notification icon
                .setContentTitle("Your booking is successful!")
                .setContentText("You have successfully booked a ticket")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


        Log.d("NotificationDebug", "Notification built with title: " + builder.build().extras.getString("android.title"));
        Log.d("NotificationDebug", "Notification content: " + builder.build().extras.getString("android.text"));

        // Check if the notification channel exists (for Android 8.0 and above)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel("booking_channel");
            if (channel == null) {
                Log.e("NotificationDebug", "Notification channel 'booking_channel' does not exist.");
                return;
            } else {
                Log.d("NotificationDebug", "Notification channel 'booking_channel' exists.");
            }
        }

        // Show the notification
        notificationManager.notify(1, builder.build());
        Log.d("NotificationDebug", "Notification sent successfully.");
     }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Booking Notifications";
            String description = "Notifications for booking status";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("booking_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = BuyTicketActivity.this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}