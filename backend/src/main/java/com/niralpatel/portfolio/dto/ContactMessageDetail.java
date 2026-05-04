package com.niralpatel.portfolio.dto;

import java.time.Instant;

/** Full contact message payload returned to authenticated admin endpoints. */
public record ContactMessageDetail(
        Long id,
        String name,
        String email,
        String subject,
        String message,
        Instant createdAt,
        Instant readAt) {
}
