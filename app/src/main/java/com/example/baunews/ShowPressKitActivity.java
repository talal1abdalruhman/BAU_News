package com.example.baunews;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.baunews.Models.NewsModel;
import com.example.baunews.Models.PressKitModel;
import com.example.baunews.databinding.ActivityShowPressKitBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ShowPressKitActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityShowPressKitBinding binding;


    Animation rotate_froward, rotate_backward, fab_image_open, fab_image_close, fab_url_open, fab_url_close, fab_pdf_open, fab_pdf_close, fab_share_open, fab_share_close;
    boolean clicked;

    private static final int IMAGE_REQUEST_CODE = 100;
    private static final int FILE_REQUEST_CODE = 101;
    String newsKey, PdfUrl;
    PressKitModel pressKitModel;
    Uri ImgUri = null, PdfUri = null;
    boolean isImgEdited = false, isPdfEdited = false, isUrlEdited = false, isOnEdit = false;
    ProgressDialog progressDialog;
    private AlertDialog dialogAddURL, dialogAddResource, dialogShare;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_press_kit);
        setAppLocale();
        initialization();
        ShowThePressKit();
    }

    private void initialization() {
        progressDialog = new ProgressDialog(this);
        binding.addFab.hide();
        binding.imageFab.setOnClickListener(this);
        binding.pdfFab.setOnClickListener(this);
        binding.urlFab.setOnClickListener(this);
        binding.removeImage.setOnClickListener(this);
        binding.removePdf.setOnClickListener(this);
        binding.removeWebURL.setOnClickListener(this);
        binding.btnAddResource.setOnClickListener(this);
        binding.removeTxtResource.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);

        String locale = ShowPressKitActivity.this.getResources().getConfiguration().locale.getDisplayName();
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
        fab_share_open = AnimationUtils.loadAnimation(this, R.anim.fab_image_close_translate);
        fab_share_close = AnimationUtils.loadAnimation(this, R.anim.fab_image_open_translate);
        fab_image_close = AnimationUtils.loadAnimation(this, R.anim.fab_image_close_translate);
        fab_image_open = AnimationUtils.loadAnimation(this, R.anim.fab_image_open_translate);
        rotate_froward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        clicked = false;
        binding.addFab.setVisibility(View.GONE);
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
            binding.imageFab.show();
            binding.pdfFab.show();
            binding.urlFab.show();
        } else {
            binding.imageFab.hide();
            binding.pdfFab.hide();
            binding.urlFab.hide();
        }
    }

    //---------------------------------------------------------------------URL Dialog--------------
    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowPressKitActivity.this);
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
                        Toast.makeText(ShowPressKitActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(ShowPressKitActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        isUrlEdited = true;
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

    //---------------------------------------------------------------------Resource Dialog--------------
    private void showAddResourceDialog() {
        if (dialogAddResource == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowPressKitActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url, findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);
            dialogAddResource = builder.create();

            if (dialogAddResource.getWindow() != null) {
                dialogAddResource.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL = view.findViewById(R.id.inputUrl);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (inputURL.getText().toString().trim().isEmpty()) {
                        Toast.makeText(ShowPressKitActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(ShowPressKitActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.txtResource.setText(inputURL.getText().toString());
                        binding.layoutResourceTxt.setVisibility(View.VISIBLE);
                        dialogAddResource.dismiss();
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddResource.dismiss();
                }
            });
        }

        dialogAddResource.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fab: {
                onAddBtnClick();
            }
            break;
            case R.id.image_fab: {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShowPressKitActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
                onAddBtnClick();
            }
            break;
            case R.id.pdf_fab: {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShowPressKitActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), FILE_REQUEST_CODE);
                }
                onAddBtnClick();
            }
            break;
            case R.id.url_fab: {
                showAddURLDialog();
                onAddBtnClick();
            }
            break;
            case R.id.removeImage: {
                ImgUri = null;
                isImgEdited = true;
                binding.imagePress.setImageURI(null);
                binding.imagePress.setVisibility(View.GONE);
                binding.removeImage.setVisibility(View.GONE);
            }
            break;
            case R.id.removePdf: {
                PdfUri = null;
                isPdfEdited = true;
                binding.textPdf.setText(null);
                binding.layoutPdf.setVisibility(View.GONE);
            }
            break;
            case R.id.removeWebURL: {
                isUrlEdited = true;
                binding.textWebURL.setText(null);
                binding.layoutWebURL.setVisibility(View.GONE);
            }
            break;
            case R.id.btn_add_resource: {
                showAddResourceDialog();
            }
            break;
            case R.id.removeTxtResource: {
                binding.txtResource.setText(null);
                binding.layoutResourceTxt.setVisibility(view.GONE);
            }
            break;
            case R.id.btnSave: {
                if (!isConnect()) return;
                SavePressKitUpdates();
            }
            break;
        }
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
            binding.imagePress.setImageURI(ImgUri);
            binding.imagePress.setVisibility(View.VISIBLE);
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

    public boolean isConnect() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void ShowThePressKit() {
        newsKey = getIntent().getStringExtra("news_id");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("press_kit").child(newsKey)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            pressKitModel = snapshot.getValue(PressKitModel.class);
                            binding.txtTitle.setText(pressKitModel.getTitle());
                            binding.txtDateAndTime.setText(pressKitModel.getDate());
                            binding.txtDescription.setText(pressKitModel.getDescription());
                            if (!pressKitModel.getImage().equals("null")) {
                                binding.imagePress.setVisibility(View.VISIBLE);
                                Glide.with(getApplicationContext())
                                        .load(pressKitModel.getImage())
                                        .into(binding.imagePress);
                            } else {
                                binding.imagePress.setVisibility(View.GONE);
                            }
                            if (!pressKitModel.getPdf().equals("null")) {
                                PdfUrl = pressKitModel.getPdf();
                                binding.layoutPdf.setVisibility(View.VISIBLE);
                            } else {
                                binding.layoutPdf.setVisibility(View.GONE);
                            }
                            if (!pressKitModel.getUrl().equals("")) {
                                binding.layoutWebURL.setVisibility(View.VISIBLE);
                                binding.textWebURL.setText(pressKitModel.getUrl());
                            } else {
                                binding.layoutWebURL.setVisibility(View.GONE);
                            }
                            binding.resourceName.setText(pressKitModel.getResourceName());
                            if (!pressKitModel.getResourceLink().equals("")) {
                                binding.layoutResourceTxt.setVisibility(View.VISIBLE);
                                binding.txtResource.setText(pressKitModel.getResourceLink());
                            } else {
                                binding.layoutResourceTxt.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void UpdatePressKit(View view) {
        isOnEdit = true;
        binding.relative.setVisibility(View.VISIBLE);
        //    binding.includeOthers.setVisibility(View.VISIBLE);
        binding.addFab.show();
        if (!pressKitModel.getImage().equals("null")) {
            binding.removeImage.setVisibility(View.VISIBLE);
        }
        if (!pressKitModel.getPdf().equals("null")) {
            binding.removePdf.setVisibility(View.VISIBLE);
        }
        if (!pressKitModel.getUrl().equals("")) {
            binding.removeWebURL.setVisibility(View.VISIBLE);
        }
        binding.removeTxtResource.setVisibility(View.VISIBLE);
        binding.resourceName.setEnabled(true);
        binding.txtTitle.setEnabled(true);
        binding.txtDescription.setEnabled(true);
        binding.btnAddResource.setVisibility(View.VISIBLE);
        binding.updateBtn.setVisibility(View.GONE);
        binding.deleteBtn.setVisibility(View.GONE);
        binding.shareBtn.setVisibility(View.GONE);
    }

    public void CancelEdit(View view) {
        isOnEdit = false;
        isUrlEdited = isImgEdited = isPdfEdited = false;
        binding.relative.setVisibility(View.GONE);
        binding.addFab.hide();
        binding.removeImage.setVisibility(View.GONE);
        binding.removePdf.setVisibility(View.GONE);
        binding.removeWebURL.setVisibility(View.GONE);
        binding.removeTxtResource.setVisibility(View.GONE);
        binding.btnAddResource.setVisibility(View.GONE);
        binding.txtTitle.setEnabled(false);
        binding.txtDescription.setEnabled(false);
        binding.resourceName.setEnabled(false);
        binding.updateBtn.setVisibility(View.VISIBLE);
        binding.deleteBtn.setVisibility(View.VISIBLE);
        binding.shareBtn.setVisibility(View.VISIBLE);
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

    private void SavePressKitUpdates() {
        progressDialog.setTitle("News Update..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mRef = database.getReference("press_kit").child(newsKey);

        if (isPdfEdited && PdfUri != null) {
            mStorageRef = storage.getInstance().getReference().child("pdfs").child(pressKitModel.getDate() + ".pdf");
            if (!pressKitModel.getPdf().equals("null")) {
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
                                                pressKitModel.setPdf(pdfLink);
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
                                    pressKitModel.setPdf(pdfLink);
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
            if (!pressKitModel.getPdf().equals("null")) {
                mStorageRef = storage.getInstance().getReference().child("pdfs").child(pressKitModel.getDate() + ".pdf");
                mStorageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DATA_UPDATE", "pdf deleted");
                            pressKitModel.setPdf("null");
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
            mStorageRef = storage.getInstance().getReference().child("images").child(pressKitModel.getDate() + ".jpg");
            if (!pressKitModel.getImage().equals("null")) {
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
                                                pressKitModel.setImage(imageLink);
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
                                    pressKitModel.setImage(imageLink);
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
            if (!pressKitModel.getImage().equals("null")) {
                mStorageRef = storage.getInstance().getReference().child("images").child(pressKitModel.getDate() + ".jpg");
                mStorageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DATA_UPDATE", "img deleted");
                            pressKitModel.setImage("null");
                            CheckTextStatus();
                        } else {
                            Log.d("DATA_UPDATE", "img NOT deleted");
                            progressDialog.dismiss();
                        }
                    }
                });
            } else {
                pressKitModel.setImage("null");
                CheckTextStatus();
            }

        } else {
            CheckTextStatus();
        }
    }

    public void CheckTextStatus() {
        if (isUrlEdited) {
            String newUrl = binding.textWebURL.getText().toString().trim();
            pressKitModel.setUrl(newUrl);
        }
        pressKitModel.setResourceName(binding.resourceName.getText().toString());
        pressKitModel.setResourceLink(binding.txtResource.getText().toString());
        pressKitModel.setTitle(binding.txtTitle.getText().toString().trim());
        pressKitModel.setDescription(binding.txtDescription.getText().toString().trim());
        mRef.setValue(pressKitModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("DATA_UPDATE", "news updated");
                    progressDialog.dismiss();
                    CancelEdit(new View(ShowPressKitActivity.this));
                } else {
                    Log.d("DATA_UPDATE", "news NOT updated");
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void DeletePressKit(View view) {
        progressDialog.setTitle("News Delete..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mRef = database.getReference("press_kit").child(newsKey);
        if (!pressKitModel.getPdf().equals("null")) {
            mStorageRef = storage.getInstance().getReference().child("pdfs").child(pressKitModel.getDate() + ".pdf");
            mStorageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("DATA_DELETE", "pdf deleted");
                        mStorageRef = storage.getInstance().getReference().child("images").child(pressKitModel.getDate() + ".jpg");
                        if (!pressKitModel.getImage().equals("null")) {
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
            mStorageRef = storage.getInstance().getReference().child("images").child(pressKitModel.getDate() + ".jpg");
            if (!pressKitModel.getImage().equals("null")) {
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

    @Override
    public void onBackPressed() {
        if (isOnEdit) {
            CancelEdit(new View(ShowPressKitActivity.this));
        } else {
            super.onBackPressed();
        }
    }

    public void ShareNews(View view) {
        if (dialogShare == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowPressKitActivity.this);
            View v = LayoutInflater.from(this).inflate(
                    R.layout.layout_share_news_dialog, findViewById(R.id.layout_share_container)
            );
            builder.setView(v);
            dialogShare = builder.create();

            if (dialogShare.getWindow() != null) {
                dialogShare.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
            Spinner spinner = v.findViewById(R.id.spinner);
            spinner.setSelection(0);
            v.findViewById(R.id.share).setOnClickListener(v1 -> {
                String category;
                long id = spinner.getSelectedItemId();
                if (id > 0) {
                    category = (id - 1) + "";
                } else {
                    category = "general";
                }
                shareToCategory(category);
            });

            v.findViewById(R.id.cancel).setOnClickListener(v1 -> {
                dialogShare.dismiss();
            });
        }
        dialogShare.show();
    }

    public void shareToCategory(String category) {
        dialogShare.dismiss();
        ProgressDialog dialog = new ProgressDialog(ShowPressKitActivity.this);
        dialog.setTitle("Sharing news..");
        dialog.setCancelable(false);
        dialog.show();
        Log.d("category_check", "shareToCategory: " + category);
        NewsModel newsModel = new NewsModel();
        newsModel.setTitle(pressKitModel.getTitle());
        newsModel.setDate(getCurrentTime());
        newsModel.setDescription(pressKitModel.getDescription());
        newsModel.setUrl(pressKitModel.getUrl());
        newsModel.setImage(pressKitModel.getImage());
        newsModel.setImageName(pressKitModel.getDate());
        newsModel.setPdf(pressKitModel.getPdf());
        newsModel.setPdfName(pressKitModel.getDate());
        newsModel.setCategory(category);
        newsModel.setResourceName(pressKitModel.getResourceName());
        newsModel.setResourceLink(pressKitModel.getResourceLink());
        mRef = database.getReference("news").child(category);
        DatabaseReference newsRef = mRef.push();
        String newsKey = newsRef.getKey();
        newsModel.setId(newsKey);
        mRef.child(newsKey).setValue(newsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("SHARE_NEWS", "onComplete: SUCCESS");
                    database.getReference("press_kit").child(pressKitModel.getId())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("SHARE_NEWS", "onComplete: value removed");
                                finish();
                            } else {
                                Log.d("SHARE_NEWS", "onComplete: value NOT removed");
                            }
                            dialog.dismiss();
                        }
                    });
                } else {
                    Log.d("SHARE_NEWS", "onComplete: NOT SUCCESS");
                }
            }
        });

    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
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