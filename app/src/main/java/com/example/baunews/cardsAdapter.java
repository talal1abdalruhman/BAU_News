package com.example.baunews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class cardsAdapter extends RecyclerView.Adapter<com.example.baunews.cardsAdapter.MyViewHolder>{
    private Context context;
    private List<cardsModel> cardsLists;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title,date,description;
        public ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.news_title);
            date = itemView.findViewById(R.id.news_date);
            image = itemView.findViewById(R.id.news_img);
            description = itemView.findViewById(R.id.news_description);
        }
    }

    public cardsAdapter(Context context, List<cardsModel> cardsLists) {
        this.context = context;
        this.cardsLists = cardsLists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_card,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        cardsModel cardsModel = cardsLists.get(position);
        holder.title.setText(cardsModel.getTitle());
        holder.date.setText(cardsModel.getDate());
        Glide.with(context)
                .load(cardsModel.getImage())
                .into(holder.image);
        holder.description.setText(cardsModel.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"You clicked: "+cardsModel.getTitle(),Toast.LENGTH_SHORT).show();
                Bitmap bitmap = holder.image.getDrawingCache();
                Intent intent = new Intent(v.getContext(),ShowNewsActivity.class);
                intent.putExtra("title",holder.title.getText());
                intent.putExtra("date",holder.date.getText());
                intent.putExtra("image",bitmap);
                intent.putExtra("description",holder.description.getText());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardsLists.size();
    }
}