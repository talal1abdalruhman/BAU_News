package com.example.baunews;

import android.content.Context;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            getContext().getTheme().applyStyle(R.style.Theme_BAUNews,true);
        }
        else {
            getContext().getTheme().applyStyle(R.style.Theme_BAUNews,true);
        }
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings, container, false);
        View view = binding.getRoot();

        //add dropdown lang
        ArrayAdapter<CharSequence> adapterLang = ArrayAdapter.createFromResource(getContext(),
                R.array.languages, android.R.layout.simple_spinner_dropdown_item);
        adapterLang.setDropDownViewResource(R.layout.dropdown_item);
        binding.langAutoComplete.setAdapter(adapterLang);

        checkDeviceTheme();

        //switch theme
        binding.btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
        return view;
    }
    public void checkDeviceTheme(){
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                binding.btnSwitch.setChecked(true);
                break;
            default:
                binding.btnSwitch.setChecked(false);
                break;
        }
    }
}