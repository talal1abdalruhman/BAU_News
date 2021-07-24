package com.example.baunews.Repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.baunews.Models.NewsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Repo {
    private static final String TAG = "Tracing Data";
    static Repo instance;
    private ArrayList<NewsModel> newsModels;
    static Context mContext;
    MutableLiveData<ArrayList<NewsModel>> newsModel;

    public static Repo getInstance() {
        if (instance == null) {
            instance = new Repo();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<NewsModel>> getData() {
        newsModels = new ArrayList<>();
        newsModel = new MutableLiveData<>();
        RetrieveAllData();
        newsModel.setValue(newsModels);

        return newsModel;
    }

    private void RetrieveAllData() {
        Log.d(TAG, "before RetrieveAllData: " + newsModels.size());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("news").orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    newsModels.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        newsModels.add(0, postSnapshot.getValue(NewsModel.class));
                    }
                    newsModel.setValue(newsModels);
                    Log.d(TAG, "after RetrieveAllData: " + newsModels.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
