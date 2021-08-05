package com.example.baunews;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.baunews.databinding.ActivityCreateEventBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CreateEventActivity extends AppCompatActivity {

    ActivityCreateEventBinding binding;

    private static final int IMAGE_REQUEST_CODE = 100;
    private static final int FILE_REQUEST_CODE = 101;

    Uri ImgUri,PdfUri;

    Calendar calendar;

    int hour=0,minute=0,year=0,month=0,day=0;
    String startEventDateAndTime, timeToCheck,dateToCheck;

    private AlertDialog dialogAddURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_event);

        binding.btnDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date()));
        binding.btnTime.setText(new SimpleDateFormat("HH:mm a", Locale.ENGLISH).format(new Date()));
        binding.txtDateAndTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.ENGLISH).format(new Date()));
        calendar=Calendar.getInstance();
        AddOthers();

        //-----------------------------------------------------------------------Event Date--------
        binding.btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        //-----------------------------------------------------------------------Event Time--------
        binding.btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });
        //--------------------------------------------------------------------save button-----------------
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //------------------------------------------If the admin does not choose the date or time---------------------------

                if(timeToCheck == null){

                }
                if(dateToCheck == null){

                }
                //------------------------------------------------------------------------------------------------------------------
                if(dateToCheck != null && timeToCheck!=null)
                getEventDateAndTime();
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
        //--------------------------------------------------------------------back button----------

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    //---------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------method----------
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




    //-------------------------------------------------------------------------------Others--------
    private void AddOthers() {
        final LinearLayout layout = findViewById(R.id.layoutAddOthers);
        Log.d("OthersId", "AddOthers: " + layout.getId());
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
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateEventActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
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
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateEventActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), FILE_REQUEST_CODE);
                }
            }
        });
    }
    //---------------------------------------------------------------------------------------------
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
}