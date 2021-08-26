package com.example.baunews.HelperClasses;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baunews.Models.PressKitModel;
import com.example.baunews.R;
import com.example.baunews.ShowNewsActivity;
import com.example.baunews.ShowPressKitActivity;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PressKitAdapter extends RecyclerView.Adapter<PressKitAdapter.MyViewHolder>{
    private static final String TAG = "DATE_TIME";
    private Context context;
    private ArrayList<PressKitModel> kitModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, date, category;
        public ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.news_title);
            date = itemView.findViewById(R.id.news_date);
            image = itemView.findViewById(R.id.news_img);
            category = itemView.findViewById(R.id.news_category);
        }
    }

    public PressKitAdapter(Context context, ArrayList<PressKitModel> kitModelList) {
        this.context = context;
        this.kitModelList = kitModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_card,parent,false);
        return new PressKitAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PressKitModel pressKitModel = kitModelList.get(position);
        pressKitModel.setId(kitModelList.get(position).getId());
        holder.title.setText(pressKitModel.getTitle());
        String currTime = getCurrentTime();
        String newsTime = pressKitModel.getDate();
        holder.date.setText(getDifferenceDateTime(newsTime, currTime));
        Glide.with(context)
                .load((pressKitModel.getImage().equals("null"))? R.drawable.bau : pressKitModel.getImage())
                .into(holder.image);
        holder.category.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShowPressKitActivity.class);
                intent.putExtra("news_id", pressKitModel.getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kitModelList.size();
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    public String getDifferenceDateTime(String date1, String date2){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Duration diff = Duration.between(LocalDateTime.parse(date1, formatter),
                LocalDateTime.parse(date2, formatter));

        String difference;
        if (diff.isZero()) {
            Log.d(TAG, "0m");
            difference = "Just Now";
            return difference;
        } else {
            long days = diff.toDays();
            if (days != 0) {
                Log.d(TAG, "" + days + "d ");
                if(days < 30) {
                    if(days == 1) {
                        difference = days + " day ago";
                    } else {
                        difference = days + " days ago";
                    }
                } else if(days >= 30 && days < 365){
                    if(days < 60) {
                        difference = days / 30 + " month ago";
                    } else {
                        difference = days / 30 + " month ago";
                    }
                }else{
                    if(days < 730){
                        difference = days / 365 + " year ago";
                    }else {
                        difference = days / 365 + " years ago";
                    }
                }
                diff = diff.minusDays(days);
                return difference;
            }
            long hours = diff.toHours();
            if (hours != 0) {
                Log.d(TAG, "" + hours + "h ");
                difference = hours + "h ago";
                diff = diff.minusHours(hours);
                return difference;
            }
            long minutes = diff.toMinutes();
            if (minutes != 0) {
                Log.d(TAG, "" + minutes + "m ");
                difference = minutes + "m ago";
                diff = diff.minusMinutes(minutes);
                return difference;
            }
            long seconds = diff.getSeconds();
            if (seconds != 0) {
                Log.d(TAG, "" + seconds + "s ");
                difference = seconds + "s ago";
                return difference;
            }
        }
        return "-1";
    }
}
