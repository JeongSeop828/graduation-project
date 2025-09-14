package com.project.meongnyangcare.model

data class InquiryDetailDTO(
    val inquiryId: Long,
    val title: String,
    val content: String,
    val reply: String?,
    val status: Boolean
)
