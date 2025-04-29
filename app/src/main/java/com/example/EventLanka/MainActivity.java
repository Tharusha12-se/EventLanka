package com.example.EventLanka;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.EventLanka.Admin.AdminMainActivity;
import com.example.EventLanka.Admin.ErrorAlert;
import com.example.EventLanka.Admin.Loading;
import com.example.EventLanka.Admin.SuccessAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText editTextEmail, editTextPassword;
    private TextView textRegisterNow;
    private ImageView fingerprintIcon;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        fingerprintIcon = findViewById(R.id.fingerprintIcon);
        checkBiometricSupport();

        fingerprintIcon.setOnClickListener(v -> showBiometricPrompt());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textRegisterNow = findViewById(R.id.textView5);
        TextView forgotPassword = findViewById(R.id.forgotPassword);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Loading loading = new Loading();
                loading.showLoading(MainActivity.this, "Loading...");

                String emailText = editTextEmail.getText().toString().trim();
                String passwordText = editTextPassword.getText().toString().trim();
                Log.i("Email",emailText);
                Log.i("Password",passwordText);

                // Validate fields
                if (emailText.isEmpty()) {
                    loading.Stop();
                    new ErrorAlert().showCustomAlert(MainActivity.this, "Oops..", "Please enter email", "error", null);
                    return;
                }
                if (passwordText.isEmpty()) {
                    loading.Stop();
                    new ErrorAlert().showCustomAlert(MainActivity.this, "Oops..", "Please enter password", "error", null);
                    return;
                }

                // Check Firestore for matching user
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("user")
                        .whereEqualTo("email", emailText)
                        .whereEqualTo("password", passwordText)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {


                                        // Found matching user
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            Log.d(TAG, "User found: " + document.getId() + " => " + document.getData());
                                        }
                                        loading.Stop();
                                       // new SuccessAlert().showCustomAlert(MainActivity.this, "Success", "Login successful!", "error", null);

                                        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("Logged_userId", task.getResult().getDocuments().get(0).getId());
                                        editor.apply();

                                        userID = task.getResult().getDocuments().get(0).getId();


                                        if ("admin".equals(querySnapshot.getDocuments().get(0).getString("type"))) {
                                            Intent intent = new Intent(MainActivity.this, AdminMainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    } else {
                                        // No user document matched email & password
                                        loading.Stop();
                                        new ErrorAlert().showCustomAlert(MainActivity.this, "Oops..", "Invalid email or password", "error", null);
                                    }
                                } else {
                                    // Query failed
                                    Log.e(TAG, "Login query failed: ", task.getException());
                                    loading.Stop();
                                    new ErrorAlert().showCustomAlert(MainActivity.this, "Oops..", "Login failed: " + task.getException().getMessage(), "error", null);
                                }
                            }
                        });
                loading.Stop();

            }
        });

        // go to login
        textRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);

            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


    }

    private void checkBiometricSupport() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Fingerprint authentication not supported", Toast.LENGTH_SHORT).show();
            fingerprintIcon.setEnabled(false);
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
                sharedPreferences.getString("Logged_userId", userID);
                goToHomeActivity();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Authentication Error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login with Fingerprint")
                .setSubtitle("Use your fingerprint to login")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}