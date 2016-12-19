package com.birdgang.viewpagerheader.viewpager;

import java.util.ArrayList;

/**
 * Created by birdgang on 2016. 6. 27..
 */
public enum HeaderFragmentChangeNotifier {
    INSTANCE;

    private ArrayList<onFragmentHeaderLifycycle> fragmentHeaderLifecycle;

    private HeaderFragmentChangeNotifier() {
        init();
    }

    public void init() {
        fragmentHeaderLifecycle = new ArrayList<onFragmentHeaderLifycycle>();
    }

    public interface onFragmentHeaderLifycycle {
        public int getPage();
        public void onPauseFragment(int page);
        public void onResumeFragment(int page);
    }

    public void addOnFragmentHeaderLifecycleListener (onFragmentHeaderLifycycle listener) {
        if (!fragmentHeaderLifecycle.contains(listener)) {
            fragmentHeaderLifecycle.add(listener);
        }
    }

    public void removeOnFragmentHeaderLifecycleListener(onFragmentHeaderLifycycle listener) {
        fragmentHeaderLifecycle.remove(listener);
    }

    public void notifyHeaderOnResumeFragment (int page) {
        if (null != fragmentHeaderLifecycle && fragmentHeaderLifecycle.size() > 0) {
            for (onFragmentHeaderLifycycle listener : fragmentHeaderLifecycle) {
                listener.onResumeFragment(page);
            }
        }
    }

    public void notifyHeaderOnPauseFragment (int page) {
        if (null != fragmentHeaderLifecycle && fragmentHeaderLifecycle.size() > 0) {
            for (onFragmentHeaderLifycycle listener : fragmentHeaderLifecycle) {
                listener.onPauseFragment(page);
            }
        }
    }
}