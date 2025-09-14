package com.example._th_project.domain.table;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diagnoses")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Diagnoses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_id")
    private Long diagnosisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = true)
    private Pets pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id")
    private Diseases disease;

    @Column(name = "diagnosed_at", nullable = false)
    private LocalDateTime diagnosedAt;

    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;


    @Column(name = "riskScore")
    private Double riskScore;

    @Column(name = "pet_name")
    private String petName;

    @Column(name = "stage")
    private String stage;

    @PrePersist
    private void prePersist() {
        this.diagnosedAt = LocalDateTime.now();
    }

    @Builder
    public Diagnoses(Users user, Pets pet, Diseases disease, byte[] img, Double progress, String petName, String stage){
        this.user = user;
        this.pet = pet;
        this.disease = disease;
        this.imageData = img;
        this.riskScore = progress;
        this.petName = petName;
        this.stage = stage;
    }
}