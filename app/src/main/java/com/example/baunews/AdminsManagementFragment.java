package com.example.baunews;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.baunews.HelperClasses.AdminManagePagerAdapter;
import com.example.baunews.databinding.FragmentAdminsManagementBinding;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;


public class AdminsManagementFragment extends Fragment {
    FragmentAdminsManagementBinding binding;
    View view;

    public AdminsManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admins_management, container, false);
        view = binding.getRoot();
        SetupTabLayout();
        return view;
    }

    public void SetupTabLayout(){
        AdminManagePagerAdapter adminManagePagerAdapter =
                new AdminManagePagerAdapter(
                        getParentFragmentManager(),
                        binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(adminManagePagerAdapter);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}