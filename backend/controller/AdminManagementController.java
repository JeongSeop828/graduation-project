package com.example._th_project.controller;


import com.example._th_project.domain.dto.AdminResponseDto;
import com.example._th_project.domain.dto.AdminUpdateDto;
import com.example._th_project.services.AdminManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/manage")
public class AdminManagementController {

    private final AdminManagementService service;

    @GetMapping
    public List<AdminResponseDto> getAllAdmins() {
        return service.getAllAdmins();
    }

    @GetMapping("/{id}")
    public AdminResponseDto getAdmin(@PathVariable Long id) {
        return service.getAdmin(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateAdmin(@PathVariable Long id, @RequestBody AdminUpdateDto dto) {
        service.updateAdmin(id, dto);
        return ResponseEntity.ok("관리자 수정 완료");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id) {
        service.deleteAdmin(id);
        return ResponseEntity.ok("관리자 삭제 완료");
    }
}