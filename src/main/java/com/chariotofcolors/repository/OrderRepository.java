package com.chariotofcolors.repository;

import com.chariotofcolors.model.ArtOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<ArtOrder, Long> {
    java.util.List<ArtOrder> findByEmail(String email);
}
