package com.rs.halo.plugin.meilisearch.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.exceptions.MeilisearchException;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.halo.app.plugin.SettingFetcher;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MeiliSearchConfig {

    @Resource
    private SettingFetcher settingFetcher;

    @Bean
    public Index meiliSearchIndex() throws MeilisearchException {
        JsonNode baseSetting = settingFetcher.get("base");
        log.info("load meilisearch config: {}", baseSetting);
        return new Client(new Config(baseSetting.get("host").asText(),
            baseSetting.get("masterKey").asText())).index("halo_post");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
