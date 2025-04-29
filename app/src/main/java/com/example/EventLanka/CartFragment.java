package com.example.EventLanka;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.EventLanka.Admin.ErrorAlert;
import com.example.EventLanka.Admin.SuccessAlert;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private Button clearButton; // Button to clear cart

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.cartContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize empty cart list
        cartItemList = new ArrayList<>();

        // Initialize adapter with empty list FIRST
        cartAdapter = new CartAdapter(cartItemList);
        recyclerView.setAdapter(cartAdapter);

        // Load data from SQLite
        loadCartData();

        // Initialize and set click listener for clear button
        clearButton = view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(v -> clearCart());

        return view;
    }

    private void loadCartData() {
        Cursor cursor = databaseHelper.getCartItems();
        if (cursor.getCount() == 0) {
            new ErrorAlert().showCustomAlert(
                    getContext(),            // Or requireContext()
                    "Oops..",
                    "No items in cart!",
                    "error",
                    null
            );
            return;
        }

        // Clear old data to prevent duplicates
       // cartItemList.clear();

        // Loop through all items and add to list
        while (cursor.moveToNext()) {
            String name = cursor.getString(1); // event_name
            String date = cursor.getString(2); // event_date
            String time = cursor.getString(3); // event_time

            cartItemList.add(new CartItem(name, date, time));
        }

        cursor.close();

        // Notify adapter about data changes AFTER data is added
        cartAdapter.notifyDataSetChanged();
    }

    private void clearCart() {
        databaseHelper.clearCartItems(); // Call method to delete all items
        cartItemList.clear(); // Clear list in adapter
        cartAdapter.notifyDataSetChanged(); // Refresh RecyclerView
        new SuccessAlert().showCustomAlert(
                getContext(),            // Or requireContext()
                "Success..",
                "Cart clear Successful!",
                "Success",
                null
        );
    }
}
