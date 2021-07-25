package com.example.baunews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.baunews.Models.NewsModel;
import com.example.baunews.databinding.ActivityShowNewsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowNewsActivity extends AppCompatActivity {
    ActivityShowNewsBinding binding;
    String newsKey, newsCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_show_news);
        Log.d("NewsKey", getIntent().getStringExtra("news_id"));
        Log.d("NewsKey", getIntent().getStringExtra("category"));
        ShowTheNews();
    }

    public void ShowTheNews(){
        newsKey = getIntent().getStringExtra("news_id");
        newsCategory = getIntent().getStringExtra("category");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("news").child(newsCategory).child(newsKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    NewsModel newsModel = snapshot.getValue(NewsModel.class);
                    binding.title.setText(newsModel.getTitle());
                    binding.date.setText(newsModel.getDate());
                    binding.description.setText(newsModel.getDescription());
                    Glide.with(ShowNewsActivity.this)
                            .load(newsModel.getImage())
                            .into(binding.image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}