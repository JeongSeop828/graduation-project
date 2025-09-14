package com.example._th_project.domain;

import lombok.Getter;

@Getter
public enum InquiryStatus {
    PENDING("답변 진행중"),
    COMPLETED("답변 완료");

    private final String description;

    InquiryStatus(String description) {
        this.description = description;
    }

}
