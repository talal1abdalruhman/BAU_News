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
import com.example.baunews.Models.EventsModel;
import com.example.baunews.R;
import com.example.baunews.ShowEventsActivity;
import com.example.baunews.ShowNewsActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder>{
    private static final String TAG = "DATE_TIME";
    private Context context;
    private ArrayList<EventsModel> eventsModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title,date,startDate;
        public ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.event_title);
            date = itemView.findViewById(R.id.event_date);
            image = itemView.findViewById(R.id.event_img);
            startDate = itemView.findViewById(R.id.event_start_date);
        }
    }

    public EventsAdapter(Context context, ArrayList<EventsModel> eventsModelList) {
        this.context = context;
        this.eventsModelList = eventsModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.events_card,parent,false);
        return new EventsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        EventsModel eventsModel = eventsModelList.get(position);
        eventsModel.setId(eventsModelList.get(position).getId());
        holder.title.setText(eventsModel.getTitle());
        String currTime = getCurrentTime();
        String eventsTime = eventsModel.getDate();
        String startEvent = eventsModel.getStart_date();
        holder.date.setText(getDifferenceDateTime(eventsTime, currTime));
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy h:mm a", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(startEvent);
            startEvent = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.startDate.setText("Start at: "+startEvent);
        Glide.with(context)
                .load((eventsModel.getImage().equals("null"))? R.drawable.bau : eventsModel.getImage())
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"You clicked: "+ eventsModel.getTitle(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), ShowEventsActivity.class);
                intent.putExtra("event_id", eventsModel.getId());
                intent.putExtra("category", eventsModel.getCategory());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsModelList.size();
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