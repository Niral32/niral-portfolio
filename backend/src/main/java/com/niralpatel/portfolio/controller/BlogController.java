package com.niralpatel.portfolio.controller;

import com.niralpatel.portfolio.dto.BlogPostDetail;
import com.niralpatel.portfolio.dto.BlogPostSummary;
import com.niralpatel.portfolio.entity.BlogPost;
import com.niralpatel.portfolio.service.BlogPostService;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    private final BlogPostService service;

    public BlogController(BlogPostService service) {
        this.service = service;
    }

    @GetMapping
    public List<BlogPostSummary> list() {
        return service.listPublished();
    }

    @GetMapping("/{slug}")
    public BlogPostDetail get(@PathVariable String slug) {
        return service.getPublishedBySlug(slug);
    }

    @PostMapping("/{id}/like")
    public Map<String, Long> like(@PathVariable Long id) {
        long count = service.like(id);
        return Map.of("likeCount", count);
    }

    /** Streams the uploaded cover image. 404 if no upload (frontend then falls back to coverUrl). */
    @GetMapping("/{slug}/cover")
    public ResponseEntity<byte[]> cover(@PathVariable String slug) {
        BlogPost p = service.loadEntityBySlug(slug);
        if (p.getCoverImageData() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(p.getCoverImageContentType()))
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=300")
                .body(p.getCoverImageData());
    }
}
