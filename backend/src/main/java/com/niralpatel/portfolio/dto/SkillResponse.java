package com.niralpatel.portfolio.dto;

import com.niralpatel.portfolio.entity.SkillCategory;

public record SkillResponse(Long id, String name, SkillCategory category, Integer displayOrder) {
}
