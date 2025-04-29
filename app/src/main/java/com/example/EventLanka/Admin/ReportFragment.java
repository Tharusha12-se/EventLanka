package com.example.EventLanka.Admin;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.EventLanka.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportFragment extends Fragment {

    private BarChart eventBarChart;
    private PieChart userAdminPieChart;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        userAdminPieChart = view.findViewById(R.id.userAdminPieChart);
        db = FirebaseFirestore.getInstance();

        loadUserAdminStatus();

        return view;
    }



    // 🎯 Load User & Admin Status (Pie Chart)
    private void loadUserAdminStatus() {
        db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    int activeUsers = 0, inactiveUsers = 0;
                    int activeAdmins = 0, inactiveAdmins = 0;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String type = document.getString("type"); // "admin" or "user"
                        String status = document.getString("status"); // "active" or "inactive"

                        if (type != null && status != null) {
                            boolean isActive = status.equalsIgnoreCase("active");

                            if (type.equalsIgnoreCase("user")) {
                                if (isActive) activeUsers++;
                                else inactiveUsers++;
                            } else if (type.equalsIgnoreCase("admin")) {
                                if (isActive) activeAdmins++;
                                else inactiveAdmins++;
                            }
                        }
                    }

                    // Populate Pie Chart
                    List<PieEntry> entries = new ArrayList<>();
                    entries.add(new PieEntry(activeUsers, "Active Users"));
                    entries.add(new PieEntry(inactiveUsers, "Inactive Users"));
                    entries.add(new PieEntry(activeAdmins, "Active Admins"));
                    entries.add(new PieEntry(inactiveAdmins, "Inactive Admins"));

                    PieDataSet dataSet = new PieDataSet(entries, "User & Admin Status");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    PieData pieData = new PieData(dataSet);

                    userAdminPieChart.setData(pieData);
                    userAdminPieChart.getDescription().setText("Active vs Inactive Users & Admins");
                    userAdminPieChart.invalidate();
                }
            }
        });
    }


    // 🕒 Get the date for one week ago
    private String getLastWeekDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
