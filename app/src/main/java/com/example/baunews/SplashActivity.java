package com.example.baunews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    TextView txt_title;
    ImageView bau_img;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
}