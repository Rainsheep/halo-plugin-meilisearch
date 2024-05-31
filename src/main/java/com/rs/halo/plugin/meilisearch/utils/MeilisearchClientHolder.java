package com.rs.halo.plugin.meilisearch.utils;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.rs.halo.plugin.meilisearch.config.MeilisearchSetting;
import com.rs.halo.plugin.meilisearch.event.ConfigUpdatedEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class MeilisearchClientHolder {

    private Client client;
    private Index index;

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @EventListener(ConfigUpdatedEvent.class)
    private void resetIndex() {
        log.info("reset meilisearch client and index");
        client =
            new Client(new Config(MeilisearchSetting.getHost(), MeilisearchSetting.getMasterKey()));
        index = client.index("halo_post");
    }
}
