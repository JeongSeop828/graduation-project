package com.project.meongnyangcare.model

data class StatusDTO(
    val status: Int,
    val data: String? = null,
    val message: String,
    val errCode: String? = null
)
