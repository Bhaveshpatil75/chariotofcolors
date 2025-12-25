package com.chariotofcolors.repository;

import com.chariotofcolors.model.ArtPiece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtPieceRepository extends JpaRepository<ArtPiece, Long> {
    java.util.List<ArtPiece> findByType(String type);

    java.util.List<ArtPiece> findByTypeAndCategory(String type, String category);
}
