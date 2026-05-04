package com.niralpatel.portfolio.dto;

import java.time.Instant;

/** Full payload (used by both public detail view and admin). */
public record BlogPostDetail(
        Long id,
        String title,
        String slug,
        String excerpt,
        String contentMarkdown,
        String coverUrl,
        boolean hasCoverImage,
        boolean published,
        Instant publishedAt,
        Instant createdAt,
        Instant updatedAt,
        long likeCount) {
}
