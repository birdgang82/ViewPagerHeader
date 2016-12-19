//package com.birdgang.sample.ui.fragment;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.birdgang.sample.IntentParams;
//import com.birdgang.sample.R;
//import com.birdgang.sample.model.HeaderItemEntry;
//import com.birdgang.sample.model.UriHeaderItemEntry;
//import com.birdgang.viewpagerheader.viewpager.HeaderFragmentChangeNotifier;
//
///**
// * Created by birdgang on 2016. 12. 15..
// */
//
//public class ViewPagerHeaderFragment extends Fragment implements HeaderFragmentChangeNotifier.onFragmentHeaderLifycycle {
//
//    private final String TAG = "ViewPagerHeaderFragment";
//
//    private UriHeaderItemEntry headerItemEntry;
//
//    public static ViewPagerHeaderFragment newInstance(HeaderItemEntry headerItemEntry) {
//        ViewPagerHeaderFragment fragment = new ViewPagerHeaderFragment();
//        Bundle args = new Bundle();
//        args.putParcelable(IntentParams.PARAMS_HEADER_ITEM, headerItemEntry);
//        fragment.setArguments(args);
//
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        headerItemEntry = getArguments().getParcelable(IntentParams.PARAMS_HEADER_ITEM);
//        Log.i("birdgangviewpager" , "headerItemEntry.name : " + headerItemEntry.name + " , headerItemEntry.uri : " + headerItemEntry.uri);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_viewpager_header_video, container, false);
//        return view;
//    }
//
//    @Override
//    public int getPage() {
//        return 0;
//    }
//
//    @Override
//    public void onPauseFragment(int page) {}
//
//    @Override
//    public void onResumeFragment(int page) {}
//
//}