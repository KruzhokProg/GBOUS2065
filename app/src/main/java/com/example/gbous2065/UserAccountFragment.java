package com.example.gbous2065;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.gbous2065.Adapters.PagerAdapter;
import com.example.gbous2065.Models.SubUnsubCombine;
import com.example.gbous2065.Models.UserDocFragment;
import com.example.gbous2065.Models.UserDocHistory;
import com.example.gbous2065.R;
import com.example.gbous2065.Utils.NetworkDownload;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class UserAccountFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    List<UserDocFragment> subDocs, unsubDocs;

    public UserAccountFragment(){
    }

    public UserAccountFragment(List<UserDocFragment> subDocs, List<UserDocFragment> unsubDocs){
        this.subDocs = subDocs;
        this.unsubDocs = unsubDocs;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.user_account_fragement, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.viewPagerUserAccount);

        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager());

        pagerAdapter.addFragment(new UnsubscribeFragment(unsubDocs), "Неподписанные");
        pagerAdapter.addFragment(new SubscribeFragment(subDocs), "Подписанные");

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }


}
