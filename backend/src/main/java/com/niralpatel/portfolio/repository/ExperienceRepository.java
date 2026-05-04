package com.niralpatel.portfolio.repository;

import com.niralpatel.portfolio.entity.Experience;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findAllByOrderByDisplayOrderAscIdAsc();
}
