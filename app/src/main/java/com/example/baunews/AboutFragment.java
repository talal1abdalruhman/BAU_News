package com.example.baunews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baunews.databinding.FragmentAboutBinding;

import java.util.Locale;

public class AboutFragment extends Fragment implements View.OnClickListener {


    Animation animation;
    int[] heights;
    Intent intent;
    private FragmentAboutBinding binding;
    ActivityOptionsCompat optionsCompat;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String locale = getActivity().getResources().getConfiguration().locale.getDisplayName();
        if (locale.equals("Arabic") || locale.equals("العربية")) {
            heights = new int[]{3161, 7079, 4801, 12644};
        } else heights = new int[]{4525, 8444, 4412, 3507};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.text_anim);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);
        View view = binding.getRoot();

        intent = new Intent(getActivity(), ShowCollegesActivity.class);
        // Inflate the layout for this fragment
        return view;
    }

    @SuppressLint("WrongConstant")
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


    public void rotateUp(View v) {
        v.setPivotX(v.getWidth() / 2);
        v.setPivotY(v.getHeight() / 2);
        v.setRotation(0);
        v.animate().setDuration(400).rotation(90);
        v.animate().setDuration(400).rotation(180);
    }

    public void rotateDown(View v) {
        v.setPivotX(v.getWidth() / 2);
        v.setPivotY(v.getHeight() / 2);
        v.animate().setDuration(400).rotation(270);
        v.animate().setDuration(400).rotation(360);
    }


    boolean[] isclick = {false, false, false, false, false};

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_bau:
                if (isclick[0] == false) {
                    rotateUp(binding.aboutBauArrow);
                    binding.aboutBau.setBackground(getActivity().getDrawable(R.drawable.item_selected_background));
                    binding.aboutBauTxt.setVisibility(View.VISIBLE);
                    binding.aboutBauTxt.startAnimation(animation);
                    //binding.aboutBauTxt.setHeight(heights[0]);
                    ViewGroup.LayoutParams params = binding.aboutBauTxt.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    binding.aboutBauTxt.setLayoutParams(params);
                } else {
                    rotateDown(binding.aboutBauArrow);
                    binding.aboutBauTxt.setVisibility(View.INVISIBLE);
                    binding.aboutBau.setBackground(getActivity().getDrawable(R.drawable.item_background));
                    //binding.aboutBauTxt.setHeight(0);
                    ViewGroup.LayoutParams params = binding.aboutBauTxt.getLayoutParams();
                    params.height = 0;
                    binding.aboutBauTxt.setLayoutParams(params);
                }
                isclick[0] = !isclick[0];
                break;
            case R.id.royal_letter:
                if (isclick[1] == false) {
                    rotateUp(binding.royalArrow);
                    binding.royalLetter.setBackground(getActivity().getDrawable(R.drawable.item_selected_background));
                    binding.txtRoyalLetter.setVisibility(View.VISIBLE);
                    binding.txtRoyalLetter.startAnimation(animation);
                    //binding.txtRoyalLetter.setHeight(heights[1]);
                    ViewGroup.LayoutParams params = binding.txtRoyalLetter.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    binding.txtRoyalLetter.setLayoutParams(params);
                } else {
                    rotateDown(binding.royalArrow);
                    binding.txtRoyalLetter.setVisibility(View.INVISIBLE);
                    binding.royalLetter.setBackground(getActivity().getDrawable(R.drawable.item_background));
                    //binding.txtRoyalLetter.setHeight(0);
                    ViewGroup.LayoutParams params = binding.txtRoyalLetter.getLayoutParams();
                    params.height = 0;
                    binding.txtRoyalLetter.setLayoutParams(params);
                }
                isclick[1] = !isclick[1];
                break;
            case R.id.prepresident_letter:
                if (isclick[2] == false) {
                    rotateUp(binding.prepresidentArrow);
                    binding.prepresidentLetter.setBackground(getActivity().getDrawable(R.drawable.item_selected_background));
                    binding.txtPrepresidentLetter.setVisibility(View.VISIBLE);
                    binding.txtPrepresidentLetter.startAnimation(animation);
                    //binding.txtPrepresidentLetter.setHeight(heights[2]);
                    ViewGroup.LayoutParams params = binding.txtPrepresidentLetter.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    binding.txtPrepresidentLetter.setLayoutParams(params);
                } else {
                    rotateDown(binding.prepresidentArrow);
                    binding.txtPrepresidentLetter.setVisibility(View.INVISIBLE);
                    binding.prepresidentLetter.setBackground(getActivity().getDrawable(R.drawable.item_background));
                    //binding.txtPrepresidentLetter.setHeight(0);
                    ViewGroup.LayoutParams params = binding.txtPrepresidentLetter.getLayoutParams();
                    params.height = 0;
                    binding.txtPrepresidentLetter.setLayoutParams(params);
                }
                isclick[2] = !isclick[2];
                break;
            case R.id.vision:
                if (isclick[3] == false) {
                    rotateUp(binding.visionArrow);
                    binding.vision.setBackground(getActivity().getDrawable(R.drawable.item_selected_background));
                    binding.txtVision.setVisibility(View.VISIBLE);
                    binding.txtVision.startAnimation(animation);
                    //binding.txtVision.setHeight(heights[3]);
                    ViewGroup.LayoutParams params = binding.txtVision.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    binding.txtVision.setLayoutParams(params);
                } else {
                    rotateDown(binding.visionArrow);
                    binding.txtVision.setVisibility(View.INVISIBLE);
                    binding.vision.setBackground(getActivity().getDrawable(R.drawable.item_background));
                    //binding.txtVision.setHeight(0);
                    ViewGroup.LayoutParams params = binding.txtVision.getLayoutParams();
                    params.height = 0;
                    binding.txtVision.setLayoutParams(params);
                }
                isclick[3] = !isclick[3];
                break;

            //--------------------------------------------------------------cards onClick------
            case R.id.engineering:
                intent.putExtra("college", 1);
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        binding.imgEng,
                        ViewCompat.getTransitionName(binding.imgEng));
                startActivity(intent, optionsCompat.toBundle());

                break;
            case R.id.medicine:
                intent.putExtra("college", 2);
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        binding.imgMed,
                        ViewCompat.getTransitionName(binding.imgMed));
                startActivity(intent, optionsCompat.toBundle());
                break;
            case R.id.it:
                intent.putExtra("college", 3);
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        binding.imgIt,
                        ViewCompat.getTransitionName(binding.imgIt));
                startActivity(intent, optionsCompat.toBundle());
                break;
            case R.id.science:
                intent.putExtra("college", 4);
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        binding.imgSci,
                        ViewCompat.getTransitionName(binding.imgSci));
                startActivity(intent, optionsCompat.toBundle());
                break;
            case R.id.agricultural:
                intent.putExtra("college", 5);
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        binding.imgAgr,
                        ViewCompat.getTransitionName(binding.imgAgr));
                startActivity(intent, optionsCompat.toBundle());
                break;
            case R.id.business:
                intent.putExtra("college", 6);
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        binding.imgBusiness,
                        ViewCompat.getTransitionName(binding.imgBusiness));
                startActivity(intent, optionsCompat.toBundle());
                break;
            case R.id.human_sciences:
                intent.putExtra("college", 7);
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        binding.imgHs,
                        ViewCompat.getTransitionName(binding.imgHs));
                startActivity(intent, optionsCompat.toBundle());
                break;
            case R.id.technical_collage:
                intent.putExtra("college", 8);
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        binding.imgTech,
                        ViewCompat.getTransitionName(binding.imgTech));
                startActivity(intent, optionsCompat.toBundle());
                break;
            case R.id.artifical_intelligence:
                intent.putExtra("college", 9);
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        binding.imgAi,
                        ViewCompat.getTransitionName(binding.imgAi));
                startActivity(intent, optionsCompat.toBundle());
                break;
            case R.id.law_college:
                intent.putExtra("college", 10);
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        binding.imgLaw,
                        ViewCompat.getTransitionName(binding.imgLaw));
                startActivity(intent, optionsCompat.toBundle());
        }
    }
}