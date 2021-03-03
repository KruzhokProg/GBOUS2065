package com.example.gbous2065.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.gbous2065.SubscribeFragment;
import com.example.gbous2065.UnsubscribeFragment;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentList = new ArrayList<>();
    List<String> titleList = new ArrayList<>();

    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        titleList.add(title);
    }
}
