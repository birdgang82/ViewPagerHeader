package com.birdgang.sample.adapter;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.birdgang.sample.model.HeaderItemEntry;
import com.birdgang.sample.ui.fragment.ViewPagerHeaderVideoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdgang on 2016. 12. 15..
 */
public class HeaderViewPagerAdapter extends FragmentStatePagerAdapter {

    static final int SLIDE_TIME = 5000;

    private List<HeaderItemEntry> headerItemEntries = null;
    private int notiCount;
    private boolean dataChanged;
    private ViewPager viewPager;
    private int count;

    private ArrayList<Fragment> fragments;

    private Handler mHandler = new Handler();

    public HeaderViewPagerAdapter(FragmentManager fragmentManager, ViewPager viewPager, List<HeaderItemEntry> headerItemEntries) {
        super(fragmentManager);
        this.viewPager = viewPager;
        this.headerItemEntries = headerItemEntries;
        this.fragments = new ArrayList<>();
        createFragments();
    }

    private void createFragments() {
        if (null == headerItemEntries || headerItemEntries.size() <= 0) {
            return;
        }

        for (int i = 0; i < headerItemEntries.size(); i++) {
            HeaderItemEntry headerItemEntry = headerItemEntries.get(i);
            Fragment fragment = ViewPagerHeaderVideoFragment.newInstance(headerItemEntry, i);
            if (fragment != null) {
                fragments.add(fragment);
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    public void setDataChanged(boolean changed) {
        this.dataChanged = changed;
    }

    public void pageSelected(int position) {
        try {
            if (position < 0 || headerItemEntries == null) {
                return;
            }
        } catch (Exception e) {
        }
    }

}