package com.example.baunews.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.baunews.Models.EventsModel;
import com.example.baunews.Repository.EventsRepo;

import java.util.ArrayList;

public class EventViewModel extends ViewModel {
    MutableLiveData<ArrayList<EventsModel>> events;

    public void init(){
        if(events != null){
            return;
        }
        events = EventsRepo.getInstance().getData();
    }


    public LiveData<ArrayList<EventsModel>> getData(){
        return events;
    }

}
