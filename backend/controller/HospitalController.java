package com.example._th_project.controller;


import com.example._th_project.domain.dto.HospitalDto;
import com.example._th_project.services.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping("/hospitals")
    public List<HospitalDto> getNearestHospitals(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return hospitalService.findNearestHospitals(lat, lon, limit);
    }
}
