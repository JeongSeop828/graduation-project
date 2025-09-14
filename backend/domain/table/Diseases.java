package com.example._th_project.domain.table;

import jakarta.persistence.*;
import lombok.*;

import java.net.SocketOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diseases")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diseases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disease_id")
    private Long diseaseId;

    @Column(nullable = false, length = 100)
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String treatment;

    @ManyToMany(mappedBy = "diseases")
    private List<Medicines> medicines = new ArrayList<>();
}

