package com.project.meongnyangcare.model

data class TokenResponseDTO(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
)
