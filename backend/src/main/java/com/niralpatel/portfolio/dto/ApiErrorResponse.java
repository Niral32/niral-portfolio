package com.niralpatel.portfolio.dto;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(Instant timestamp, int status, String error, String message, List<String> details) {
}
