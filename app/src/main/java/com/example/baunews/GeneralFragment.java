package com.example.baunews;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.baunews.HelperClasses.NewsAdapter;
import com.example.baunews.Models.NewsModel;
import com.example.baunews.ViewModels.NewsViewModel;
import com.example.baunews.databinding.FragmentGeneralBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        isAdmin();

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

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pair pair = Pair.create(binding.floatingActionButton, "layout");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pair);
                Intent intent = new Intent(getContext(), CreateNewsActivity.class);
                intent.putExtra("news_category", "general");
                startActivity(intent, options.toBundle());
            }
        });

        if (isConnect()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.animationView.setVisibility(View.GONE);
                }
            }, 2500);

        }

    }

    private int dpToPx(int dp) {

        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));

    }

    public boolean isConnect() {
        ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            ShowConnectionDialog();
            return false;
        }
        return true;
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
                            if (snapshot.getValue(String.class).equals("G")) {
                                binding.floatingActionButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void ShowConnectionDialog(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.no_connection_dialog,
                getActivity().findViewById(R.id.container));
        builder.setView(view);
        dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        view.findViewById(R.id.txt_close).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }
}