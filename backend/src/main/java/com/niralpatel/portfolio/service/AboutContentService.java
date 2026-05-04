package com.niralpatel.portfolio.service;

import com.niralpatel.portfolio.dto.AboutContentDto;
import com.niralpatel.portfolio.entity.AboutContent;
import com.niralpatel.portfolio.repository.AboutContentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AboutContentService {

    private static final long SINGLETON_ID = 1L;

    private final AboutContentRepository repository;

    public AboutContentService(AboutContentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public AboutContentDto get() {
        AboutContent c = repository.findById(SINGLETON_ID).orElseGet(this::seedDefault);
        return toDto(c);
    }

    @Transactional
    public AboutContentDto update(AboutContentDto in) {
        AboutContent c = repository.findById(SINGLETON_ID).orElseGet(() -> {
            AboutContent fresh = new AboutContent();
            fresh.setId(SINGLETON_ID);
            return fresh;
        });
        c.setSummary(in.summary() == null ? "" : in.summary());
        c.setEducationHtml(in.educationHtml() == null ? "" : in.educationHtml());
        c.setPassions(in.passions() == null ? "" : in.passions());
        return toDto(repository.save(c));
    }

    private AboutContent seedDefault() {
        AboutContent c = new AboutContent();
        c.setId(SINGLETON_ID);
        c.setSummary(
                "I am a Java / Full Stack Developer based in Barrie, Ontario, Canada. " +
                        "I hold a Bachelor's degree in Computer Engineering and a postgraduate " +
                        "diploma in Mobile Solutions Development from Conestoga College.");
        c.setEducationHtml(
                "<ul>" +
                        "<li><strong>Mobile Solution Development</strong> — Conestoga College, Canada (2025)</li>" +
                        "<li><strong>Computer Engineering</strong> — Sardar Patel College of Engineering, India (2023)</li>" +
                        "</ul>");
        c.setPassions(
                "I am especially interested in backend development with Spring Boot, microservice-style " +
                        "architectures, cloud deployment, and writing clean, testable code that teams can " +
                        "evolve confidently.");
        return repository.save(c);
    }

    private AboutContentDto toDto(AboutContent c) {
        return new AboutContentDto(c.getSummary(), c.getEducationHtml(), c.getPassions());
    }
}
