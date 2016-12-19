package com.birdgang.sample.model;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.birdgang.sample.IntentParams;
import com.birdgang.sample.ui.MainActivity;

import java.util.UUID;

/**
 * Created by birdgang on 2016. 12. 15..
 */

public class HeaderItemEntry implements Parcelable {

    private String mediaType;

    public enum HeaderMediaType {
        URI_HEADER("URI_HEADER"),
        PLAYLIST_HEADER("PLAYLIST_HEADER");

        private String type;

        HeaderMediaType (String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public final String name;
    public final boolean preferExtensionDecoders;
    public final UUID drmSchemeUuid;
    public final String drmLicenseUrl;
    public final String[] drmKeyRequestProperties;

    public HeaderItemEntry(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                  String[] drmKeyRequestProperties, boolean preferExtensionDecoders) {
        this.name = name;
        this.drmSchemeUuid = drmSchemeUuid;
        this.drmLicenseUrl = drmLicenseUrl;
        this.drmKeyRequestProperties = drmKeyRequestProperties;
        this.preferExtensionDecoders = preferExtensionDecoders;
    }

    public Intent buildIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(IntentParams.PARAMS_PREFER_EXTENSION_DECODERS, preferExtensionDecoders);
        if (drmSchemeUuid != null) {
            intent.putExtra(IntentParams.PARAMS_DRM_SCHEME_UUID_EXTRA, drmSchemeUuid.toString());
            intent.putExtra(IntentParams.PARAMS_DRM_LICENSE_URL, drmLicenseUrl);
            intent.putExtra(IntentParams.PARAMS_DRM_KEY_REQUEST_PROPERTIES, drmKeyRequestProperties);
        }
        return intent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeByte(this.preferExtensionDecoders ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.drmSchemeUuid);
        dest.writeString(this.drmLicenseUrl);
        dest.writeStringArray(this.drmKeyRequestProperties);
    }

    protected HeaderItemEntry(Parcel in) {
        this.name = in.readString();
        this.preferExtensionDecoders = in.readByte() != 0;
        this.drmSchemeUuid = (UUID) in.readSerializable();
        this.drmLicenseUrl = in.readString();
        this.drmKeyRequestProperties = in.createStringArray();
    }

    public static final Parcelable.Creator<HeaderItemEntry> CREATOR = new Parcelable.Creator<HeaderItemEntry>() {
        @Override
        public HeaderItemEntry createFromParcel(Parcel source) {
            return new HeaderItemEntry(source);
        }

        @Override
        public HeaderItemEntry[] newArray(int size) {
            return new HeaderItemEntry[size];
        }
    };


    @Override
    public String toString() {
        return "name : " + name + " , preferExtensionDecoders : " + preferExtensionDecoders + " , drmSchemeUuid : " + drmSchemeUuid
                + " , drmLicenseUrl : " + drmLicenseUrl + " , drmKeyRequestProperties : " + drmKeyRequestProperties;
    }

}