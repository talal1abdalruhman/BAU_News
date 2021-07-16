package com.example.baunews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.baunews.HelperClasses.Token;
import com.example.baunews.HelperClasses.User;
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
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;


public class RegisterActivity extends AppCompatActivity {
    private static final String AUTH_COMPLETE = "AUTH_COMPLETE";
    ActivityRegisterBinding binding;
    Validation validation;
    String collageId = "", collageName;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_register);
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);

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
                | !validation.validateCollage(binding.dropdown, collageId)) return;

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
                                Log.d(AUTH_COMPLETE, "user verified");

                                User user = new User(email, password, collageName, collageId);
                                Log.d(AUTH_COMPLETE, collageId+" "+collageName);
                                mDatabaseReference.child(currUser.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(AUTH_COMPLETE, "user data added");
                                        // TODO: return user to loginScreen, Add progressBar and Disaple register button
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(AUTH_COMPLETE, "user data NOT added");
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
}