package com.niralpatel.portfolio.dto;

import java.time.Instant;

/** Lightweight metadata returned to the admin UI (no bytes). */
public record ResumeInfo(boolean present, String filename, long sizeBytes, Instant uploadedAt) {

    public static ResumeInfo absent() {
        return new ResumeInfo(false, null, 0L, null);
    }
}
