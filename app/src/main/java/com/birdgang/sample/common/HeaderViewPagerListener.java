package com.birdgang.sample.common;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.birdgang.sample.GlobalApplication;
import com.birdgang.sample.adapter.HeaderViewPagerAdapter;
import com.birdgang.sample.ui.fragment.ViewPagerHeaderVideoFragment;
import com.birdgang.viewpagerheader.viewpager.HeaderFragmentChangeNotifier;

/**
 * Created by birdgang on 2016. 12. 15..
 */
public class HeaderViewPagerListener implements ViewPager.OnPageChangeListener {

    private final String TAG = "HeaderViewPagerListener";

    private Context context = null;

    private int currentPosition = -1;

    private final int ON_RESUME_FRAGMENT = 0;
    private final int ON_PAUSE_FRAGMENT = 1;

    private HeaderViewPagerAdapter headerViewPagerAdapter = null;

    public HeaderViewPagerListener(HeaderViewPagerAdapter headerViewPagerAdapter) {
        context = GlobalApplication.getContext();
        this.headerViewPagerAdapter = headerViewPagerAdapter;
        int count = headerViewPagerAdapter.getCount();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        if (position < 0) {
            return;
        }

        try {
            tabChangedNotify(ON_RESUME_FRAGMENT, position);
            tabChangedNotify(ON_PAUSE_FRAGMENT, currentPosition);

            currentPosition = position;
            headerViewPagerAdapter.pageSelected(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void tabChangedNotify (int type, int page) {
        if (page <= -1) {
            return;
        }

        Fragment fragment = headerViewPagerAdapter.getItem(page);

        switch (type) {
            case ON_RESUME_FRAGMENT :
                if (fragment instanceof ViewPagerHeaderVideoFragment) {
                    HeaderFragmentChangeNotifier.INSTANCE.notifyHeaderOnResumeFragment(page);
                }
                break;

            case ON_PAUSE_FRAGMENT :
                if (fragment instanceof ViewPagerHeaderVideoFragment) {
                    HeaderFragmentChangeNotifier.INSTANCE.notifyHeaderOnPauseFragment(page);
                }
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

}
