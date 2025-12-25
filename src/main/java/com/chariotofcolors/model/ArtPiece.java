package com.chariotofcolors.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ArtPiece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String price; // e.g. "$250" or "Start from $200"
    private String category; // e.g. Canvas, Sketch
    private String imageUrl; // Main image URL

    // For simplicity, additional images can be stored as a comma-separated string
    // or just hardcoded for this demo, but let's add a placeholder field.
    private String thumbnailUrl;

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

    private String type = "GALLERY"; // GALLERY, ABOUT_US

    public ArtPiece() {
    }

    public ArtPiece(String title, String description, String price, String category, String imageUrl, String type) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.type = type != null ? type : "GALLERY";
    }
}
