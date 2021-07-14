package com.example.baunews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.baunews.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_register);

        //intent Activity
        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        //add dropdown
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.colleges_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        binding.autoComplete.setAdapter(adapter);
    }
}