package com.example._th_project.repository;

import com.example._th_project.domain.table.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String userName);

    Users findUsersByUserId(Long userId);

    boolean existsByName(String nickname);

    long countByCreatedAtAfter(LocalDateTime localDateTime);

    boolean existsByNickname(String nickname);
}
