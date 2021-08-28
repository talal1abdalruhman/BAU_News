package com.example.baunews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.baunews.HelperClasses.Validation;
import com.example.baunews.databinding.ChangePassDialogBinding;
import com.example.baunews.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    SharedPreferences DarkModePreference, langPreference, settingsChanged;
    FirebaseUser currentUser;
    AlertDialog dialog;
    TextInputLayout currPass, newPass, confirmNewPass, password;
    TextView save, cancel, confirm;
    LottieAnimationView loadAnim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        View view = binding.getRoot();

        SetupLangSpinner();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        settingsChanged = getActivity().getSharedPreferences("CHANGE_STATE", Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = settingsChanged.edit();


        DarkModePreference = getActivity().getSharedPreferences("DARK_MODE_PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = DarkModePreference.edit();

        checkCurrentTheme();

        //switch theme
        binding.btnSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editor.putBoolean("DARK_MODE", true).commit();
                settingsEditor.putBoolean("IS_CHANGED", true).commit();
                getActivity().recreate();
            } else {
                editor.putBoolean("DARK_MODE", false).commit();
                settingsEditor.putBoolean("IS_CHANGED", true).commit();
                getActivity().recreate();
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
                            settingsEditor.putBoolean("IS_CHANGED", true).commit();
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

        binding.changePass.setOnClickListener(v -> {
            ShowChangePassDialog();
        });

        binding.deleteAccount.setOnClickListener(v -> {
            ShowDeleteAccountDialog();
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

    private void ShowChangePassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.change_pass_dialog,
                getActivity().findViewById(R.id.container));
        builder.setView(view);
        dialog = builder.create();

        currPass = view.findViewById(R.id.current_pass);
        newPass = view.findViewById(R.id.new_password);
        confirmNewPass = view.findViewById(R.id.confirm_password);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        save = view.findViewById(R.id.save_changes);
        cancel = view.findViewById(R.id.cancel);
        loadAnim = view.findViewById(R.id.loadingAnim);

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        save.setOnClickListener(v -> {
            Validation validation = new Validation(getResources());
            currPass.setErrorEnabled(false);
            currPass.setError(null);
            newPass.setErrorEnabled(false);
            newPass.setError(null);
            confirmNewPass.setErrorEnabled(false);
            confirmNewPass.setError(null);
            String currPassText = currPass.getEditText().getText().toString().trim();
            String newPassText = newPass.getEditText().getText().toString().trim();
            if (!validation.validatePassword(newPass)
                    | !validation.validateConfirmPassword(confirmNewPass, newPassText)) return;
            ChangePass(currPassText, newPassText);
        });

        dialog.show();
    }

    private void ChangePass(String currPassText, String newPassText) {
        if (currentUser != null) {
            currPass.getEditText().setEnabled(false);
            newPass.getEditText().setEnabled(false);
            confirmNewPass.getEditText().setEnabled(false);
            save.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            loadAnim.setVisibility(View.VISIBLE);

            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currPassText);
            currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    currentUser.updatePassword(newPassText).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                            userRef.child(currentUser.getUid()).child("password").setValue(newPassText)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task1) {
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    });

                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    currPass.setError(getResources().getString(R.string.incorrect_password));
                    currPass.setErrorEnabled(true);
                    currPass.getEditText().setEnabled(true);
                    newPass.getEditText().setEnabled(true);
                    confirmNewPass.getEditText().setEnabled(true);
                    save.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    loadAnim.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    currPass.getEditText().setEnabled(true);
                    newPass.getEditText().setEnabled(true);
                    confirmNewPass.getEditText().setEnabled(true);
                    save.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    loadAnim.setVisibility(View.GONE);
                }
            });
        }
    }

    public void ShowDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.delete_account_dialog,
                getActivity().findViewById(R.id.container));
        builder.setView(view);
        dialog = builder.create();

        password = view.findViewById(R.id.password);
        cancel = view.findViewById(R.id.cancel);
        confirm = view.findViewById(R.id.confirm);
        loadAnim = view.findViewById(R.id.loadingAnim);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        confirm.setOnClickListener(v -> {
            password.setErrorEnabled(false);
            password.setError(null);
            String passTxt = password.getEditText().getText().toString().trim();
            if (passTxt.isEmpty()) {
                password.setErrorEnabled(true);
                password.setError(getResources().getString(R.string.field_cannot_be_empty));
                return;
            }
            DeleteAccount(passTxt);
        });

        dialog.show();
    }

    private void DeleteAccount(String passTxt) {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            Log.d("DELETE_ACCOUNT", "Uid: " + uid);
            loadAnim.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            password.getEditText().setEnabled(false);
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), passTxt);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
            currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DELETE_ACCOUNT", "DeleteAccount: success 1");
                    userRef.child(uid).setValue(null).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            Log.d("DELETE_ACCOUNT", "DeleteAccount: success 2");
                            currentUser.delete().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d("DELETE_ACCOUNT", "DeleteAccount: success 3");
                                    dialog.dismiss();
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    Log.d("DELETE_ACCOUNT", "DeleteAccount: un-success 3");
                                }
                            });
                        } else {
                            Log.d("DELETE_ACCOUNT", "DeleteAccount: un-success 2");
                        }
                    });
                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    password.setError(getResources().getString(R.string.incorrect_password));
                    password.setErrorEnabled(true);
                    password.getEditText().setEnabled(true);
                    password.getEditText().setEnabled(true);
                    loadAnim.setVisibility(View.GONE);
                    confirm.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                } else {
                    Log.d("DELETE_ACCOUNT", "DeleteAccount: un-success 1");
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    password.getEditText().setEnabled(true);
                    loadAnim.setVisibility(View.GONE);
                    confirm.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                }
            });


        }
    }

}