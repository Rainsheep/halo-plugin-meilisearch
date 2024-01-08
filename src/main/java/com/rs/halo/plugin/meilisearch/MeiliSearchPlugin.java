package com.rs.halo.plugin.meilisearch;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

/**
 * <p>Plugin main class to manage the lifecycle of the plugin.</p>
 * <p>This class must be public and have a public constructor.</p>
 * <p>Only one main class extending {@link BasePlugin} is allowed per plugin.</p>
 *
 * @author guqing
 * @since 1.0.0
 */
@Slf4j
@Component
public class MeiliSearchPlugin extends BasePlugin {

    public MeiliSearchPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        log.info("MeiliSearch plugin started");
    }

    @Override
    public void stop() {
        log.info("MeiliSearch plugin stop");
    }
}
