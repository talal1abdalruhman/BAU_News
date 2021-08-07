package com.example.baunews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.baunews.databinding.ActivityShowPressKitBinding;

public class ShowPressKitActivity extends AppCompatActivity {

    ActivityShowPressKitBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_show_press_kit);
    }
}