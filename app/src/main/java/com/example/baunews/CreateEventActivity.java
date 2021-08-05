package com.example.baunews;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.format.DateFormat;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.baunews.databinding.ActivityCreateEventBinding;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityCreateEventBinding binding;

    private static final int IMAGE_REQUEST_CODE = 100;
    private static final int FILE_REQUEST_CODE = 101;

    Uri ImgUri,PdfUri;

    Calendar calendar;

    int hour=0,minute=0,year=0,month=0,day=0;
    String startEventDateAndTime, timeToCheck,dateToCheck;

    Animation rotate_froward,rotate_backward,fab_image_open,fab_image_close,fab_url_open,fab_url_close,fab_pdf_open,fab_pdf_close;
    boolean clicked;

    private AlertDialog dialogAddURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_event);

        binding.addFab.setOnClickListener(this);
        binding.urlFab.setOnClickListener(this);
        binding.pdfFab.setOnClickListener(this);
        binding.imageFab.setOnClickListener(this);
        binding.btnDate.setOnClickListener(this);
        binding.btnTime.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.removeImage.setOnClickListener(this);
        binding.removePdf.setOnClickListener(this);
        binding.removeWebURL.setOnClickListener(this);

        rotate_froward= AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward= AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        fab_image_open=AnimationUtils.loadAnimation(this,R.anim.fab_image_open_translate);
        fab_image_close=AnimationUtils.loadAnimation(this,R.anim.fab_image_close_translate);
        fab_url_open=AnimationUtils.loadAnimation(this,R.anim.fab_url_open_translate);
        fab_url_close=AnimationUtils.loadAnimation(this,R.anim.fab_url_close_translate);
        fab_pdf_open=AnimationUtils.loadAnimation(this,R.anim.fab_pdf_open_translate);
        fab_pdf_close=AnimationUtils.loadAnimation(this,R.anim.fab_pdf_close_translate);

        clicked=false;

        binding.btnDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date()));
        binding.btnTime.setText(new SimpleDateFormat("HH:mm a", Locale.ENGLISH).format(new Date()));
        binding.txtDateAndTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.ENGLISH).format(new Date()));
        calendar=Calendar.getInstance();
    }







    //------------------------------------------------------------methods to set fabs animations----

    private void onAddBtnClick() {
        setVisibility(clicked);
        setAnimation(clicked);
        clicked=!clicked;
    }
    private void setAnimation(boolean b) {
        if(!b){
            binding.imageFab.startAnimation(fab_image_open);
            binding.pdfFab.startAnimation(fab_pdf_open);
            binding.urlFab.startAnimation(fab_url_open);
            binding.addFab.startAnimation(rotate_froward);
            binding.addFab.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDelete)));
        }else {
            binding.imageFab.startAnimation(fab_image_close);
            binding.pdfFab.startAnimation(fab_pdf_close);
            binding.urlFab.startAnimation(fab_url_close);
            binding.addFab.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainColor)));
            binding.addFab.startAnimation(rotate_backward);
        }
    }
    private void setVisibility(boolean b) {
        if(!b){
            binding.imageFab.setVisibility(View.VISIBLE);
            binding.pdfFab.setVisibility(View.VISIBLE);
            binding.urlFab.setVisibility(View.VISIBLE);
        }else {
            binding.imageFab.setVisibility(View.INVISIBLE);
            binding.pdfFab.setVisibility(View.INVISIBLE);
            binding.urlFab.setVisibility(View.INVISIBLE);
        }
    }

    //--------------------------------------------------------------method to set date format----------

    private void getEventDateAndTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        Date eventDate = calendar.getTime();
        startEventDateAndTime=simpleDateFormat.format(eventDate);
        Toast.makeText(CreateEventActivity.this,startEventDateAndTime,Toast.LENGTH_LONG).show();
    }

    //-----------------------------------------------------------------------------TimePicker------
    private void showTimePicker() {
        TimePickerDialog.OnTimeSetListener listener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourS, int minuteS) {
                hour=hourS;
                minute=minuteS;
                calendar.set(year,month,day,hour,minute);
                timeToCheck=DateFormat.format("hh:mm aa", calendar)+"";
                binding.btnTime.setText(timeToCheck);
            }
        };
        TimePickerDialog timeDialog =new TimePickerDialog(CreateEventActivity.this,listener,hour,minute,false);
        timeDialog.show();
    }

    //-----------------------------------------------------------------------------DatePicker------
    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener listener =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int yearS, int monthS, int dayS) {
                year=yearS;
                month=monthS;
                day=dayS;
                calendar.set(year,month,day,hour,minute);
                dateToCheck=day+"/"+(month+1)+"/"+year;
                binding.btnDate.setText(dateToCheck);
            }
        };
        DatePickerDialog dateDialog=new DatePickerDialog(CreateEventActivity.this,listener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        //dateDialog.getWindow().setBackgroundDrawableResource(R.color.mainColor);
        dateDialog.show();
    }

    //--------------------------------------------------------------------URL Dialog---------------
    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);
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
                        Toast.makeText(CreateEventActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(CreateEventActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
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

    //--------------------------------------------------------------------result ------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            ImgUri = null;
            if (data != null) {
                ImgUri = data.getData();
            }
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

            String ss = PdfUri.getPath();
            binding.textPdf.setText(pdfFileName);
            binding.layoutPdf.setVisibility(View.VISIBLE);
            binding.removePdf.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_fab:{onAddBtnClick();}break;
            case R.id.image_fab:{
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateEventActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
                onAddBtnClick();
            }break;
            case R.id.pdf_fab:{
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateEventActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), FILE_REQUEST_CODE);
                }
                onAddBtnClick();
            }break;
            case R.id.url_fab:{
                showAddURLDialog();
                onAddBtnClick();
            }break;
            case R.id.btn_date:{ showDatePicker(); }break;
            case R.id.btn_time:{showTimePicker(); }break;
            case R.id.removeImage:{
                binding.imageNews.setImageURI(null);
                binding.imageNews.setVisibility(View.GONE);
                binding.removeImage.setVisibility(View.GONE);
            }break;
            case R.id.removePdf:{
                binding.textPdf.setText(null);
                binding.layoutPdf.setVisibility(View.GONE);
            }break;
            case R.id.removeWebURL:{
                binding.textWebURL.setText(null);
                binding.layoutWebURL.setVisibility(View.GONE);
            }break;
            case R.id.btnBack:{
                Intent intent = new Intent(CreateEventActivity.this,MainActivity.class);
                startActivity(intent);
            }break;
            case R.id.btnSave:{
                //-------------------If the admin does not choose the date or time-----------------
                if(timeToCheck == null){
                    Toast.makeText(CreateEventActivity.this,"choose Event Time",Toast.LENGTH_SHORT).show();
                }
                if(dateToCheck == null){
                    Toast.makeText(CreateEventActivity.this,"choose choose Event Date",Toast.LENGTH_SHORT).show();
                }
                if(dateToCheck != null && timeToCheck!=null)
                    getEventDateAndTime();
            }break;
        }
    }
}