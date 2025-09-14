package com.example._th_project.controller;


import com.example._th_project.domain.dto.*;
import com.example._th_project.services.DiagnosisService;
import com.example._th_project.services.DiseaseStatService;
import com.example._th_project.services.PetService;
import com.example._th_project.services.RiskCalculator;
import com.example._th_project.status.DefaultRes;
import com.example._th_project.status.StatusCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.resource.beans.container.spi.BeanLifecycleStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.io.InputStream;

@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    @Autowired
    private DiagnosisService diagnosisService;
    @Autowired
    private PetService petService;
    private final DiseaseStatService statService;



    @PostMapping(value = "/analyze", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DefaultRes<DiagnosisResponseDTO>> analyze(@RequestPart("diagnosisRequest")DiagnosisRequestDTO diagnosisRequestDTO,
                                                                    @RequestPart("image") MultipartFile imageFile) throws IOException{

        String diseasesId = "";
        String stage = "";
        Long diagnosisId;
        double risk;


        File tempFile = File.createTempFile("image_", ".jpg");

        try (InputStream inputStream = imageFile.getInputStream()) {
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("tempFile.getAbsolutePath() = " + tempFile.getAbsolutePath());

        if(diagnosisRequestDTO.getPetId() == null || diagnosisRequestDTO.getPetId() == 0){
            if(diagnosisRequestDTO.getSpecies().equals("dog")){
                diseasesId = diagnosisService.diseaseDog(tempFile);
                stage = diagnosisService.stageDog(tempFile, diseasesId);

            }
            else if (diagnosisRequestDTO.getSpecies().equals("cat")) {
                diseasesId = diagnosisService.diseaseCat(tempFile);
                stage = diagnosisService.stageCat(tempFile, diseasesId);
            }

            risk = (int) RiskCalculator.calculateRisk(diseasesId, stage);

            diagnosisId = diagnosisService.saveDiagnosisNull(diagnosisRequestDTO.getUserId(), RiskCalculator.getDiseaseName(diseasesId), imageFile.getBytes(), risk, stage);
        }
        else{
            if(diagnosisRequestDTO.getSpecies().equals("dog")){
                System.out.println(diagnosisRequestDTO.getSpecies());

                diseasesId = diagnosisService.diseaseDog(tempFile);
                stage = diagnosisService.stageDog(tempFile, diseasesId);
            }
            else if (diagnosisRequestDTO.getSpecies().equals("cat")) {
                diseasesId = diagnosisService.diseaseCat(tempFile);

                System.out.println("diseasesId = " + diseasesId);
                System.out.println("-----------------------------------------------------");
                stage = diagnosisService.stageCat(tempFile, diseasesId);
            }


            System.out.println("diseasesId = " + diseasesId);
            System.out.println("stage = " + stage);

            risk = (int) RiskCalculator.calculateRisk(diseasesId, stage);

            System.out.println("risk = " + risk);

            diagnosisId = diagnosisService.saveDiagnosis(diagnosisRequestDTO.getUserId(), diagnosisRequestDTO.getPetId(), RiskCalculator.getDiseaseName(diseasesId), imageFile.getBytes(), risk, stage);
        }

        try {
            Files.deleteIfExists(tempFile.toPath());
        } catch (IOException e) {
            System.err.println("임시 파일 삭제 실패: " + e.getMessage());
        }

        DiagnosisResponseDTO dto = diagnosisService.diagnosisDetail(diagnosisId);

        return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "반려동물 질환 진단 성공", dto));
    }


    @GetMapping("/{userId}")
    public ResponseEntity<DefaultRes<DiagnosisListResponseDTO>> diagnosisResults(@PathVariable Long userId){

        List<DiagnosisDTO> diagnosisDTOS = diagnosisService.DiagnosisList(userId);

        DiagnosisListResponseDTO dto = new DiagnosisListResponseDTO(diagnosisDTOS, userId);

        return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "진단 목록 조회", dto));
    }

    @GetMapping("/{userID}/{diagnosisId}")
    public ResponseEntity<DefaultRes<DiagnosisResponseDTO>> diagnosisDetail(@PathVariable Long diagnosisId){

        DiagnosisResponseDTO dto = diagnosisService.diagnosisDetail(diagnosisId);

        return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "진단 세부정보 조회", dto));
    }
}
