package com.rs.halo.plugin.meilisearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeilisearchPlugin extends BasePlugin {

    @Override
    public void start() {
        log.info("Meilisearch plugin started");
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
        // Do nothing for now
    }
}
