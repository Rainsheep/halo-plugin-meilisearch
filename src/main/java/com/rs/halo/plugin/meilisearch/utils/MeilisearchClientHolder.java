package com.rs.halo.plugin.meilisearch.utils;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.rs.halo.plugin.meilisearch.event.ConfigUpdatedEvent;
import lombok.Getter;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MeilisearchClientHolder {

    private Client client;
    private Index index;

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @EventListener
    public void resetIndex(ConfigUpdatedEvent event) {
        client =
            new Client(new Config(event.getConfig().getHost(), event.getConfig().getMasterKey()));
        index = client.index("halo_post");
    }
}
