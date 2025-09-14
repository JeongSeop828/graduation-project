package com.project.meongnyangcare.model

data class UserResponseDto(
    val userId: Long,
    val username: String,
    val name: String,
    val email: String,
    val nickname: String
)