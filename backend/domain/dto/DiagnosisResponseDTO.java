package com.example._th_project.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DiagnosisResponseDTO {
    private Long diagnosisId;
    private byte[] diseaseImg;
    private String petName;
    private String stage;
    private Double riskScore;
    private String diseases;
    private String diseasesContent;
    private String treatment;
    private List<String> medications;

    @Builder
    public  DiagnosisResponseDTO(Long diagnosisId, byte[] diseaseImg, String petName, String stage, Double riskScore, String diseases, String diseasesContent, String treatment, List<String> medications){
        this.diagnosisId = diagnosisId;
        this.diseaseImg = diseaseImg;
        this.petName = petName;
        this.stage = stage;
        this.riskScore = riskScore;
        this.diseases = diseases;
        this.diseasesContent = diseasesContent;
        this.treatment = treatment;
        this.medications = medications;
    }
}
