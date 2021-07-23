package com.example.baunews;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.baunews.databinding.FragmentCollegeBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollegeFragment extends Fragment {

    private List<cardsModel> cardsLists;
    private cardsAdapter adapter;
    private FragmentCollegeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_college, container, false);
        View view = binding.getRoot();
        cardsLists = new ArrayList<>();
        adapter = new cardsAdapter(getContext(),cardsLists);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(1,dpToPx(10),true));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(adapter);

        insertDataIntoCards();
        return view;
    }
    private void insertDataIntoCards() {
        int[] appscovers = new int[]{
                R.drawable.bau
        };
        cardsModel a = new cardsModel("الخبر الأول","23/7/2021",appscovers[0],"الكلية");
        cardsLists.add(a);

        a = new cardsModel("الخبر الثاني","23/7/2021",appscovers[0],"الكلية");
        cardsLists.add(a);

        adapter.notifyDataSetChanged();
    }

    private int dpToPx(int dp) {

        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,r.getDisplayMetrics()));
    }
}