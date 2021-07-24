package com.example.baunews;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.baunews.databinding.ActivityCreateNewsBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNewsActivity extends AppCompatActivity {

    ActivityCreateNewsBinding binding;

    Uri uri;
    private AlertDialog dialogAddURL;

    private static final int IMAGE_REQUEST_CODE=100;
    private static final int FILE_REQUEST_CODE=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_news);


        AddOthers();
        binding.txtDateAndTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));

        //--------------------------------------------------------------------save button-----------------
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //--------------------------------------------------------------------back button-----------------
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateNewsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });



        binding.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CreateNewsActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},IMAGE_REQUEST_CODE);
                }
                else{
                    Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent,IMAGE_REQUEST_CODE);}
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
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CreateNewsActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},FILE_REQUEST_CODE);
                }
                else{
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), FILE_REQUEST_CODE);
                }
            }
        });
    }
    //--------------------------------------------------------------------Dialog-------------------
    private void showAddURLDialog(){
        if(dialogAddURL == null){
            AlertDialog.Builder builder =new AlertDialog.Builder(CreateNewsActivity.this);
            View view= LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);
            dialogAddURL =builder.create();

            if(dialogAddURL.getWindow() != null){
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL=view.findViewById(R.id.inputUrl);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(inputURL.getText().toString().trim().isEmpty()){
                        Toast.makeText(CreateNewsActivity.this,"Enter URL",Toast.LENGTH_SHORT).show();
                    }
                    else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                        Toast.makeText(CreateNewsActivity.this,"Enter Valid URL",Toast.LENGTH_SHORT).show();
                    }
                    else {
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
        if (requestCode == IMAGE_REQUEST_CODE && resultCode==RESULT_OK){
            uri= null;
            if (data != null) {
                uri = data.getData();
            }
            binding.imageNews.setImageURI(uri);
            binding.imageNews.setVisibility(View.VISIBLE);
            binding.removeImage.setVisibility(View.VISIBLE);
        }
        if(requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK){
            uri= null;
            if (data != null) {
                uri = data.getData();
            }
            String ss=uri.getPath();
            binding.textPdf.setText(ss);
            binding.layoutPdf.setVisibility(View.VISIBLE);
            binding.removePdf.setVisibility(View.VISIBLE);
        }
    }
}