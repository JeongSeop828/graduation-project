package com.example._th_project.domain.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PetListResponseDTO {
    private List<PetList> petLists;

    public PetListResponseDTO(List<PetList> petLists) {
        this.petLists = petLists;
    }
}
