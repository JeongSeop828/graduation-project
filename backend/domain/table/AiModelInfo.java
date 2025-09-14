package com.example._th_project.domain.table;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_model_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiModelInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(nullable = false)
    private String version;

    private Double accuracy;

    @Column(name = "trained_at")
    private LocalDateTime trainedAt;

    private Integer active;
}
