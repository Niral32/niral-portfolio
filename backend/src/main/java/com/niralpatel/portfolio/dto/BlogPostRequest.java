package com.niralpatel.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlogPostRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 220) String slug,
        @Size(max = 500) String excerpt,
        @NotBlank String contentMarkdown,
        @Size(max = 500) String coverUrl,
        Boolean published) {
}
