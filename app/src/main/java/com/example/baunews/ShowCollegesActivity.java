package com.example.baunews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.util.Pair;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.baunews.databinding.ActivityShowCollegesBinding;
import com.example.baunews.databinding.FragmentAboutBinding;

import java.util.Locale;

public class ShowCollegesActivity extends AppCompatActivity {

    ActivityShowCollegesBinding binding;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_show_colleges);
        binding.txt.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);

        SharedPreferences langPreference = getSharedPreferences("LANGUAGE_PREFERENCE", Context.MODE_PRIVATE);
        String currLng = langPreference.getString("language", null);
        if(currLng != null){
            setAppLocale(currLng);
        }

        Intent intent=getIntent();
        Bundle b=intent.getExtras();

        int x=b.getInt("college");
        GetResult(x);

        OnBackBtnClicked(binding.imageCollege);
    }

    private void GetResult(int x) {
        switch (x){
            case 1 :
                GetInfoToShow(R.string.engineering,R.drawable.engineering,R.string.engineering_description);
                break;
            case 2:
                GetInfoToShow(R.string.medicine,R.drawable.medicine,R.string.midicine_descripton);
                break;
            case 3:
                GetInfoToShow(R.string.it,R.drawable.it,R.string.it_descripton);
                break;
            case 4:
                GetInfoToShow(R.string.science,R.drawable.science,R.string.Science_descripton);
                break;
            case 5:
                GetInfoToShow(R.string.agricultural,R.drawable.agricultural,R.string.Agricultural_descripton);
                break;
            case 6:
                GetInfoToShow(R.string.business,R.drawable.business,R.string.Business_description);
                break;
            case 7:
                GetInfoToShow(R.string.human_sciences,R.drawable.human_sciences,R.string.Human_Sciences_description);
                break;
            case 8:
                GetInfoToShow(R.string.technical_collage,R.drawable.technical_collage,R.string.Technical_Collage_description);
                break;
            case 9:
                GetInfoToShow(R.string.artifical_intelligence,R.drawable.artifical_intelligence,R.string.Artifical_Intelligence_description);
                break;
            case 10:
                GetInfoToShow(R.string.law_collage,R.drawable.law_college,R.string.Law_Collage_description);
        }
    }

    private void GetInfoToShow(int title, int img ,int description){
        binding.collapsing.setTitle(getString(title));
        binding.imageCollege.setImageResource(img);
        binding.txt.setText(description);
    }

    }

    public void setAppLocale(String language) {
        Resources resources = getBaseContext().getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        configuration.locale = new Locale(language);
        configuration.setLayoutDirection(new Locale(language));
        resources.updateConfiguration(configuration, displayMetrics);

    private void OnBackBtnClicked(ImageView img) {
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back =new Intent(ShowCollegesActivity.this,MainActivity.class);
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(ShowCollegesActivity.this, img ,ViewCompat.getTransitionName(img));
                startActivity(back,compat.toBundle());
                finish();
            }
        });
    }
}