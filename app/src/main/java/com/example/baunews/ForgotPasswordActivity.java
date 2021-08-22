package com.example.baunews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.baunews.HelperClasses.Validation;
import com.example.baunews.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Locale;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    Validation validation;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        validation = new Validation(getResources());
        mAuth = FirebaseAuth.getInstance();

        setAppLocale();
        SharedPreferences langPref = getSharedPreferences("LANGUAGE_PREFERENCE", Context.MODE_PRIVATE);
        if(langPref.getString("language", "").equals("ar")) {
            binding.backBtn.setRotationY(180);
        }

    }

    public void SendResetPasswordLink(View view) {
        binding.email.setErrorEnabled(false);
        binding.email.setError(null);
        if(!isConnect()) return;
        String email = binding.email.getEditText().getText().toString();
        if(!validation.validateLoginEmail(binding.email)) return;
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    binding.text.setVisibility(View.GONE);
                    binding.email.setVisibility(View.GONE);
                    binding.restPasswordBtn.setVisibility(View.GONE);
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.progressCircular.setVisibility(View.GONE);
                            binding.successText.setVisibility(View.VISIBLE);
                        }
                    }, 3000);
                } else if(task.getException() instanceof FirebaseAuthInvalidUserException) {
                    binding.email.setErrorEnabled(true);
                    binding.email.setError(getResources().getString(R.string.email_address_not_register));
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Something going Wrong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean isConnect() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            ShowConnectionDialog();
            return false;
        }
        return true;
    }

    public void GoBackToLogin(View view) {
        Pair[] pairs = new Pair[3];
        pairs[0] = Pair.create(binding.animationView, "logo");
        pairs[1] = Pair.create(binding.email, "email");
        pairs[2] = Pair.create(binding.restPasswordBtn, "button");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordActivity.this, pairs);

        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent, options.toBundle());
    }

    public void ShowConnectionDialog(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.no_connection_dialog,
                findViewById(R.id.container));
        builder.setView(view);
        dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        view.findViewById(R.id.txt_close).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    public void setAppLocale() {
        SharedPreferences langPreference = getSharedPreferences("LANGUAGE_PREFERENCE", Context.MODE_PRIVATE);
        String language = langPreference.getString("language", "en");

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        configuration.locale = new Locale(language);
        configuration.setLayoutDirection(new Locale(language));
        resources.updateConfiguration(configuration, displayMetrics);
    }
}