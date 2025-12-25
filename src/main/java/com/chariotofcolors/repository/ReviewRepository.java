package com.chariotofcolors.repository;

import com.chariotofcolors.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByApprovedTrueAndTop10True();

    List<Review> findByApprovedTrue();
}
