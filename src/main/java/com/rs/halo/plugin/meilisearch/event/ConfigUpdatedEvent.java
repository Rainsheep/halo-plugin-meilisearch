package com.rs.halo.plugin.meilisearch.event;

import com.rs.halo.plugin.meilisearch.config.MeilisearchSetting;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ConfigUpdatedEvent extends ApplicationEvent {
    private final MeilisearchSetting config;

    public ConfigUpdatedEvent(Object source, MeilisearchSetting config) {
        super(source);
        this.config = config;
    }
}
