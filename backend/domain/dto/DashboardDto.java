package com.example._th_project.domain.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardDto {
    private long todaySignups;
    private long totalUsers;
    private long dailyActiveUsers;
    private Map<String, Long> weeklyVisitors;
}
