package com.example._th_project.services;

import com.example._th_project.domain.dto.DashboardDto;
import com.example._th_project.repository.UserAccessLogRepository;
import com.example._th_project.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UsersRepository userRepository;
    private final UserAccessLogRepository logRepository;

    public DashboardDto getDashboardData() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);

        long total = userRepository.count();
        long todaySignups = userRepository.countByCreatedAtAfter(today.atStartOfDay());
        long dailyActive = logRepository.countUniqueUsersByDate(today);

        // ✅ 1. 날짜별 기본값 0으로 초기화
        Map<String, Long> weeklyVisitors = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = sevenDaysAgo.plusDays(i);
            weeklyVisitors.put(date.toString(), 0L);
        }

        // ✅ 2. DB 결과 덮어쓰기
        List<Object[]> stats = logRepository.findDailyVisitorsSince(sevenDaysAgo);
        for (Object[] row : stats) {
            String dateStr = row[0].toString();
            Long count = ((Number) row[1]).longValue();
            weeklyVisitors.put(dateStr, count);
        }

        return DashboardDto.builder()
                .totalUsers(total)
                .todaySignups(todaySignups)
                .dailyActiveUsers(dailyActive)
                .weeklyVisitors(weeklyVisitors)
                .build();
    }
}
