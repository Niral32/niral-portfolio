package com.niralpatel.portfolio.service;

import com.niralpatel.portfolio.dto.SkillRequest;
import com.niralpatel.portfolio.dto.SkillResponse;
import com.niralpatel.portfolio.entity.Skill;
import com.niralpatel.portfolio.repository.SkillRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> findAllOrdered() {
        return skillRepository.findAllByOrderByCategoryAscDisplayOrderAscIdAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SkillResponse create(SkillRequest req) {
        Skill s = new Skill();
        apply(s, req);
        return toResponse(skillRepository.save(s));
    }

    @Transactional
    public SkillResponse update(Long id, SkillRequest req) {
        Skill s = skillRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));
        apply(s, req);
        return toResponse(s);
    }

    @Transactional
    public void delete(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found");
        }
        skillRepository.deleteById(id);
    }

    private void apply(Skill s, SkillRequest req) {
        s.setName(req.name().trim());
        s.setCategory(req.category());
        s.setDisplayOrder(req.displayOrder() == null ? 0 : req.displayOrder());
    }

    private SkillResponse toResponse(Skill s) {
        return new SkillResponse(s.getId(), s.getName(), s.getCategory(), s.getDisplayOrder());
    }
}
