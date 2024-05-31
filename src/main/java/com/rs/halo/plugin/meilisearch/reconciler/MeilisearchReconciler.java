package com.rs.halo.plugin.meilisearch.reconciler;

import com.rs.halo.plugin.meilisearch.config.MeilisearchSetting;
import com.rs.halo.plugin.meilisearch.event.ConfigUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
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

    private final ApplicationContext applicationContext;

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
        settingFetcher.get("base").doOnSuccess(settings -> {
            log.info("Meilisearch setting update: {}", settings);
            MeilisearchSetting.resetFromJsonNode(settings);
            applicationContext.publishEvent(new ConfigUpdatedEvent(this));
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
