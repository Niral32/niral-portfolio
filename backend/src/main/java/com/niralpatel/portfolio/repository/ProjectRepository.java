package com.niralpatel.portfolio.repository;

import com.niralpatel.portfolio.entity.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByOrderByDisplayOrderAscIdAsc();
}
