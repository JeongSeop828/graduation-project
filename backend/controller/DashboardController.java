package com.example._th_project.controller;

import com.example._th_project.domain.dto.DashboardDto;
import com.example._th_project.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardDto getDashboard() {
        return dashboardService.getDashboardData();
    }
}
