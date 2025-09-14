package com.example._th_project.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PetRegisterDTO {
    private String petName;
    private String species;
    private String breed;
    private Integer age;
    private Double weight;
    private String gender;
}
