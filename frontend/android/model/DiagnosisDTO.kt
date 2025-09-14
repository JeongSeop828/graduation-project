package com.project.meongnyangcare.model

data class DiagnosisDTO(
    val diagnosisId: Long,
    val petName: String,
    val diagnosisImg: String, // 서버에서 Base64 문자열로 받을 경우 String으로 변경 가능
    val disease: String,
    val stage: String,
    val riskScore: Double
)