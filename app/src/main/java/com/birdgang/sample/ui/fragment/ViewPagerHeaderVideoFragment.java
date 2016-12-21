package com.birdgang.sample.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.birdgang.sample.GlobalApplication;
import com.birdgang.sample.HeaderItemManager;
import com.birdgang.sample.IntentParams;
import com.birdgang.sample.R;
import com.birdgang.sample.model.HeaderItemEntry;
import com.birdgang.sample.model.UriHeaderItemEntry;
import com.birdgang.sample.view.ProgressView;
import com.birdgang.viewpagerheader.video.CustomPlaybackControlView;
import com.birdgang.viewpagerheader.video.CustomSimpleExoPlayer;
import com.birdgang.viewpagerheader.video.CustomSimpleExoPlayerView;
import com.birdgang.viewpagerheader.video.EventLogger;
import com.birdgang.viewpagerheader.video.ExoPlayerFactory;
import com.birdgang.viewpagerheader.video.TrackSelectionHelper;
import com.birdgang.viewpagerheader.viewpager.HeaderFragmentChangeNotifier;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.StreamingDrmSessionManager;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by birdgang on 2016. 12. 15..
 */
public class ViewPagerHeaderVideoFragment extends Fragment implements HeaderFragmentChangeNotifier.onFragmentHeaderLifycycle, SimpleExoPlayer.EventListener, CustomPlaybackControlView.VisibilityListener {

    private final String TAG = "ViewPagerHeaderFragment";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private Handler mainHandler;
    private Timeline.Window window;
    private EventLogger eventLogger;
    private CustomSimpleExoPlayerView simpleCustomExoPlayerView;
    private ProgressView mProgressView;

    private DataSource.Factory mediaDataSourceFactory;
    private CustomSimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private TrackSelectionHelper trackSelectionHelper;
    private boolean playerNeedsSource;

    private boolean shouldAutoPlay;
    private boolean isTimelineStatic;
    private int playerWindow;
    private long playerPosition;
    private int mHeaderVideoPosition;


    private HeaderItemEntry mHeaderItemEntry = null;

    public static ViewPagerHeaderVideoFragment newInstance(HeaderItemEntry headerItemEntry, int headerVideoPosition) {
        ViewPagerHeaderVideoFragment fragment = new ViewPagerHeaderVideoFragment();
        Bundle args = new Bundle();
        args.putParcelable(IntentParams.PARAMS_HEADER_ITEM, headerItemEntry);
        args.putInt(IntentParams.PARAMS_EXTRA_INDEX, headerVideoPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HeaderFragmentChangeNotifier.INSTANCE.addOnFragmentHeaderLifecycleListener(this);

        mHeaderItemEntry = getArguments().getParcelable(IntentParams.PARAMS_HEADER_ITEM);
        mHeaderVideoPosition = getArguments().getInt(IntentParams.PARAMS_EXTRA_INDEX);

        Log.i("birdgangviewpager", " ViewPagerHeaderVideoFragment > onCreate > headerItemEntry : " + mHeaderItemEntry.toString());

        shouldAutoPlay = true;
        mediaDataSourceFactory = buildDataSourceFactory(true);
        mainHandler = new Handler();
        window = new Timeline.Window();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_header_video, container, false);

        simpleCustomExoPlayerView = (CustomSimpleExoPlayerView) view.findViewById(R.id.player_view);
        simpleCustomExoPlayerView.setControllerVisibilityListener(this);
        simpleCustomExoPlayerView.requestFocus();

        mProgressView = (ProgressView) view.findViewById(R.id.progress_view);

        return view;
    }

    @Override
    public void onPauseFragment(int page) {
        Log.i("birdgangviewpager", "onPauseFragment > page : " + page);
        if (page == mHeaderVideoPosition) {
            releasePlayer();
        }
    }

    @Override
    public void onResumeFragment(int page) {
        Log.i("birdgangviewpager", "onResumeFragment > page : " + page);
        mHeaderItemEntry = HeaderItemManager.INSTANCE.getHeaderItemEntryByPosition(page);
        Log.i("birdgangviewpager", "onResumeFragment > name : " + mHeaderItemEntry.name);

        if (page == mHeaderVideoPosition) {
            initializePlayer();
        }
    }

