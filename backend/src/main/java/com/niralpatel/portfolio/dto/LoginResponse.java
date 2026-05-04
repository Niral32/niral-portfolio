package com.niralpatel.portfolio.dto;

public record LoginResponse(String token, String type, long expiresInMs) {
}
