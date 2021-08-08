package com.example.baunews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.baunews.HelperClasses.Validation;
import com.example.baunews.Models.NewsModel;
import com.example.baunews.Models.UserModel;
import com.example.baunews.NotificationPackage.APIService;
import com.example.baunews.NotificationPackage.Client;
import com.example.baunews.NotificationPackage.Data;
import com.example.baunews.NotificationPackage.MyResponse;
import com.example.baunews.NotificationPackage.NotificationSender;
import com.example.baunews.databinding.ActivityCreateNewsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Upload_Process";
    ActivityCreateNewsBinding binding;
    private APIService apiService;
    Uri ImgUri = null, PdfUri = null;
    private AlertDialog dialogAddURL;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private StorageReference mStorageRef;
    private DatabaseReference mRef;

    private static final int IMAGE_REQUEST_CODE = 100;
    private static final int FILE_REQUEST_CODE = 101;
    private String category, collageId;
    ArrayList<String> usersToken;

    Animation rotate_froward,rotate_backward,fab_image_open,fab_image_close,fab_url_open,fab_url_close,fab_pdf_open,fab_pdf_close;
    boolean clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_news);

        initialization();
    }



    //-----------------------------------------------------------------------------initialization
    private void initialization() {

        category = getIntent().getStringExtra("news_category");
        if (category.equals("general")) {
            Log.d("ADMIN", "general");
        } else if (category.equals("collage")) {
            collageId = getIntent().getStringExtra("collage_id");
            Log.d("ADMIN", "collage id = " + collageId);
        }
        getAllUser();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        binding.txtDateAndTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy h:mm a", Locale.getDefault()).format(new Date()));

        binding.addFab.setOnClickListener(this);
        binding.imageFab.setOnClickListener(this);
        binding.pdfFab.setOnClickListener(this);
        binding.urlFab.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.removeImage.setOnClickListener(this);
        binding.removePdf.setOnClickListener(this);
        binding.removeWebURL.setOnClickListener(this);

        String locale = CreateNewsActivity.this.getResources().getConfiguration().locale.getDisplayName();
        if (locale.equals("Arabic") || locale.equals("العربية")) {
            fab_pdf_close = AnimationUtils.loadAnimation(this, R.anim.fab_pdf_close_translate_arabic);
            fab_url_close = AnimationUtils.loadAnimation(this, R.anim.fab_url_close_translate_arabic);
            fab_url_open = AnimationUtils.loadAnimation(this, R.anim.fab_url_open_translate_arabic);
            fab_pdf_open = AnimationUtils.loadAnimation(this, R.anim.fab_pdf_open_translate_arabic);
        } else {
            fab_pdf_close = AnimationUtils.loadAnimation(this, R.anim.fab_pdf_close_translate);
            fab_url_close = AnimationUtils.loadAnimation(this, R.anim.fab_url_close_translate);
            fab_url_open = AnimationUtils.loadAnimation(this, R.anim.fab_url_open_translate);
            fab_pdf_open = AnimationUtils.loadAnimation(this, R.anim.fab_pdf_open_translate);
        }
        fab_image_close = AnimationUtils.loadAnimation(this, R.anim.fab_image_close_translate);
        fab_image_open = AnimationUtils.loadAnimation(this, R.anim.fab_image_open_translate);
        rotate_froward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        clicked=false;
    }


    //-----------------------------------------------------------------------ButtonsOnClick--------
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave : {
                Validation validation = new Validation(getResources());
                if (!isConnect()
                        | !validation.validateNewsText(binding.txtTitle)
                        | !validation.validateNewsText(binding.txtDescription))
                    return;
                binding.btnSave.setFocusable(true);
                binding.btnSave.requestFocus();
                UploadNewsData();
            }
            break;
            case R.id.btnBack : {
                Intent intent = new Intent(CreateNewsActivity.this, MainActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.add_fab : {
                onAddBtnClick();
            }
            break;
            case R.id.image_fab : {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNewsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
                onAddBtnClick();
            }
            break;
            case R.id.pdf_fab : {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNewsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), FILE_REQUEST_CODE);
                }
                onAddBtnClick();
            }
            break;
            case R.id.url_fab : {
                showAddURLDialog();
                onAddBtnClick();
            }
            break;
            case R.id.removeImage : {
                ImgUri = null;
                binding.imageNews.setImageURI(null);
                binding.imageNews.setVisibility(View.GONE);
                binding.removeImage.setVisibility(View.GONE);
            }
            break;
            case R.id.removePdf : {
                PdfUri = null;
                binding.textPdf.setText(null);
                binding.layoutPdf.setVisibility(View.GONE);
            }
            break;
            case R.id.removeWebURL :{
                binding.textWebURL.setText(null);
                binding.layoutWebURL.setVisibility(View.GONE);
            }
            break;
        }
    }

    //--------------------------------------------------------------------URL Dialog---------------
    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewsActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url, findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);
            dialogAddURL = builder.create();

            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL = view.findViewById(R.id.inputUrl);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (inputURL.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CreateNewsActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(CreateNewsActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.textWebURL.setText(inputURL.getText().toString());
                        binding.layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddURL.dismiss();
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddURL.dismiss();
                }
            });
        }

        dialogAddURL.show();
    }

    //--------------------------------------------------------------------result ------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            ImgUri = null;
            if (data != null) {
                ImgUri = data.getData();
            }
            binding.imageNews.setImageURI(ImgUri);
            binding.imageNews.setVisibility(View.VISIBLE);
            binding.removeImage.setVisibility(View.VISIBLE);
        }
        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            PdfUri = null;
            String pdfFileName = null;
            if (data != null) {
                PdfUri = data.getData();
                String uriString = PdfUri.toString();
                File myFile = new File(uriString);

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(PdfUri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            pdfFileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    pdfFileName = myFile.getName();
                }
            }

            String ss = PdfUri.getPath();
            binding.textPdf.setText(pdfFileName);
            binding.layoutPdf.setVisibility(View.VISIBLE);
            binding.removePdf.setVisibility(View.VISIBLE);
        }
    }

    public void UploadNewsData() {
        String currentTime = getCurrentTime();
        if (category.equals("general")) {
            mRef = database.getReference("news").child("general");
        } else {
            mRef = database.getReference("news").child(collageId);
        }
        DatabaseReference newsRef = mRef.push();
        String newsId = newsRef.getKey();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        if (ImgUri == null && PdfUri == null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Data Upload");
            progressDialog.setCancelable(false);
            progressDialog.show();
            String title = binding.txtTitle.getText().toString().trim();
            String desc = binding.txtDescription.getText().toString().trim();
            String link = (!binding.textWebURL.getText().equals(null)) ? binding.textWebURL.getText().toString().trim() : "null";
            NewsModel newsModel = new NewsModel(
                    newsId, title, currentTime,
                    desc, "null", "null",
                    link, (category.equals("general") ? category : collageId));
            mRef.child(newsId).setValue(newsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "data uploaded");
                        progressDialog.dismiss();
                        sendNotificationForAllUsers();
                        showDoneAnim();
                    } else {
                        Log.d(TAG, "data NOT uploaded");
                        progressDialog.dismiss();
                    }
                }
            });
        } else if (ImgUri != null && PdfUri == null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Data Upload");
            progressDialog.setCancelable(false);
            progressDialog.show();
            StorageReference imgRef = mStorageRef.child("news_images").child(currentTime + ".jpg");
            imgRef.putFile(ImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "image uploaded");
                                String imageLink = task.getResult().toString();
                                String title = binding.txtTitle.getText().toString().trim();
                                String desc = binding.txtDescription.getText().toString().trim();
                                String link = (!binding.textWebURL.getText().equals(null)) ? binding.textWebURL.getText().toString().trim() : "null";
                                NewsModel newsModel = new NewsModel(
                                        newsId, title, currentTime,
                                        desc, imageLink, "null",
                                        link, (category.equals("general") ? category : collageId));
                                mRef.child(newsId).setValue(newsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "data uploaded");
                                            progressDialog.dismiss();
                                            sendNotificationForAllUsers();
                                            showDoneAnim();
                                        } else {
                                            Log.d(TAG, "data NOT uploaded");
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "image NOT uploaded");
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage((int) progress + "% of data uploaded.");
                }
            });

        } else if (ImgUri == null && PdfUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Data Upload");
            progressDialog.setCancelable(false);
            progressDialog.show();
            StorageReference pdfRef = mStorageRef.child("news_pdfs").child(currentTime + ".pdf");
            pdfRef.putFile(PdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "pdf uploaded");
                                String pdfLink = task.getResult().toString();
                                String title = binding.txtTitle.getText().toString().trim();
                                String desc = binding.txtDescription.getText().toString().trim();
                                String link = (!binding.textWebURL.getText().equals(null)) ? binding.textWebURL.getText().toString().trim() : "null";
                                NewsModel newsModel = new NewsModel(
                                        newsId, title, currentTime,
                                        desc, "null", pdfLink,
                                        link, (category.equals("general") ? category : collageId));
                                mRef.child(newsId).setValue(newsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "data uploaded");
                                            progressDialog.dismiss();
                                            sendNotificationForAllUsers();
                                            showDoneAnim();
                                        } else {
                                            Log.d(TAG, "data NOT uploaded");
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "pdf NOT uploaded");
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage((int) progress + "% of data uploaded.");
                }
            });
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Data Upload");
            progressDialog.setCancelable(false);
            progressDialog.show();
            StorageReference imgRef = mStorageRef.child("news_images").child(currentTime + ".jpg");
            imgRef.putFile(ImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "image uploaded");
                                String imageLink = task.getResult().toString();
                                StorageReference pdfRef = mStorageRef.child("news_pdfs").child(currentTime + ".pdf");
                                pdfRef.putFile(PdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "pdf uploaded");
                                                    String pdfLink = task.getResult().toString();
                                                    String title = binding.txtTitle.getText().toString().trim();
                                                    String desc = binding.txtDescription.getText().toString().trim();
                                                    String link = (!binding.textWebURL.getText().equals(null)) ? binding.textWebURL.getText().toString().trim() : "null";
                                                    NewsModel newsModel = new NewsModel(
                                                            newsId, title, currentTime,
                                                            desc, imageLink, pdfLink,
                                                            link, (category.equals("general") ? category : collageId));
                                                    mRef.child(newsId).setValue(newsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "data uploaded");
                                                                progressDialog.dismiss();
                                                                sendNotificationForAllUsers();
                                                                showDoneAnim();
                                                            } else {
                                                                Log.d(TAG, "data NOT uploaded");
                                                                progressDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Log.d(TAG, "pdf NOT uploaded");
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                });

                            } else {
                                Log.d(TAG, "image NOT uploaded");
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage((int) progress + "% of data uploaded.");
                }
            });
        }
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    public boolean isConnect() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Log.d("notification_tracker", "Field");
                    } else if (response.body().success == 1) {
                        Log.d("notification_tracker", "Success");
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    public void getAllUser() {
        usersToken = new ArrayList<>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (category.equals("general")) {
            database.getReference("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            UserModel userModel = userSnapshot.getValue(UserModel.class);
                            if (!currentUser.getEmail().equals(userModel.getEmail())) {
                                usersToken.add(userModel.getDevice_token());
                            }
                        }
                        Log.d("GET_USERS", "getAllUser: "+usersToken.size());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            database.getReference("users").orderByChild("collageId").equalTo(collageId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    UserModel userModel = userSnapshot.getValue(UserModel.class);
                                    if (!currentUser.getEmail().equals(userModel.getEmail())) {
                                        usersToken.add(userModel.getDevice_token());
                                    }
                                }
                                Log.d("GET_USERS", "getAllUser: "+usersToken.size());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

    }

    public void sendNotificationForAllUsers(){
        for(String userToken : usersToken){
            sendNotifications(userToken, "BAU News", category);
        }
    }

    public void showDoneAnim(){
        hideKeyboard(binding.btnSave);
        binding.rootLayout.animate().scaleX(0).scaleY(0).setDuration(250);
        binding.doneAnim.setVisibility(View.VISIBLE);
        binding.doneAnim.playAnimation();
        binding.doneAnim.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 150 + binding.doneAnim.getDuration());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public  void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }
    //------------------------------------------------------------methods to set fabs animations----

    private void onAddBtnClick() {
        setVisibility(clicked);
        setAnimation(clicked);
        clicked=!clicked;
    }
    private void setAnimation(boolean b) {
        if(!b){
            binding.imageFab.startAnimation(fab_image_open);
            binding.pdfFab.startAnimation(fab_pdf_open);
            binding.urlFab.startAnimation(fab_url_open);
            binding.addFab.startAnimation(rotate_froward);
            binding.addFab.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDelete)));
        }else {
            binding.imageFab.startAnimation(fab_image_close);
            binding.pdfFab.startAnimation(fab_pdf_close);
            binding.urlFab.startAnimation(fab_url_close);
            binding.addFab.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainColor)));
            binding.addFab.startAnimation(rotate_backward);
        }
    }
    private void setVisibility(boolean b) {
        if(!b){
            binding.imageFab.setVisibility(View.VISIBLE);
            binding.pdfFab.setVisibility(View.VISIBLE);
            binding.urlFab.setVisibility(View.VISIBLE);
        }else {
            binding.imageFab.setVisibility(View.INVISIBLE);
            binding.pdfFab.setVisibility(View.INVISIBLE);
            binding.urlFab.setVisibility(View.INVISIBLE);
        }
    }
}