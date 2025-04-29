package com.example.EventLanka;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.example.EventLanka.Admin.Loading;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.location.*;
import com.google.firebase.firestore.*;
import com.google.maps.android.PolyUtil;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;
import retrofit2.http.Query;

import java.util.*;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LatLng liveLocationLatLng; // To store live location
    private FirebaseFirestore firestore;

    private Loading loading;
    private String branch_name_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        loading = new Loading();

        // Initialize Google Map Fragment
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_layout, supportMapFragment);
        fragmentTransaction.commit();

        // Set up map callback
        supportMapFragment.getMapAsync(this);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Define the location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (android.location.Location location : locationResult.getLocations()) {
                    // Get live location
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    liveLocationLatLng = new LatLng(latitude, longitude);

                    // Log or display live location
                    Log.d("LiveLocation", "Lat: " + latitude + ", Lng: " + longitude);
                }
            }
        };

        // Check and request location permissions
        if (checkLocationPermissions()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (googleMap == null) {
            Log.e("LocationActivity", "GoogleMap is null");
            return;
        }

        // Enable map controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        // Set marker click listener
        googleMap.setOnMarkerClickListener(this);

        // Set initial camera position to Sri Lanka
        LatLng sriLankaLatLng = new LatLng(7.8731, 80.7718); // Coordinates for Sri Lanka
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLankaLatLng, 7)); // Zoom level 7

        // Enable location layer
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

            firestore.collection("event").get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    if (loading != null) {
                        loading.Stop();
                    }
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        // Get the GeoPoint from the document
                        GeoPoint geoPoint = document.getGeoPoint("gioLocation");

                        if (geoPoint != null) {
                            // Get latitude and longitude
                            double latitude = geoPoint.getLatitude();
                            double longitude = geoPoint.getLongitude();

                            // Get branch name
                            branch_name_str = document.getString("location");

                            // Create a LatLng object with latitude and longitude
                            LatLng position = new LatLng(latitude, longitude);

                            // Add the marker on the map
                            googleMap.addMarker(
                                    new MarkerOptions()
                                            .position(position)  // Use the LatLng object here
                                            .title(branch_name_str)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                            );
                        }
                    }
                } else {
                    if (loading != null) {
                        loading.Stop();
                    }
                }
            }).addOnFailureListener(e -> {
                Log.e("FirestoreError", "Error fetching events: " + e.getMessage());
                if (loading != null) {
                    loading.Stop();
                }
            });
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        // Get the title of the clicked marker (branch name)
        branch_name_str = marker.getTitle();

        // Fetch the end location and show directions
        fetchEndLocationAndShowDirections();

        // Return false to allow default behavior (e.g., centering the marker)
        return false;
    }

    private boolean checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 100);
            return false;
        }
        return true;
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // 10 seconds interval
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000) // 5 seconds minimum interval
                .setMaxUpdateDelayMillis(1000)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void fetchEndLocationAndShowDirections() {
        String branchName = branch_name_str;

        if (branchName == null) {
            Log.e("LocationActivity", "Branch name is null");
            return;
        }

        firestore.collection("event")
                .whereEqualTo("location", branchName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the first document (assuming there's only one matching document)
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        // Log the document data for debugging
                        Log.d("FirestoreData", document.getId() + " => " + document.getData());

                        // Check if the "location" field exists and is a GeoPoint
                        if (document.contains("location") && document.get("location") instanceof GeoPoint) {
                            GeoPoint geoPoint = document.getGeoPoint("location");
                            if (geoPoint != null) {
                                LatLng endLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                                // Show directions on the map
                                if (liveLocationLatLng != null && googleMap != null) {
                                    showDirectionsOnMap(liveLocationLatLng, endLatLng);
                                }
                            }
                        } else if (document.contains("location") && document.get("location") instanceof String) {
                            // Handle the case where "location" is a string (e.g., "latitude,longitude")
                            String locationString = document.getString("location");
                            if (locationString != null) {
                                String[] latLng = locationString.split(",");
                                if (latLng.length == 2) {
                                    try {
                                        double latitude = Double.parseDouble(latLng[0]);
                                        double longitude = Double.parseDouble(latLng[1]);
                                        LatLng endLatLng = new LatLng(latitude, longitude);

                                        // Show directions on the map
                                        if (liveLocationLatLng != null && googleMap != null) {
                                            showDirectionsOnMap(liveLocationLatLng, endLatLng);
                                        }
                                    } catch (NumberFormatException e) {
                                        Log.e("LocationActivity", "Invalid location format: " + locationString);
                                    }
                                } else {
                                    Log.e("LocationActivity", "Invalid location format: " + locationString);
                                }
                            }
                        } else {
                            // Handle the case where "location" is not a GeoPoint or string
                            Log.e("LocationActivity", "The 'location' field is not a GeoPoint or is missing.");
                        }
                    } else {
                        Log.i("TAG", "Branch not found");
                    }
                })
                .addOnFailureListener(e -> Log.e("TAG", "Error fetching branch: " + e.getMessage()));
    }

    private void showDirectionsOnMap(LatLng start, LatLng end) {
        String origin = start.latitude + "," + start.longitude;
        String destination = end.latitude + "," + end.longitude;

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionsApi api = retrofit.create(DirectionsApi.class);

        // Fetch directions
        api.getDirections(origin, destination, "AIzaSyD_7wEHvZgNOQaSjATGQ3Uv62JZPyPCAhc").enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the encoded polyline
                    String encodedPolyline = response.body().routes.get(0).overview_polyline.points;

                    // Decode the polyline
                    List<LatLng> points = PolyUtil.decode(encodedPolyline);

                    // Draw the polyline on the map
                    googleMap.addPolyline(new PolylineOptions()
                            .addAll(points)
                            .width(10)
                            .color(ContextCompat.getColor(LocationActivity.this, R.color.black))
                            .startCap(new RoundCap())
                            .endCap(new RoundCap())
                            .jointType(JointType.ROUND)
                    );
                } else {
                    Log.e("TAG", "Failed to fetch directions");
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e("TAG", "Error: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    // Interface for Directions API
    private interface DirectionsApi {
        @GET("maps/api/directions/json")
        Call<DirectionsResponse> getDirections(
                @Query("origin") String origin,
                @Query("destination") String destination,
                @Query("key") String apiKey
        );
    }

    // Data model for Directions API response
    private static class DirectionsResponse {
        List<Route> routes;

        public List<Route> getRoutes() {
            return routes;
        }
    }

    private static class Route {
        OverviewPolyline overview_polyline;

        public OverviewPolyline getOverviewPolyline() {
            return overview_polyline;
        }
    }

    private static class OverviewPolyline {
        String points;

        public String getPoints() {
            return points;
        }
    }
}