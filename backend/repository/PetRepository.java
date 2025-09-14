package com.example._th_project.repository;

import com.example._th_project.domain.dto.PetList;
import com.example._th_project.domain.table.Pets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pets, Long> {

    @Query("""
    SELECT p.petId
    from Pets p
    JOIN p.user u
    where u.userId = :userId
""")
    List<Long> findByUserId(@Param("userId") Long userId);

    @Query("""
    SELECT new com.example._th_project.domain.dto.PetList(p.petId, p.petName, p.species, p.age, p.gender, p.petImg)
    FROM Pets p
    WHERE p.petId = :petId
""")
    PetList findPetListByPetId(@Param("petId") Long petId);
}
