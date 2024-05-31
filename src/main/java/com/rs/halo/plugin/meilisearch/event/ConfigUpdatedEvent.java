package com.rs.halo.plugin.meilisearch.event;

import org.springframework.context.ApplicationEvent;

public class ConfigUpdatedEvent extends ApplicationEvent {

    public ConfigUpdatedEvent(Object source) {
        super(source);
    }
}
