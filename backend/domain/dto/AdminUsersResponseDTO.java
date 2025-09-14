package com.example._th_project.domain.dto;

import com.example._th_project.domain.table.Users;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUsersResponseDTO {

    private Long id;
    private String username;
    private String nickname;
    private int petCount;

    public AdminUsersResponseDTO(Users user) {
    }
}
