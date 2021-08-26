package com.example.baunews.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.baunews.Models.EventsModel;
import com.example.baunews.Models.NewsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventsRepo {
    private static final String TAG = "Tracing Data";
    static EventsRepo instance;
    private ArrayList<EventsModel> eventsModels;
    MutableLiveData<ArrayList<EventsModel>> eventsModel;

    public static EventsRepo getInstance() {
        if (instance == null) {
            instance = new EventsRepo();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<EventsModel>> getData() {
        eventsModels = new ArrayList<>();
        eventsModel = new MutableLiveData<>();
        RetrieveAllData();
        eventsModel.setValue(eventsModels);

        return eventsModel;
    }

    private void RetrieveAllData() {
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users").child(currUser.getUid()).child("collageId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String collageId = snapshot.getValue(String.class);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            database.getReference("events").orderByChild("date")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                eventsModels.clear();
                                                if (!collageId.equals("-1")) {
                                                    for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                                                        String eventCategory = eventSnapshot.child("category").getValue(String.class);
                                                        if (eventCategory.equals("G") || eventCategory.equals(collageId)) {
                                                            eventsModels.add(0, eventSnapshot.getValue(EventsModel.class));
                                                        }
                                                    }
                                                } else {
                                                    for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                                                        String eventCategory = eventSnapshot.child("category").getValue(String.class);
                                                        eventsModels.add(0, eventSnapshot.getValue(EventsModel.class));
                                                    }
                                                }
                                                eventsModel.setValue(eventsModels);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}
