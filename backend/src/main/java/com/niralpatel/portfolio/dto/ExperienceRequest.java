package com.niralpatel.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExperienceRequest(
        @NotBlank(message = "Role title is required")
        @Size(max = 200) String roleTitle,

        @NotBlank(message = "Organization is required")
        @Size(max = 200) String organization,

        @NotBlank(message = "Summary is required")
        @Size(max = 4000) String summary,

        @Size(max = 64) String startPeriod,
        @Size(max = 64) String endPeriod,

        Integer displayOrder) {
}
