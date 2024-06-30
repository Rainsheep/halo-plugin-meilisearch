package com.rs.halo.plugin.meilisearch;

import com.rs.halo.plugin.meilisearch.reconciler.MeilisearchService;
import java.util.LinkedHashSet;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ConfigMap;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.SystemSetting;
import run.halo.app.infra.utils.JsonUtils;
import run.halo.app.plugin.BasePlugin;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeilisearchPlugin extends BasePlugin {

    private final ReactiveExtensionClient reactiveExtensionClient;

    private final MeilisearchService meilisearchService;

    @Override
    public void start() {
        log.info("Meilisearch plugin started");
        meilisearchService.loadPluginSetting();

        reactiveExtensionClient.get(ConfigMap.class, SystemSetting.SYSTEM_CONFIG)
            .flatMap(config -> {
                Map<String, String> configData = config.getData();
                String extensionPointEnabled =
                    configData.getOrDefault(SystemSetting.ExtensionPointEnabled.GROUP, "{}");
                SystemSetting.ExtensionPointEnabled extensionPointEnabledSetting =
                    JsonUtils.jsonToObject(extensionPointEnabled,
                        SystemSetting.ExtensionPointEnabled.class);
                LinkedHashSet<String> searchEngineSet =
                    extensionPointEnabledSetting.getOrDefault("search-engine",
                        new LinkedHashSet<>());
                // 加入到最前面
                LinkedHashSet<String> newSearchEngineSet = new LinkedHashSet<>();
                newSearchEngineSet.add("search-engine-meilisearch");
                newSearchEngineSet.addAll(searchEngineSet);
                extensionPointEnabledSetting.put("search-engine", newSearchEngineSet);
                configData.put(SystemSetting.ExtensionPointEnabled.GROUP,
                    JsonUtils.objectToJson(extensionPointEnabledSetting));
                return reactiveExtensionClient.update(config);
            }).subscribe();
    }

    @Override
    public void stop() {
        log.info("Meilisearch plugin stop");
        stopMeilisearch();
    }

    @Override
    public void delete() {
        log.info("Meilisearch plugin delete");
        stopMeilisearch();
    }


    private void stopMeilisearch() {
        reactiveExtensionClient.get(ConfigMap.class, SystemSetting.SYSTEM_CONFIG)
            .flatMap(config -> {
                Map<String, String> configData = config.getData();
                String extensionPointEnabled =
                    configData.getOrDefault(SystemSetting.ExtensionPointEnabled.GROUP, "{}");
                SystemSetting.ExtensionPointEnabled extensionPointEnabledSetting =
                    JsonUtils.jsonToObject(extensionPointEnabled,
                        SystemSetting.ExtensionPointEnabled.class);
                LinkedHashSet<String> searchEngineSet =
                    extensionPointEnabledSetting.getOrDefault("search-engine",
                        new LinkedHashSet<>());
                searchEngineSet.remove("search-engine-meilisearch");
                if (searchEngineSet.isEmpty()) {
                    extensionPointEnabledSetting.remove("search-engine");
                    if (extensionPointEnabledSetting.isEmpty()) {
                        configData.remove(SystemSetting.ExtensionPointEnabled.GROUP);
                    }
                } else {
                    extensionPointEnabledSetting.put("search-engine", searchEngineSet);
                    configData.put(SystemSetting.ExtensionPointEnabled.GROUP,
                        JsonUtils.objectToJson(extensionPointEnabledSetting));
                }
                return reactiveExtensionClient.update(config);
            }).subscribe();
    }
}
