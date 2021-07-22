package com.example.baunews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.baunews.databinding.ActivityShowNewsBinding;

public class ShowNewsActivity extends AppCompatActivity {
    ActivityShowNewsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_show_news);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        //Bitmap bitmap = intent.getParcelableExtra("image");
        String description = intent.getStringExtra("description");
        binding.title.setText(title);
        binding.date.setText(date);
        //Glide.with(this).load(bitmap).into(binding.image);
        binding.description.setText(description);
    }
}