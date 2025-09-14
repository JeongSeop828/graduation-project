package com.example._th_project.services;

import com.example._th_project.domain.dto.MedicineRequestDto;
import com.example._th_project.domain.dto.MedicineResponseDto;
import com.example._th_project.domain.table.Medicines;
import com.example._th_project.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineMapper     mapper;

    public List<MedicineResponseDto> findAll() {
        return medicineRepository.findAll().stream().map(mapper::toDto).toList();
    }

    public MedicineResponseDto findById(Long id) {
        return mapper.toDto(getEntity(id));
    }

    public MedicineResponseDto save(MedicineRequestDto dto) {
        Medicines saved = medicineRepository.save(mapper.toEntity(dto));
        return mapper.toDto(saved);
    }

    public MedicineResponseDto update(Long id, MedicineRequestDto dto) {
        Medicines m = getEntity(id);
        mapper.updateEntityFromDto(dto, m);
        return mapper.toDto(m);
    }

    public void delete(Long id) {
        medicineRepository.deleteById(id);
    }

    /* ------- private ------- */
    private Medicines getEntity(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("약품이 존재하지 않습니다: " + id));
    }
}
