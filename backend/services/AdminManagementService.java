package com.example._th_project.services;


import com.example._th_project.domain.AdminRole;
import com.example._th_project.domain.dto.AdminResponseDto;
import com.example._th_project.domain.dto.AdminUpdateDto;
import com.example._th_project.domain.table.Admins;
import com.example._th_project.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminManagementService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder encoder;

    public List<AdminResponseDto> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(a -> new AdminResponseDto(
                        a.getId(),
                        a.getUsername(),
                        a.getName(),
                        a.getRole().name()
                ))
                .toList();
    }

    public AdminResponseDto getAdmin(Long id) {
        Admins admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("관리자 없음"));

        return new AdminResponseDto(
                admin.getId(),
                admin.getUsername(),
                admin.getName(),
                admin.getRole().name()
        );
    }

    public void updateAdmin(Long id, AdminUpdateDto dto) {
        Admins admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("관리자 없음"));

        admin.setName(dto.getName());

        if (dto.getRole() != null) {
            admin.setRole(AdminRole.valueOf(dto.getRole()));
        }

        adminRepository.save(admin);
    }

    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }
}
