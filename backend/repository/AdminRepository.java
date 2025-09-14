package com.example._th_project.repository;

import com.example._th_project.domain.table.Admins;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admins, Long> {

    Optional<Admins> findByUsername(String username);

    boolean existsByUsername(String username);
}
