//package com.example.EventLanka;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//import java.util.HashMap;
//import java.util.Map;
//
//public class AddFingerprintFragment extends Fragment {
//
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_add_fingerprint, container, false);
//
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//
//        Button btnEnableFingerprint = view.findViewById(R.id.btnEnableFingerprint);
//        btnEnableFingerprint.setOnClickListener(v -> saveFingerprintToFirebase());
//
//        return view;
//    }
//
//    private void saveFingerprintToFirebase() {
//        String userId = mAuth.getCurrentUser().getUid();
//        if (userId != null) {
//            Map<String, Object> userFingerprint = new HashMap<>();
//            userFingerprint.put("fingerprintEnabled", true);
//
//            db.collection("users").document(userId)
//                    .set(userFingerprint)
//                    .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Fingerprint enabled!", Toast.LENGTH_SHORT).show())
//                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//        }
//    }
//}
