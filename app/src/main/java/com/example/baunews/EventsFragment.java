package com.example.baunews;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.baunews.HelperClasses.EventsAdapter;
import com.example.baunews.HelperClasses.NewsAdapter;
import com.example.baunews.Models.EventsModel;
import com.example.baunews.Models.NewsModel;
import com.example.baunews.ViewModels.EventViewModel;
import com.example.baunews.ViewModels.NewsViewModel;
import com.example.baunews.databinding.FragmentEventsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;
    EventViewModel eventViewModel;
    EventsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_events, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isAdmin();
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        eventViewModel.init();
        adapter = new EventsAdapter(getContext(), eventViewModel.getData().getValue());

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(adapter);

        eventViewModel.getData().observe(getViewLifecycleOwner(), new Observer<ArrayList<EventsModel>>() {
            @Override
            public void onChanged(ArrayList<EventsModel> eventsModels) {
                adapter = new EventsAdapter(getContext(), eventsModels);
                binding.recyclerView.setAdapter(adapter);
            }
        });

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String category = snapshot.child("admin").getValue(String.class);
                                    if (!category.equals("G")) {
                                        category = snapshot.child("collageId").getValue(String.class);
                                    }
                                    Pair pair = Pair.create(binding.floatingActionButton, "layout");
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pair);
                                    Intent intent = new Intent(getContext(), CreateEventActivity.class);
                                    intent.putExtra("category", category);
                                    startActivity(intent, options.toBundle());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void isAdmin() {
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users").child(userId).child("admin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String admin = snapshot.getValue(String.class);
                            if (admin.equals("G") || admin.equals("C")) {
                                binding.floatingActionButton.show();
                            } else {
                                binding.floatingActionButton.hide();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}