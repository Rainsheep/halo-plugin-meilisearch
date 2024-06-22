package com.rs.halo.plugin.meilisearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.exceptions.MeilisearchException;
import com.meilisearch.sdk.model.Searchable;
import com.rs.halo.plugin.meilisearch.config.MeilisearchSetting;
import com.rs.halo.plugin.meilisearch.utils.IndexHolder;
import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import run.halo.app.search.HaloDocument;
import run.halo.app.search.SearchEngine;
import run.halo.app.search.SearchResult;

@Slf4j
@Service
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MeilisearchPostService implements SearchEngine {

    private static final String[] highlightAttributes =
        {"title", "description", "content", "categories", "tags"};
    private static final String[] searchAttributes = {"title", "description", "content"};
    private static final String[] cropAttributes = {"description", "content"};

    private final ObjectMapper objectMapper;

    @Override
    public void init() {
        // Do nothing
    }

    @Override
    public void addOrUpdate(Iterable<HaloDocument> iterable) {
        List<HaloDocument> documents = StreamSupport.stream(iterable.spliterator(), false).toList();
        List<String> titles = documents.stream().map(HaloDocument::getTitle).toList();
        log.info("add documents: {}", titles);
        documents.forEach(doc -> log.info("add document: {}", doc));

        try {
            String documentsJson = objectMapper.writeValueAsString(documents);
            log.info("add documents json: {}", documentsJson);
            IndexHolder.getIndex().addDocumentsInBatches(documentsJson);
        } catch (MeilisearchException | JsonProcessingException e) {
            log.error("add documents error, documents: {}", titles, e);
        }

    }

    @Override
    public void deleteDocument(Iterable<String> ids) {
        List<String> idList = StreamSupport.stream(ids.spliterator(), false).toList();
        log.info("remove documents: {}", idList);
        IndexHolder.getIndex().deleteDocuments(idList);
    }

    @Override
    public void deleteAll() {
        log.info("remove all documents");
        IndexHolder.getIndex().deleteAllDocuments();
    }

    @Override
    public SearchResult search(SearchOption searchOption) {
        log.info("search keyword: {}", searchOption.getKeyword());
        SearchRequest searchRequest =
            SearchRequest.builder()
                .q(searchOption.getKeyword())
                .limit(searchOption.getLimit())
                .attributesToCrop(cropAttributes)
                .cropLength(MeilisearchSetting.CROP_LENGTH)
                .cropMarker("")
                .attributesToSearchOn(searchAttributes)
                .attributesToHighlight(highlightAttributes)
                .highlightPreTag(searchOption.getHighlightPreTag())
                .highlightPostTag(searchOption.getHighlightPostTag())
                .build();

        Searchable searchResult = IndexHolder.getIndex().search(searchRequest);
        log.info("search result: {}", searchResult.getHits());
        var result = new SearchResult();
        result.setHits(convert(searchResult.getHits()));
        result.setTotal((long) searchResult.getHits().size());
        result.setKeyword(searchOption.getKeyword());
        result.setLimit(searchOption.getLimit());
        result.setProcessingTimeMillis(searchResult.getProcessingTimeMs());
        return result;
    }

    private List<HaloDocument> convert(List<HashMap<String, Object>> hits) {
        return hits.stream()
            .map(hit -> objectMapper.convertValue(hit.get("_formatted"), HaloDocument.class))
            .toList();
    }
}






