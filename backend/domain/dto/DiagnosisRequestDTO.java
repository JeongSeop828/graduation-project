package com.example._th_project.domain.dto;

import lombok.Getter;

@Getter
public class DiagnosisRequestDTO {
    private Long petId;
    private Long userId;
    private String petName;
    private String species;
}
