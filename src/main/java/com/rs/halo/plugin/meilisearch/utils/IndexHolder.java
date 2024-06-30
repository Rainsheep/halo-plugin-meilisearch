package com.rs.halo.plugin.meilisearch.utils;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.exceptions.MeilisearchException;
import com.meilisearch.sdk.model.Settings;
import com.rs.halo.plugin.meilisearch.config.MeilisearchSetting;

public class IndexHolder {

    private volatile static Index index;

    public static Index getIndex() throws MeilisearchException {
        if (index == null) {
            synchronized (IndexHolder.class) {
                if (index == null) {
                    index = generateIndex();
                }
            }
        }
        return index;
    }

    public static void resetIndex() {
        index = null;
    }

    private static Index generateIndex() throws MeilisearchException {
        Client client =
            new Client(new Config(MeilisearchSetting.host, MeilisearchSetting.masterKey));
        Index index = client.index("halo_post");
        Settings settings = new Settings();
        settings.setFilterableAttributes(new String[] {"published", "recycled", "exposed"});
        index.updateSettings(settings);
        return index;
    }
}
