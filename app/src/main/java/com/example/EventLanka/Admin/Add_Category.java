package com.example.EventLanka.Admin;

import android.app.Dialog;
import android.content.Context;
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

import com.example.EventLanka.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class Add_Category extends AppCompatActivity {

    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Show(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_add_category);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Loading loading = new Loading();

            Button addButton = dialog.findViewById(R.id.add_button);
            Button cancleButton = dialog.findViewById(R.id.cancleButton);
            EditText category = dialog.findViewById(R.id.alert_message);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loading.showLoading(context, "Loading...");

                    String category_str = category.getText().toString().trim();

                    if (category_str.isEmpty()) {
                        loading.Stop();
                        new ErrorAlert().showCustomAlert(context, "Oops..", "Please Enter Category Name", "error", null);
                    } else {
                        HashMap<String, Object> cate = new HashMap<>();
                        cate.put("name", category_str);

                        firestore.collection("category")
                                .whereEqualTo("name", category_str)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (queryDocumentSnapshots.isEmpty()) {
                                            firestore.collection("category")
                                                    .add(cate)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            loading.Stop();
                                                            new SuccessAlert().showCustomAlert(context, "Success", "Category Added Successfully", "success", null);
                                                            dialog.dismiss(); // Close dialog on success
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            loading.Stop();
                                                            new ErrorAlert().showCustomAlert(context, "Oops..", "Something Went Wrong", "error", null);
                                                        }
                                                    });
                                        } else {
                                            loading.Stop();
                                            new ErrorAlert().showCustomAlert(context, "Oops..", "Category Already Exists", "error", null);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loading.Stop();
                                        new ErrorAlert().showCustomAlert(context, "Oops..", "Failed to fetch categories", "error", null);
                                    }
                                });
                    }
                }
            });

            cancleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
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