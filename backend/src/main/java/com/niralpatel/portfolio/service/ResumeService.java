package com.niralpatel.portfolio.service;

import com.niralpatel.portfolio.dto.ResumeInfo;
import com.niralpatel.portfolio.entity.Resume;
import com.niralpatel.portfolio.repository.ResumeRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResumeService {

    private static final long SINGLETON_ID = 1L;
    private static final long MAX_BYTES = 10L * 1024L * 1024L; // 10 MB

    private final ResumeRepository repository;

    public ResumeService(ResumeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<Resume> get() {
        return repository.findById(SINGLETON_ID);
    }

    @Transactional(readOnly = true)
    public ResumeInfo info() {
        return repository.findById(SINGLETON_ID)
                .map(r -> new ResumeInfo(true, r.getFilename(), r.getSizeBytes(), r.getUploadedAt()))
                .orElseGet(ResumeInfo::absent);
    }

    @Transactional
    public ResumeInfo replace(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file provided");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                    "Resume must be 10 MB or smaller");
        }

        // Accept PDF, DOC, DOCX. We rely on the file extension as the source of truth
        // because browser MIME types vary, then derive a sensible contentType to send back.
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        String contentType;
        if (name.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (name.endsWith(".docx")) {
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (name.endsWith(".doc")) {
            contentType = "application/msword";
        } else {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Only PDF, DOC, or DOCX files are supported");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read uploaded file", e);
        }

        Resume r = repository.findById(SINGLETON_ID).orElseGet(() -> {
            Resume fresh = new Resume();
            fresh.setId(SINGLETON_ID);
            return fresh;
        });
        String original = file.getOriginalFilename();
        r.setFilename(original != null && !original.isBlank() ? original : "resume.pdf");
        r.setContentType(contentType);
        r.setSizeBytes(bytes.length);
        r.setData(bytes);
        r.setUploadedAt(Instant.now());

        Resume saved = repository.save(r);
        return new ResumeInfo(true, saved.getFilename(), saved.getSizeBytes(), saved.getUploadedAt());
    }

    @Transactional
    public void delete() {
        repository.deleteById(SINGLETON_ID);
    }
}
