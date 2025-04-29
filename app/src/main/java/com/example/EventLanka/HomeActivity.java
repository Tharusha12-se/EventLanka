package com.example.EventLanka;

// Remove this import
// import static android.os.Build.VERSION_CODES.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;


public class HomeActivity extends AppCompatActivity {

    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        if(savedInstanceState==null){
            loadFragment( new HomeFragment());
        }

        ImageView profile = findViewById(R.id.imageView2);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProfileFragment());
            }
        });

        ImageView profile1 = findViewById(R.id.imageView6);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProfileFragment());
            }
        });

        FloatingActionButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment());
            }
        });



        ImageView viewTicket = findViewById(R.id.imageView15);
        viewTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ViewTicketFragment());
            }
        });

        ImageView cart = findViewById(R.id.imageView6);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new CartFragment());
            }
        });

        ImageView mapView = findViewById(R.id.mapView);
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  loadFragment(new MapFragment());

                Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
               startActivity(intent);
            }
        });

        ImageView callButton = findViewById(R.id.call);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "tel:+94781408623";

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(phoneNumber));

                startActivity(intent);
            }
        });





    }



    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainScrillView, fragment).commit();
            return true;
        }
        return false;
    }




}
