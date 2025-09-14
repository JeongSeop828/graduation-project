package com.project.meongnyangcare.network

import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.DiagnosisResponseDTO
import com.project.meongnyangcare.model.DiagnosisListResponseDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface DiagnosisService {

    // 1. 질병 분석 API (이미지 + 진단 요청 DTO 전송)
    @Multipart
    @POST("/api/diagnosis/analyze")
    suspend fun analyzeDiagnosis(
        @Part("diagnosisRequest") diagnosisRequest: RequestBody, // RequestBody로 유지
        @Part image: MultipartBody.Part // MultipartBody.Part로 수정
    ): Response<DefaultRes<DiagnosisResponseDTO>>

    // 2. 진단 결과 목록 조회
    @GET("/api/diagnosis/{userId}")
    suspend fun getDiagnosisResults(
        @Path("userId") userId: Long
    ): Response<DefaultRes<DiagnosisListResponseDTO>> // suspend 함수로 변경

    // 3. 진단 세부정보 조회
    @GET("/api/diagnosis/{userId}/{diagnosisId}")
    suspend fun getDiagnosisDetail(
        @Path("userId") userId: Long,
        @Path("diagnosisId") diagnosisId: Long
    ): Response<DefaultRes<DiagnosisResponseDTO>> // Response로 래핑된 DefaultRes 반환
}