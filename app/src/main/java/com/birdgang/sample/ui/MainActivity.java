package com.birdgang.sample.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.birdgang.sample.HeaderItemManager;
import com.birdgang.sample.R;
import com.birdgang.sample.adapter.HeaderViewPagerAdapter;
import com.birdgang.sample.common.Command;
import com.birdgang.sample.common.CommandHandler;
import com.birdgang.sample.common.HeaderViewPagerListener;
import com.birdgang.sample.model.HeaderItemEntry;
import com.birdgang.sample.model.HeaderItemGroupEntry;
import com.birdgang.sample.model.PlaylistHeaderItemEntry;
import com.birdgang.sample.model.UriHeaderItemEntry;
import com.birdgang.viewpagerheader.indicator.SpringIndicator;
import com.birdgang.viewpagerheader.viewpager.ScrollerViewPager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private View mHeaderBackgroundContainer;
    private ScrollerViewPager mScrollerViewPager;
    private SpringIndicator mSpringIndicator;

    private HeaderViewPagerAdapter mHeaderViewPagerAdapter = null;
    private HeaderViewPagerListener mHeaderViewPagerListener = null;

    private List<HeaderItemEntry> mHeaderItemEntries = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String dataUri = intent.getDataString();
        String[] uris;
        if (dataUri != null) {
            uris = new String[] {dataUri};
        } else {
            uris = new String[] {
                    "asset:///media.headervideo2.json",
            };
        }

        mHeaderItemEntries = new ArrayList<HeaderItemEntry>();

        SampleListLoader loaderTask = new SampleListLoader();
        loaderTask.execute(uris);

        mHeaderBackgroundContainer = findViewById(R.id.header_background_container);
        mHeaderBackgroundContainer.setFocusable(true);

    }


    private void onSampleGroups(final List<HeaderItemGroupEntry> groups, boolean sawError) {
        if (sawError) {
            Toast.makeText(getApplicationContext(), "sample_list_load_error", Toast.LENGTH_LONG).show();
        }

        for (HeaderItemGroupEntry group : groups) {
            List<HeaderItemEntry> samples = group.samples;
            for (HeaderItemEntry sample : samples) {
                //Log.i("birdgangviewpager" , "sample.name : " + sample.name);
                mHeaderItemEntries.add(sample);
            }
        }

        HeaderItemManager.INSTANCE.setHeaderItemEntries(mHeaderItemEntries);

        int limitedSize = mHeaderItemEntries.size();
        if (limitedSize <= 0) {
            return;
        }

        mHeaderViewPagerAdapter = new HeaderViewPagerAdapter(getSupportFragmentManager(), mScrollerViewPager, mHeaderItemEntries);

        mScrollerViewPager = (ScrollerViewPager) mHeaderBackgroundContainer.findViewById(R.id.view_pager);
        mScrollerViewPager.setAdapter(mHeaderViewPagerAdapter);
        mScrollerViewPager.setOffscreenPageLimit(limitedSize);
        mScrollerViewPager.fixScrollSpeed();
        mScrollerViewPager.invalidate();
        mScrollerViewPager.getAdapter().notifyDataSetChanged();
        mHeaderViewPagerListener = new HeaderViewPagerListener(mHeaderViewPagerAdapter);
        mScrollerViewPager.addOnPageChangeListener(mHeaderViewPagerListener);

        mSpringIndicator = (SpringIndicator) findViewById(R.id.indicator);
        mSpringIndicator.setViewPager(mScrollerViewPager);
        mSpringIndicator.bringToFront();
        mSpringIndicator.invalidate();

        CommandHandler selectedPageForDelay = new CommandHandler();
        selectedPageForDelay.sendForDelay(new SelectedPage(), 300);
    }


    public class SelectedPage implements Command {
        @Override
        public void execute() {
            try {
                mHeaderViewPagerListener.onPageSelected(0);
            } catch (Exception e) {
                Log.e("birdgangviewpager" , "e message : " + e.getMessage());
            }
        }
    }


    private final class SampleListLoader extends AsyncTask<String, Void, List<HeaderItemGroupEntry>> {
        private boolean sawError;
        @Override
        protected List<HeaderItemGroupEntry> doInBackground(String... uris) {
            List<HeaderItemGroupEntry> result = new ArrayList<>();
            Context context = getApplicationContext();
            String userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
            DataSource dataSource = new DefaultDataSource(context, null, userAgent, false);
            for (String uri : uris) {
                DataSpec dataSpec = new DataSpec(Uri.parse(uri));
                InputStream inputStream = new DataSourceInputStream(dataSource, dataSpec);
                try {
                    readSampleGroups(new JsonReader(new InputStreamReader(inputStream, "UTF-8")), result);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading sample list: " + uri, e);
                    sawError = true;
                } finally {
                    Util.closeQuietly(dataSource);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<HeaderItemGroupEntry> result) {
            onSampleGroups(result, sawError);
        }

        private void readSampleGroups(JsonReader reader, List<HeaderItemGroupEntry> groups) throws IOException {
            reader.beginArray();
            while (reader.hasNext()) {
                readSampleGroup(reader, groups);
            }
            reader.endArray();
        }

        private void readSampleGroup(JsonReader reader, List<HeaderItemGroupEntry> groups) throws IOException {
            String groupName = "";
            ArrayList<HeaderItemEntry> samples = new ArrayList<>();

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "name":
                        groupName = reader.nextString();
                        break;
                    case "samples":
                        reader.beginArray();
                        while (reader.hasNext()) {
                            samples.add(readEntry(reader, false));
                        }
                        reader.endArray();
                        break;
                    case "_comment":
                        reader.nextString(); // Ignore.
                        break;
                    default:
                        throw new ParserException("Unsupported name: " + name);
                }
            }
            reader.endObject();

            HeaderItemGroupEntry group = getGroup(groupName, groups);
            group.samples.addAll(samples);
        }

        private HeaderItemEntry readEntry(JsonReader reader, boolean insidePlaylist) throws IOException {
            HeaderItemEntry headerItemEntry = null;
            String sampleName = null;
            String uri = null;
            String extension = null;
            UUID drmUuid = null;
            String drmLicenseUrl = null;
            String[] drmKeyRequestProperties = null;
            boolean preferExtensionDecoders = false;
            ArrayList<UriHeaderItemEntry> playlistSamples = null;

            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "name":
                        sampleName = reader.nextString();
                        break;
                    case "uri":
                        uri = reader.nextString();
                        break;
                    case "extension":
                        extension = reader.nextString();
                        break;
                    case "drm_scheme":
                        Assertions.checkState(!insidePlaylist, "Invalid attribute on nested item: drm_scheme");
                        drmUuid = getDrmUuid(reader.nextString());
                        break;
                    case "drm_license_url":
                        Assertions.checkState(!insidePlaylist, "Invalid attribute on nested item: drm_license_url");
                        drmLicenseUrl = reader.nextString();
                        break;
                    case "drm_key_request_properties":
                        Assertions.checkState(!insidePlaylist, "Invalid attribute on nested item: drm_key_request_properties");
                        ArrayList<String> drmKeyRequestPropertiesList = new ArrayList<>();
                        reader.beginObject();
                        while (reader.hasNext()) {
                            drmKeyRequestPropertiesList.add(reader.nextName());
                            drmKeyRequestPropertiesList.add(reader.nextString());
                        }
                        reader.endObject();
                        drmKeyRequestProperties = drmKeyRequestPropertiesList.toArray(new String[0]);
                        break;
                    case "prefer_extension_decoders":
                        Assertions.checkState(!insidePlaylist, "Invalid attribute on nested item: prefer_extension_decoders");
                        preferExtensionDecoders = reader.nextBoolean();
                        break;
                    case "playlist":
                        Assertions.checkState(!insidePlaylist, "Invalid nesting of playlists");
                        playlistSamples = new ArrayList<>();
                        reader.beginArray();
                        while (reader.hasNext()) {
                            playlistSamples.add((UriHeaderItemEntry) readEntry(reader, true));
                        }
                        reader.endArray();
                        break;
                    default:
                        throw new ParserException("Unsupported attribute name: " + name);
                }
            }

            reader.endObject();

            if (playlistSamples != null) {
                UriHeaderItemEntry[] playlistSamplesArray = playlistSamples.toArray(new UriHeaderItemEntry[playlistSamples.size()]);
                headerItemEntry = new PlaylistHeaderItemEntry(sampleName, drmUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders, playlistSamplesArray);
            } else {
                headerItemEntry = new UriHeaderItemEntry(sampleName, drmUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders, uri, extension);
            }

            return headerItemEntry;
        }

        private HeaderItemGroupEntry getGroup(String groupName, List<HeaderItemGroupEntry> groups) {
            for (int i = 0; i < groups.size(); i++) {
                if (Util.areEqual(groupName, groups.get(i).title)) {
                    return groups.get(i);
                }
            }
            HeaderItemGroupEntry group = new HeaderItemGroupEntry(groupName);
            groups.add(group);
            return group;
        }

        private UUID getDrmUuid(String typeString) throws ParserException {
            switch (typeString.toLowerCase()) {
                case "widevine":
                    return C.WIDEVINE_UUID;
                case "playready":
                    return C.PLAYREADY_UUID;
                default:
                    try {
                        return UUID.fromString(typeString);
                    } catch (RuntimeException e) {
                        throw new ParserException("Unsupported drm type: " + typeString);
                    }
            }
        }
    }


}
