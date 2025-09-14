package com.example._th_project.repository;

import com.example._th_project.domain.dto.DiagnosisDTO;
import com.example._th_project.domain.dto.InquiryDTO;
import com.example._th_project.domain.table.Diagnoses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnoses, Long> {

    @Query("""
    select new com.example._th_project.domain.dto.DiagnosisDTO(d.diagnosisId, COALESCE(p.petName, '없음'), d.imageData, di.name, d.stage, d.riskScore)
    from Diagnoses d
    join d.user u
    left join d.pet p
    join d.disease di
    where u.userId = :userId
""")
    List<DiagnosisDTO> findDiagnosisDTObyUserId(@Param("userId") Long userId);
}
