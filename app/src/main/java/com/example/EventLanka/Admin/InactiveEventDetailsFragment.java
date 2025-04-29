package com.example.EventLanka.Admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.EventLanka.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;

public class InactiveEventDetailsFragment extends Fragment {

    private String eventID; // Stores the eventID

    private TextView eventDate, eventTime, eventLocation; // Updated to TextView instead of EditText

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve eventID from arguments
        if (getArguments() != null) {
            eventID = getArguments().getString("eventID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inactive_event_details, container, false);

        Loading loading = new Loading();

        TextView eventName = view.findViewById(R.id.title);
        TextView eventLocation = view.findViewById(R.id.location);
        eventDate = view.findViewById(R.id.date);  // Initialize eventDate
        eventTime = view.findViewById(R.id.time);  // Initialize eventTime
        TextView eventDescription = view.findViewById(R.id.discription);
        ImageView eventImage = view.findViewById(R.id.card_image);
        TextView eventType = view.findViewById(R.id.type);
        AppCompatButton updateButton = view.findViewById(R.id.updateButton);
        AppCompatButton deleteButton = view.findViewById(R.id.eventInactiveBtn);

        // ----- Date Picker -----
        eventDate.setOnClickListener(v -> showDatePickerDialog(eventDate));

        // ----- Time Picker -----
        eventTime.setOnClickListener(v -> showTimePickerDialog(eventTime));

        Log.i("EventID", eventID);

        if(eventID != null){
            loading.showLoading(requireContext(), "Loading...");

            // Fetch event details from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("event")
                    .document(eventID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Extract event details
                            String title = documentSnapshot.getString("title");
                            String location = documentSnapshot.getString("location");
                            String date = documentSnapshot.getString("date");
                            String time = documentSnapshot.getString("time");
                            String description = documentSnapshot.getString("description");
                            String image = documentSnapshot.getString("imageUrl");
                            String type = documentSnapshot.getString("type");

                            // Set event details to UI components
                            eventName.setText(title);
                            eventLocation.setText(location);
                            eventDate.setText(date);
                            eventTime.setText(time);
                            eventDescription.setText(description);
                            eventType.setText(type);

                            Glide.with(requireContext())
                                    .load(image)
                                    .apply(new RequestOptions().transform(new RoundedCorners(50)))
                                    .error(R.drawable.image_slider_4) // Default image if loading fails
                                    .into(eventImage);
                        }
                        loading.Stop();
                    });
        }



        deleteButton.setOnClickListener(v -> {
            // Delete event from Firestore
            loading.showLoading(requireContext(), "Active...");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("event")
                    .document(eventID)
                    .update("status", "active")
                    .addOnSuccessListener(aVoid -> {
                        loading.Stop();
                        new SuccessAlert().showCustomAlert(
                                getContext(),
                                "Success..",
                                "Event Activated Successfully",
                                "success",
                                null);
                    });
            loading.Stop();
        });

        return view;
    }

    // ---------------------- Date Picker ----------------------
    private void showDatePickerDialog(TextView dateField) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getActivity(), (view, year, month, day) -> {
            String selectedDate = (month + 1) + "/" + day + "/" + year;
            dateField.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // ---------------------- Time Picker ----------------------
    private void showTimePickerDialog(TextView timeField) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(getActivity(), (view, hour, minute) -> {
            String formattedTime = String.format(
                    "%02d:%02d %s",
                    (hour == 0 || hour == 12) ? 12 : hour % 12,
                    minute,
                    hour < 12 ? "AM" : "PM");
            timeField.setText(formattedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }
}
