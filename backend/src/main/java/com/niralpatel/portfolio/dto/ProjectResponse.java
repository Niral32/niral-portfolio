package com.niralpatel.portfolio.dto;

public record ProjectResponse(
        Long id,
        String title,
        String description,
        String techStack,
        String linkUrl,
        Integer displayOrder) {
}
