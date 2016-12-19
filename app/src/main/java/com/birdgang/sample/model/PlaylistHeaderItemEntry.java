package com.birdgang.sample.model;

import android.content.Context;
import android.content.Intent;

import com.birdgang.sample.IntentAction;
import com.birdgang.sample.IntentParams;

import java.util.UUID;

/**
 * Created by birdgang on 2016. 12. 15..
 */
public class PlaylistHeaderItemEntry extends HeaderItemEntry {

    public final UriHeaderItemEntry[] children;

    public PlaylistHeaderItemEntry(String name, UUID drmSchemeUuid, String drmLicenseUrl, String[] drmKeyRequestProperties, boolean preferExtensionDecoders, UriHeaderItemEntry... children) {
        super(name, drmSchemeUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders);
        this.children = children;
    }


    @Override
    public Intent buildIntent(Context context) {
        String[] uris = new String[children.length];
        String[] extensions = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            uris[i] = children[i].uri;
            extensions[i] = children[i].extension;
        }
        return super.buildIntent(context)
                .putExtra(IntentParams.PARAMS_URI_LIST_EXTRA, uris)
                .putExtra(IntentParams.PARAMS_EXTENSION_LIST_EXTRA, extensions)
                .setAction(IntentAction.INTENT_ACTION_VIEW_LIST);
    }

}