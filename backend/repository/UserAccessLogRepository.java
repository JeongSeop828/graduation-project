package com.example._th_project.repository;

import com.example._th_project.domain.table.Users;
import com.example._th_project.log.UserAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface UserAccessLogRepository extends JpaRepository<UserAccessLog, Long> {

    boolean existsByUserAndAccessDate(Users user, LocalDate accessDate);

    @Query("SELECT COUNT(DISTINCT l.user.id) FROM UserAccessLog l WHERE l.accessDate = :date")
    long countUniqueUsersByDate(LocalDate date);

    @Query("SELECT COUNT(DISTINCT l.user.id) FROM UserAccessLog l WHERE l.accessDate >= :startDate")
    long countUniqueUsersSince(LocalDate startDate);

    @Query("SELECT l.accessDate, COUNT(DISTINCT l.user.id) FROM UserAccessLog l " +
            "WHERE l.accessDate >= :startDate GROUP BY l.accessDate ORDER BY l.accessDate")
    List<Object[]> findDailyVisitorsSince(LocalDate startDate);
}
