package com.example._th_project.config;


import com.example._th_project.domain.table.Diseases;
import com.example._th_project.domain.table.Medicines;
import com.example._th_project.repository.DiseasesRepository;
import com.example._th_project.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiseaseDataLoader implements ApplicationRunner {

    private final DiseasesRepository diseaseRepository;
    private final MedicineRepository medicineRepo;
    private final DiseasesRepository diseaseRepo;

    private static final String MEDICINE_CSV = "/data/medicine.csv";
    private static final String MAP_CSV      = "/data/medicine_disease.csv";

    @Override
    public void run(ApplicationArguments args) {
        if (diseaseRepository.count() > 0) {
            log.info("[DataLoader] Disease data already present.");
            return;
        }

        try (Reader in = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass()
                        .getResourceAsStream("/data/disease.csv")), StandardCharsets.UTF_8))) {

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(in);

            for (CSVRecord record : records) {
                String name        = record.get("name").trim();
                String description = record.get("description").trim();
                String treatment   = record.get("treatment").trim();

                // 동일 이름 존재 시 건너뛰기 (또는 update 로직으로 교체 가능)
                if (diseaseRepository.findByName(name).isPresent()) {
                    log.debug("[DataLoader] Duplicate disease '{}', skipped.", name);
                    continue;
                }

                Diseases disease = Diseases.builder()
                        .name(name)
                        .description(description)
                        .treatment(treatment)
                        .build();

                diseaseRepository.save(disease);
            }

            log.info("[DataLoader] Disease data loaded successfully!");

        } catch (Exception e) {
            log.error("[DataLoader] Failed to load disease data", e);
            throw new RuntimeException("질병 데이터 로딩 실패", e);
        }


        loadMedicines();
        mapMedicineDiseases();
    }

    private void loadMedicines() {
        if (medicineRepo.count() > 0) {
            log.info("[Loader] medicines table already contains data, skipping insert");
            return;
        }

        parseCsv(MEDICINE_CSV).forEach(r -> {
            String name    = r.get("medicine_name").trim();
            String effect  = r.get("effect").trim();
            String caution = r.get("caution").trim();

            medicineRepo.findByMedicineName(name).orElseGet(() ->
                    medicineRepo.save(
                            Medicines.builder()
                                    .medicineName(name)
                                    .effect(effect)
                                    .caution(caution)
                                    .build()
                    )
            );
        });
        log.info("[Loader] medicines.csv inserted into DB");
    }

    private void mapMedicineDiseases() {
        List<CSVRecord> records = parseCsv(MAP_CSV);

        records.forEach(r -> {
            String medName = r.get("medicine_name").trim();
            String disName = r.get("disease_name").trim();

            Medicines medicine = medicineRepo.findByMedicineName(medName)
                    .orElseThrow(() -> new IllegalStateException("Medicine not found: " + medName));

            Diseases disease = diseaseRepo.findByName(disName)
                    .orElseThrow(() -> new IllegalStateException("Disease not found: " + disName));

            // ✅ diseases 리스트가 null인 경우 초기화
            if (medicine.getDiseases() == null) {
                medicine.setDiseases(new ArrayList<>());
            }

            if (!medicine.getDiseases().contains(disease)) {
                medicine.getDiseases().add(disease);
            }
        });

        medicineRepo.flush();
        log.info("[Loader] medicine_disease.csv mapping complete");
    }

    private List<CSVRecord> parseCsv(String classpath) {
        try (Reader in = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(classpath)),
                StandardCharsets.UTF_8))) {

            return StreamSupport.stream(
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in).spliterator(), false
            ).toList();

        } catch (Exception e) {
            throw new RuntimeException("CSV 로드 실패: " + classpath, e);
        }
    }
}
