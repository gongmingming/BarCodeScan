package com.njupt.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;
public class FragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public FragmentAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.fragments=fragments;
    }

    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    public int getCount() {
        return fragments.size();
    }

}
