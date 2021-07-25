package com.example.baunews;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.baunews.databinding.FragmentSettingsBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
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

        //add dropdown theme
        ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(getContext(),
                R.array.themes, android.R.layout.simple_spinner_dropdown_item);
        adapterTheme.setDropDownViewResource(R.layout.dropdown_item);
        binding.themeAutoComplete.setAdapter(adapterTheme);
        return view;
    }
}