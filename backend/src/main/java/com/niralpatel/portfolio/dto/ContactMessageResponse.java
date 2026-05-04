package com.niralpatel.portfolio.dto;

import java.time.Instant;

public record ContactMessageResponse(Long id, String name, String email, String subject, Instant createdAt) {
}
