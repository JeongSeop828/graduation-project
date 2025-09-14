package com.example._th_project.services;

import com.example._th_project.domain.dto.DiagnosisDTO;
import com.example._th_project.domain.dto.DiagnosisResponseDTO;
import com.example._th_project.domain.table.*;
import com.example._th_project.repository.DiagnosisRepository;
import com.example._th_project.repository.DiseasesRepository;
import com.example._th_project.repository.PetRepository;
import com.example._th_project.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@AllArgsConstructor
@Service
public class DiagnosisService {

    @Autowired
    private DiseasesRepository diseasesRepository;
    @Autowired
    private DiagnosisRepository diagnosisRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PetRepository petRepository;


    @Transactional
    public Long saveDiagnosis(Long userId, Long petId, String diseases, byte[] img, Double progress, String stage){
        // 유저 조회, 존재하지 않으면 저장
        Users user = usersRepository.findUsersByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        // 펫 조회, 존재하지 않으면 예외 처리
        Pets pet = petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("Pet with ID " + petId + " not found"));

        // 질병 조회, 존재하지 않으면 예외 처리
        Diseases findDisease = diseasesRepository.findDiseasesByName(diseases);
        if (findDisease == null) {
            throw new IllegalArgumentException("Disease with name " + diseases + " not found");
        }

        // Diagnoses 저장
        Diagnoses save = diagnosisRepository.save(new Diagnoses(user, pet, findDisease, img, progress, pet.getPetName(), stage));

        return save.getDiagnosisId();
    }

    @Transactional
    public Long saveDiagnosisNull(Long userId, String diseases, byte[] img, Double progress, String stage) {
        // 유저 조회, 존재하지 않으면 예외 처리
        Users user = usersRepository.findUsersByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        // 질병 조회, 존재하지 않으면 예외 처리
        Diseases findDisease = diseasesRepository.findDiseasesByName(diseases);
        if (findDisease == null) {
            throw new IllegalArgumentException("Disease with name " + diseases + " not found");
        }

        // Diagnoses 저장 (반려동물 이름 "없음"으로 설정)
        Diagnoses save = diagnosisRepository.save(new Diagnoses(user, null, findDisease, img, progress, "없음", stage));
        return save.getDiagnosisId();
    }

    public String diseaseDog(File tempFile){

        String pythonFilePath = "/home/t25114/v1.0src/backend/src/main/python/dog/dog_disease.py";
        String diseases = "";

        try {
            // Python 파일 실행
            ProcessBuilder proBuilder = new ProcessBuilder("python", pythonFilePath, tempFile.getAbsolutePath());
            proBuilder.redirectErrorStream(true);
            Process process = proBuilder.start();

            // Python 출력 읽기
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            StringBuilder result = new StringBuilder();
            String line;


            while ((line = bufferReader.readLine()) != null) {
                System.out.println("line = " + line);
                result.append(line); // 출력 내용 콘솔에 표시
                diseases = line;;
            }

            bufferReader.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return diseases;
    }

    public String diseaseCat(File tempFile){
        String pythonFilePath = "/home/t25114/v1.0src/backend/src/main/python/cat/cat_disease.py";

        System.out.println("pythonFilePath = " + pythonFilePath);

        String diseases = "";

        try {
            // Python 파일 실행
            ProcessBuilder proBuilder = new ProcessBuilder("python", pythonFilePath, tempFile.getAbsolutePath());
            proBuilder.redirectErrorStream(true);
            Process process = proBuilder.start();

            // Python 출력 읽기
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            StringBuilder result = new StringBuilder();
            String line;


            while ((line = bufferReader.readLine()) != null) {
                System.out.println("line = " + line);
                result.append(line); // 출력 내용 콘솔에 표시
                diseases = line;;
            }

            bufferReader.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return diseases;
    }

    public String stageDog(File tempFile, String disease){
        String pythonFilePath = "/home/t25114/v1.0src/backend/src/main/python/dog/dog_progress.py";
        String stage = "";

        try {
            // Python 파일 실행
            ProcessBuilder proBuilder = new ProcessBuilder("python", pythonFilePath, tempFile.getAbsolutePath(), disease);
            proBuilder.redirectErrorStream(true);
            Process process = proBuilder.start();

            // Python 출력 읽기
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            StringBuilder result = new StringBuilder();
            String line;


            while ((line = bufferReader.readLine()) != null) {
                System.out.println("line = " + line);
                result.append(line); // 출력 내용 콘솔에 표시
                stage = line;;
            }

            bufferReader.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return stage;
    }

    public String stageCat(File tempFile, String disease){

        // 실행 디렉토리
        String pythonFilePath = "/home/t25114/v1.0src/backend/src/main/python/cat/cat_progress.py";

//        String pythonFilePath = "src/main/python/cat/cat_progress.py";

        System.out.println("pythonFilePath = " + pythonFilePath);

        String stage = "";

        try {
            // Python 파일 실행
            ProcessBuilder proBuilder = new ProcessBuilder("python", pythonFilePath, tempFile.getAbsolutePath(), disease);
            proBuilder.redirectErrorStream(true);
            Process process = proBuilder.start();

            // Python 출력 읽기
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            StringBuilder result = new StringBuilder();
            String line;


            while ((line = bufferReader.readLine()) != null) {
                System.out.println("line = " + line);
                result.append(line); // 출력 내용 콘솔에 표시
                stage = line;;
            }

            bufferReader.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return stage;
    }

    public List<DiagnosisDTO> DiagnosisList(Long userId){
        Optional<Users> optional = usersRepository.findById(userId);

        if(optional.isEmpty()){
            throw new NoSuchElementException("유저정보 없음");
        }

        List<DiagnosisDTO> dtos = diagnosisRepository.findDiagnosisDTObyUserId(userId);

        return dtos;
    }

    public DiagnosisResponseDTO diagnosisDetail(Long diagnosisId){
        Optional<Diagnoses> optional = diagnosisRepository.findById(diagnosisId);

        if(optional.isEmpty()){
            throw new NoSuchElementException("진단 정보 없음");
        }

        Diagnoses diagnosis = optional.get();

        List<String> medicineList = new ArrayList<>();

        List<Medicines> medicines = diagnosis.getDisease().getMedicines();

        for(Medicines m : medicines){
            medicineList.add(m.getMedicineName());
        }

        DiagnosisResponseDTO dto = new DiagnosisResponseDTO(diagnosis.getDiagnosisId(), diagnosis.getImageData(), diagnosis.getPetName(), diagnosis.getStage(),
                diagnosis.getRiskScore(), diagnosis.getDisease().getName(), diagnosis.getDisease().getDescription(),
                diagnosis.getDisease().getTreatment(), medicineList);

        return dto;
    }
}
