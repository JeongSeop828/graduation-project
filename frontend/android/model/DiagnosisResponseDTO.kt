package com.project.meongnyangcare.model

import com.google.gson.annotations.SerializedName

data class DiagnosisResponseDTO(
    @SerializedName("diagnosisId") val diagnosisId: Long,
    @SerializedName("diseaseImg") val diseaseImg: String,
    @SerializedName("petName") val petName: String,
    @SerializedName("stage") val stage: String,
    @SerializedName("riskScore") val riskScore: Double,
    @SerializedName("diseases") val diseases: String,
    @SerializedName("diseasesContent") val diseasesContent: String,
    @SerializedName("treatment") val treatment: String,
    @SerializedName("medications") val medications: List<String>
)
