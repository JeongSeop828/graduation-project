package com.project.meongnyangcare.network.dto

data class HospitalDto(
    val id: Long,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double
)