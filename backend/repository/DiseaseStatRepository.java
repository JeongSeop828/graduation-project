package com.example._th_project.repository;


import com.example._th_project.domain.table.Diagnoses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DiseaseStatRepository extends JpaRepository<Diagnoses, Long> {

    List<Diagnoses> findByDiagnosedAtAfter(LocalDateTime from);
}