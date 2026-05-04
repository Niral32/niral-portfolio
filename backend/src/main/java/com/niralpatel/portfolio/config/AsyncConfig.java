package com.niralpatel.portfolio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables {@code @Async} so background work (e.g. sending notification emails)
 * doesn't block the request thread serving POST /api/contact.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
