package com.example.baunews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.baunews.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment implements View.OnClickListener {


    Animation text_down,text_up;
    Intent intent;
    boolean[] isClick;
    private FragmentAboutBinding binding;
    ActivityOptionsCompat optionsCompat;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        text_down = AnimationUtils.loadAnimation(getActivity(), R.anim.text_anim);
        text_up= AnimationUtils.loadAnimation(getActivity(), R.anim.text_anim_up);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);
        View view = binding.getRoot();
        isClick = new boolean[]{false, false, false, false};
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

    //------------------------------------------------------------------------mathod--------------
    public void rotateUp(View v) {
        v.setPivotX(v.getWidth() / 2);
        v.setPivotY(v.getHeight() / 2);
        v.setRotation(0);
        v.animate().setDuration(400).rotation(90);
        v.animate().setDuration(400).rotation(180);
    }
    //------------------------------------------------------------------------mathod--------------
    public void rotateDown(View v) {
        v.setPivotX(v.getWidth() / 2);
        v.setPivotY(v.getHeight() / 2);
        v.animate().setDuration(400).rotation(270);
        v.animate().setDuration(400).rotation(360);
    }
    //------------------------------------------------------------------------mathod--------------
    private void showOrHideText(boolean b, View arrow, View layout, View txt) {
        ViewGroup.LayoutParams params = txt.getLayoutParams();
        if (!b) {
            rotateUp(arrow);
            layout.setBackground(getActivity().getDrawable(R.drawable.item_selected_background));
            txt.setVisibility(View.VISIBLE);
            txt.startAnimation(text_down);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            txt.setLayoutParams(params);
        } else {
            rotateDown(arrow);
            layout.setBackground(getActivity().getDrawable(R.drawable.item_background));
            txt.startAnimation(text_up);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    params.height = 0;
                    txt.setLayoutParams(params);
                }
            },500);
            txt.setVisibility(View.INVISIBLE);
        }
    }
    //------------------------------------------------------------------------mathod--------------
    private void startShowCollegesActivity(int x,View view) {
        intent.putExtra("college", x);
        optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                view,
                ViewCompat.getTransitionName(view));
        startActivity(intent, optionsCompat.toBundle());
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //--------------------------------------------------------------layouts onclick--------
            case R.id.about_bau:
                showOrHideText(isClick[0],binding.aboutBauArrow,binding.aboutBau,binding.aboutBauTxt);
                isClick[0]=!isClick[0];
                break;
            case R.id.royal_letter:
                showOrHideText(isClick[1],binding.royalArrow,binding.royalLetter,binding.txtRoyalLetter);
                isClick[1] = !isClick[1];
                break;
            case R.id.prepresident_letter:
                showOrHideText(isClick[2],binding.prepresidentArrow,binding.prepresidentLetter,binding.txtPrepresidentLetter);
                isClick[2] = !isClick[2];
                break;
            case R.id.vision:
                showOrHideText(isClick[3],binding.visionArrow,binding.vision,binding.txtVision);
                isClick[3] = !isClick[3];
                break;

            //--------------------------------------------------------------cards onClick----------
            case R.id.engineering:
                startShowCollegesActivity(1,binding.imgEng);
                break;
            case R.id.medicine:
                startShowCollegesActivity(2,binding.imgMed);
                break;
            case R.id.it:
                startShowCollegesActivity(3,binding.imgIt);
                break;
            case R.id.science:
                startShowCollegesActivity(4,binding.imgSci);
                break;
            case R.id.agricultural:
                startShowCollegesActivity(5,binding.imgAgr);
                break;
            case R.id.business:
                startShowCollegesActivity(6,binding.imgBusiness);
                break;
            case R.id.human_sciences:
                startShowCollegesActivity(7,binding.imgHs);
                break;
            case R.id.technical_collage:
                startShowCollegesActivity(8,binding.imgTech);
                break;
            case R.id.artifical_intelligence:
                startShowCollegesActivity(9,binding.imgAi);
                break;
            case R.id.law_college:
                startShowCollegesActivity(10,binding.imgLaw);
        }
    }
}