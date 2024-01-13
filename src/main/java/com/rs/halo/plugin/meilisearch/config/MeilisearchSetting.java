package com.rs.halo.plugin.meilisearch.config;

public class MeilisearchSetting {
    public static final int DEFAULT_CROP_LENGTH = 80;

    public static String HOST = "";
    public static String MASTER_KEY = "";
    public static int CROP_LENGTH = DEFAULT_CROP_LENGTH;

    public static void updateSetting(String host, String masterKey, int cropLength) {
        HOST = host;
        MASTER_KEY = masterKey;
        CROP_LENGTH = cropLength;
    }
}
