package com.example.baunews;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.abhinav.passwordgenerator.PasswordGenerator;
import com.example.baunews.HelperClasses.Validation;
import com.example.baunews.Models.UserModel;
import com.example.baunews.databinding.ActivityRegisterBinding;
import com.example.baunews.databinding.FragmentAddAdminBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddAdminFragment extends Fragment {

    FragmentAddAdminBinding binding;
    View view;
    private static final String AUTH_COMPLETE = "AUTH_COMPLETE";
    Validation validation;
    String collageId = "", collageName, admin;
    private FirebaseAuth mAuth, mAuth2;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    String[] collages;

    public AddAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_admin, container, false);
        view = binding.getRoot();

        SetupCollageMenu();

        collages = getResources().getStringArray(R.array.colleges_names);
        validation = new Validation(getResources());

        binding.addAdmin.setOnClickListener(v -> {
            RegisterAdmin();
        });
        return view;
    }

    public void SetupCollageMenu() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.share_names, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        binding.autoComplete.setAdapter(adapter);

        binding.autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                admin = (position > 0) ? "C" : "G";
                collageId = (position - 1) + "";
                collageName = parent.getItemAtPosition(position).toString();
            }
        });
    }

    public boolean isConnect() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            ShowConnectionDialog();
            return false;
        }
        return true;
    }

    public void ShowConnectionDialog() {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.no_connection_dialog,
                getActivity().findViewById(R.id.container));
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


    private void RegisterAdmin() {
        String email, password;
        email = binding.email.getEditText().getText().toString();
        PasswordGenerator passwordGenerator = new PasswordGenerator(
                10, true,
                true, true,
                true);
        password = passwordGenerator.generatePassword();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("users");

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("[https://bau-news-default-rtdb.firebaseio.com/]")
                .setApiKey("AIzaSyBbsZYhI41EVkG8RCkv9rACb036t-cAZ4w")
                .setApplicationId("bau-news").build();

        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getActivity().getApplicationContext(), firebaseOptions, "BAU News");
            mAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e) {
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("BAU News"));
        }


        if (!validation.validateEmail(binding.email)
                | !validation.validateCollage(binding.dropdown, collageId)
                | !isConnect()) return;

        binding.addAdmin.setEnabled(false);
        mAuth2.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(AUTH_COMPLETE, "user created");

                    FirebaseUser currUser = mAuth2.getCurrentUser();
                    currUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(AUTH_COMPLETE, "userModel verified");

                                UserModel userModel = new UserModel(currUser.getUid(), email, password, collageName, collageId, admin);
                                Log.d(AUTH_COMPLETE, collageId + " " + collageName);
                                mDatabaseReference.child(currUser.getUid()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(AUTH_COMPLETE, "userModel data added");
                                        int id = Integer.parseInt(collageId);
                                        String cate = admin.equals("G") ? getResources().getString(R.string.gene) : collages[id];
                                        ShowAdminInfoDialog(email, password, cate);
                                        binding.addAdmin.setEnabled(true);
                                        mAuth2.signOut();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(AUTH_COMPLETE, "userModel data NOT added");
                                        binding.addAdmin.setEnabled(true);
                                        mAuth2.signOut();
                                    }
                                });

                            } else {
                                Log.d(AUTH_COMPLETE, "user NOt verified");
                                binding.addAdmin.setEnabled(true);
                                mAuth2.signOut();
                            }
                        }
                    });
                } else {
                    Log.d(AUTH_COMPLETE, "user NOT created");
                    binding.addAdmin.setEnabled(true);
                    mAuth2.signOut();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthUserCollisionException) {
                    binding.email.setError(getString(R.string.email_already_used));
                    binding.addAdmin.setEnabled(true);
                    mAuth2.signOut();
                }
            }
        });
    }

    public void ShowAdminInfoDialog(String email, String password, String category) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.register_admin_dialog,
                getActivity().findViewById(R.id.container));
        builder.setView(view);
        dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        TextView emailTV = view.findViewById(R.id.email);
        emailTV.setText(getResources().getString(R.string.admin_email) + ": " + email);
        TextView passTV = view.findViewById(R.id.password);
        passTV.setText(getString(R.string.admin_password) + " " + password);
        TextView catTV = view.findViewById(R.id.category);
        catTV.setText(getString(R.string.admin_category) + " " + category);

        view.findViewById(R.id.txt_send_email).setOnClickListener(v -> {
            String subject = "BAU News admin";
            String message = "We are happy to let you join our team in (BAU News) as admin." +
                    "\nThis is your login email, password and section that you'll be manage it:" +
                    "\n\nEmail: " + email +
                    "\nPassword: " + password +
                    "\nSection: " + category +
                    "\n\nBe sure to verify your account using the mail we sent before, and change your password as soon as possible." +
                    "\n\n\nBest regards.";
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:"));
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            dialog.dismiss();
        });

        view.findViewById(R.id.txt_send_sms).setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            String message = "We are happy to let you join our team in (BAU News) as admin." +
                    "\nThis is your login email, password and section that you'll be manage it:" +
                    "\n\nEmail: " + email +
                    "\nPassword: " + password +
                    "\nSection: " + category +
                    "\n\nBe sure to verify your account using the mail we sent before, and change your password as soon as possible." +
                    "\n\nBest regards.";
            sendIntent.putExtra("sms_body", message);
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);
            dialog.dismiss();
        });

        dialog.show();
    }

}