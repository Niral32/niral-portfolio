package com.niralpatel.portfolio.repository;

import com.niralpatel.portfolio.entity.BlogPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    List<BlogPost> findAllByPublishedTrueOrderByPublishedAtDesc();

    List<BlogPost> findAllByOrderByCreatedAtDesc();

    Optional<BlogPost> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Modifying
    @Query("update BlogPost p set p.likeCount = p.likeCount + 1 where p.id = :id")
    int incrementLikes(@Param("id") Long id);
}
