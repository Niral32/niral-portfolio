package com.niralpatel.portfolio.service;

import com.niralpatel.portfolio.dto.ProjectRequest;
import com.niralpatel.portfolio.dto.ProjectResponse;
import com.niralpatel.portfolio.entity.Project;
import com.niralpatel.portfolio.repository.ProjectRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAllOrdered() {
        return projectRepository.findAllByOrderByDisplayOrderAscIdAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProjectResponse create(ProjectRequest req) {
        Project p = new Project();
        apply(p, req);
        return toResponse(projectRepository.save(p));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest req) {
        Project p = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        apply(p, req);
        return toResponse(p);
    }

    @Transactional
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        projectRepository.deleteById(id);
    }

    private void apply(Project p, ProjectRequest req) {
        p.setTitle(req.title().trim());
        p.setDescription(req.description().trim());
        p.setTechStack(req.techStack().trim());
        p.setLinkUrl(req.linkUrl() == null || req.linkUrl().isBlank() ? null : req.linkUrl().trim());
        p.setDisplayOrder(req.displayOrder() == null ? 0 : req.displayOrder());
    }

    private ProjectResponse toResponse(Project p) {
        return new ProjectResponse(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getTechStack(),
                p.getLinkUrl(),
                p.getDisplayOrder());
    }
}
