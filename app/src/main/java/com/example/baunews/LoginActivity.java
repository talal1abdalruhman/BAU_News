package com.example.baunews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baunews.HelperClasses.Validation;
import com.example.baunews.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    private static final String LOGIN_TAG = "LOGIN_PROCESS";
    ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    Validation validation;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        validation = new Validation(getResources());
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.login_verifing_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair[] pairs = new Pair[5];
                pairs[0] = Pair.create(binding.img, "logo");
                pairs[1] = Pair.create(binding.email, "email");
                pairs[2] = Pair.create(binding.password, "password");
                pairs[3] = Pair.create(binding.linearLayout, "underText");
                pairs[4] = Pair.create(binding.login, "button");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);

                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent, options.toBundle());
            }
        });

        binding.forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair[] pairs = new Pair[3];
                pairs[0] = Pair.create(binding.img, "logo");
                pairs[1] = Pair.create(binding.email, "email");
                pairs[2] = Pair.create(binding.login, "button");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);

                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent, options.toBundle());
            }
        });

    }

    public void LoginUser(View view) {
        if(!isConnect()) return;
        binding.login.animate().scaleX(0).scaleY(0).setDuration(300);
        binding.loadingAnim.setVisibility(View.VISIBLE);
        binding.email.setError(null);
        binding.email.setErrorEnabled(false);
        binding.password.setError(null);
        binding.password.setErrorEnabled(false);
        String loginEmail = binding.email.getEditText().getText().toString();
        String loginPassword = binding.password.getEditText().getText().toString();

        if(!validation.validateLoginEmail(binding.email) | !validation.validateLoginPassword(binding.password)) return;

        mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOGIN_TAG, "success");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    boolean emailVerified = user.isEmailVerified();
                    final String[] token = new String[1];

                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            token[0] = task.getResult();
                            mRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
                            mRef.child("device_token").setValue(token[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d(LOGIN_TAG, "Token added");
                                        Log.d(LOGIN_TAG, token[0]);
                                    }
                                    else Log.d(LOGIN_TAG, "Token NOT added");
                                }
                            });
                        }
                    });

                    if (emailVerified) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        dialog.show();
                    }
                } else {

                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        binding.password.setError(getString(R.string.incorrect_password));
                        binding.password.requestFocus();
                    }else if (task.getException() instanceof FirebaseAuthInvalidUserException){
                        binding.email.setError(getString(R.string.email_address_not_register));
                        binding.email.requestFocus();
                    }
                }
            }
        });

    }

    public boolean isConnect() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void SendVerification(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                    Log.d(LOGIN_TAG, "verification email sent");
                }
            });
        }
    }
}