package com.niralpatel.portfolio.repository;

import com.niralpatel.portfolio.entity.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, Long> {
}
