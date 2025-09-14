package com.example._th_project.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InquiryListResponseDTO {
    private List<InquiryDTO> inquiryDTOS;

    public InquiryListResponseDTO(List<InquiryDTO> inquiryDTOS) {
        this.inquiryDTOS = inquiryDTOS;
    }
}
