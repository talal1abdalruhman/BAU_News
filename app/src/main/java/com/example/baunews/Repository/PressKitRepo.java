package com.example.baunews.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.baunews.Models.PressKitModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PressKitRepo {
    private static final String TAG = "Tracing Data";
    static PressKitRepo instance;
    private ArrayList<PressKitModel> pressKitModels;
    MutableLiveData<ArrayList<PressKitModel>> pressKitModel;

    public static PressKitRepo getInstance() {
        if (instance == null) {
            instance = new PressKitRepo();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<PressKitModel>> getData() {
        pressKitModels = new ArrayList<>();
        pressKitModel = new MutableLiveData<>();
        RetrieveAllData();
        pressKitModel.setValue(pressKitModels);

        return pressKitModel;
    }

    private void RetrieveAllData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("press_kit").orderByChild("date")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            pressKitModels.clear();
                            for (DataSnapshot pressKitSnapshot : snapshot.getChildren()) {
                                pressKitModels.add(0, pressKitSnapshot.getValue(PressKitModel.class));
                            }
                            pressKitModel.setValue(pressKitModels);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}
