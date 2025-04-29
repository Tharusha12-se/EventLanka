package com.example.EventLanka;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize the SupportMapFragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapView);

        // Set up the map asynchronously
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    Log.i("MapFragment", "Google map ready!");

                    // Define a location
                    LatLng latLng = new LatLng(7.480448237522471, 80.35846588363013); // Kurunegala

                    // Move the camera to the location
                    googleMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition.Builder()
                                            .target(latLng)
                                            .zoom(12) // Adjust zoom level as needed
                                            .build()
                            )
                    );

                    // Add a marker
                    googleMap.addMarker(
                            new MarkerOptions()
                                    .position(latLng)
                                    .title("Slotcare - Kurunegala")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.l_icon)) // Replace with your custom icon
                    );
                }
            });
        } else {
            Log.e("MapFragment", "SupportMapFragment is null!");
        }

        return view;
    }
}