package com.rs.halo.plugin.meilisearch.reconciler;

import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_CROP_LENGTH;

import com.rs.halo.plugin.meilisearch.config.MeilisearchSetting;
import com.rs.halo.plugin.meilisearch.utils.IndexHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ConfigMap;
import run.halo.app.extension.controller.Controller;
import run.halo.app.extension.controller.ControllerBuilder;
import run.halo.app.extension.controller.Reconciler;
import run.halo.app.plugin.ReactiveSettingFetcher;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeilisearchReconciler implements Reconciler<Reconciler.Request> {

    private final ReactiveSettingFetcher settingFetcher;

    public static final String DEFAULT_EMPTY_STRING = "";

    @Override
    public Result reconcile(Request request) {
        String name = request.name();
        if (!isMeilisearchSetting(name)) {
            return Result.doNotRetry();
        }
        loadPluginSetting();
        return Result.doNotRetry();
    }

    private void loadPluginSetting() {
        settingFetcher.get("base")
            .doOnSuccess(baseSetting -> {
                log.info("Meilisearch setting update: {}", baseSetting);
                MeilisearchSetting.updateSetting(
                    baseSetting.path("host").asText(DEFAULT_EMPTY_STRING),
                    baseSetting.path("masterKey").asText(DEFAULT_EMPTY_STRING),
                    baseSetting.path("cropLength").asInt(DEFAULT_CROP_LENGTH));
                IndexHolder.resetIndex();
                // todo update index document
            }).subscribe();
    }

    @Override
    public Controller setupWith(ControllerBuilder builder) {
        return builder.extension(new ConfigMap()).build();
    }

    private boolean isMeilisearchSetting(String name) {
        return "meilisearch-configmap".equals(name);
    }
}
