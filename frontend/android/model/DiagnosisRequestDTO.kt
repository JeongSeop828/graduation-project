package com.project.meongnyangcare.model

data class DiagnosisRequestDTO(
    val userId: Long,
    val petId: Long? = null,
    val petName: String?,
    val species: String
)
