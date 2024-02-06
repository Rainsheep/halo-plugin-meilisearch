package com.rs.halo.plugin.meilisearch.reconciler;

import static com.rs.halo.plugin.meilisearch.config.MeilisearchSetting.DEFAULT_CROP_LENGTH;

import cn.hutool.core.util.StrUtil;
import com.rs.halo.plugin.meilisearch.config.MeilisearchSetting;
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
        settingFetcher.get("base").doOnSuccess(baseSetting -> {
            log.info("Meilisearch setting update: {}", baseSetting);
            MeilisearchSetting newSetting =
                new MeilisearchSetting(baseSetting.path("host").asText(StrUtil.EMPTY),
                    baseSetting.path("masterKey").asText(StrUtil.EMPTY),
                    baseSetting.path("cropLength").asInt(DEFAULT_CROP_LENGTH));
            MeilisearchSetting.SETTING_CACHE = newSetting;
            applicationContext.publishEvent(newSetting);
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
