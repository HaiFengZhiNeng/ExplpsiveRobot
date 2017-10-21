package com.example.explosiverobot.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 */

public class ActionViewPagerAdapter extends FragmentPagerAdapter {

    private List<String> mTitleList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();

    public ActionViewPagerAdapter(FragmentManager pagerAdapter, List<String> titleList, List<Fragment> fragmentList) {
        super(pagerAdapter);
        mTitleList = titleList;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
         return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
