package com.niralpatel.portfolio.repository;

import com.niralpatel.portfolio.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
