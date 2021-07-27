package com.example.baunews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.baunews.Models.NewsModel;
import com.example.baunews.databinding.ActivityShowNewsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowNewsActivity extends AppCompatActivity {
    ActivityShowNewsBinding binding;
    String newsKey, newsCategory, PdfUrl, adminType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_news);
        Log.d("NewsKey", getIntent().getStringExtra("news_id"));
        Log.d("NewsKey", getIntent().getStringExtra("category"));
        ShowTheNews();
        isAdmin();
    }

    public void ShowTheNews() {
        newsKey = getIntent().getStringExtra("news_id");
        newsCategory = getIntent().getStringExtra("category");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("news").child(newsCategory).child(newsKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    NewsModel newsModel = snapshot.getValue(NewsModel.class);
                    binding.txtTitle.setText(newsModel.getTitle());
                    binding.txtDateAndTime.setText(newsModel.getDate());
                    binding.txtDescription.setText(newsModel.getDescription());
                    Glide.with(ShowNewsActivity.this)
                            .load((newsModel.getImage().equals("null")) ? R.drawable.bau : newsModel.getImage())
                            .into(binding.imageNews);
                    if (!newsModel.getPdf().equals("null")) {
                        PdfUrl = newsModel.getPdf();
                        binding.pdfImage.setVisibility(View.VISIBLE);
                    }
                    if (!newsModel.getUrl().equals("")) {
                        binding.textWebURL.setVisibility(View.VISIBLE);
                        binding.textWebURL.setText(newsModel.getUrl());
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
        binding.includeOthers.setVisibility(View.VISIBLE);
        binding.removeImage.setVisibility(View.VISIBLE);
        binding.removePdf.setVisibility(View.VISIBLE);
        binding.removeWebURL.setVisibility(View.VISIBLE);
        binding.txtTitle.setEnabled(true);
        binding.txtDescription.setEnabled(true);
    }

    public void CancelEdit(View view) {
        binding.relative.setVisibility(View.GONE);
        binding.includeOthers.setVisibility(View.GONE);
        binding.removeImage.setVisibility(View.GONE);
        binding.removePdf.setVisibility(View.GONE);
        binding.removeWebURL.setVisibility(View.GONE);
        binding.txtTitle.setEnabled(false);
        binding.txtDescription.setEnabled(false);
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
}