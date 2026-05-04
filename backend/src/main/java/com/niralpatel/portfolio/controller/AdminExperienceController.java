package com.niralpatel.portfolio.controller;

import com.niralpatel.portfolio.dto.ExperienceRequest;
import com.niralpatel.portfolio.dto.ExperienceResponse;
import com.niralpatel.portfolio.service.ExperienceService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/experience")
public class AdminExperienceController {

    private final ExperienceService experienceService;

    public AdminExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExperienceResponse create(@Valid @RequestBody ExperienceRequest req) {
        return experienceService.create(req);
    }

    @PutMapping("/{id}")
    public ExperienceResponse update(@PathVariable Long id, @Valid @RequestBody ExperienceRequest req) {
        return experienceService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        experienceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
