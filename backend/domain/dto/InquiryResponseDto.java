package com.example._th_project.domain.dto;

import com.example._th_project.domain.table.Inquiry;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InquiryResponseDto {
    private Long id;
    private String title;
    private String content;
    private String reply;
    private String status;
    private String nickname;
    private LocalDateTime createdAt;

    public InquiryResponseDto(Inquiry inquiry) {
        this.id = inquiry.getInquiryId();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.reply = inquiry.getReply();
        this.status = inquiry.getStatus().getDescription();
        this.nickname = inquiry.getUser().getNickname(); // 변경된 부분
        this.createdAt = inquiry.getCreatedAt();
    }
}
