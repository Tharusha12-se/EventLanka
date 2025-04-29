package com.example.EventLanka.Admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.EventLanka.LocationActivity;
import com.example.EventLanka.R;

public class AdminDashbordFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Loading loading = new Loading();
        loading.showLoading(requireContext(), "Loading...");

        View view = inflater.inflate(R.layout.fragment_admin_dashbord, container, false);

        CardView profileCard = view.findViewById(R.id.profile);
        CardView typeCard = view.findViewById(R.id.type);
        CardView eventCard = view.findViewById(R.id.eventManagemnt);
        CardView userCard = view.findViewById(R.id.userManagement);
        CardView adminCard = view.findViewById(R.id.adminManagement);
        CardView ticketCard = view.findViewById(R.id.ticket_management);
        CardView loactionCard = view.findViewById(R.id.locationn);
        CardView notificationCard = view.findViewById(R.id.notification);
        CardView reportCard = view.findViewById(R.id.report);
        CardView paymentCard = view.findViewById(R.id.payment);

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addEventFragment = new AdminProfileFragment();

                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                // Hide this fragment
                transaction.hide(AdminDashbordFragment.this);

                // Add the detail fragment to your activity container (NOT the linear layout)
                // Make sure R.id.mainFragmentContainer is in your activity layout
                transaction.add(R.id.mainScrillView, addEventFragment);

                // Add to back stack so pressing BACK will remove detail and unhide this
                transaction.addToBackStack(null);

                // Commit
                transaction.commit();
            }
        });

        typeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_Category add_category = new Add_Category();
                add_category.Show(requireContext());
            }
        });

        userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addEventFragment = new UserManagementFragment();

                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                // Hide this fragment
                transaction.hide(AdminDashbordFragment.this);

                // Add the detail fragment to your activity container (NOT the linear layout)
                // Make sure R.id.mainFragmentContainer is in your activity layout
                transaction.add(R.id.mainScrillView, addEventFragment);

                // Add to back stack so pressing BACK will remove detail and unhide this
                transaction.addToBackStack(null);

                // Commit
                transaction.commit();
            }
        });

        adminCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addEventFragment = new AdminManagementFragment();

                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                // Hide this fragment
                transaction.hide(AdminDashbordFragment.this);

                // Add the detail fragment to your activity container (NOT the linear layout)
                // Make sure R.id.mainFragmentContainer is in your activity layout
                transaction.add(R.id.mainScrillView, addEventFragment);

                // Add to back stack so pressing BACK will remove detail and unhide this
                transaction.addToBackStack(null);

                // Commit
                transaction.commit();
            }
        });

        ticketCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addEventFragment = new TicketManagementFragment();

                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                // Hide this fragment
                transaction.hide(AdminDashbordFragment.this);

                // Add the detail fragment to your activity container (NOT the linear layout)
                // Make sure R.id.mainFragmentContainer is in your activity layout
                transaction.add(R.id.mainScrillView, addEventFragment);

                // Add to back stack so pressing BACK will remove detail and unhide this
                transaction.addToBackStack(null);

                // Commit
                transaction.commit();
            }
        });

        eventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addEventFragment = new EventManagementFragment();

                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                // Hide this fragment
                transaction.hide(AdminDashbordFragment.this);

                // Add the detail fragment to your activity container (NOT the linear layout)
                // Make sure R.id.mainFragmentContainer is in your activity layout
                transaction.add(R.id.mainScrillView, addEventFragment);

                // Add to back stack so pressing BACK will remove detail and unhide this
                transaction.addToBackStack(null);

                // Commit
                transaction.commit();
            }
        });

        loactionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                startActivity(intent);
            }
        });

        notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addEventFragment = new NotificationFragment();

                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                // Hide this fragment
                transaction.hide(AdminDashbordFragment.this);

                // Add the detail fragment to your activity container (NOT the linear layout)
                // Make sure R.id.mainFragmentContainer is in your activity layout
                transaction.add(R.id.mainScrillView, addEventFragment);

                // Add to back stack so pressing BACK will remove detail and unhide this
                transaction.addToBackStack(null);

                // Commit
                transaction.commit();
            }
        });

        paymentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addEventFragment = new PaymentFragment();

                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                // Hide this fragment
                transaction.hide(AdminDashbordFragment.this);

                // Add the detail fragment to your activity container (NOT the linear layout)
                // Make sure R.id.mainFragmentContainer is in your activity layout
                transaction.add(R.id.mainScrillView, addEventFragment);

                // Add to back stack so pressing BACK will remove detail and unhide this
                transaction.addToBackStack(null);

                // Commit
                transaction.commit();
            }
        });

        reportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addEventFragment = new ReportFragment();

                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                // Hide this fragment
                transaction.hide(AdminDashbordFragment.this);

                // Add the detail fragment to your activity container (NOT the linear layout)
                // Make sure R.id.mainFragmentContainer is in your activity layout
                transaction.add(R.id.mainScrillView, addEventFragment);

                // Add to back stack so pressing BACK will remove detail and unhide this
                transaction.addToBackStack(null);

                // Commit
                transaction.commit();
            }
        });



        loading.Stop();
        return view;
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainScrillView, fragment)
                    .commit();
            return true;
        }
        return false;
    }



}