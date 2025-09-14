package com.example._th_project.status;

public class StatusCode {

    // 일반 성공 및 기본 오류
    public static final int OK = 200;                      // 정상 처리
    public static final int UNKNOWN_ERROR = 000;           // 정의되지 않은 에러

    // 사용자 관련 오류
    public static final int VALIDATION_ERROR = 100;        // Validation 오류
    public static final int DUPLICATE_ACCOUNT = 101;       // 회원가입 중복 계정
    public static final int LOGIN_FAILED = 102;            // 로그인 실패

    // 반려동물 관련 오류
    public static final int PET_VALIDATION_ERROR = 201;    // 반려동물 유효성 오류
    public static final int PET_DB_SAVE_ERROR = 202;       // 반려동물 DB 저장 오류
    public static final int DUPLICATE_PET = 203;           // 중복 등록

    // 진단/AI 관련 오류
    public static final int AI_COMMUNICATION_ERROR = 301;  // AI 서버 통신 오류
    public static final int DISEASE_DATA_MISSING = 302;    // 질병 데이터 누락
    public static final int DIAGNOSIS_DB_ERROR = 303;      // 진단 DB 저장 오류
    public static final int HOSPITAL_FETCH_ERROR = 304;    // 병원 데이터 조회 실패
    public static final int AI_ANALYSIS_FAIL = 305;        // 진단 AI 분석 실패

    // HTTP 표준 오류
    public static final int BAD_REQUEST = 400;             // 요청 파라미터 미비
    public static final int UNAUTHORIZED = 401;            // 인증 실패 / 토큰 만료
    public static final int FORBIDDEN = 403;               // 인가 실패
    public static final int NOT_FOUND = 404;               // 리소스 없음
    public static final int INTERNAL_SERVER_ERROR = 500;   // 서버 내부 오류
    public static final int FATAL = 501;                   // 서비스 중단 수준 오류 (치명적 장애)

    // 추가로 필요할 경우
    public static final int SERVICE_UNAVAILABLE = 503;
}
