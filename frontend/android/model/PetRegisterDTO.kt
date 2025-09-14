package com.project.meongnyangcare.model

data class PetRegisterDTO(
    val petName: String,
    val species: String,
    val breed: String,
    val age: Int,
    val weight: Double,
    val gender: String
)
