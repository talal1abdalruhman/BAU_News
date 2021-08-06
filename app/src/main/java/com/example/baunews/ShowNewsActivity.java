package com.example.baunews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
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
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.baunews.HelperClasses.Validation;
import com.example.baunews.Models.NewsModel;
import com.example.baunews.databinding.ActivityShowNewsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class ShowNewsActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST_CODE = 100;
    private static final int FILE_REQUEST_CODE = 101;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private DatabaseReference mRef;
    ActivityShowNewsBinding binding;
    String newsKey, newsCategory, PdfUrl, adminType;
    LinearLayout layout;
    NewsModel newsModel;
    AlertDialog dialogAddURL;
    Uri ImgUri = null, PdfUri = null;
    boolean isImgEdited = false, isPdfEdited = false, isUrlEdited = false;
    ProgressDialog progressDialog;
    Animation rotate_froward, rotate_backward, fab_image_open, fab_image_close, fab_url_open, fab_url_close, fab_pdf_open, fab_pdf_close;
    boolean clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_news);
        binding.addFab.setVisibility(View.GONE);
        Log.d("NewsKey", getIntent().getStringExtra("news_id"));
        Log.d("NewsKey", getIntent().getStringExtra("category"));
        progressDialog = new ProgressDialog(this);
        ShowTheNews();
        isAdmin();

        rotate_froward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        fab_image_open = AnimationUtils.loadAnimation(this, R.anim.fab_image_open_translate);
        fab_image_close = AnimationUtils.loadAnimation(this, R.anim.fab_image_close_translate);
        fab_url_open = AnimationUtils.loadAnimation(this, R.anim.fab_url_open_translate);
        fab_url_close = AnimationUtils.loadAnimation(this, R.anim.fab_url_close_translate);
        fab_pdf_open = AnimationUtils.loadAnimation(this, R.anim.fab_pdf_open_translate);
        fab_pdf_close = AnimationUtils.loadAnimation(this, R.anim.fab_pdf_close_translate);

        clicked = false;

        binding.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddBtnClick();
            }
        });
        binding.imageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShowNewsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
                onAddBtnClick();
            }
        });
        binding.pdfFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShowNewsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), FILE_REQUEST_CODE);
                }
                onAddBtnClick();
            }
        });
        binding.urlFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddURLDialog();
                onAddBtnClick();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validation validation = new Validation(getResources());
                if (!isConnect()
                        | !validation.validateNewsText(binding.txtTitle)
                        | !validation.validateNewsText(binding.txtDescription))
                    return;
                SaveNewsUpdates();
            }
        });

        binding.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImgUri = null;
                isImgEdited = true;
                binding.imageNews.setImageURI(null);
                binding.imageNews.setVisibility(View.GONE);
                binding.removeImage.setVisibility(View.GONE);
            }
        });

        binding.removePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPdfEdited = true;
                PdfUri = null;
                binding.layoutPdf.setVisibility(View.GONE);
            }
        });

        binding.removeWebURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUrlEdited = true;
                binding.textWebURL.setText(null);
                binding.layoutWebURL.setVisibility(View.GONE);
            }
        });
    }
    //------------------------------------------------------------methods to set fabs animations----

    private void onAddBtnClick() {
        setVisibility(clicked);
        setAnimation(clicked);
        clicked = !clicked;
    }

    private void setAnimation(boolean b) {
        if (!b) {
            binding.imageFab.startAnimation(fab_image_open);
            binding.pdfFab.startAnimation(fab_pdf_open);
            binding.urlFab.startAnimation(fab_url_open);
            binding.addFab.startAnimation(rotate_froward);
            binding.addFab.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDelete)));
        } else {
            binding.imageFab.startAnimation(fab_image_close);
            binding.pdfFab.startAnimation(fab_pdf_close);
            binding.urlFab.startAnimation(fab_url_close);
            binding.addFab.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainColor)));
            binding.addFab.startAnimation(rotate_backward);
        }
    }

    private void setVisibility(boolean b) {
        if (!b) {
            binding.imageFab.setVisibility(View.VISIBLE);
            binding.pdfFab.setVisibility(View.VISIBLE);
            binding.urlFab.setVisibility(View.VISIBLE);
        } else {
            binding.imageFab.setVisibility(View.INVISIBLE);
            binding.pdfFab.setVisibility(View.INVISIBLE);
            binding.urlFab.setVisibility(View.INVISIBLE);
        }
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

    public void ShowTheNews() {
        newsKey = getIntent().getStringExtra("news_id");
        newsCategory = getIntent().getStringExtra("category");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("news").child(newsCategory)
                .child(newsKey).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    newsModel = snapshot.getValue(NewsModel.class);
                    binding.txtTitle.setText(newsModel.getTitle());
                    binding.txtDateAndTime.setText(newsModel.getDate());
                    binding.txtDescription.setText(newsModel.getDescription());
                    if (!newsModel.getImage().equals("null")) {
                        Glide.with(ShowNewsActivity.this)
                                .load(newsModel.getImage())
                                .into(binding.imageNews);
                    } else {
                        binding.imageNews.setVisibility(View.GONE);
                    }
                    if (!newsModel.getPdf().equals("null")) {
                        PdfUrl = newsModel.getPdf();
                        binding.layoutPdf.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutPdf.setVisibility(View.GONE);
                    }
                    if (!newsModel.getUrl().equals("")) {
                        binding.layoutWebURL.setVisibility(View.VISIBLE);
                        binding.textWebURL.setText(newsModel.getUrl());
                    } else {
                        binding.textWebURL.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void UpdateNews(View view) {
        binding.relative.setVisibility(View.VISIBLE);
        //    binding.includeOthers.setVisibility(View.VISIBLE);
        binding.addFab.setVisibility(View.VISIBLE);
        if (!newsModel.getImage().equals("null")) {
            binding.removeImage.setVisibility(View.VISIBLE);
        }
        if (!newsModel.getPdf().equals("null")) {
            binding.removePdf.setVisibility(View.VISIBLE);
        }
        if (!newsModel.getUrl().equals("")) {
            binding.removeWebURL.setVisibility(View.VISIBLE);
        }
        binding.txtTitle.setEnabled(true);
        binding.txtDescription.setEnabled(true);
        binding.updateBtn.setVisibility(View.GONE);
        binding.deleteBtn.setVisibility(View.GONE);
    }

    public void CancelEdit(View view) {
        binding.relative.setVisibility(View.GONE);
        binding.addFab.setVisibility(View.GONE);
        binding.removeImage.setVisibility(View.GONE);
        binding.removePdf.setVisibility(View.GONE);
        binding.removeWebURL.setVisibility(View.GONE);
        binding.txtTitle.setEnabled(false);
        binding.txtDescription.setEnabled(false);
        binding.updateBtn.setVisibility(View.VISIBLE);
        binding.deleteBtn.setVisibility(View.VISIBLE);
    }

    public void OpenPdfFile(View view) {
        Intent intent = new Intent(this, PdfViewerActivity.class);
        intent.putExtra("pdf_link", PdfUrl);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                binding.layoutPdf,
                ViewCompat.getTransitionName(binding.layoutPdf));
        startActivity(intent, optionsCompat.toBundle());
    }

    public void isAdmin() {
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users").child(userId).child("admin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            adminType = snapshot.getValue(String.class);
                            if (adminType.equals("G") && newsCategory.equals("general")) {
                                binding.updateBtn.setVisibility(View.VISIBLE);
                                binding.deleteBtn.setVisibility(View.VISIBLE);
                            } else if (adminType.equals("C") && !newsCategory.equals("general")) {
                                binding.updateBtn.setVisibility(View.VISIBLE);
                                binding.deleteBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowNewsActivity.this);
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
                        Toast.makeText(ShowNewsActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(ShowNewsActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            ImgUri = null;
            if (data != null) {
                ImgUri = data.getData();
            }
            isImgEdited = true;
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
            isPdfEdited = true;
            String ss = PdfUri.getPath();
            binding.textPdf.setText(pdfFileName);
            binding.layoutPdf.setVisibility(View.VISIBLE);
            binding.removePdf.setVisibility(View.VISIBLE);
        }
    }

    private void SaveNewsUpdates() {
        progressDialog.setTitle("News Update..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mRef = database.getReference("news").child(newsCategory).child(newsKey);

        if (isPdfEdited && PdfUri != null) {
            mStorageRef = storage.getInstance().getReference().child("news_pdfs").child(newsModel.getDate() + ".pdf");
            if (!newsModel.getPdf().equals("null")) {
                mStorageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DATA_UPDATE", "pdf deleted");
                            mStorageRef.putFile(PdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("DATA_UPDATE", "pdf uploaded");
                                                String pdfLink = task.getResult().toString();
                                                newsModel.setPdf(pdfLink);
                                                CheckImageStatus();
                                            } else {
                                                Log.d("DATA_UPDATE", "pdf NOT uploaded");
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            Log.d("DATA_UPDATE", "pdf NOT deleted");
                            progressDialog.dismiss();
                        }
                    }
                });
            } else {
                mStorageRef.putFile(PdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Log.d("DATA_UPDATE", "pdf uploaded");
                                    String pdfLink = task.getResult().toString();
                                    newsModel.setPdf(pdfLink);
                                    CheckImageStatus();
                                } else {
                                    progressDialog.dismiss();
                                    Log.d("DATA_UPDATE", "pdf NOT uploaded");
                                }
                            }
                        });
                    }
                });
            }
        } else if (isPdfEdited && PdfUri == null) {
            if (!newsModel.getPdf().equals("null")) {
                mStorageRef = storage.getInstance().getReference().child("news_pdfs").child(newsModel.getDate() + ".pdf");
                mStorageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DATA_UPDATE", "pdf deleted");
                            newsModel.setPdf("null");
                            CheckImageStatus();
                        } else {
                            Log.d("DATA_UPDATE", "pdf NOT deleted");
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        } else {
            CheckImageStatus();
        }
    }

    public void CheckImageStatus() {
        if (isImgEdited && ImgUri != null) {
            mStorageRef = storage.getInstance().getReference().child("news_images").child(newsModel.getDate() + ".jpg");
            if (!newsModel.getImage().equals("null")) {
                mStorageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DATA_UPDATE", "img deleted");
                            mStorageRef.putFile(ImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("DATA_UPDATE", "image uploaded");
                                                String imageLink = task.getResult().toString();
                                                newsModel.setImage(imageLink);
                                                CheckTextStatus();
                                            } else {
                                                Log.d("DATA_UPDATE", "image NOT uploaded");
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            Log.d("DATA_UPDATE", "img NOT deleted");
                            progressDialog.dismiss();
                        }
                    }
                });
            } else {
                mStorageRef.putFile(ImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Log.d("DATA_UPDATE", "image uploaded");
                                    String imageLink = task.getResult().toString();
                                    newsModel.setImage(imageLink);
                                    CheckTextStatus();
                                } else {
                                    Log.d("DATA_UPDATE", "image NOT uploaded");
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                });
            }
        } else if (isImgEdited && ImgUri == null) {
            if (!newsModel.getImage().equals("null")) {
                mStorageRef = storage.getInstance().getReference().child("news_images").child(newsModel.getDate() + ".jpg");
                mStorageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DATA_UPDATE", "img deleted");
                            newsModel.setImage("null");
                            CheckTextStatus();
                        } else {
                            Log.d("DATA_UPDATE", "img NOT deleted");
                            progressDialog.dismiss();
                        }
                    }
                });
            } else {
                newsModel.setImage("null");
                CheckTextStatus();
            }

        } else {
            CheckTextStatus();
        }
    }

    public void CheckTextStatus() {
        if (isUrlEdited) {
            String newUrl = binding.textWebURL.getText().toString().trim();
            newsModel.setUrl(newUrl);
        }
        newsModel.setTitle(binding.txtTitle.getText().toString().trim());
        newsModel.setDescription(binding.txtDescription.getText().toString().trim());
        mRef.setValue(newsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("DATA_UPDATE", "news updated");
                    progressDialog.dismiss();
                    CancelEdit(new View(ShowNewsActivity.this));
                } else {
                    Log.d("DATA_UPDATE", "news NOT updated");
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void DeleteNews(View view) {
        progressDialog.setTitle("News Delete..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mRef = database.getReference("news").child(newsCategory).child(newsKey);
        if (!newsModel.getPdf().equals("null")) {
            mStorageRef = storage.getInstance().getReference().child("news_pdfs").child(newsModel.getDate() + ".pdf");
            mStorageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("DATA_DELETE", "pdf deleted");
                        mStorageRef = storage.getInstance().getReference().child("news_images").child(newsModel.getDate() + ".jpg");
                        if (!newsModel.getImage().equals("null")) {
                            mStorageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("DATA_DELETE", "img deleted");
                                        mRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("DATA_DELETE", "data deleted");
                                                    progressDialog.dismiss();
                                                    finish();
                                                } else {
                                                    Log.d("DATA_DELETE", "data Not deleted");
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    } else {
                                        Log.d("DATA_DELETE", "img NOT deleted");
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            mRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("DATA_DELETE", "data deleted");
                                        progressDialog.dismiss();
                                        finish();
                                    } else {
                                        Log.d("DATA_DELETE", "data Not deleted");
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }

                    } else {
                        Log.d("DATA_DELETE", "pdf Not deleted");
                        progressDialog.dismiss();
                    }
                }
            });
        } else {
            mStorageRef = storage.getInstance().getReference().child("news_images").child(newsModel.getDate() + ".jpg");
            if (!newsModel.getImage().equals("null")) {
                mStorageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DATA_DELETE", "img deleted");
                            mRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("DATA_DELETE", "data deleted");
                                        progressDialog.dismiss();
                                        finish();
                                    } else {
                                        Log.d("DATA_DELETE", "data Not deleted");
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            Log.d("DATA_DELETE", "img NOT deleted");
                            progressDialog.dismiss();
                        }
                    }
                });
            } else {
                mRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DATA_DELETE", "data deleted");
                            progressDialog.dismiss();
                            finish();
                        } else {
                            Log.d("DATA_DELETE", "data Not deleted");
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
    }
}