package com.niralpatel.portfolio.dto;

import com.niralpatel.portfolio.entity.SkillCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SkillRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 120) String name,

        @NotNull(message = "Category is required") SkillCategory category,

        Integer displayOrder) {
}
