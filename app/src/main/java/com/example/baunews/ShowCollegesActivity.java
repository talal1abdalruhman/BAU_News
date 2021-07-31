package com.example.baunews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.example.baunews.databinding.ActivityShowCollegesBinding;

public class ShowCollegesActivity extends AppCompatActivity {

    ActivityShowCollegesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_show_colleges);


        Intent intent=getIntent();
        Bundle b=intent.getExtras();
        int x=b.getInt("college");

        switch (x){
            case 1 :
                binding.collapsing.setTitle(getString(R.string.engineering));
                binding.imageCollege.setImageResource(R.drawable.engineering);
                binding.txt.setText(R.string.engineering_description);
                break;
            case 2:
                binding.collapsing.setTitle(getString(R.string.medicine));
                binding.imageCollege.setImageResource(R.drawable.medicine);
                binding.txt.setText(R.string.midicine_descripton);
                break;
            case 3:
                binding.collapsing.setTitle(getString(R.string.it));
                binding.imageCollege.setImageResource(R.drawable.it);
                binding.txt.setText(R.string.it_descripton);
                break;
            case 4:
                binding.collapsing.setTitle(getString(R.string.science));
                binding.imageCollege.setImageResource(R.drawable.science);
                binding.txt.setText(R.string.Science_descripton);
                break;
            case 5:
                binding.collapsing.setTitle(getString(R.string.agricultural));
                binding.imageCollege.setImageResource(R.drawable.agricultural);
                binding.txt.setText(R.string.Agricultural_descripton);
                break;
            case 6:
                binding.collapsing.setTitle(getString(R.string.business));
                binding.imageCollege.setImageResource(R.drawable.business);
                binding.txt.setText(R.string.Business_description);
                break;
            case 7:
                binding.collapsing.setTitle(getString(R.string.human_sciences));
                binding.imageCollege.setImageResource(R.drawable.human_sciences);
                binding.txt.setText(R.string.Human_Sciences_description);
                break;
            case 8:
                binding.collapsing.setTitle(getString(R.string.technical_collage));
                binding.imageCollege.setImageResource(R.drawable.technical_collage);
                binding.txt.setText(R.string.Technical_Collage_description);
                break;
            case 9:
                binding.collapsing.setTitle(getString(R.string.artifical_intelligence));
                binding.imageCollege.setImageResource(R.drawable.artifical_intelligence);
                binding.txt.setText(R.string.Artifical_Intelligence_description);
                break;
            case 10:
                binding.collapsing.setTitle(getString(R.string.law_collage));
                binding.imageCollege.setImageResource(R.drawable.law_college);
                binding.txt.setText(R.string.Law_Collage_description);
        }
    }
}