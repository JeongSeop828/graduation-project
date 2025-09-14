package com.example._th_project.services;


import com.example._th_project.domain.dto.AdminUsersResponseDTO;
import com.example._th_project.domain.dto.UserUpdateDto;
import com.example._th_project.domain.table.Users;
import com.example._th_project.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UsersRepository userRepository;

    // 전체 사용자 조회
    public List<AdminUsersResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> AdminUsersResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .petCount(user.getPets().size())
                        .build())
                .toList();
    }

    // 단일 사용자 조회
    public AdminUsersResponseDTO getUser(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        return AdminUsersResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .petCount(user.getPets().size())
                .build();
    }

    // 사용자 정보 수정
    public void updateUser(Long id, UserUpdateDto dto) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        if (!user.getNickname().equals(dto.getNickname())
                && userRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("중복된 닉네임입니다.");
        }

        user.setNickname(dto.getNickname());
        userRepository.save(user);
    }

    // 사용자 삭제
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
