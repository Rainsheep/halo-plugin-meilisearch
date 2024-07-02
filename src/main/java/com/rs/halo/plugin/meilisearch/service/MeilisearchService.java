package com.rs.halo.plugin.meilisearch.service;

import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_CROP_LENGTH;
import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_HOST;
import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_MASTER_KEY;
import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_SEARCH_UNEXPOSED;
import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_SEARCH_UNPUBLISHED;
import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_SEARCH_UNRECYCLED;

import com.fasterxml.jackson.databind.JsonNode;
import com.rs.halo.plugin.meilisearch.config.MeilisearchSetting;
import com.rs.halo.plugin.meilisearch.utils.IndexHolder;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.PluginConfigUpdatedEvent;
import run.halo.app.plugin.SettingFetcher;
import run.halo.app.plugin.event.PluginStartedEvent;
import run.halo.app.search.event.HaloDocumentRebuildRequestEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeilisearchService implements DisposableBean {

    private final SettingFetcher settingFetcher;

    private final ApplicationEventPublisher eventPublisher;

    private void updateSettingCache(JsonNode settings) {
        boolean needRefresh = false;
        var newHost = settings.path("host").asText(DEFAULT_HOST);
        if (!Objects.equals(MeilisearchSetting.host, newHost)) {
            needRefresh = true;
        }

        var newMasterKey = settings.path("masterKey").asText(DEFAULT_MASTER_KEY);
        if (!Objects.equals(MeilisearchSetting.masterKey, newMasterKey)) {
            needRefresh = true;
        }

        MeilisearchSetting.updateSetting(
            newHost,
            newMasterKey,
            settings.path("cropLength").asInt(DEFAULT_CROP_LENGTH),
            settings.path("searchUnpublished").asBoolean(DEFAULT_SEARCH_UNPUBLISHED),
            settings.path("searchUnexposed").asBoolean(DEFAULT_SEARCH_UNEXPOSED),
            settings.path("searchRecycled").asBoolean(DEFAULT_SEARCH_UNRECYCLED)
        );

        IndexHolder.resetIndex();

        if (needRefresh) {
            log.info("Request to rebuild document index due to plugin setting change.");
            eventPublisher.publishEvent(new HaloDocumentRebuildRequestEvent(this));
        }
    }

    @EventListener
    void onPluginConfigUpdate(PluginConfigUpdatedEvent event) {
        log.info("Detected plugin setting change, reloading plugin setting.");
        updateSettingCache(event.getNewConfig().get("base"));
    }

    @EventListener
    void onPluginStartedEvent(PluginStartedEvent event) {
        // TODO Initialize after plugin started
        log.info("Initializing plugin setting for the first startup.");
        var settings = this.settingFetcher.get("base");
        this.updateSettingCache(settings);
    }

    @Override
    public void destroy() {
        // reset configuration
        MeilisearchSetting.resetSetting();
        IndexHolder.resetIndex();
    }
}
