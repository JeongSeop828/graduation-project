package com.project.meongnyangcare.model

data class DiagnosisListResponseDTO(
    val diagnosisDTOS: List<DiagnosisDTO>,
    val userId: Long
)
