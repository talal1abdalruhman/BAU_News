package com.example.baunews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import com.example.baunews.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences screenMode = getSharedPreferences("DARK_MODE_PREFERENCE", Context.MODE_PRIVATE);
        if (screenMode.getBoolean("DARK_MODE", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        SharedPreferences langPref = getSharedPreferences("LANGUAGE_PREFERENCE", Context.MODE_PRIVATE);
        String lang = langPref.getString("language", getResources().getConfiguration().locale.getLanguage());
        setAppLocale(lang);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        SetupTheNav();
        ItemSelectListener();
    }

    public void SetupTheNav() {
        setSupportActionBar(binding.toolbar);
        binding.drawerLayout.setScrimColor(getColor(android.R.color.transparent));
        binding.drawerLayout.setDrawerElevation(0);
        binding.navigationView.setItemBackground(getDrawable(R.drawable.item_select_theme));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navController = Navigation.findNavController(this, R.id.fragment);
        binding.navigationView.setCheckedItem(R.id.general_news);
        binding.toolbar.setTitle(binding.navigationView.getCheckedItem().getTitle());
    }

    public void ItemSelectListener() {
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    binding.drawerLayout.closeDrawers();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    navController.popBackStack();
                    navController.navigate(item.getItemId());
                    binding.toolbar.setTitle(item.getTitle());
                    item.setChecked(true);
                    binding.drawerLayout.closeDrawers();
                }
                return true;
            }
        });
    }

    public void setAppLocale(String language) {
        Resources resources = getBaseContext().getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        configuration.locale = new Locale(language);
        configuration.setLayoutDirection(new Locale(language));
        resources.updateConfiguration(configuration, displayMetrics);

    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.close();
        } else {
            finish();
            super.onBackPressed();
        }
    }
}