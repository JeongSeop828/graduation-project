package com.example._th_project.controller;



import com.example._th_project.domain.dto.AdminUsersResponseDTO;
import com.example._th_project.domain.dto.UserUpdateDto;
import com.example._th_project.services.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public List<AdminUsersResponseDTO> getUsers() {
        return adminUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public AdminUsersResponseDTO getUser(@PathVariable Long id) {
        return adminUserService.getUser(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserUpdateDto dto) {
        adminUserService.updateUser(id, dto);
        return ResponseEntity.ok("회원 수정 완료");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok("회원 삭제 완료");
    }
}
