package com.example._th_project.domain.dto;

import lombok.Value;

@Value
public class TopDiseaseResponseDto {
    String disease;
    Long   count;
}