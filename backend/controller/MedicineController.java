package com.example._th_project.controller;

import com.example._th_project.domain.dto.MedicineRequestDto;
import com.example._th_project.domain.dto.MedicineResponseDto;
import com.example._th_project.services.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService service;

    /* 조회: USER, ADMIN 모두 */
    @GetMapping
    public List<MedicineResponseDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public MedicineResponseDto get(@PathVariable Long id) {
        return service.findById(id);
    }

    /* 이하 ADMIN 전용 */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public MedicineResponseDto create(@RequestBody MedicineRequestDto dto) {
        return service.save(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public MedicineResponseDto update(@PathVariable Long id,
                                      @RequestBody MedicineRequestDto dto) {
        return service.update(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

