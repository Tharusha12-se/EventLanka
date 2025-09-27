// SplashActivity.java
package com.example.EventLanka;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.EventLanka.IntroActivity;
import com.example.EventLanka.MainActivity;
import com.example.EventLanka.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Find views
        ImageView logo = findViewById(R.id.logo);
        TextView textView = findViewById(R.id.textView);

        // Load animations
        Animation splingAnimation = AnimationUtils.loadAnimation(this, R.anim.spling_animation);
        Animation flingAnimation = AnimationUtils.loadAnimation(this, R.anim.fling_animation);

        // Start animationss
        logo.startAnimation(splingAnimation);
        textView.startAnimation(flingAnimation);

        // Navigate to the main activity after the animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
        }, 2000);
        // Adjust the delay as needed
        System.out.println("hello");
    }
}