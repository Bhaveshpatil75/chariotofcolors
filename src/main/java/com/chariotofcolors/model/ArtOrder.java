package com.chariotofcolors.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "art_orders")
public class ArtOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String email;
    private String contactNumber;

    private String artType; // e.g. Canvas Painting, Sketch, etc.

    @Column(length = 2000)
    private String description;

    // e.g., PENDING, COMPLETED, CANCELLED
    private String status;

    private LocalDateTime orderDate;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }
}
