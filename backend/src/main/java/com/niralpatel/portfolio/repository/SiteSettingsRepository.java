package com.niralpatel.portfolio.repository;

import com.niralpatel.portfolio.entity.SiteSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteSettingsRepository extends JpaRepository<SiteSettings, Long> {
}
