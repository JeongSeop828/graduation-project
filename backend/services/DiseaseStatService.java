package com.example._th_project.services;

import com.example._th_project.domain.dto.TopDiseaseResponseDto;
import com.example._th_project.domain.dto.WeeklyStatResponseDto;
import com.example._th_project.repository.DiseaseStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiseaseStatService {

    private final DiseaseStatRepository diseaseStatRepository;
    private final WeekFields ISO = WeekFields.ISO;

    /** 최근 1주 Top-5 */
    public List<TopDiseaseResponseDto> getTop5LastWeek() {
        LocalDateTime from = LocalDate.now().minusDays(6).atStartOfDay();

        return diseaseStatRepository.findByDiagnosedAtAfter(from).stream()
                .collect(Collectors.groupingBy(
                        d -> d.getDisease().getName(),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new TopDiseaseResponseDto(e.getKey(), e.getValue()))
                .toList();
    }

    /** 최근 N주 주간 통계 (기본 4주) */
    public List<WeeklyStatResponseDto> getWeeklyStats(int weeks) {
        LocalDateTime from = LocalDate.now()
                .minusWeeks(weeks - 1)
                .with(DayOfWeek.MONDAY)
                .atStartOfDay();

        return diseaseStatRepository.findByDiagnosedAtAfter(from).stream()
                .collect(Collectors.groupingBy(d -> {
                    LocalDate date = d.getDiagnosedAt().toLocalDate();
                    String yw = date.get(ISO.weekBasedYear()) + "-"
                            + String.format("%02d", date.get(ISO.weekOfWeekBasedYear()));
                    return Map.entry(yw, d.getDisease().getName());
                }, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new WeeklyStatResponseDto(
                        e.getKey().getKey(),
                        e.getKey().getValue(),
                        e.getValue()))
                .toList();
    }
}
