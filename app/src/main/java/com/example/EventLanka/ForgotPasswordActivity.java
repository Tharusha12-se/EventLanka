package com.example.EventLanka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.EventLanka.Admin.ErrorAlert;
import com.example.EventLanka.Admin.Loading;
import com.example.EventLanka.Admin.SuccessAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Loading loading = new Loading();

        Button forget_password = findViewById(R.id.next);
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loading.showLoading(ForgotPasswordActivity.this, "Loading...");

                EditText email = findViewById(R.id.editTextForgotEmail);
                String email_text = email.getText().toString();

                if (email_text.trim().isEmpty()) {
                    loading.Stop();
                    new ErrorAlert().showCustomAlert(ForgotPasswordActivity.this, "Opps..", "Please Enter Email Address", "error", null);
                }else {

                    firestore.collection("user")
                            .where(
                                    Filter.equalTo("email", email_text)
                            ).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            loading.Stop();
                                            new ErrorAlert().showCustomAlert(ForgotPasswordActivity.this, "Opps..", "Email Not Found", "error", null);
                                        } else {

                                            // send email to user
                                            final int code = (int) (Math.random() * 1000000);

                                            String userId = task.getResult().getDocuments().get(0).getId();

                                            firestore.collection("user").document(userId).update("vCode", Integer.toString(code));

                                            boolean status = sendEmail(email_text, code);

                                            if (status) {

                                                ForgetPasswordCode forgetPasswordCode = new ForgetPasswordCode();
                                                forgetPasswordCode.Show(ForgotPasswordActivity.this, userId);

                                                loading.Stop();
                                                new SuccessAlert().showCustomAlert(ForgotPasswordActivity.this, "Success..", "Email Sent Successfully. Check Your Inbox.", "success", null);

                                            } else {
                                                loading.Stop();
                                                new ErrorAlert().showCustomAlert(ForgotPasswordActivity.this, "Opps..", "Failed to send email", "error", null);
                                            }


                                        }
                                    } else {
                                        loading.Stop();
                                        new ErrorAlert().showCustomAlert(ForgotPasswordActivity.this, "Opps..", "Something Went Wrong", "error", null);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.Stop();

                                    // log error
                                    Log.e("error", String.valueOf(e));

                                    // show error Alert
                                    new ErrorAlert().showCustomAlert(ForgotPasswordActivity.this, "Opps..", "Something Went Wrong", "error", null);
                                }
                            });

                }

            }
        });

    }

    private boolean sendEmail(String email, int code) {
        AtomicBoolean status = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1); // Wait until request completes

        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            String json = "{ \"email\": \"" + email + "\", \"code\": \"" + code + "\" }";
            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url("https://521b-2402-4000-2140-d897-d1cc-3cb8-6437-5a81.ngrok-free.app/EventLanka/EmailSending")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    String responseText = response.body().string();
                    Log.i("Email Request", "Email Response: " + responseText);
                    status.set(true);
                }
            } catch (Exception e) {
                Log.e("Email Request", "Email request failed", e);
            } finally {
                latch.countDown(); // Signal completion
            }
        }).start();

        try {
            latch.await(); // Wait for the request to finish
        } catch (InterruptedException e) {
            Log.e("Email Request", "Thread interrupted", e);
        }

        return status.get();
}


}