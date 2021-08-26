package com.example.baunews.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.baunews.Models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminsRepo {
    static AdminsRepo instance;
    private ArrayList<UserModel> adminsModels;
    MutableLiveData<ArrayList<UserModel>> adminsModel;

    public static AdminsRepo getInstance() {
        if (instance == null) {
            instance = new AdminsRepo();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<UserModel>> getData() {
        adminsModels = new ArrayList<>();
        adminsModel = new MutableLiveData<>();
        RetrieveAllData();
        adminsModel.setValue(adminsModels);

        return adminsModel;
    }

    private void RetrieveAllData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users").orderByChild("admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            adminsModels.clear();
                            for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                                UserModel admin = adminSnapshot.getValue(UserModel.class);
                                if (admin.getAdmin().equals("G") || admin.getAdmin().equals("C")) {
                                    adminsModels.add(admin);
                                }
                            }
                            adminsModel.setValue(adminsModels);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
