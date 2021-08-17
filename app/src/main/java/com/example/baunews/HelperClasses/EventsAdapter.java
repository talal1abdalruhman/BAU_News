package com.example.baunews.HelperClasses;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baunews.MainActivity;
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
import java.util.Random;
import java.util.TimeZone;

import static android.content.Intent.FLAG_INCLUDE_STOPPED_PACKAGES;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {
    private static final String TAG = "DATE_TIME";
    private Context context;
    private ArrayList<EventsModel> eventsModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, startDate;
        public ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.event_title);
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
                .inflate(R.layout.events_card, parent, false);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(startEvent);
            startEvent = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.startDate.setText("Start at: " + startEvent);
        Glide.with(context)
                .load((eventsModel.getImage().equals("null")) ? R.drawable.bau : eventsModel.getImage())
                .into(holder.image);

        long diffInMillis = getDifferenceDT(getCurrentTime(), eventsModel.getStart_date());

        CountDownTimer countDownTimer = new CountDownTimer(diffInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long d, h, m, s, r;
                d = millisUntilFinished / 86400000;
                r = millisUntilFinished % 86400000;
                h = r / 3600000;
                r = r % 3600000;
                m = r / 60000;
                r = r % 60000;
                s = r / 1000;
                String time = "";
                if (d > 0) time += d + "d ";
                if (h > 0) time += h + "h ";
                if (m > 0) time += m + "m ";
                if (s > 0) time += s + "s";
                Log.d("TIME_TRACKING", "onTick: " + time);
                holder.startDate.setText("Start within: " + time);
            }

            @Override
            public void onFinish() {
                Log.d("TIME_TRACKING", "onTick: finished");
                holder.startDate.setText("Finished");
            }
        };
        countDownTimer.start();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShowEventsActivity.class);
                intent.putExtra("event_id", eventsModel.getId());
                intent.putExtra("category", eventsModel.getCategory());
                intent.putExtra("start_date", eventsModel.getStart_date());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsModelList.size();
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    public String getDifferenceDateTime(String date1, String date2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Duration diff = Duration.between(LocalDateTime.parse(date1, formatter),
                LocalDateTime.parse(date2, formatter2));

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

    public long getDifferenceDT(String date1, String date2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Duration diff = Duration.between(LocalDateTime.parse(date1, formatter),
                LocalDateTime.parse(date2, formatter));
        return diff.toMillis();
    }
}