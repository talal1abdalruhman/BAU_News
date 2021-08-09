package com.example.baunews.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.baunews.Models.PressKitModel;
import com.example.baunews.Repository.PressKitRepo;

import java.util.ArrayList;

public class PressKitViewModel extends ViewModel {
    MutableLiveData<ArrayList<PressKitModel>> pressKits;

    public void init(){
        if(pressKits != null){
            return;
        }
        pressKits = PressKitRepo.getInstance().getData();
    }


    public LiveData<ArrayList<PressKitModel>> getData(){
        return pressKits;
    }
}
