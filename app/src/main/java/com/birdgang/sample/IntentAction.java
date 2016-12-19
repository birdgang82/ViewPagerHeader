package com.birdgang.sample;

/**
 * Created by birdgang on 2016. 11. 25..
 */
public class IntentAction {

    public static final String INTENT_ACTION_VIEW = buildPkgString("action.VIEW");
    public static final String INTENT_ACTION_VIEW_LIST = buildPkgString("action.VIEW_LIST");


    public static final String INTENT_ACTION_MEDIA_SCAN_START = buildPkgString("action.scanstart");
    public static final String INTENT_ACTION_MEDIA_SCAN_STOP = buildPkgString("action.scanstop");


    public static String buildPkgString(String string) {
        return GlobalApplication.APPLICATION_ID + "." + string;
    }

}
