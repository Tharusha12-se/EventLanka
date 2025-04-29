package com.example.EventLanka.Admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.EventLanka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PaymentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view1 = inflater.inflate(R.layout.fragment_payment, container, false);

        Loading loading = new Loading();
        loading.showLoading(requireContext(), "Loading...");

        LinearLayout paymentContainer = view1.findViewById(R.id.paymentContainer);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("buyTicket")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (int i = 0; i < task.getResult().size(); i++) {
                                View view = inflater.inflate(R.layout.payment_card, container, false);

                                String userID = task.getResult().getDocuments().get(i).getString("userID");
                                String ticketName = task.getResult().getDocuments().get(i).getString("ticketName");
                                String ticketBookingDate = task.getResult().getDocuments().get(i).getString("ticketBookingDate");
                                String ticketPrice = task.getResult().getDocuments().get(i).getString("ticketPrice");

                                EditText name = view.findViewById(R.id.name);
                                EditText eventName = view.findViewById(R.id.eventName);
                                TextView date = view.findViewById(R.id.date);
                                EditText ticket = view.findViewById(R.id.amount);

                                eventName.setText(ticketName);
                                date.setText(ticketBookingDate);
                                ticket.setText(ticketPrice);

                                db.collection("user")
                                                .document(userID)
                                                .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    name.setText(task.getResult().getString("fname")+ " " + task.getResult().getString("lname"));
                                                                }
                                                            }
                                                        });




                                paymentContainer.addView(view);
                            }
                        }
                        loading.Stop();
                    }
                });


        return view1;

    }
}