package com.example._th_project.domain.table;

import com.example._th_project.domain.dto.PetRegisterDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Pets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Long petId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "pet_name", nullable = false, length = 15)
    private String petName;

    @Column(nullable = false, length = 10)
    private String species;

    @Column(length = 30)
    private String breed;

    private Integer age;

    private Double weight;

    @Column(length = 10)
    private String gender;

    @Lob
    @Column(name = "pet_img", columnDefinition = "LONGBLOB")
    private byte[] petImg;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diagnoses> diagnosisRecords = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Pets(Users user, PetRegisterDTO dto, byte[] imgData){
        this.user = user;
        this.petName = dto.getPetName();
        this.species = dto.getSpecies();
        this.breed = dto.getBreed();
        this.age = dto.getAge();
        this.weight = dto.getWeight();
        this.gender = dto.getGender();
        this.petImg = imgData;
    }


}