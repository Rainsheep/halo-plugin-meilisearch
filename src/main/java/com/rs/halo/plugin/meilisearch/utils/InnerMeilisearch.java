package com.rs.halo.plugin.meilisearch.utils;

import com.meilisearch.sdk.exceptions.MeilisearchException;
import com.rs.halo.plugin.meilisearch.event.ConfigUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InnerMeilisearch {

    private final MeilisearchClientHolder meilisearchClientHolder;

    private boolean enable() {
        if (Boolean.parseBoolean(System.getenv("MEILISEARCH_DISABLE"))) {
            return false;
        }

        if (meilisearchClientHolder.getClient() == null) {
            return true;
        }

        try {
            meilisearchClientHolder.getClient().health();
        } catch (MeilisearchException e) {
            return true;
        }

        return false;
    }

    private boolean disable() {
        return !enable();
    }

    @EventListener
    private void loadInnerMeilisearch(ConfigUpdatedEvent event) {
        if (disable()) {
            return;
        }
        log.info("meiliSearch is disabled, prepare to download meiliSearch");
    }

    private void downloadMeiliSearch() {

    }
}
