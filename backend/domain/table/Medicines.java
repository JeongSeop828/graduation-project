package com.example._th_project.domain.table;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicines")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Medicines {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_id")
    private Long medicineId;

    @Column(name = "medicine_name", nullable = false, length = 100)
    private String medicineName;

    @Column(length = 255)
    private String effect;

    @Column(columnDefinition = "TEXT")
    private String caution;

    @ManyToMany
    @JoinTable(
            name = "medicine_diseases",
            joinColumns = @JoinColumn(name = "medicine_id"),
            inverseJoinColumns = @JoinColumn(name = "disease_id")
    )
    private List<Diseases> diseases = new ArrayList<>();
}
