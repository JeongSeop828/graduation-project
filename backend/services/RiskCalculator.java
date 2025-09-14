package com.example._th_project.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RiskCalculator {

    private static final Map<String, Double> stageMultiplier = Map.of(
            "Early", 1.0,
            "Middle", 1.5,
            "Late", 2.0
    );

    private static final Map<String, DiseaseInfo> diseaseMap = new HashMap<>();

    static {
        diseaseMap.put("A1", new DiseaseInfo(4, "구진/플라크"));
        diseaseMap.put("A2", new DiseaseInfo(2, "비듬/각질/상피성잔고리"));
        diseaseMap.put("A3", new DiseaseInfo(0, "태선화/과다색소침착")); // 필요 시 점수 설정
        diseaseMap.put("A4", new DiseaseInfo(5, "농포/여드름"));
        diseaseMap.put("A5", new DiseaseInfo(7, "미란/궤양"));
        diseaseMap.put("A6", new DiseaseInfo(8, "결절/종괴"));
    }


    // 위험도 계산 메서드
    public static double calculateRisk(String classId, String stage) {
        DiseaseInfo info = diseaseMap.get(classId);
        if (info == null || !stageMultiplier.containsKey(stage)) {
            throw new IllegalArgumentException("잘못된 질병 코드 또는 진행 단계입니다.");
        }
        return info.baseScore * stageMultiplier.get(stage);
    }

    // 질병명 반환 메서드
    public static String getDiseaseName(String classId) {
        DiseaseInfo info = diseaseMap.get(classId);
        return info != null ? info.diseaseName : "알 수 없는 질병";
    }

    // 내부 정적 클래스: 질병 정보 저장
    private static class DiseaseInfo {
        int baseScore;
        String diseaseName;

        DiseaseInfo(int baseScore, String diseaseName) {
            this.baseScore = baseScore;
            this.diseaseName = diseaseName;
        }
    }

}
