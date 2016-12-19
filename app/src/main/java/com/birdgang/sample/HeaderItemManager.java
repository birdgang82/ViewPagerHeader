package com.birdgang.sample;

import com.birdgang.sample.model.HeaderItemEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdgang on 2016. 12. 16..
 */

public enum HeaderItemManager {
    INSTANCE;

    private List<HeaderItemEntry> headerItemEntries = null;

    private HeaderItemManager () {
        headerItemEntries = new ArrayList<>();
    }

    public List<HeaderItemEntry> getHeaderItemEntries() {
        return this.headerItemEntries;
    }

    public void setHeaderItemEntries(List<HeaderItemEntry> headerItemEntries) {
        this.headerItemEntries = headerItemEntries;
    }

    public HeaderItemEntry getHeaderItemEntryByPosition (int position) {
        if (null != headerItemEntries && headerItemEntries.size() > position) {
            return headerItemEntries.get(position);
        }
        return null;
    }

}
