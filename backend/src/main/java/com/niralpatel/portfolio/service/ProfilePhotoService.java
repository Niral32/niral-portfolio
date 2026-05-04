package com.niralpatel.portfolio.service;

import com.niralpatel.portfolio.entity.ProfilePhoto;
import com.niralpatel.portfolio.repository.ProfilePhotoRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfilePhotoService {

    private static final long SINGLETON_ID = 1L;
    private static final long MAX_BYTES = 5L * 1024L * 1024L; // 5 MB cap for profile photos

    private final ProfilePhotoRepository repository;

    public ProfilePhotoService(ProfilePhotoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<ProfilePhoto> get() {
        return repository.findById(SINGLETON_ID);
    }

    @Transactional
    public void replace(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file provided");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                    "Photo must be 5 MB or smaller");
        }

        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        String contentType;
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (name.endsWith(".png")) {
            contentType = "image/png";
        } else if (name.endsWith(".webp")) {
            contentType = "image/webp";
        } else {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Only JPG, PNG, or WebP images are supported");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read uploaded file", e);
        }

        ProfilePhoto p = repository.findById(SINGLETON_ID).orElseGet(() -> {
            ProfilePhoto fresh = new ProfilePhoto();
            fresh.setId(SINGLETON_ID);
            return fresh;
        });
        p.setFilename(file.getOriginalFilename() == null ? "photo" : file.getOriginalFilename());
        p.setContentType(contentType);
        p.setSizeBytes(bytes.length);
        p.setData(bytes);
        p.setUploadedAt(Instant.now());
        repository.save(p);
    }

    @Transactional
    public void delete() {
        repository.deleteById(SINGLETON_ID);
    }
}
