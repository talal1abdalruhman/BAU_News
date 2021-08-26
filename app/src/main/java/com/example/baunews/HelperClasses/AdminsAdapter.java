package com.example.baunews.HelperClasses;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baunews.Models.PressKitModel;
import com.example.baunews.Models.UserModel;
import com.example.baunews.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminsAdapter extends RecyclerView.Adapter<AdminsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<UserModel> adminsList;
    String[] collages;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView email, category;
        ImageView remove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.email);
            category = itemView.findViewById(R.id.category);
            remove = itemView.findViewById(R.id.delete);
        }
    }

    public AdminsAdapter(Context context, ArrayList<UserModel> adminsList) {
        this.context = context;
        this.adminsList = adminsList;
        collages = context.getResources().getStringArray(R.array.colleges_names);
    }

    @NonNull
    @Override
    public AdminsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_admin_layout, parent, false);
        return new AdminsAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull AdminsAdapter.MyViewHolder holder, int position) {
        UserModel admin = adminsList.get(position);
        holder.email.setText(context.getResources().getString(R.string.admin_email) + ": " + admin.getEmail());
        int id = Integer.parseInt(admin.getCollageId());
        String cate;
        if (id != -1) cate = collages[id];
        else cate = context.getResources().getString(R.string.gene);
        holder.category.setText(context.getResources().getString(R.string.admin_category) + " " + cate);

        holder.remove.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.confirme_delete_admin_dialog);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            dialog.findViewById(R.id.txt_remove).setOnClickListener(v1 -> {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference("users").child(admin.getId())
                        .child("admin").setValue("N").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                    }
                });
            });

            dialog.findViewById(R.id.txt_cancel).setOnClickListener(v12 -> {
                dialog.dismiss();
            });

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return adminsList.size();
    }

}
