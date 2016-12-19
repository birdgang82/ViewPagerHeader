package com.birdgang.sample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.birdgang.sample.R;

/**
 * Created by birdgang on 2016. 6. 22..
 */
public class ProgressView extends FrameLayout {

    private Context context = null;

    private Animation animation = null;

    private ImageView progressImg;

    public ProgressView(Context context) {
        this(context, null);
        this.context = context;
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
        this.context = context;
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        init();
    }


    private void init () {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.progress_loading_content, null);

        animation = AnimationUtils.loadAnimation(context, R.anim.loading_rotation);
        progressImg = (ImageView) view.findViewById(R.id.loading_img);
        progressImg.startAnimation(animation);

        addView(view);
    }


    public void start() {
        if (null != animation) {
            animation.start();
        }
    }


    public void dismiss() {
        if (null != animation) {
            animation.cancel();
        }
    }

}
