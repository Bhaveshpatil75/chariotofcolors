package com.chariotofcolors.config;

import com.chariotofcolors.model.ArtPiece;
import com.chariotofcolors.model.Review;
import com.chariotofcolors.repository.ArtPieceRepository;
import com.chariotofcolors.repository.ReviewRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

        @Bean
        public CommandLineRunner loadData(ReviewRepository reviewRepository, ArtPieceRepository artPieceRepository) {
                return args -> {
                        // Seed Reviews
                        if (reviewRepository.count() == 0) {
                                Review r1 = new Review("Sarah J.", LocalDate.now().minusDays(10),
                                                "Absolutely stunning work! The colors are so vibrant.", "Portrait",
                                                "Oil");
                                r1.setApproved(true);
                                r1.setTop10(true);
                                reviewRepository.save(r1);

                                Review r2 = new Review("Mike T.", LocalDate.now().minusDays(25),
                                                "Ordered a birthday gift and it was the highlight of the party.",
                                                "Gift", "Sketch");
                                r2.setApproved(true);
                                r2.setTop10(true);
                                reviewRepository.save(r2);

                                Review r3 = new Review("Emily R.", LocalDate.now().minusDays(5),
                                                "Professional, fast, and the detail is incredible. Highly recommended!",
                                                "Landscape",
                                                "Watercolor");
                                r3.setApproved(true);
                                r3.setTop10(false); // Approved but not top 10
                                reviewRepository.save(r3);
                        }

                        // Seed Art Pieces (Gallery Items)
                        if (artPieceRepository.count() == 0) {
                                artPieceRepository.save(new ArtPiece("Sunset over the Hill", "A beautiful sunset...",
                                                "$250", "Canvas Painting", "https://picsum.photos/300/200?random=1",
                                                "GALLERY"));
                                artPieceRepository.save(new ArtPiece("Abstract Mind", "Deep thoughts...", "$300",
                                                "Abstract", "https://picsum.photos/300/200?random=2", "GALLERY"));
                                artPieceRepository.save(new ArtPiece("Forest Walk", "Greenery everywhere...", "$150",
                                                "Sketch", "https://picsum.photos/300/200?random=3", "GALLERY"));
                                artPieceRepository.save(new ArtPiece("Couple Portrait",
                                                "A lovely sketch for a wedding anniversary.",
                                                "$150", "Wedding Gift", "https://picsum.photos/300/400?random=3",
                                                "GALLERY"));
                                artPieceRepository.save(
                                                new ArtPiece("Neon City", "Futuristic vibes for home decor.", "$400",
                                                                "Home Decoration",
                                                                "https://picsum.photos/300/400?random=4", "GALLERY"));
                                artPieceRepository
                                                .save(new ArtPiece("Charcoal Dreams",
                                                                "Detailed charcoal sketch suitable for framing.",
                                                                "$120",
                                                                "Sketches", "https://picsum.photos/300/400?random=5",
                                                                "GALLERY"));
                                artPieceRepository.save(new ArtPiece("Birthday Blast", "Custom pop-art style painting.",
                                                "$180",
                                                "Birthday Gift", "https://picsum.photos/300/400?random=6", "GALLERY"));
                        }
                };
        }
}
