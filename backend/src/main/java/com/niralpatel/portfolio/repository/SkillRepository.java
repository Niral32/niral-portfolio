package com.niralpatel.portfolio.repository;

import com.niralpatel.portfolio.entity.Skill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findAllByOrderByCategoryAscDisplayOrderAscIdAsc();
}
