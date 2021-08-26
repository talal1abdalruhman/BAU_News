package com.example.baunews;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.baunews.HelperClasses.AdminsAdapter;
import com.example.baunews.Models.UserModel;
import com.example.baunews.ViewModels.AdminViewModel;
import com.example.baunews.databinding.FragmentAdminManageBinding;

import java.util.ArrayList;

public class AdminManageFragment extends Fragment {

    FragmentAdminManageBinding binding;
    View view;
    AdminsAdapter adapter;
    AdminViewModel adminViewModel;

    public AdminManageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_manage, container, false);
        view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        adminViewModel.init();
        adapter = new AdminsAdapter(getContext(), adminViewModel.getData().getValue());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(5), true));
        binding.recycler.setItemAnimator(new DefaultItemAnimator());
        binding.recycler.setAdapter(adapter);
        adminViewModel.getData().observe(getViewLifecycleOwner(), userModels -> {
            adapter = new AdminsAdapter(getContext(), userModels);
            binding.recycler.setAdapter(adapter);
        });
    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}