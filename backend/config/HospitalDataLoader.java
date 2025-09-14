package com.example._th_project.config;


import com.example._th_project.domain.table.Hospital;
import com.example._th_project.repository.HospitalRepository;
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
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalDataLoader implements ApplicationRunner {

    private final HospitalRepository hospitalRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(hospitalRepository.count() > 0) {
            log.info("[Dataloader] Already have hospital data.");
            return;
        }

        try (
            Reader in = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(getClass().getResourceAsStream("/data/hospital.csv")), StandardCharsets.UTF_8))
        ){
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader().parse(in);

            for(CSVRecord record: records) {
                try {
                    String name = record.get("name");

                    String address = record.get("address");
                    Double lat = Double.valueOf(record.get("latitude"));
                    Double lon = Double.valueOf(record.get("longitude"));

                    Hospital hospital = Hospital.builder()
                            .name(name)
                            .address(address)
                            .latitude(lat)
                            .longitude(lon)
                            .build();

                    hospitalRepository.save(hospital);
                } catch (NumberFormatException ex) {
                    log.error("[DataLoader] 데이터 변환 오류 - 레코드: {}", record, ex);
                }
            }
            log.info("[DataLoader] Hospital data loaded successfully!");
        } catch (Exception e) {
            log.error("[DataLoader] 병원 데이터 로딩 중 예외 발생", e);
            throw new RuntimeException("병원 데이터 로딩 실패", e);
        }

    }
}
