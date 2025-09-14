package com.example._th_project.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusDTO {
    private int status;
    private String data;
    private String message;
    private String errCode;
}
