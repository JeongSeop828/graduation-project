package com.project.meongnyangcare.model

data class DefaultRes<T>(
    val status: String,
    val message: String,
    val data: T?,
    val accessToken: String? // 로그인 후 받은 토큰
)