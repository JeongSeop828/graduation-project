package com.example._th_project.controller;


import com.example._th_project.domain.dto.*;
import com.example._th_project.services.DiseaseStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/diagnosis")
@RequiredArgsConstructor
public class AdminDiagnosisController {

    private final DiseaseStatService statService;

    /* 최근 1주 TOP 5 */
    @GetMapping("/top5")
    public ResponseEntity<List<TopDiseaseResponseDto>> top5() {
        return ResponseEntity.ok(statService.getTop5LastWeek());
    }

    /* 최근 4주(기본) 주간 통계 */
    @GetMapping("/weekly")
    public ResponseEntity<List<WeeklyStatResponseDto>> weekly(
            @RequestParam(defaultValue = "4") int weeks) {
        return ResponseEntity.ok(statService.getWeeklyStats(weeks));
    }
}
