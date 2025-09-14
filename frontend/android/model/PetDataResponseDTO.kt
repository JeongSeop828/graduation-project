package com.project.meongnyangcare.model

import com.google.gson.annotations.SerializedName

data class PetDataResponseDTO(
    @SerializedName("petId") val petId: Long,
    @SerializedName("petName") val petName: String,
    @SerializedName("species") val species: String,
    @SerializedName("breed") val breed: String,
    @SerializedName("age") val age: Int,
    @SerializedName("weight") val weight: Double,
    @SerializedName("gender") val gender: String,
    @SerializedName("petImg") val petImg: String // Base64 인코딩된 이미지 데이터
)
