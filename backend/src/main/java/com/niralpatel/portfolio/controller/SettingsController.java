package com.niralpatel.portfolio.controller;

import com.niralpatel.portfolio.dto.SiteSettingsDto;
import com.niralpatel.portfolio.service.SiteSettingsService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class SettingsController {

    private final SiteSettingsService service;

    public SettingsController(SiteSettingsService service) {
        this.service = service;
    }

    @GetMapping("/api/settings")
    public SiteSettingsDto get() {
        return service.get();
    }

    @PutMapping("/api/admin/settings")
    public SiteSettingsDto update(@Valid @RequestBody SiteSettingsDto body) {
        return service.update(body);
    }
}
