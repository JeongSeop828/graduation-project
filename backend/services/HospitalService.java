package com.example._th_project.services;


import com.example._th_project.domain.dto.HospitalDto;
import com.example._th_project.domain.table.Hospital;
import com.example._th_project.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private static final double EARTH_RADIUS = 6371.0;

    public List<HospitalDto> findNearestHospitals(double userLat, double userLon, int limit){
        List<Hospital> all = hospitalRepository.findAll();

        return all.stream()
                .map(h ->{
                    double distance = calcDistance(userLat, userLon, h.getLatitude(), h.getLongitude());

                    return HospitalDto.builder()
                            .id(h.getHospitalId())
                            .name(h.getName())
                            .address(h.getAddress())
                            .latitude(h.getLatitude())
                            .longitude(h.getLongitude())
                            .distance(distance)
                            .build();
                })
                .sorted(Comparator.comparing(HospitalDto::getDistance))
                .limit(limit)
                .collect(Collectors.toList());
    }


    private double calcDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}


