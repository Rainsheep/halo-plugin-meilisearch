package com.rs.halo.plugin.meilisearch.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeilisearchSetting {
    public static final int DEFAULT_CROP_LENGTH = 80;

    // 保存一份现有配置，充当缓存
    public static MeilisearchSetting SETTING_CACHE =
        new MeilisearchSetting("http://meilisearch:7700",
            "95d031f029c0f93289791d39f01a7f42a2211973", DEFAULT_CROP_LENGTH);

    private String host;
    private String masterKey;
    private int cropLength;
}
