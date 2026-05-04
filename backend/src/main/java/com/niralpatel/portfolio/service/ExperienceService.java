package com.niralpatel.portfolio.service;

import com.niralpatel.portfolio.dto.ExperienceRequest;
import com.niralpatel.portfolio.dto.ExperienceResponse;
import com.niralpatel.portfolio.entity.Experience;
import com.niralpatel.portfolio.repository.ExperienceRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    public ExperienceService(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponse> findAllOrdered() {
        return experienceRepository.findAllByOrderByDisplayOrderAscIdAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ExperienceResponse create(ExperienceRequest req) {
        Experience e = new Experience();
        apply(e, req);
        return toResponse(experienceRepository.save(e));
    }

    @Transactional
    public ExperienceResponse update(Long id, ExperienceRequest req) {
        Experience e = experienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Experience not found"));
        apply(e, req);
        return toResponse(e);
    }

    @Transactional
    public void delete(Long id) {
        if (!experienceRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Experience not found");
        }
        experienceRepository.deleteById(id);
    }

    private void apply(Experience e, ExperienceRequest req) {
        e.setRoleTitle(req.roleTitle().trim());
        e.setOrganization(req.organization().trim());
        e.setSummary(req.summary().trim());
        e.setStartPeriod(req.startPeriod() == null ? null : req.startPeriod().trim());
        e.setEndPeriod(req.endPeriod() == null ? null : req.endPeriod().trim());
        e.setDisplayOrder(req.displayOrder() == null ? 0 : req.displayOrder());
    }

    private ExperienceResponse toResponse(Experience e) {
        return new ExperienceResponse(
                e.getId(),
                e.getRoleTitle(),
                e.getOrganization(),
                e.getSummary(),
                e.getStartPeriod(),
                e.getEndPeriod(),
                e.getDisplayOrder());
    }
}
