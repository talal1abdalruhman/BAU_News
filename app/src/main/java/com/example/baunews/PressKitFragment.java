package com.example.baunews;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.baunews.HelperClasses.NewsAdapter;
import com.example.baunews.HelperClasses.PressKitAdapter;
import com.example.baunews.Models.NewsModel;
import com.example.baunews.Models.PressKitModel;
import com.example.baunews.ViewModels.NewsViewModel;
import com.example.baunews.databinding.FragmentPressKitBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PressKitFragment extends Fragment {

    public PressKitAdapter adapter;
    FragmentPressKitBinding binding;
    Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_press_kit, container, false);
        View view = binding.getRoot();
        isAdmin();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pair pair = Pair.create(binding.floatingActionButton, "layout");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pair);
                Intent intent = new Intent(getContext(), CreatePressKitActivity.class);
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
            Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_LONG).show();
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
}