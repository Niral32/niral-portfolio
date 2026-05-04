package com.niralpatel.portfolio.dto;

public record ExperienceResponse(
        Long id,
        String roleTitle,
        String organization,
        String summary,
        String startPeriod,
        String endPeriod,
        Integer displayOrder) {
}
