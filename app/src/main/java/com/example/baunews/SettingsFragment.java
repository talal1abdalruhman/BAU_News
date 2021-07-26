package com.example.baunews;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.example.baunews.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    SharedPreferences DarkModePreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings, container, false);
        View view = binding.getRoot();

        //add dropdown lang
        ArrayAdapter<CharSequence> adapterLang = ArrayAdapter.createFromResource(getContext(),
                R.array.languages, android.R.layout.simple_spinner_dropdown_item);
        adapterLang.setDropDownViewResource(R.layout.dropdown_item);
        binding.langAutoComplete.setAdapter(adapterLang);

        DarkModePreference = getActivity().getSharedPreferences("DARK_MODE_PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = DarkModePreference.edit();

        checkDeviceTheme();

        //switch theme
        binding.btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("DARK_MODE", true).commit();
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("DARK_MODE", false).commit();
                }
            }
        });
        return view;
    }
    public void checkDeviceTheme(){
        Boolean isDark = DarkModePreference.getBoolean("DARK_MODE", false);
        binding.btnSwitch.setChecked(isDark);
    }
}