package com.niralpatel.portfolio.service;

import com.niralpatel.portfolio.dto.BlogPostDetail;
import com.niralpatel.portfolio.dto.BlogPostRequest;
import com.niralpatel.portfolio.dto.BlogPostSummary;
import com.niralpatel.portfolio.entity.BlogPost;
import com.niralpatel.portfolio.repository.BlogPostRepository;

import java.io.IOException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BlogPostService {

    private final BlogPostRepository repository;

    public BlogPostService(BlogPostRepository repository) {
        this.repository = repository;
    }

    /** Public — only published posts, newest first. */
    @Transactional(readOnly = true)
    public List<BlogPostSummary> listPublished() {
        return repository.findAllByPublishedTrueOrderByPublishedAtDesc().stream()
                .map(this::toSummary)
                .toList();
    }

    /** Admin — all posts including drafts, newest first. */
    @Transactional(readOnly = true)
    public List<BlogPostSummary> listAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toSummary)
                .toList();
    }

    /** Public — by slug. 404s if missing or unpublished. */
    @Transactional(readOnly = true)
    public BlogPostDetail getPublishedBySlug(String slug) {
        BlogPost p = repository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        if (!p.isPublished()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return toDetail(p);
    }

    /** Admin — by id, includes drafts. */
    @Transactional(readOnly = true)
    public BlogPostDetail getById(Long id) {
        return toDetail(load(id));
    }

    @Transactional
    public BlogPostDetail create(BlogPostRequest req) {
        BlogPost p = new BlogPost();
        apply(p, req, true);
        return toDetail(repository.save(p));
    }

    @Transactional
    public BlogPostDetail update(Long id, BlogPostRequest req) {
        BlogPost p = load(id);
        apply(p, req, false);
        return toDetail(p);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        repository.deleteById(id);
    }

    /** Returns the post entity (with bytes loaded) for the cover endpoint. */
    @Transactional(readOnly = true)
    public BlogPost loadEntityBySlug(String slug) {
        BlogPost p = repository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        if (!p.isPublished()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        // Touch the lazy field while still in the transaction so it's loaded.
        if (p.getCoverImageData() != null) {
            int unused = p.getCoverImageData().length;
        }
        return p;
    }

    @Transactional
    public void uploadCoverImage(Long id, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file provided");
        }
        if (file.getSize() > 5L * 1024L * 1024L) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Cover image must be 5 MB or smaller");
        }
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        String contentType;
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) contentType = "image/jpeg";
        else if (name.endsWith(".png")) contentType = "image/png";
        else if (name.endsWith(".webp")) contentType = "image/webp";
        else throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only JPG, PNG, or WebP supported");

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read uploaded file", e);
        }

        BlogPost p = load(id);
        p.setCoverImageData(bytes);
        p.setCoverImageContentType(contentType);
    }

    @Transactional
    public void clearCoverImage(Long id) {
        BlogPost p = load(id);
        p.setCoverImageData(null);
        p.setCoverImageContentType(null);
    }

    @Transactional
    public long like(Long id) {
        BlogPost p = load(id);
        if (!p.isPublished()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        repository.incrementLikes(id);
        return p.getLikeCount() + 1;
    }

    private void apply(BlogPost p, BlogPostRequest req, boolean isCreate) {
        p.setTitle(req.title().trim());

        // Slug: use provided, else slugify the title. Ensure uniqueness on creates.
        String slugCandidate = req.slug() == null || req.slug().isBlank() ? slugify(req.title()) : slugify(req.slug());
        if (isCreate || !slugCandidate.equals(p.getSlug())) {
            String unique = slugCandidate;
            int i = 2;
            while (repository.existsBySlug(unique) && (p.getId() == null || !unique.equals(p.getSlug()))) {
                unique = slugCandidate + "-" + i++;
            }
            p.setSlug(unique);
        }

        p.setExcerpt(req.excerpt() == null ? "" : req.excerpt().trim());
        p.setContentMarkdown(req.contentMarkdown());
        p.setCoverUrl(req.coverUrl() == null || req.coverUrl().isBlank() ? null : req.coverUrl().trim());

        boolean newPublished = Boolean.TRUE.equals(req.published());
        if (newPublished && !p.isPublished()) {
            p.setPublishedAt(Instant.now());
        }
        p.setPublished(newPublished);
    }

    private BlogPost load(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    private BlogPostSummary toSummary(BlogPost p) {
        return new BlogPostSummary(
                p.getId(), p.getTitle(), p.getSlug(),
                p.getExcerpt(), p.getCoverUrl(),
                p.getCoverImageContentType() != null,
                p.getPublishedAt(), p.getLikeCount());
    }

    private BlogPostDetail toDetail(BlogPost p) {
        return new BlogPostDetail(
                p.getId(), p.getTitle(), p.getSlug(),
                p.getExcerpt(), p.getContentMarkdown(), p.getCoverUrl(),
                p.getCoverImageContentType() != null,
                p.isPublished(), p.getPublishedAt(),
                p.getCreatedAt(), p.getUpdatedAt(), p.getLikeCount());
    }

    /** Tiny slug helper: lowercase, ascii-only, hyphenated. */
    private String slugify(String input) {
        String n = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        n = n.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return n.isEmpty() ? "post" : n;
    }
}
