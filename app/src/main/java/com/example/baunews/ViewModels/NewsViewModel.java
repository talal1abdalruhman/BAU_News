package com.example.baunews.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.baunews.Models.NewsModel;
import com.example.baunews.Repository.Repo;

import java.util.ArrayList;

public class NewsViewModel extends ViewModel {
    MutableLiveData<ArrayList<NewsModel>> news;

    public void init(){
        if(news != null){
            return;
        }
        news = Repo.getInstance().getData();
    }

    public LiveData<ArrayList<NewsModel>> getData(){
        return news;
    }
}
