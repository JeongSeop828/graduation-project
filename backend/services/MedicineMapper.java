package com.example._th_project.services;

import com.example._th_project.domain.dto.MedicineRequestDto;
import com.example._th_project.domain.dto.MedicineResponseDto;
import com.example._th_project.domain.table.Medicines;
import org.springframework.stereotype.Component;

@Component
public class MedicineMapper {

    /* RequestDto → Entity */
    public Medicines toEntity(MedicineRequestDto dto) {
        return Medicines.builder()
                .medicineName(dto.getMedicineName())
                .effect(dto.getEffect())
                .caution(dto.getCaution())
                .build();
    }

    /* Entity → ResponseDto */
    public MedicineResponseDto toDto(Medicines m) {
        return MedicineResponseDto.builder()
                .id(m.getMedicineId())
                .medicineName(m.getMedicineName())
                .effect(m.getEffect())
                .caution(m.getCaution())
                .build();
    }

    /* 부분 업데이트 */
    public void updateEntityFromDto(MedicineRequestDto dto, Medicines m) {
        if (dto.getMedicineName() != null) m.setMedicineName(dto.getMedicineName());
        if (dto.getEffect() != null) m.setEffect(dto.getEffect());
        if (dto.getCaution() != null) m.setCaution(dto.getCaution());
    }
}
