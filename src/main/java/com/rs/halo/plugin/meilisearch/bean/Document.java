package com.rs.halo.plugin.meilisearch.bean;

import java.time.Instant;
import java.util.List;
import lombok.Data;
import run.halo.app.search.post.PostDoc;
import run.halo.app.search.post.PostHit;

@Data
public class Document {
    private String name;
    private String title;
    private String excerpt;
    private String content;
    private String publishTimestamp;
    private String permalink;

    private static Document convertFromPostDoc(PostDoc post) {
        var doc = new Document();
        doc.name = post.name();
        doc.title = post.title();
        doc.excerpt = post.excerpt();
        doc.content = post.content();
        doc.publishTimestamp = post.publishTimestamp().toString();
        doc.permalink = post.permalink();
        return doc;
    }

    public static List<Document> convertFromPostDocList(List<PostDoc> posts) {
        return posts.stream().map(Document::convertFromPostDoc).toList();
    }

    private PostHit convertToPostHit() {
        var hit = new PostHit();
        hit.setName(name);
        hit.setTitle(title);
        hit.setContent(content);
        hit.setPublishTimestamp(Instant.parse(publishTimestamp));
        hit.setPermalink(permalink);
        return hit;
    }

    public static List<PostHit> convertToPostHitList(List<Document> docs) {
        return docs.stream().map(Document::convertToPostHit).toList();
    }
}