package com.example.baunews.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.baunews.Models.EventsModel;
import com.example.baunews.Models.UserModel;
import com.example.baunews.Repository.AdminsRepo;
import com.example.baunews.Repository.EventsRepo;

import java.util.ArrayList;

public class AdminViewModel extends ViewModel {
    MutableLiveData<ArrayList<UserModel>> admins;

    public void init(){
        if(admins != null){
            return;
        }
        admins = AdminsRepo.getInstance().getData();
    }


    public LiveData<ArrayList<UserModel>> getData(){
        return admins;
    }
}
