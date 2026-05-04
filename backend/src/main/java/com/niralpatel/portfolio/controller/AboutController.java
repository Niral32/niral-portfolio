package com.niralpatel.portfolio.controller;

import com.niralpatel.portfolio.dto.AboutContentDto;
import com.niralpatel.portfolio.service.AboutContentService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class AboutController {

    private final AboutContentService service;

    public AboutController(AboutContentService service) {
        this.service = service;
    }

    /** Public read for the About page. */
    @GetMapping("/api/about")
    public AboutContentDto get() {
        return service.get();
    }

    /** Admin write to update the About page. */
    @PutMapping("/api/admin/about")
    public AboutContentDto update(@Valid @RequestBody AboutContentDto body) {
        return service.update(body);
    }
}
