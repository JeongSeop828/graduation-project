package com.example._th_project.repository;

import com.example._th_project.domain.table.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
}
