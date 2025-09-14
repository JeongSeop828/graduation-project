package com.project.meongnyangcare.model

data class UserDTO(
    val username: String,
    val password: String,
    val nickname: String? = null,
    val email: String? = null,
    val name: String? = null,
    val phone: String? = null,
)
