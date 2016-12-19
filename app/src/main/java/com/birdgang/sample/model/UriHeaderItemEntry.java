package com.birdgang.sample.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.birdgang.sample.IntentAction;
import com.birdgang.sample.IntentParams;

import java.util.UUID;

/**
 * Created by birdgang on 2016. 12. 15..
 */

public class UriHeaderItemEntry extends HeaderItemEntry {

    public final String uri;
    public final String extension;


    public UriHeaderItemEntry(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                     String[] drmKeyRequestProperties, boolean preferExtensionDecoders, String uri,
                     String extension) {
        super(name, drmSchemeUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders);
        this.uri = uri;
        this.extension = extension;
    }


    @Override
    public Intent buildIntent(Context context) {
        return super.buildIntent(context)
                .setData(Uri.parse(uri))
                .putExtra(IntentParams.PARAMS_EXTENSION_EXTRA, extension)
                .setAction(IntentAction.INTENT_ACTION_VIEW);
    }


}