package com.example._th_project.domain.dto;


import com.example._th_project.domain.InquiryStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InquiryDTO {
    private Long inquiryId;
    private String title;
    private InquiryStatus status;

    public InquiryDTO(Long inquiryId, String title, InquiryStatus status) {
        this.inquiryId = inquiryId;
        this.title = title;
        this.status = status;
    }
}
