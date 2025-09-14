package com.example._th_project.domain.dto;


import com.example._th_project.domain.table.Inquiry;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquiryDetailDTO {
    private Long inquiryId;
    private String title;
    private String content;
    private String reply;
    private String status;


    public InquiryDetailDTO(Inquiry inquiry) {
        this.inquiryId = inquiry.getInquiryId();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.reply = inquiry.getReply();
    }
}
