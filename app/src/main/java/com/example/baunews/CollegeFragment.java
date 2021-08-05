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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;


import com.example.baunews.HelperClasses.NewsAdapter;
import com.example.baunews.Models.NewsModel;
import com.example.baunews.ViewModels.NewsViewModel;
import com.example.baunews.databinding.FragmentCollegeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CollegeFragment extends Fragment {

    private NewsAdapter adapter;
    private FragmentCollegeBinding binding;
    public NewsViewModel newsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_college, container, false);
        View view = binding.getRoot();
        isAdmin();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        newsViewModel.init2();
        adapter = new NewsAdapter(getContext(), newsViewModel.getData2().getValue());

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d("ADAPTER", "item count: " + adapter.getItemCount());
        binding.recyclerView.setAdapter(adapter);

        newsViewModel.getData2().observe(getViewLifecycleOwner(), new Observer<ArrayList<NewsModel>>() {
            @Override
            public void onChanged(ArrayList<NewsModel> newsModels) {
                Log.d("DataTracing", "GeneralFragment: ");
                adapter = new NewsAdapter(getContext(), newsModels);
                binding.recyclerView.setAdapter(adapter);

            }
        });


        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference("users").child(currUser.getUid()).child("collageId")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String collageId = snapshot.getValue(String.class);
                                    Pair pair = Pair.create(binding.floatingActionButton, "layout");
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pair);
                                    Intent intent = new Intent(getContext(), CreateNewsActivity.class);
                                    intent.putExtra("news_category", "collage");
                                    intent.putExtra("collage_id", collageId);
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
                            if (snapshot.getValue(String.class).equals("C")) {
                                binding.floatingActionButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}