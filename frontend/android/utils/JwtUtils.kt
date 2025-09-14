package com.project.meongnyangcare.utils;

import com.auth0.android.jwt.JWT

object JwtUtils {

    // JWT 토큰에서 userId 추출하는 메서드
    fun getUserIdFromToken(token: String): Long? {
        try {
            val jwt = JWT(token)
            // "userId"라는 이름의 클레임을 찾아서 Long 타입으로 변환
            return jwt.getClaim("userId").asLong()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // 다른 유틸리티 메서드를 추가할 수 있음
}