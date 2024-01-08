package com.rs.halo.plugin.meilisearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.Searchable;
import com.rs.halo.plugin.meilisearch.bean.Document;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import run.halo.app.plugin.SettingFetcher;
import run.halo.app.search.SearchParam;
import run.halo.app.search.SearchResult;
import run.halo.app.search.post.PostDoc;
import run.halo.app.search.post.PostHit;
import run.halo.app.search.post.PostSearchService;

@Slf4j
@Service
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MeiliSearchService implements PostSearchService {

    private static final String[] highlightAttributes = {"title", "excerpt", "content"};
    private static final String[] cropAttributes = {"excerpt", "content"};
    private static final int DEFAULT_CONTENT_CROP_LENGTH = 80;

    private final ObjectMapper objectMapper;

    private final Index meiliSearchIndex;

    private final SettingFetcher settingFetcher;

    private int cropLengthCache = DEFAULT_CONTENT_CROP_LENGTH;

    @PostConstruct
    public void init() {
        cropLengthCache =
            settingFetcher.get("base").path("cropLength").asInt(DEFAULT_CONTENT_CROP_LENGTH);
    }

    @Override
    public SearchResult<PostHit> search(SearchParam searchParam) throws Exception {
        log.info("search keyword: {}", searchParam.getKeyword());
        SearchRequest searchRequest =
            SearchRequest.builder().q(searchParam.getKeyword()).limit(searchParam.getLimit())
                .attributesToCrop(cropAttributes).cropLength(cropLengthCache).cropMarker("")
                .attributesToHighlight(highlightAttributes)
                .highlightPreTag(searchParam.getHighlightPreTag())
                .highlightPostTag(searchParam.getHighlightPostTag()).build();

        Searchable searchResult = meiliSearchIndex.search(searchRequest);

        var hits = Document.convertToPostHitList(convert(searchResult.getHits()));
        var result = new SearchResult<PostHit>();
        result.setHits(hits);
        result.setTotal((long) searchResult.getHits().size());
        result.setKeyword(searchParam.getKeyword());
        result.setLimit(searchParam.getLimit());
        result.setProcessingTimeMillis(searchResult.getProcessingTimeMs());
        return result;
    }

    @Override
    public void addDocuments(List<PostDoc> list) throws Exception {
        log.info("add documents: {}", list.stream().map(PostDoc::title).toList());
        meiliSearchIndex.addDocumentsInBatches(
            objectMapper.writeValueAsString(Document.convertFromPostDocList(list)), list.size(),
            "name");
    }

    @Override
    public void removeDocuments(Set<String> names) throws Exception {
        log.info("remove documents: {}", names);
        meiliSearchIndex.deleteDocuments(names.stream().toList());
    }

    @Override
    public void removeAllDocuments() throws Exception {
        log.info("remove all documents");
        meiliSearchIndex.deleteAllDocuments();
    }

    private List<Document> convert(List<HashMap<String, Object>> hits) {
        return hits.stream()
            .map(hit -> objectMapper.convertValue(hit.get("_formatted"), Document.class)).toList();
    }
}






