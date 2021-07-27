package com.example.baunews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.example.baunews.databinding.FragmentSettingsBinding;

import java.util.Locale;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    SharedPreferences DarkModePreference, langPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        View view = binding.getRoot();

        SetupLangSpinner();


        DarkModePreference = getActivity().getSharedPreferences("DARK_MODE_PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = DarkModePreference.edit();

        checkCurrentTheme();

        //switch theme
        binding.btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("DARK_MODE", true).commit();
                    getActivity().recreate();
                } else {
                    editor.putBoolean("DARK_MODE", false).commit();
                    getActivity().recreate();
                }
            }
        });

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                langPreference = getActivity().getSharedPreferences("LANGUAGE_PREFERENCE", Context.MODE_PRIVATE);
                String currLng = langPreference.getString("language", null);
                if ((currLng.equals("en") && position != 0) || (currLng.equals("ar") && position != 1)) {
                    SharedPreferences.Editor editor = langPreference.edit();
                    Log.d("LANG_SELECTED", "onItemSelected: " + position);
                    editor.putString("language", (position > 0) ? "ar" : "en").commit();
                    editor.apply();

                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle(getString(R.string.change_language));
                    progressDialog.setMessage(getString(R.string.loading));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    setAppLocale((position > 0) ? "ar" : "en");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().recreate();
                            progressDialog.dismiss();
                        }
                    }, 1500);

                } else {
                    binding.spinner.setSelection(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    public void checkCurrentTheme() {
        Boolean isDark = DarkModePreference.getBoolean("DARK_MODE", false);
        binding.btnSwitch.setChecked(isDark);
    }

    public void SetupLangSpinner() {
        ArrayAdapter<CharSequence> adapterLang = ArrayAdapter.createFromResource(
                getContext(),
                R.array.languages,
                R.layout.language_spinner_items);
        adapterLang.setDropDownViewResource(R.layout.dropdown_item);
        binding.langAutoComplete.setAdapter(adapterLang);
        binding.spinner.setAdapter(adapterLang);
        String appLang = getResources().getConfiguration().locale.getLanguage();
        binding.spinner.setSelection((appLang.equals("en") ? 0 : 1));
        langPreference = getActivity().getSharedPreferences("LANGUAGE_PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = langPreference.edit();
        Log.d("LANG_SELECTED", "onSplash: " + appLang);
        editor.putString("language", appLang).commit();
        editor.apply();
    }

    public void setAppLocale(String language) {
        Resources resources = getActivity().getBaseContext().getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        configuration.locale = new Locale(language);
        configuration.setLayoutDirection(new Locale(language));
        resources.updateConfiguration(configuration, displayMetrics);

    }

}