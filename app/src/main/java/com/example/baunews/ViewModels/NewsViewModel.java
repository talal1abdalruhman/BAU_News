package com.example.baunews.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.baunews.Models.NewsModel;
import com.example.baunews.Repository.NewsRepo;

import java.util.ArrayList;

public class NewsViewModel extends ViewModel {
    MutableLiveData<ArrayList<NewsModel>> news;
    MutableLiveData<ArrayList<NewsModel>> news2;

    public void init(){
        if(news != null){
            return;
        }
        news = NewsRepo.getInstance().getData();
    }

    public void init2(){
        if(news != null){
            return;
        }
        news2 = NewsRepo.getInstance().getData2();
    }

    public LiveData<ArrayList<NewsModel>> getData(){
        return news;
    }

    public LiveData<ArrayList<NewsModel>> getData2(){
        return news2;
    }
}
