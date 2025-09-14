package com.example._th_project.repository;

import com.example._th_project.domain.table.Diseases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseasesRepository extends JpaRepository<Diseases, Long> {

    Diseases findDiseasesByName(String disease);

    Optional<Diseases> findByName(String name);

}
