package com.birdgang.sample.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdgang on 2016. 12. 15..
 */
public class HeaderItemGroupEntry {

    public final String title;
    public final List<HeaderItemEntry> samples;

    public HeaderItemGroupEntry(String title) {
        this.title = title;
        this.samples = new ArrayList<>();
    }

}
