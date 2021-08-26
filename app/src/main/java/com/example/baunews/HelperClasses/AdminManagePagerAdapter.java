package com.example.baunews.HelperClasses;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.baunews.AddAdminFragment;
import com.example.baunews.AdminManageFragment;

public class AdminManagePagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;

    public AdminManagePagerAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AddAdminFragment();
            case 1:
                return new AdminManageFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
