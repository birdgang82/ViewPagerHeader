package com.birdgang.sample.ui.fragment;

import android.support.v4.app.Fragment;

import com.birdgang.viewpagerheader.viewpager.HeaderFragmentChangeNotifier;

/**
 * Created by birdgang on 2016. 12. 20..
 */

public class ViewpagerHeaderImageFragment extends Fragment implements HeaderFragmentChangeNotifier.onFragmentHeaderLifycycle {

    @Override
    public void onPauseFragment(int page) {
    }

    @Override
    public void onResumeFragment(int page) {
    }

}
