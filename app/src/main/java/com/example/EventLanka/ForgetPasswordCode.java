package com.example.EventLanka;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class ForgetPasswordCode extends AppCompatActivity {

    private Dialog dialog;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password_code);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Loading loading = new Loading();

//        Button rest_button = findViewById(R.id.resetPassword);
//        rest_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                loading.showLoading(ForgetPasswordCode.this, "Loading...");
//
//                EditText new_password = findViewById(R.id.newPassword);
//                EditText r_new_password = findViewById(R.id.newPassword2);
//                EditText v_code = findViewById(R.id.code);
//
//                String new_pass = new_password.getText().toString();
//                String r_new_pass = r_new_password.getText().toString();
//                String v_code_text = v_code.getText().toString();
//
//                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//
//                if (new_pass.trim().isEmpty()) {
//                    loading.Stop();
//                    new ErrorAlert().showCustomAlert(ForgetPasswordCode.this, "Opps..", "Please Enter New Password", "error", null);
//                } else if (r_new_pass.trim().isEmpty()) {
//                    loading.Stop();
//                    new ErrorAlert().showCustomAlert(ForgetPasswordCode.this, "Opps..", "Please Re-Enter New Password", "error", null);
//                } else if (!new_pass.equals(r_new_pass)) {
//                    loading.Stop();
//                    new ErrorAlert().showCustomAlert(ForgetPasswordCode.this, "Opps..", "Password Doesn't Match", "error", null);
//                }else if(v_code_text.isEmpty()){
//                    loading.Stop();
//                    new ErrorAlert().showCustomAlert(ForgetPasswordCode.this, "Opps..", "Please Enter Verification Code", "error", null);
//                }else{
//
//                    firestore.collection("user").document(userID).get().addOnSuccessListener(documentSnapshot -> {
//                        if(documentSnapshot.exists()){
//                            String vCode = String.valueOf(documentSnapshot.getString("vCode"));
//
//                            if(v_code_text.equals(vCode)){
//                                Intent intent = new Intent(ForgetPasswordCode.this, MainActivity.class);
//                                firestore.collection("user").document(userID).update("password", new_pass);
//                                loading.Stop();
//                                new SuccessAlert().showCustomAlert(ForgetPasswordCode.this, "Success", "Password Changed Successfully", "success", intent);
//                            }else{
//                                loading.Stop();
//                                new ErrorAlert().showCustomAlert(ForgetPasswordCode.this, "Opps..", "Verification Code Doesn't Match", "error", null);
//                            }
//                        } else{
//                            loading.Stop();
//                            new ErrorAlert().showCustomAlert(ForgetPasswordCode.this, "Opps..", "Something Went Wrong", "error", null);
//                        }
//                    });
//
//                }
//
//            }
//    });

}


    public void Show(Context context,String userId) {

        userID = userId;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_forget_password_code);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Loading loading = new Loading();

            Button loginButton = dialog.findViewById(R.id.resetPassword);
            EditText email = dialog.findViewById(R.id.newPassword2);
            EditText password = dialog.findViewById(R.id.newPassword);
            EditText code = dialog.findViewById(R.id.code);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    loading.showLoading(context, "Loading...");

                    String new_pass = password.getText().toString();
                    String r_new_pass = email.getText().toString();
                    String v_code_text = code.getText().toString();

                    if (new_pass.trim().isEmpty()) {
                        loading.Stop();
                        new ErrorAlert().showCustomAlert(context, "Opps..", "Please Enter New Password", "error", null);
                    } else if (r_new_pass.trim().isEmpty()) {
                        loading.Stop();
                        new ErrorAlert().showCustomAlert(context, "Opps..", "Please Re-Enter New Password", "error", null);
                    } else if (!new_pass.equals(r_new_pass)) {
                        loading.Stop();
                        new ErrorAlert().showCustomAlert(context, "Opps..", "Password Doesn't Match", "error", null);
                    }else if(v_code_text.isEmpty()){
                        loading.Stop();
                        new ErrorAlert().showCustomAlert(context, "Opps..", "Please Enter Verification Code", "error", null);
                    }else{

                        firestore.collection("user").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                            if(documentSnapshot.exists()){
                                String vCode = String.valueOf(documentSnapshot.getString("vCode"));

                                if(v_code_text.equals(vCode)){
                                    Intent intent = new Intent(context, MainActivity.class);
                                    firestore.collection("user").document(userId).update("password", new_pass);
                                    loading.Stop();
                                    new SuccessAlert().showCustomAlert(context, "Success", "Password Changed Successfully", "success", intent);
                                }else{
                                    loading.Stop();
                                    new ErrorAlert().showCustomAlert(context, "Opps..", "Verification Code Doesn't Match", "error", null);
                                }
                            } else{
                                loading.Stop();
                                new ErrorAlert().showCustomAlert(context, "Opps..", "Something Went Wrong", "error", null);
                            }
                        });

                    }

                }
            });

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(params);
        }

        dialog.show();

    }
}