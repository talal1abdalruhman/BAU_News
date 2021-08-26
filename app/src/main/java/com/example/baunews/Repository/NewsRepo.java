package com.example.baunews.Repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.baunews.Models.NewsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewsRepo {
    private static final String TAG = "Tracing Data";
    static NewsRepo instance;
    private ArrayList<NewsModel> newsModels;
    private ArrayList<NewsModel> newsModels2;
    static Context mContext;
    MutableLiveData<ArrayList<NewsModel>> newsModel;
    MutableLiveData<ArrayList<NewsModel>> newsModel2;

    public static NewsRepo getInstance() {
        if (instance == null) {
            instance = new NewsRepo();
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
                        String category = postSnapshot.child("category").getValue(String.class);
                        if (category.equals("general")) {
                            newsModels.add(0, postSnapshot.getValue(NewsModel.class));
                        }
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

    public MutableLiveData<ArrayList<NewsModel>> getData2() {
        newsModels2 = new ArrayList<>();
        newsModel2 = new MutableLiveData<>();
        RetrieveAllData2();
        newsModel2.setValue(newsModels2);

        return newsModel2;
    }

    private void RetrieveAllData2() {
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users").child(currUser.getUid()).child("collageId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String collageId = snapshot.getValue(String.class);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            if (!collageId.equals("-1")) {
                                database.getReference("news").orderByChild("date").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            newsModels2.clear();
                                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                String category = postSnapshot.child("category").getValue(String.class);
                                                if (collageId.equals(category)) {
                                                    newsModels2.add(0, postSnapshot.getValue(NewsModel.class));
                                                }
                                            }
                                            newsModel2.setValue(newsModels2);
                                            Log.d(TAG, "after RetrieveAllData: " + newsModels2.size());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                database.getReference("news").orderByChild("date").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            newsModels2.clear();
                                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                String category = postSnapshot.child("category").getValue(String.class);
                                                if (!category.equals("general")) {
                                                    newsModels2.add(0, postSnapshot.getValue(NewsModel.class));
                                                }
                                            }
                                            newsModel2.setValue(newsModels2);
                                            Log.d(TAG, "after RetrieveAllData: " + newsModels2.size());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
