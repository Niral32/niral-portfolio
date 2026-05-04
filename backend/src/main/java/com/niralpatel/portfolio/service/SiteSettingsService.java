package com.niralpatel.portfolio.service;

import com.niralpatel.portfolio.dto.SiteSettingsDto;
import com.niralpatel.portfolio.entity.SiteSettings;
import com.niralpatel.portfolio.repository.SiteSettingsRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiteSettingsService {

    private static final long SINGLETON_ID = 1L;

    private final SiteSettingsRepository repository;

    public SiteSettingsService(SiteSettingsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SiteSettings getOrCreate() {
        return repository.findById(SINGLETON_ID).orElseGet(() -> {
            SiteSettings s = new SiteSettings();
            s.setId(SINGLETON_ID);
            return repository.save(s);
        });
    }

    @Transactional(readOnly = true)
    public SiteSettingsDto get() {
        SiteSettings s = repository.findById(SINGLETON_ID).orElseGet(() -> {
            SiteSettings fresh = new SiteSettings();
            fresh.setId(SINGLETON_ID);
            return fresh;
        });
        return toDto(s);
    }

    @Transactional
    public SiteSettingsDto update(SiteSettingsDto in) {
        SiteSettings s = getOrCreate();
        s.setContactEnabled(in.contactEnabled());
        s.setBookingEnabled(in.bookingEnabled());
        if (in.contactDisabledMessage() != null) {
            s.setContactDisabledMessage(in.contactDisabledMessage());
        }
        if (in.ownerName() != null && !in.ownerName().isBlank()) {
            s.setOwnerName(in.ownerName().trim());
        }
        if (in.ownerTitle() != null && !in.ownerTitle().isBlank()) {
            s.setOwnerTitle(in.ownerTitle().trim());
        }
        return toDto(s);
    }

    private SiteSettingsDto toDto(SiteSettings s) {
        return new SiteSettingsDto(
                s.isContactEnabled(),
                s.getContactDisabledMessage(),
                s.getOwnerName(),
                s.getOwnerTitle(),
                s.isBookingEnabled());
    }
}
