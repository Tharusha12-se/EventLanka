//package com.example.EventLanka.Admin;
//
//import android.app.Activity;
//import android.app.DatePickerDialog;
//import android.app.TimePickerDialog;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.core.app.NotificationCompat;
//import androidx.fragment.app.Fragment;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
//import com.bumptech.glide.request.RequestOptions;
//import com.example.EventLanka.R;
//import com.google.android.gms.common.api.Status;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.android.libraries.places.widget.Autocomplete;
//import com.google.android.libraries.places.widget.AutocompleteActivity;
//import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.GeoPoint;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.UUID;
//
//public class EventDetailsFragment extends Fragment {
//
//    private ImageView cardImage;
//    private EditText titleEditText, locationEditText, descriptionEditText, dateEditText, timeEditText;
//    private Spinner typeSpinner;
//    private Button saveButton;
//    private Uri selectedImageUri;
//    private ActivityResultLauncher<Intent> imagePickerLauncher;
//    private ActivityResultLauncher<Intent> locationPickerLauncher;
//    private double selectedLatitude = 0.0;
//    private double selectedLongitude = 0.0;
//    private FirebaseFirestore firestore;
//    private StorageReference storageReference;
//    private String selectedCategory = "";
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
//
//        firestore = FirebaseFirestore.getInstance();
//        storageReference = FirebaseStorage.getInstance().getReference("event_images");
//        cardImage = view.findViewById(R.id.card_image);
//        titleEditText = view.findViewById(R.id.title);
//        typeSpinner = view.findViewById(R.id.spinner2);
//        locationEditText = view.findViewById(R.id.location);
//        descriptionEditText = view.findViewById(R.id.discription);
//        dateEditText = view.findViewById(R.id.date);
//        timeEditText = view.findViewById(R.id.time);
//        saveButton = view.findViewById(R.id.add_button);
//
//        if (!Places.isInitialized()) {
//            Places.initialize(requireContext(), "YOUR_GOOGLE_PLACES_API_KEY");
//        }
//        PlacesClient placesClient = Places.createClient(requireContext());
//
//        Glide.with(this).load(R.drawable.image_add).apply(RequestOptions.bitmapTransform(new RoundedCorners(50))).into(cardImage);
//
//        loadSpinner();
//
//        // Image Picker Registration
//        imagePickerLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        selectedImageUri = result.getData().getData();
//                        Glide.with(requireContext())
//                                .load(selectedImageUri)
//                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
//                                .into(cardImage);
//                    }
//                }
//        );
//
//        // Place Picker Registration
//        locationPickerLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        Place place = Autocomplete.getPlaceFromIntent(result.getData());
//                        locationEditText.setText(place.getName());
//
//                        if (place.getLatLng() != null) {
//                            selectedLatitude = place.getLatLng().latitude;
//                            selectedLongitude = place.getLatLng().longitude;
//                        }
//                    } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
//                        Status status = Autocomplete.getStatusFromIntent(result.getData());
//                        Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//
//        cardImage.setOnClickListener(v -> openImagePicker());
//        locationEditText.setOnClickListener(v -> openPlacePicker());
//        dateEditText.setOnClickListener(v -> showDatePicker());
//        timeEditText.setOnClickListener(v -> showTimePicker());
//        saveButton.setOnClickListener(v -> saveEvent());
//
//        return view;
//    }
//
//    // ---------------------- Date Picker ----------------------
//    private void showDatePicker() {
//        Calendar calendar = Calendar.getInstance();
//        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
//            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
//            dateEditText.setText(selectedDate);
//        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
//    }
//
//    // ---------------------- Time Picker ----------------------
//    private void showTimePicker() {
//        Calendar calendar = Calendar.getInstance();
//        new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
//            String formattedTime = String.format("%02d:%02d %s",
//                    (hourOfDay == 0 || hourOfDay == 12) ? 12 : hourOfDay % 12,
//                    minute,
//                    (hourOfDay < 12) ? "AM" : "PM");
//            timeEditText.setText(formattedTime);
//        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
//    }
//
//    // ---------------------- Place Picker ----------------------
//    private void openPlacePicker() {
//        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
//        Intent intent = new Autocomplete.IntentBuilder(
//                AutocompleteActivityMode.OVERLAY, fields)
//                .setCountry("LK")  // Restrict to Sri Lanka
//                .build(requireContext());
//        locationPickerLauncher.launch(intent);
//    }
//
//    // ---------------------- Load Spinner Data from Firestore ----------------------
//    private void loadSpinner() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        List<String> categories = new ArrayList<>();
//
//        db.collection("category").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    categories.add(document.getString("name"));
//                }
//
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                typeSpinner.setAdapter(adapter);
//
//                typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        selectedCategory = categories.get(position);
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                        selectedCategory = "";
//                    }
//                });
//            } else {
//                Log.e("Firestore", "Error fetching categories: ", task.getException());
//            }
//        });
//    }
//
//    private void saveEvent() {
//        String titleText = titleEditText.getText().toString().trim();
//        String locText = locationEditText.getText().toString().trim();
//        String descText = descriptionEditText.getText().toString().trim();
//        String dateText = dateEditText.getText().toString().trim();
//        String timeText = timeEditText.getText().toString().trim();
//
//        if (titleText.isEmpty() || selectedCategory.isEmpty() || locText.isEmpty() || descText.isEmpty() || dateText.isEmpty() || timeText.isEmpty()) {
//            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        HashMap<String, Object> eventData = new HashMap<>();
//        eventData.put("title", titleText);
//        eventData.put("type", selectedCategory);
//        eventData.put("location", locText);
//        eventData.put("description", descText);
//        eventData.put("date", dateText);
//        eventData.put("time", timeText);
//        eventData.put("status", "active");
//
//        if (selectedImageUri != null) {
//            uploadImageToFirebase(eventData);
//        } else {
//            saveEventToFirestore(eventData);
//        }
//    }
//
//
//    private void sendNotification(String eventTitle) {
//        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("event_notifications", "Event Notifications", NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "event_notifications")
//                .setSmallIcon(R.drawable.notification)
//                .setContentTitle("New Event Added")
//                .setContentText("Check out the new event: " + eventTitle)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        notificationManager.notify(1, builder.build());
//    }
//
//    // ---------------------- Upload Image and Save Data ----------------------
//    private void uploadImageToFirebase(HashMap<String, Object> eventData) {
//        String fileName = UUID.randomUUID().toString() + ".jpg";
//        StorageReference imageRef = storageReference.child(fileName);
//
//        imageRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot ->
//                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                    eventData.put("imageUrl", uri.toString());
//                    saveEventToFirestore(eventData);
//                })
//        ).addOnFailureListener(e ->
//                Toast.makeText(getActivity(), "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    private void saveEventToFirestore(HashMap<String, Object> eventData) {
//        firestore.collection("event").add(eventData)
//                .addOnSuccessListener(documentReference -> {
//
//                    new SuccessAlert().showCustomAlert(
//                            getContext(),            // Or requireContext()
//                            "Success..",
//                            "Event added successfully!",
//                            "success",
//                            null
//                    );
//
//                    clearFields();
//                })
//                .addOnFailureListener(e ->
//                        new ErrorAlert().showCustomAlert(
//                                getContext(),            // Or requireContext()
//                                "Error..",
//                                "Event addition failed!",
//                                "error",
//                                null
//                        ) );
//    }
//
//
//    private void clearFields() {
//        titleEditText.setText("");
//        locationEditText.setText("");
//        descriptionEditText.setText("");
//        dateEditText.setText("");
//        timeEditText.setText("");
//
//        // Reset the spinner to the first item
//        if (typeSpinner.getAdapter() != null && typeSpinner.getAdapter().getCount() > 0) {
//            typeSpinner.setSelection(0);
//        }
//
//        // Reset latitude and longitude
//        selectedLatitude = 0.0;
//        selectedLongitude = 0.0;
//
//        // Reset the selected image
//        selectedImageUri = null;
//        Glide.with(this)
//                .load(R.drawable.image_add) // Load default image
//                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
//                .into(cardImage);
//
//        Toast.makeText(getContext(), "All fields cleared!", Toast.LENGTH_SHORT).show();
//    }
//
//
//
//    // ---------------------- Image Picker ----------------------
//    private void openImagePicker() {
//        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        imagePickerLauncher.launch(pickIntent);
//    }
//}
