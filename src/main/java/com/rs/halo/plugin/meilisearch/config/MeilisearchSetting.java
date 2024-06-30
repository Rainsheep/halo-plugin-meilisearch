package com.rs.halo.plugin.meilisearch.config;

public class MeilisearchSetting {
    public static final int DEFAULT_CROP_LENGTH = 80;
    public static final String DEFAULT_HOST = "http://meilisearch:7700";
    public static final String DEFAULT_MASTER_KEY = "95d031f029c0f93289791d39f01a7f42a2211973";
    public static final boolean DEFAULT_SEARCH_UNPUBLISHED = false;
    public static final boolean DEFAULT_SEARCH_UNEXPOSED = false;
    public static final boolean DEFAULT_SEARCH_UNRECYCLED = false;

    public static String host = DEFAULT_HOST;
    public static String masterKey = DEFAULT_MASTER_KEY;
    public static int cropLength = DEFAULT_CROP_LENGTH;
    public static boolean searchUnpublished = DEFAULT_SEARCH_UNPUBLISHED;
    public static boolean searchUnexposed = DEFAULT_SEARCH_UNEXPOSED;
    public static boolean searchRecycled = DEFAULT_SEARCH_UNRECYCLED;

    public static void updateSetting(String host, String masterKey, int cropLength,
        boolean searchUnpublished, boolean searchUnexposed, boolean searchRecycled) {
        MeilisearchSetting.host = host;
        MeilisearchSetting.masterKey = masterKey;
        MeilisearchSetting.cropLength = cropLength;
        MeilisearchSetting.searchUnpublished = searchUnpublished;
        MeilisearchSetting.searchUnexposed = searchUnexposed;
        MeilisearchSetting.searchRecycled = searchRecycled;
    }
}
