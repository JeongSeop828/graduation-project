package com.example._th_project.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MedicineResponseDto {
    private Long id;
    private String medicineName;
    private String effect;
    private String caution;
}
