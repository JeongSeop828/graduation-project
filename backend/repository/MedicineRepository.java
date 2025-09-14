package com.example._th_project.repository;

import com.example._th_project.domain.table.Medicines;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicines, Long> {
    Optional<Medicines> findByMedicineName(String name);
}
