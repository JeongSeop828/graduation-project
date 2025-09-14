package com.example._th_project.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Getter
@Setter
public class PetList {
    private Long petId;
    private String petName;
    private String species;
    private Integer age;
    private String gender;
    private String petImg;  // byte[]에서 String (Base64)로 변경

    // 생성자에서 byte[]를 Base64 문자열로 변환
    public PetList(Long petId, String petName, String species, Integer age, String gender, byte[] petImg) {
        this.petId = petId;
        this.petName = petName;
        this.species = species;
        this.age = age;
        this.gender = gender;
        this.petImg = Base64.getEncoder().encodeToString(petImg);  // byte[] -> Base64 String
    }
}
