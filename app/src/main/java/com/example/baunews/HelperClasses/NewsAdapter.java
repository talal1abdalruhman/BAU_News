package com.example.baunews.HelperClasses;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
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
import com.example.baunews.Models.NewsModel;
import com.example.baunews.R;
import com.example.baunews.ShowNewsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    private static final String TAG = "DATE_TIME";
    private Context context;
    private ArrayList<NewsModel> newsModelList;
    private String[] collages;
    String admin="N";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, category;
        public ImageView image;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.news_title);
            date = itemView.findViewById(R.id.news_date);
            image = itemView.findViewById(R.id.news_img);
            category = itemView.findViewById(R.id.news_category);
        }
    }

    public NewsAdapter(Context context, ArrayList<NewsModel> newsModelList, String admin) {
        this.context = context;
        this.newsModelList = newsModelList;
        this.admin = admin;
        collages = context.getResources().getStringArray(R.array.colleges_names);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NewsModel newsModel = newsModelList.get(position);
        newsModel.setId(newsModelList.get(position).getId());
        holder.title.setText(newsModel.getTitle());
        String currTime = getCurrentTime();
        String newsTime = newsModel.getDate();
        holder.date.setText(getDifferenceDateTime(newsTime, currTime));
        Glide.with(context)
                .load((newsModel.getImage().equals("null")) ? R.drawable.bau : newsModel.getImage())
                .into(holder.image);

        if(admin.equals("G") && !newsModel.getCategory().equals("general")){
            int id = Integer.parseInt(newsModel.getCategory());
            holder.category.setText(collages[id]);
        }else {
            holder.category.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShowNewsActivity.class);
                intent.putExtra("news_id", newsModel.getId());
                intent.putExtra("category", newsModel.getCategory());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsModelList.size();
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    public String getDifferenceDateTime(String date1, String date2) {
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
                if (days < 30) {
                    if (days == 1) {
                        difference = days + " day ago";
                    } else {
                        difference = days + " days ago";
                    }
                } else if (days >= 30 && days < 365) {
                    if (days < 60) {
                        difference = days / 30 + " month ago";
                    } else {
                        difference = days / 30 + " month ago";
                    }
                } else {
                    if (days < 730) {
                        difference = days / 365 + " year ago";
                    } else {
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