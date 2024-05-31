package com.rs.halo.plugin.meilisearch.config;

import com.fasterxml.jackson.databind.JsonNode;

public class MeilisearchSetting {
    private static final int DEFAULT_CROP_LENGTH = 80;
    private static final String DEFAULT_MASTER_KEY = "95d031f029c0f93289791d39f01a7f42a2211973";
    private static final String DEFAULT_HOST = "http://localhost:7700";

    public static boolean innerServiceEnable = true;
    public static String host = DEFAULT_HOST;
    public static String masterKey = DEFAULT_MASTER_KEY;
    public static int cropLength = DEFAULT_CROP_LENGTH;

    public static String getHost() {
        return innerServiceEnable ? DEFAULT_HOST : host;
    }

    public static String getMasterKey() {
        return innerServiceEnable ? DEFAULT_MASTER_KEY : masterKey;
    }

    public static void resetFromJsonNode(JsonNode jsonNode) {
        MeilisearchSetting.innerServiceEnable = jsonNode.path("innerServiceEnable").asBoolean();
        MeilisearchSetting.host = jsonNode.path("host").asText();
        MeilisearchSetting.masterKey = jsonNode.path("masterKey").asText();
        MeilisearchSetting.cropLength = jsonNode.path("cropLength").asInt(DEFAULT_CROP_LENGTH);
    }
}
