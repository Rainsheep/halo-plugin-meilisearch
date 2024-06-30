package com.rs.halo.plugin.meilisearch.reconciler;

import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_CROP_LENGTH;
import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_HOST;
import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_MASTER_KEY;
import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_SEARCH_UNEXPOSED;
import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_SEARCH_UNPUBLISHED;
import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_SEARCH_UNRECYCLED;

import com.fasterxml.jackson.databind.JsonNode;
import com.rs.halo.plugin.meilisearch.config.MeilisearchSetting;
import com.rs.halo.plugin.meilisearch.utils.IndexHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.PluginConfigUpdatedEvent;
import run.halo.app.plugin.ReactiveSettingFetcher;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeilisearchService {

    private final ReactiveSettingFetcher settingFetcher;

    public void loadPluginSetting() {
        settingFetcher.get("base")
            .doOnSuccess(this::updateSettingCache)
            .subscribe();
    }

    private void updateSettingCache(JsonNode settings) {
        log.info("update plugin settings: {}", settings);
        MeilisearchSetting.updateSetting(
            settings.path("host").asText(DEFAULT_HOST),
            settings.path("masterKey").asText(DEFAULT_MASTER_KEY),
            settings.path("cropLength").asInt(DEFAULT_CROP_LENGTH),
            settings.path("searchUnpublished").asBoolean(DEFAULT_SEARCH_UNPUBLISHED),
            settings.path("searchUnexposed").asBoolean(DEFAULT_SEARCH_UNEXPOSED),
            settings.path("searchRecycled").asBoolean(DEFAULT_SEARCH_UNRECYCLED)
        );
        IndexHolder.resetIndex();
        // todo update index document
    }

    @EventListener
    public void onPluginConfigUpdate(PluginConfigUpdatedEvent event) {
        updateSettingCache(event.getNewConfig().get("base"));
    }
}
