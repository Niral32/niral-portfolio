package com.niralpatel.portfolio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "portfolio.cors")
public record PortfolioCorsProperties(String allowedOrigins) {
}
