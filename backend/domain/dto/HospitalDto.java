package com.example._th_project.domain.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HospitalDto {

    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double distance;

}
