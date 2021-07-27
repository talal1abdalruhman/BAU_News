package com.example.baunews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    TextView txt_title;
    ImageView bau_img;
    Animation animation;
    SharedPreferences DarkModePreference, langPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        langPreference = getSharedPreferences("LANGUAGE_PREFERENCE", Context.MODE_PRIVATE);
        String appLng = langPreference.getString("language", "");

        if (!appLng.equals("")) {
            setAppLocale(appLng);
        }

        DarkModePreference = getSharedPreferences("DARK_MODE_PREFERENCE", Context.MODE_PRIVATE);
        if (DarkModePreference.getBoolean("DARK_MODE", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        txt_title = findViewById(R.id.txt_title);
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_to_txt);
        txt_title.setAnimation(animation);

        bau_img = findViewById(R.id.bau_img);
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_to_img);
        bau_img.setAnimation(animation);
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currUser != null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this, bau_img, ViewCompat.getTransitionName(bau_img));
                    startActivity(intent, optionsCompat.toBundle());
                    finish();
                }
            }
        }, 2000);
    }

    public void setAppLocale(String language) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        configuration.locale = new Locale(language);
        configuration.setLayoutDirection(new Locale(language));
        resources.updateConfiguration(configuration, displayMetrics);
    }
}