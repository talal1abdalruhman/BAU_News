package com.example.baunews;

import android.content.Context;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.baunews.HelperClasses.NewsAdapter;
import com.example.baunews.Models.NewsModel;
import com.example.baunews.ViewModels.NewsViewModel;
import com.example.baunews.databinding.FragmentGeneralBinding;

import java.util.ArrayList;

public class GeneralFragment extends Fragment {

    public NewsAdapter adapter;
    private FragmentGeneralBinding binding;
    public NewsViewModel newsViewModel;
    Context mContext;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_general, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        newsViewModel.init();
        adapter = new NewsAdapter(getContext(), newsViewModel.getData().getValue());

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d("ADAPTER", "item count: " + adapter.getItemCount());
        binding.recyclerView.setAdapter(adapter);

        newsViewModel.getData().observe(getViewLifecycleOwner(), new Observer<ArrayList<NewsModel>>() {
            @Override
            public void onChanged(ArrayList<NewsModel> newsModels) {
                Log.d("DataTracing", "GeneralFragment: ");
                adapter = new NewsAdapter(getContext(), newsModels);
                binding.recyclerView.setAdapter(adapter);
            }
        });

    }

    private int dpToPx(int dp) {

        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));

    }

}