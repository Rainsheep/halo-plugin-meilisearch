package com.rs.halo.plugin.meilisearch;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

@Slf4j
@Component
public class MeilisearchPlugin extends BasePlugin {

    public MeilisearchPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        log.info("Meilisearch plugin started");
    }

    @Override
    public void stop() {
        log.info("Meilisearch plugin stop");
    }
}
