package com.niralpatel.portfolio.dto;

import java.time.Instant;

/** Card view returned by the public blog list endpoint. No body. */
public record BlogPostSummary(
        Long id,
        String title,
        String slug,
        String excerpt,
        String coverUrl,
        boolean hasCoverImage,
        Instant publishedAt,
        long likeCount) {
}
