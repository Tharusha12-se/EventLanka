package com.example.EventLanka.Admin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.EventLanka.R;

public class SuccessAlert extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_success_alert);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void showCustomConfirmAlert(Context fromContext, String title, String message, String icon, SuccessAlert.ConfirmDialogListener listener) {
        Dialog dialog = new Dialog(fromContext);
        dialog.setContentView(R.layout.activity_success_alert);

        TextView title_text = dialog.findViewById(R.id.success_title);
        TextView message_text = dialog.findViewById(R.id.succes_msg);
        Button button1 = dialog.findViewById(R.id.success_button);

        title_text.setText(title);
        message_text.setText(message);



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onConfirm(true); // Notify that the user confirmed
            }
        });


        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();
    }

    public interface ConfirmDialogListener {
        void onConfirm(boolean confirmed);
    }

    public void showCustomAlert(Context context, String title, String message, String icon, Intent intent) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_success_alert);

        TextView title_text = dialog.findViewById(R.id.success_title);
        TextView message_text = dialog.findViewById(R.id.succes_msg);
        Button button = dialog.findViewById(R.id.success_button);

        title_text.setText(title);
        message_text.setText(message);


        if (intent != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
//                    Intent intent = new Intent(fromContext, toContext);
                    context.startActivity(intent);
                }
            });
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();

    }


}