package com.example.locationlog_02.Activitys.MyMapLog.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class DetailImgaeFragmentAdapter extends FragmentStatePagerAdapter {

    //ViewPager에 들어갈 Fragment들을 담을 리스트
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    public DetailImgaeFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public  void addItem(Fragment fragment){
        fragmentList.add(fragment);
    }
}
