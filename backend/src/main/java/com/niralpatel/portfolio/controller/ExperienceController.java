package com.niralpatel.portfolio.controller;

import com.niralpatel.portfolio.dto.ExperienceResponse;
import com.niralpatel.portfolio.service.ExperienceService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/experience")
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @GetMapping
    public List<ExperienceResponse> list() {
        return experienceService.findAllOrdered();
    }
}
