package com.niralpatel.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200) String title,

        @NotBlank(message = "Description is required")
        @Size(max = 2000) String description,

        @NotBlank(message = "Tech stack is required")
        @Size(max = 500) String techStack,

        @Size(max = 500) String linkUrl,

        Integer displayOrder) {
}