    public void onNewIntent(Intent intent) {
        releasePlayer();
        isTimelineStatic = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("birdgangviewpager", " ViewPagerHeaderVideoFragment > onDestroy");

        HeaderFragmentChangeNotifier.INSTANCE.removeOnFragmentHeaderLifecycleListener(this);
        releasePlayer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer();
        } else {
            showToast(R.string.storage_permission_denied);
        }
    }

    public void showPlayerViewController () {
        simpleCustomExoPlayerView.showController();
    }

    @Override
    public void onVisibilityChange(int visibility) {
    }

    private void initializePlayer() {
        if (player == null) {
            boolean preferExtensionDecoders = mHeaderItemEntry.preferExtensionDecoders;
            UUID drmSchemeUuid = null;
            if (null != mHeaderItemEntry.drmSchemeUuid) {
                drmSchemeUuid = mHeaderItemEntry.drmSchemeUuid;
            } else {
                drmSchemeUuid = null;
            }

            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
            if (drmSchemeUuid != null) {
                String drmLicenseUrl = mHeaderItemEntry.drmLicenseUrl;
                String[] keyRequestPropertiesArray = mHeaderItemEntry.drmKeyRequestProperties;
                Map<String, String> keyRequestProperties;
                if (keyRequestPropertiesArray == null || keyRequestPropertiesArray.length < 2) {
                    keyRequestProperties = null;
                } else {
                    keyRequestProperties = new HashMap<>();
                    for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                        keyRequestProperties.put(keyRequestPropertiesArray[i], keyRequestPropertiesArray[i + 1]);
                    }
                }
                try {
                    drmSessionManager = buildDrmSessionManager(drmSchemeUuid, drmLicenseUrl, keyRequestProperties);
                } catch (UnsupportedDrmException e) {
                    int errorStringId = Util.SDK_INT < 18 ? R.string.error_drm_not_supported  : (e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown);
                    showToast(errorStringId);
                    return;
                }
            }

            @SimpleExoPlayer.ExtensionRendererMode
            int extensionRendererMode = ((GlobalApplication) getActivity().getApplication()).useExtensionRenderers()
                            ? (preferExtensionDecoders ? SimpleExoPlayer.EXTENSION_RENDERER_MODE_PREFER
                            : SimpleExoPlayer.EXTENSION_RENDERER_MODE_ON)
                            : SimpleExoPlayer.EXTENSION_RENDERER_MODE_OFF;

            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            trackSelectionHelper = new TrackSelectionHelper(trackSelector, videoTrackSelectionFactory);
            player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, new DefaultLoadControl(), drmSessionManager, extensionRendererMode);
            player.addListener(this);

            eventLogger = new EventLogger(trackSelector);
            player.addListener(eventLogger);
            player.setAudioDebugListener(eventLogger);
            player.setVideoDebugListener(eventLogger);

            simpleCustomExoPlayerView.setPlayer(player);
            if (isTimelineStatic) {
                if (playerPosition == C.TIME_UNSET) {
                    player.seekToDefaultPosition(playerWindow);
                } else {
                    player.seekTo(playerWindow, playerPosition);
                }
            }
            player.setPlayWhenReady(shouldAutoPlay);
            playerNeedsSource = true;
        }

        if (playerNeedsSource) {
            Uri[] uris = new Uri[1];
            String[] extensions = new String[1];

            UriHeaderItemEntry uriHeaderItemEntry = (UriHeaderItemEntry) mHeaderItemEntry;
            uris[0] = Uri.parse(uriHeaderItemEntry.uri);
            extensions[0] = uriHeaderItemEntry.extension;

            if (Util.maybeRequestReadExternalStoragePermission(getActivity(), uris)) {
                return;
            }

            MediaSource[] mediaSources = new MediaSource[uris.length];
            for (int i = 0; i < uris.length; i++) {
                mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
            }

            MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);
            player.prepare(mediaSource, !isTimelineStatic, !isTimelineStatic);
            playerNeedsSource = false;
        }

        if (null != simpleCustomExoPlayerView) {
            simpleCustomExoPlayerView.setVisibleController();
        }
    }


    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = Util.inferContentType(!TextUtils.isEmpty(overrideExtension) ? "." + overrideExtension : uri.getLastPathSegment());
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false), new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false), new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(), mainHandler, eventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }


    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManager(UUID uuid, String licenseUrl, Map<String, String> keyRequestProperties) throws UnsupportedDrmException {
        if (Util.SDK_INT < 18) {
            return null;
        }

        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl, buildHttpDataSourceFactory(false), keyRequestProperties);
        return new StreamingDrmSessionManager<>(uuid, FrameworkMediaDrm.newInstance(uuid), drmCallback, null, mainHandler, eventLogger);
    }

    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            playerWindow = player.getCurrentWindowIndex();
            playerPosition = C.TIME_UNSET;
            Timeline timeline = player.getCurrentTimeline();
            if (!timeline.isEmpty() && timeline.getWindow(playerWindow, window).isSeekable) {
                playerPosition = player.getCurrentPosition();
            }
            player.release();
            player = null;
            trackSelector = null;
            trackSelectionHelper = null;
            eventLogger = null;
        }
    }


    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return ((GlobalApplication) getActivity().getApplication()).buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
        return ((GlobalApplication) getActivity().getApplication()).buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            showControls();
        }

        String text = "playWhenReady : " + playWhenReady + " , playbackState : ";
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                showProgress();
                break;
            case ExoPlayer.STATE_ENDED:
                text += "> ended";
                break;
            case ExoPlayer.STATE_IDLE:
                text += "> idle";
                break;
            case ExoPlayer.STATE_READY:
                text += "> ready";
                hideProgress();
                break;
            default:
                text += "> unknown";
                break;
        }
        Log.i("birdgangviewpager" , "onStateChanged > text : " + text);
    }


    @Override
    public void onPlayerError(ExoPlaybackException error) {
        String errorString = null;
        if (error.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = error.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException = (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            showToast(errorString);
        }
        playerNeedsSource = true;
        showControls();
        showError();
    }

    @Override
    public void onPositionDiscontinuity() {
        // Do nothing.
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        isTimelineStatic = !timeline.isEmpty() && !timeline.getWindow(timeline.getWindowCount() - 1, window).isDynamic;
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_VIDEO) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                showToast(R.string.error_unsupported_video);
            }

            if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_AUDIO) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                showToast(R.string.error_unsupported_audio);
            }
        }
    }

    private void showControls() {}

    private void showError () {
        Log.i("birdgangviewpager" , "showError");
    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void showProgress () {
        if (null != mProgressView && mProgressView.getVisibility() == View.GONE) {
            mProgressView.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress () {
        if (null != mProgressView) {
            mProgressView.setVisibility(View.GONE);
        }
    }


}