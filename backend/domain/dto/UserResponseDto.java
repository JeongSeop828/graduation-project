package com.example._th_project.domain.dto;

import com.example._th_project.domain.table.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long userId;
    private String username;
    private String name;
    private String nickname;

    @Builder
    public UserResponseDto(Users user) {

        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.nickname = user.getNickname();
    }
}
