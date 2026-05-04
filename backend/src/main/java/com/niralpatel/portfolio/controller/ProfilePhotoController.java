package com.niralpatel.portfolio.controller;

import com.niralpatel.portfolio.entity.ProfilePhoto;
import com.niralpatel.portfolio.service.ProfilePhotoService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProfilePhotoController {

    private final ProfilePhotoService service;

    public ProfilePhotoController(ProfilePhotoService service) {
        this.service = service;
    }

    /** Public — streams the navbar profile photo. 404 if not uploaded yet. */
    @GetMapping("/api/identity/photo")
    public ResponseEntity<byte[]> get() {
        return service.get()
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(p.getContentType()))
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=300")
                        .body(p.getData()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/api/admin/identity/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) {
        service.replace(file);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/admin/identity/photo")
    public ResponseEntity<Void> delete() {
        service.delete();
        return ResponseEntity.noContent().build();
    }
}
