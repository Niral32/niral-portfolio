package com.niralpatel.portfolio.controller;

import com.niralpatel.portfolio.dto.ResumeInfo;
import com.niralpatel.portfolio.entity.Resume;
import com.niralpatel.portfolio.service.ResumeService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ResumeController {

    private final ResumeService service;

    public ResumeController(ResumeService service) {
        this.service = service;
    }

    /** Public — streams the resume PDF inline so it opens in the browser tab. */
    @GetMapping("/api/resume")
    public ResponseEntity<byte[]> download() {
        return service.get()
                .map(r -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(r.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + r.getFilename() + "\"")
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                        .body(r.getData()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Admin — lightweight metadata for the admin UI (no bytes). */
    @GetMapping("/api/admin/resume/info")
    public ResumeInfo info() {
        return service.info();
    }

    /** Admin — upload (or replace) the resume PDF. */
    @PostMapping(value = "/api/admin/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResumeInfo upload(@RequestParam("file") MultipartFile file) {
        return service.replace(file);
    }

    /** Admin — remove the stored resume. */
    @DeleteMapping("/api/admin/resume")
    public ResponseEntity<Void> delete() {
        service.delete();
        return ResponseEntity.noContent().build();
    }
}
