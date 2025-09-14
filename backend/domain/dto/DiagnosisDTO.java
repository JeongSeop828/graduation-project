package com.example._th_project.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiagnosisDTO {
    private Long diagnosisId;
    private String petName;
    private byte[] diagnosisImg;
    private String disease;
    private String stage;
    private Double riskScore;

    public DiagnosisDTO(Long diagnosisId, String petName, byte[] diagnosisImg, String disease, String stage, Double riskScore){
        this.diagnosisId = diagnosisId;
        this.petName = petName;
        this.diagnosisImg = diagnosisImg;
        this.disease = disease;
        this.stage = stage;
        this.riskScore = riskScore;
    }
}
