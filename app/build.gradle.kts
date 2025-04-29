plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.EventLanka"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.EventLanka"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.preference)
    implementation(libs.firebase.storage)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.support.annotations)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-firestore")
    //    image Slider
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation("com.google.android.material:material:1.9.0")

    //rouded image
    implementation("com.google.android.material:material:1.9.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Google Maps SDK
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Google Places API for Place Picker
    implementation("com.google.android.libraries.places:places:3.3.0")

    // LOTTIE
    implementation("com.airbnb.android:lottie:6.0.0")

    implementation("com.github.bumptech.glide:glide:4.x.x")
    annotationProcessor("com.github.bumptech.glide:compiler:4.x.x")

    implementation("com.github.PayHereDevs:payhere-android-sdk:v3.0.17")
    implementation("androidx.appcompat:appcompat:1.6.0") // ignore if you have already added
    implementation("com.google.code.gson:gson:2.8.0") // ignore if you have already added

    //map
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation ("com.google.android.gms:play-services-location:21.3.0")

    implementation ("com.google.maps.android:android-maps-utils:2.4.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // for chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.google.firebase:firebase-messaging:23.0.0")

    //OkHTTP3
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Fingerpring
//    implementation ("androidx.biometric:biometric:1.2.0")
//    implementation ("com.google.firebase:firebase-auth:22.1.1")
//    implementation ("com.google.firebase:firebase-firestore:24.10.2")
    implementation ("androidx.biometric:biometric:1.2.0-alpha05")

}