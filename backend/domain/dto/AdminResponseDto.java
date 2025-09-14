package com.example._th_project.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDto {
    private Long id;
    private String username;
    private String name;
    private String role;
}