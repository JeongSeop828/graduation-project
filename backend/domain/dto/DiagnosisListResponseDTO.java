package com.example._th_project.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DiagnosisListResponseDTO {
    private List<DiagnosisDTO> diagnosisDTOS;
    private Long userId;

    public DiagnosisListResponseDTO(List<DiagnosisDTO> diagnosisDTOS, Long userId){
        this.diagnosisDTOS = diagnosisDTOS;
        this.userId = userId;
    }
}
