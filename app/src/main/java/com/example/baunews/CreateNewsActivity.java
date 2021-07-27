package com.example.baunews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.baunews.HelperClasses.Validation;
import com.example.baunews.Models.NewsModel;
import com.example.baunews.databinding.ActivityCreateNewsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CreateNewsActivity extends AppCompatActivity {

    private static final String TAG = "Upload_Process";
    ActivityCreateNewsBinding binding;

    Uri ImgUri = null, PdfUri = null;
    private AlertDialog dialogAddURL;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private StorageReference mStorageRef;
    private DatabaseReference mRef;

    private static final int IMAGE_REQUEST_CODE = 100;
    private static final int FILE_REQUEST_CODE = 101;
    private String category, collageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_news);

        category = getIntent().getStringExtra("news_category");
        if (category.equals("general")) {
            Log.d("ADMIN", "general");
        } else if (category.equals("collage")) {
            collageId = getIntent().getStringExtra("collage_id");
            Log.d("ADMIN", "collage id = " + collageId);
        }


        AddOthers();
        binding.txtDateAndTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));

        //--------------------------------------------------------------------save button-----------------
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validation validation = new Validation(getResources());
                if (!isConnect()
                        | !validation.validateNewsText(binding.txtTitle)
                        | !validation.validateNewsText(binding.txtDescription))
                    return;
                UploadNewsData();
            }
        });


        //--------------------------------------------------------------------back button-----------------
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateNewsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        binding.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImgUri = null;
                binding.imageNews.setImageURI(null);
                binding.imageNews.setVisibility(View.GONE);
                binding.removeImage.setVisibility(View.GONE);
            }
        });
        binding.removeWebURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.textWebURL.setText(null);
                binding.layoutWebURL.setVisibility(View.GONE);
            }
        });
        binding.removePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PdfUri = null;
                binding.textPdf.setText(null);
                binding.layoutPdf.setVisibility(View.GONE);
            }
        });
    }

    private void AddOthers() {
        final LinearLayout layout = findViewById(R.id.layoutAddOthers);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layout);
        layout.findViewById(R.id.others).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        //--------------------------------------------------------------------add Image------------
        layout.findViewById(R.id.addImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNewsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
            }
        });
        //--------------------------------------------------------------------add Url--------------
        layout.findViewById(R.id.addUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddURLDialog();
            }
        });
        //--------------------------------------------------------------------add Pdf--------------
        layout.findViewById(R.id.addPdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNewsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), FILE_REQUEST_CODE);
                }
            }
        });
    }

    //--------------------------------------------------------------------Dialog-------------------
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
            if (data != null) {
                PdfUri = data.getData();
            }
            String ss = PdfUri.getPath();
            binding.textPdf.setText(ss);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
}