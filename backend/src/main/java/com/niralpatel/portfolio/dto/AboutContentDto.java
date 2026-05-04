package com.niralpatel.portfolio.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AboutContentDto(
        @NotNull
        @Size(max = 4000) String summary,

        @Size(max = 4000) String educationHtml,

        @Size(max = 4000) String passions) {
}
