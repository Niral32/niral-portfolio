package com.niralpatel.portfolio.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;

@Entity
@Table(name = "blog_posts")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    /** URL-safe slug, used as the public path /blog/{slug}. Unique. */
    @Column(nullable = false, length = 220, unique = true)
    private String slug;

    @Column(length = 500)
    private String excerpt;

    /** Markdown source. Rendered to HTML on the client. */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "content_markdown", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String contentMarkdown;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    /** Optional uploaded cover image bytes. If present, served at /api/blog/{slug}/cover. */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "cover_image_data", length = 16_777_215)
    private byte[] coverImageData;

    @Column(name = "cover_image_content_type", length = 100)
    private String coverImageContentType;

    @Column(name = "published", nullable = false)
    private boolean published = false;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "like_count", nullable = false)
    private long likeCount = 0;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }
    public String getContentMarkdown() { return contentMarkdown; }
    public void setContentMarkdown(String contentMarkdown) { this.contentMarkdown = contentMarkdown; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
    public byte[] getCoverImageData() { return coverImageData; }
    public void setCoverImageData(byte[] coverImageData) { this.coverImageData = coverImageData; }
    public String getCoverImageContentType() { return coverImageContentType; }
    public void setCoverImageContentType(String coverImageContentType) { this.coverImageContentType = coverImageContentType; }
}
