package com.example.EventLanka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.EventLanka.Admin.ErrorAlert;
import com.example.EventLanka.Admin.Loading;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        Button button = findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Loading loading = new Loading();
                loading.showLoading(SignupActivity.this, "Loading...");

                EditText email = findViewById(R.id.editTextEmail);
                EditText password = findViewById(R.id.editTextUserPassword);
                EditText mobile = findViewById(R.id.editTextUserMobile);
                EditText fname = findViewById(R.id.editTextFname);
                EditText lname = findViewById(R.id.editTextLname);

                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                String mobileText = mobile.getText().toString();
                String fnameText = fname.getText().toString();
                String lnameText = lname.getText().toString();

                if (fnameText.isEmpty()) {
                    new ErrorAlert().showCustomAlert(SignupActivity.this, "Oops..", "Please enter first name", "error", null);
                } else if (lnameText.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please enter last name", Toast.LENGTH_SHORT).show();
                    new ErrorAlert().showCustomAlert(SignupActivity.this, "Oops..", "Please enter last name", "error", null);
                } else if (emailText.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    new ErrorAlert().showCustomAlert(SignupActivity.this, "Oops..", "Please enter first name", "error", null);
                } else if (passwordText.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    new ErrorAlert().showCustomAlert(SignupActivity.this, "Oops..", "Please enter first name", "error", null);
                } else if (mobileText.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please enter mobile", Toast.LENGTH_SHORT).show();
                    new ErrorAlert().showCustomAlert(SignupActivity.this, "Oops..", "Please enter first name", "error", null);
                } else {

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("email", emailText);
                    userMap.put("password", passwordText);
                    userMap.put("mobile", mobileText);
                    userMap.put("fname", fnameText);
                    userMap.put("lname", lnameText);
                    userMap.put("status", "active");
                    userMap.put("type", "user");


                    firestore.collection("user")
                            .add(userMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    Log.i("TAG", "onComplete: " + task.isSuccessful());

                                    email.setText("");
                                    password.setText("");
                                    mobile.setText("");
                                    fname.setText("");
                                    lname.setText("");

                                    Intent i = new Intent(SignupActivity.this, MainActivity.class);
                                    startActivity(i);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignupActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                    Log.i("TAG", "onFailure: " + e.getMessage());
                                }
                            });

                }

            }
        });

    }
}