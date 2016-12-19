package com.birdgang.sample;

import android.app.Application;
import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by birdgang on 2016. 12. 15..
 */
public class GlobalApplication extends Application {

    public static GlobalApplication application = null;
    public static Context context = null;

    public static String APPLICATION_ID = "com.birdgang.useful";

    private ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(0, 2, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    protected String userAgent;

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalApplication.application = this;
        GlobalApplication.context = getApplicationContext();

        userAgent = Util.getUserAgent(this, "sample viewpagerheader");
    }


    public static void runBackground(Runnable runnable) {
        application.mThreadPool.execute(runnable);
    }

    public static Context getApplication() {
        return application;
    }

    public static Context getContext () {
        return GlobalApplication.context;
    }


    public DataSource.Factory buildDataSourceFactory (DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter, buildHttpDataSourceFactory (bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory (DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }

    public boolean useExtensionRenderers () {
        return BuildConfig.FLAVOR.equals("withExtensions");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        application = null;
    }


}
