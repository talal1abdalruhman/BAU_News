package com.example.baunews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.ConfigurationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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


    Animation animation;
    int []heights;
    Intent intent;
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


        String locale = getActivity().getResources().getConfiguration().locale.getDisplayName();
        if(locale.equals("Arabic")||locale.equals("العربية")){
            heights= new int[]{3161, 7079, 4801, 12644};
        }
        else heights= new int[]{4525, 8444, 4412, 3507};
        Toast.makeText(getActivity(),locale,Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        animation= AnimationUtils.loadAnimation(getActivity(),R.anim.text_anim);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);
        View view=binding.getRoot();

        intent=new Intent(getActivity(),ShowCollegesActivity.class);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.aboutBauTxt.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        binding.txtVision.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        binding.txtPrepresidentLetter.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        binding.txtRoyalLetter.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);

        binding.aboutBau.setOnClickListener(this);
        binding.royalLetter.setOnClickListener(this);
        binding.prepresidentLetter.setOnClickListener(this);
        binding.vision.setOnClickListener(this);
        binding.engineering.setOnClickListener(this);
        binding.medicine.setOnClickListener(this);
        binding.it.setOnClickListener(this);
        binding.science.setOnClickListener(this);
        binding.agricultural.setOnClickListener(this);
        binding.business.setOnClickListener(this);
        binding.humanSciences.setOnClickListener(this);
        binding.technicalCollage.setOnClickListener(this);
        binding.artificalIntelligence.setOnClickListener(this);
        binding.lawCollege.setOnClickListener(this);
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
                    binding.aboutBau.setBackground(getActivity().getDrawable(R.drawable.item_selected_background));
                    binding.aboutBauTxt.setVisibility(View.VISIBLE);
                    binding.aboutBauTxt.startAnimation(animation);
                    binding.aboutBauTxt.setHeight(heights[0]);
                }
                else{
                    rotateDown(binding.aboutBauArrow);
                    binding.aboutBauTxt.setVisibility(View.INVISIBLE);
                    binding.aboutBau.setBackground(getActivity().getDrawable(R.drawable.item_background));
                    binding.aboutBauTxt.setHeight(0);
                }
                isclick[0] =!isclick[0];
                break;
            case  R.id.royal_letter:
                if(isclick[1]==false){
                    rotateUp(binding.royalArrow);
                    binding.royalLetter.setBackground(getActivity().getDrawable(R.drawable.item_selected_background));
                    binding.txtRoyalLetter.setVisibility(View.VISIBLE);
                    binding.txtRoyalLetter.startAnimation(animation);
                    binding.txtRoyalLetter.setHeight(heights[1]);
                }
                else{
                    rotateDown(binding.royalArrow);
                    binding.txtRoyalLetter.setVisibility(View.INVISIBLE);
                    binding.royalLetter.setBackground(getActivity().getDrawable(R.drawable.item_background));
                    binding.txtRoyalLetter.setHeight(0);
                }
                isclick[1] =!isclick[1];
                break;
            case R.id.prepresident_letter:
                if(isclick[2]==false){
                    rotateUp(binding.prepresidentArrow);
                    binding.prepresidentLetter.setBackground(getActivity().getDrawable(R.drawable.item_selected_background));
                    binding.txtPrepresidentLetter.setVisibility(View.VISIBLE);
                    binding.txtPrepresidentLetter.startAnimation(animation);
                    binding.txtPrepresidentLetter.setHeight(heights[2]);
                }
                else{
                    rotateDown(binding.prepresidentArrow);
                    binding.txtPrepresidentLetter.setVisibility(View.INVISIBLE);
                    binding.prepresidentLetter.setBackground(getActivity().getDrawable(R.drawable.item_background));
                    binding.txtPrepresidentLetter.setHeight(0);
                }
                isclick[2] =!isclick[2];
                break;
            case R.id.vision:
                if(isclick[3]==false){
                    rotateUp(binding.visionArrow);
                    binding.vision.setBackground(getActivity().getDrawable(R.drawable.item_selected_background));
                    binding.txtVision.setVisibility(View.VISIBLE);
                    binding.txtVision.startAnimation(animation);
                    binding.txtVision.setHeight(heights[3]);
                }
                else{
                    rotateDown(binding.visionArrow);
                    binding.txtVision.setVisibility(View.INVISIBLE);
                    binding.vision.setBackground(getActivity().getDrawable(R.drawable.item_background));
                    binding.txtVision.setHeight(0);
                }
                isclick[3] =!isclick[3];
                break;

                //--------------------------------------------------------------cards onClick------
            case R.id.engineering:intent.putExtra("college",1);
                startActivity(intent);
                break;
            case R.id.medicine:intent.putExtra("college",2);
                startActivity(intent);
                break;
            case R.id.it:intent.putExtra("college",3);
                startActivity(intent);
                break;
            case R.id.science:intent.putExtra("college",4);
                startActivity(intent);
                break;
            case R.id.agricultural:intent.putExtra("college",5);
                startActivity(intent);
                break;
            case R.id.business:intent.putExtra("college",6);
                startActivity(intent);
                break;
            case R.id.human_sciences:intent.putExtra("college",7);
                startActivity(intent);
                break;
            case R.id.technical_collage:intent.putExtra("college",8);
                startActivity(intent);
                break;
            case R.id.artifical_intelligence:intent.putExtra("college",9);
                startActivity(intent);
                break;
            case R.id.law_college:intent.putExtra("college",10);
                startActivity(intent);
        }
    }
}