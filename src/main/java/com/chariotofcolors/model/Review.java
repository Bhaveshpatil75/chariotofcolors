package com.chariotofcolors.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reviewerName;
    private LocalDate deliveryDate;
    private String content;

    private boolean approved = false;
    private boolean top10 = false;
    private String artType; // e.g. "Portrait", "Landscape"
    private String category; // e.g. "Oil", "Sketch"

    public Review() {
    }

    public Review(String reviewerName, LocalDate deliveryDate, String content) {
        this.reviewerName = reviewerName;
        this.deliveryDate = deliveryDate;
        this.content = content;
    }

    public Review(String reviewerName, LocalDate deliveryDate, String content, String artType, String category) {
        this.reviewerName = reviewerName;
        this.deliveryDate = deliveryDate;
        this.content = content;
        this.artType = artType;
        this.category = category;
    }
}
