package com.niralpatel.portfolio.dto;

import jakarta.validation.constraints.Size;

public record SiteSettingsDto(
        boolean contactEnabled,
        @Size(max = 500) String contactDisabledMessage,
        @Size(max = 120) String ownerName,
        @Size(max = 200) String ownerTitle,
        boolean bookingEnabled) {
}
