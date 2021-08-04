package com.example.baunews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.baunews.Models.UserModel;
import com.example.baunews.HelperClasses.Validation;
import com.example.baunews.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    private static final String AUTH_COMPLETE = "AUTH_COMPLETE";
    ActivityRegisterBinding binding;
    Validation validation;
    String collageId = "", collageName;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    SharedPreferences langPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_register);
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);

        langPref = getSharedPreferences("LANGUAGE_PREFERENCE", Context.MODE_PRIVATE);
        if(langPref.getString("language", "").equals("ar")) {
            binding.backBtn.setRotationY(180);
        }

        validation = new Validation(this.getResources());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("users");

        //intent Activity
        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair[] pairs = new Pair[5];
                pairs[0] = Pair.create(binding.img, "logo");
                pairs[1] = Pair.create(binding.email, "email");
                pairs[2] = Pair.create(binding.password, "password");
                pairs[3] = Pair.create(binding.linearLayout, "underText");
                pairs[4] = Pair.create(binding.register, "button");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);

                Intent intents = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intents, options.toBundle());
            }
        });

        binding.autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                collageId = position+"";
                collageName = parent.getItemAtPosition(position).toString();
            }
        });

        //add dropdown
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.colleges_names, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        binding.autoComplete.setAdapter(adapter);
    }


    public void RegisterUser(View view) {
        String email, password, cofirmedPassword;
        email = binding.email.getEditText().getText().toString();
        password = binding.password.getEditText().getText().toString();
        cofirmedPassword = binding.confirmPassword.getEditText().getText().toString();

        if(!validation.validateEmail(binding.email) | !validation.validatePassword(binding.password)
                | !validation.validateConfirmPassword(binding.confirmPassword, password)
                | !validation.validateCollage(binding.dropdown, collageId) | !isConnect()) return;

        binding.register.animate().scaleX(0).scaleY(0).setDuration(300);
        binding.loadingAnim.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(AUTH_COMPLETE, "user created");

                    FirebaseUser currUser = mAuth.getCurrentUser();
                    currUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(AUTH_COMPLETE, "userModel verified");

                                UserModel userModel = new UserModel(email, password, collageName, collageId, "N");
                                Log.d(AUTH_COMPLETE, collageId+" "+collageName);
                                mDatabaseReference.child(currUser.getUid()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(AUTH_COMPLETE, "userModel data added");
                                        // TODO: return userModel to loginScreen, Add progressBar and Disable register button
                                        binding.loadingAnim.setVisibility(View.GONE);
                                        binding.formLayout.animate().scaleY(0).scaleX(0).setDuration(300);
                                        binding.finishedAnim.setVisibility(View.VISIBLE);
                                        binding.finishedAnim.playAnimation();
                                        binding.finishedAnim.addAnimatorListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //GoBackToLogin(new View(RegisterActivity.this));
                                                        Pair pair = Pair.create(binding.finishedAnim, "logo");
                                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pair);
                                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                        startActivity(intent, options.toBundle());
                                                    }
                                                }, 600 + binding.finishedAnim.getDuration());
                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(AUTH_COMPLETE, "userModel data NOT added");
                                    }
                                });

                            }else{
                                Log.d(AUTH_COMPLETE, "user NOt verified");
                            }
                        }
                    });
                }
                else {
                    Log.d(AUTH_COMPLETE, "user NOT created");
                }
            }
        });

    }

    public void GoBackToLogin(View view) {
        Pair[] pairs = new Pair[5];
        pairs[0] = Pair.create(binding.img, "logo");
        pairs[1] = Pair.create(binding.email, "email");
        pairs[2] = Pair.create(binding.password, "password");
        pairs[3] = Pair.create(binding.linearLayout, "underText");
        pairs[4] = Pair.create(binding.register, "button");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);

        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent, options.toBundle());
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
}