package com.chariotofcolors.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String subject;

    @Column(length = 5000)
    private String description;

    private LocalDateTime submissionDate;

    public Complaint() {
        this.submissionDate = LocalDateTime.now();
    }

    public Complaint(String name, String email, String subject, String description) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.description = description;
        this.submissionDate = LocalDateTime.now();
    }
}
