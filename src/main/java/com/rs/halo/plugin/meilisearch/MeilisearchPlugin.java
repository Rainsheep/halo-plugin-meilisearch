package com.rs.halo.plugin.meilisearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

@Slf4j
@Component
public class MeilisearchPlugin extends BasePlugin {

    public MeilisearchPlugin(PluginContext pluginContext) {
        super(pluginContext);
    }

    @Override
    public void start() {
        log.info("Meilisearch plugin started");
        // 发现没有 meilisearch，则使用内置 meilisearch

    }

    @Override
    public void stop() {
        log.info("Meilisearch plugin stop");
    }
}
