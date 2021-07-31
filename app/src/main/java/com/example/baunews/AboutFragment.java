package com.example.baunews;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.ConfigurationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.baunews.databinding.FragmentAboutBinding;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {


    int []heights;
    private FragmentAboutBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String locale = this.getResources().getConfiguration().locale.getDisplayName();
        if(locale.equals("Arabic")){
            heights= new int[]{5000, 5000, 5000, 8000, 5000};
        }
        else heights= new int[]{1699, 3861, 2075, 1323, 3080};
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);
        View view=binding.getRoot();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.aboutBau.setOnClickListener(this);
        binding.royalLetter.setOnClickListener(this);
        binding.prepresidentLetter.setOnClickListener(this);
        binding.vision.setOnClickListener(this);
        binding.aboutColleges.setOnClickListener(this);
    }


    public void rotateUp(View v){
        v.setPivotX(v.getWidth()/2);
        v.setPivotY(v.getHeight()/2);
        v.setRotation(0);
        v.animate().setDuration(400).rotation(90);
        v.animate().setDuration(400).rotation(180);
    }
    public void rotateDown(View v){
        v.setPivotX(v.getWidth()/2);
        v.setPivotY(v.getHeight()/2);
        v.animate().setDuration(400).rotation(270);
        v.animate().setDuration(400).rotation(360);
    }



    final boolean[] isclick = {false,false,false,false,false};
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.about_bau:
                if(isclick[0]==false){
                    rotateUp(binding.aboutBauArrow);
                    binding.aboutBauTxt.setVisibility(View.VISIBLE);
                    binding.aboutBauTxt.setHeight(heights[0]);
                }
                else{
                    rotateDown(binding.aboutBauArrow);
                    binding.aboutBauTxt.setVisibility(View.INVISIBLE);
                    binding.aboutBauTxt.setHeight(0);
                }
                isclick[0] =!isclick[0];
                break;
            case  R.id.royal_letter:
                if(isclick[1]==false){
                    rotateUp(binding.royalArrow);
                    binding.txtRoyalLetter.setVisibility(View.VISIBLE);
                    binding.txtRoyalLetter.setHeight(heights[1]);
                }
                else{
                    rotateDown(binding.royalArrow);
                    binding.txtRoyalLetter.setVisibility(View.INVISIBLE);
                    binding.txtRoyalLetter.setHeight(0);
                }
                isclick[1] =!isclick[1];
                break;
            case R.id.prepresident_letter:
                if(isclick[2]==false){
                    rotateUp(binding.prepresidentArrow);
                    binding.txtPrepresidentLetter.setVisibility(View.VISIBLE);
                    binding.txtPrepresidentLetter.setHeight(heights[2]);
                }
                else{
                    rotateDown(binding.prepresidentArrow);
                    binding.txtPrepresidentLetter.setVisibility(View.INVISIBLE);
                    binding.txtPrepresidentLetter.setHeight(0);
                }
                isclick[2] =!isclick[2];
                break;
            case R.id.vision:
                if(isclick[3]==false){
                    rotateUp(binding.visionArrow);
                    binding.txtVision.setVisibility(View.VISIBLE);
                    binding.txtVision.setHeight(heights[3]);
                }
                else{
                    rotateDown(binding.visionArrow);
                    binding.txtVision.setVisibility(View.INVISIBLE);
                    binding.txtVision.setHeight(0);
                }
                isclick[3] =!isclick[3];
                break;
            case R.id.about_colleges:
                if(isclick[4]==false){
                    rotateUp(binding.collegeArrow);
                    binding.colleges.setVisibility(View.VISIBLE);
                }
                else{
                    rotateDown(binding.collegeArrow);
                    binding.colleges.setVisibility(View.INVISIBLE);
                    binding.line.setVisibility(View.VISIBLE);
                }
                isclick[4] =!isclick[4];
        }
    }
}