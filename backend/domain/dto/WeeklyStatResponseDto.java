package com.example._th_project.domain.dto;


import lombok.Value;

@Value
public class WeeklyStatResponseDto {
    String yearWeek;
    String disease;
    Long   count;
}