package com.rs.halo.plugin.meilisearch.utils;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.exceptions.MeilisearchException;
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
            new Client(new Config(MeilisearchSetting.HOST, MeilisearchSetting.MASTER_KEY));
        return client.index("halo_post");
    }
}
