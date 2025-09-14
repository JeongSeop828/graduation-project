package com.example._th_project.domain.dto;


import com.example._th_project.domain.table.Pets;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetDataResponseDTO {

    private Long petId;
    private String petName;
    private String species;
    private String breed;
    private Integer age;
    private Double weight;
    private String gender;
    private byte[] petImg;

    public PetDataResponseDTO(Pets pet) {
        this.petId = pet.getPetId();
        this.petName = pet.getPetName();
        this.species = pet.getSpecies();
        this.breed = pet.getBreed();
        this.age = pet.getAge();
        this.weight = pet.getWeight();
        this.gender = pet.getGender();
        this.petImg = pet.getPetImg();
    }
}
