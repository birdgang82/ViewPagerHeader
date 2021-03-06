package com.birdgang.viewpagerheader.video;

import android.content.Context;
import android.os.Looper;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.trackselection.TrackSelector;

/**
 * Created by birdgang on 2016. 12. 20..
 */
public final class ExoPlayerFactory {
    /**
     * The default maximum duration for which a video renderer can attempt to seamlessly join an
     * ongoing playback.
     */
    public static final long DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS = 5000;

    private ExoPlayerFactory() {}

    /**
     * Creates a {@link SimpleExoPlayer} instance. Must be called from a thread that has an associated
     * {@link Looper}.
     *
     * @param context A {@link Context}.
     * @param trackSelector The {@link TrackSelector} that will be used by the instance.
     * @param loadControl The {@link LoadControl} that will be used by the instance.
     */
    public static CustomSimpleExoPlayer newSimpleInstance(Context context, TrackSelector trackSelector,
                                                    LoadControl loadControl) {
        return newSimpleInstance(context, trackSelector, loadControl, null);
    }

    /**
     * Creates a {@link SimpleExoPlayer} instance. Must be called from a thread that has an associated
     * {@link Looper}. Available extension renderers are not used.
     *
     * @param context A {@link Context}.
     * @param trackSelector The {@link TrackSelector} that will be used by the instance.
     * @param loadControl The {@link LoadControl} that will be used by the instance.
     * @param drmSessionManager An optional {@link DrmSessionManager}. May be null if the instance
     *     will not be used for DRM protected playbacks.
     */
    public static CustomSimpleExoPlayer newSimpleInstance(Context context, TrackSelector trackSelector,
                                                    LoadControl loadControl, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        return newSimpleInstance(context, trackSelector, loadControl,
                drmSessionManager, SimpleExoPlayer.EXTENSION_RENDERER_MODE_OFF);
    }

    /**
     * Creates a {@link SimpleExoPlayer} instance. Must be called from a thread that has an associated
     * {@link Looper}.
     *
     * @param context A {@link Context}.
     * @param trackSelector The {@link TrackSelector} that will be used by the instance.
     * @param loadControl The {@link LoadControl} that will be used by the instance.
     * @param drmSessionManager An optional {@link DrmSessionManager}. May be null if the instance
     *     will not be used for DRM protected playbacks.
     * @param extensionRendererMode The extension renderer mode, which determines if and how available
     *     extension renderers are used. Note that extensions must be included in the application
     *     build for them to be considered available.
     */
    public static CustomSimpleExoPlayer newSimpleInstance(Context context, TrackSelector trackSelector,
                                                    LoadControl loadControl, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                                                    @SimpleExoPlayer.ExtensionRendererMode int extensionRendererMode) {
        return newSimpleInstance(context, trackSelector, loadControl, drmSessionManager,
                extensionRendererMode, DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    /**
     * Creates a {@link SimpleExoPlayer} instance. Must be called from a thread that has an associated
     * {@link Looper}.
     *
     * @param context A {@link Context}.
     * @param trackSelector The {@link TrackSelector} that will be used by the instance.
     * @param loadControl The {@link LoadControl} that will be used by the instance.
     * @param drmSessionManager An optional {@link DrmSessionManager}. May be null if the instance
     *     will not be used for DRM protected playbacks.
     * @param extensionRendererMode The extension renderer mode, which determines if and how available
     *     extension renderers are used. Note that extensions must be included in the application
     *     build for them to be considered available.
     * @param allowedVideoJoiningTimeMs The maximum duration for which a video renderer can attempt to
     *     seamlessly join an ongoing playback.
     */
    public static CustomSimpleExoPlayer newSimpleInstance(Context context, TrackSelector trackSelector,
                                                    LoadControl loadControl, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                                                    @SimpleExoPlayer.ExtensionRendererMode int extensionRendererMode,
                                                    long allowedVideoJoiningTimeMs) {
        return new CustomSimpleExoPlayer(context, trackSelector, loadControl, drmSessionManager,
                extensionRendererMode, allowedVideoJoiningTimeMs);
    }

    /**
     * Creates an {@link ExoPlayer} instance. Must be called from a thread that has an associated
     * {@link Looper}.
     *
     * @param renderers The {@link Renderer}s that will be used by the instance.
     * @param trackSelector The {@link TrackSelector} that will be used by the instance.
     */
    public static ExoPlayer newInstance(Renderer[] renderers, TrackSelector trackSelector) {
        return newInstance(renderers, trackSelector, new DefaultLoadControl());
    }

    /**
     * Creates an {@link ExoPlayer} instance. Must be called from a thread that has an associated
     * {@link Looper}.
     *
     * @param renderers The {@link Renderer}s that will be used by the instance.
     * @param trackSelector The {@link TrackSelector} that will be used by the instance.
     * @param loadControl The {@link LoadControl} that will be used by the instance.
     */
    public static ExoPlayer newInstance(Renderer[] renderers, TrackSelector trackSelector,
                                        LoadControl loadControl) {
        return new ExoPlayerImpl(renderers, trackSelector, loadControl);
    }
}
