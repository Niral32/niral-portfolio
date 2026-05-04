package com.niralpatel.portfolio.controller;

import com.niralpatel.portfolio.dto.BlogPostDetail;
import com.niralpatel.portfolio.dto.BlogPostRequest;
import com.niralpatel.portfolio.dto.BlogPostSummary;
import com.niralpatel.portfolio.service.BlogPostService;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/blog")
public class AdminBlogController {

    private final BlogPostService service;

    public AdminBlogController(BlogPostService service) {
        this.service = service;
    }

    @GetMapping
    public List<BlogPostSummary> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public BlogPostDetail get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BlogPostDetail create(@Valid @RequestBody BlogPostRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public BlogPostDetail update(@PathVariable Long id, @Valid @RequestBody BlogPostRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        service.uploadCoverImage(id, file);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/cover")
    public ResponseEntity<Void> clearCover(@PathVariable Long id) {
        service.clearCoverImage(id);
        return ResponseEntity.noContent().build();
    }
}
