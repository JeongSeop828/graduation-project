package com.project.meongnyangcare.network

import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.InquiryDetailDTO
import com.project.meongnyangcare.model.InquiryListResponseDTO
import com.project.meongnyangcare.model.InquiryRegisterDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.*

interface InquiryService {

    // 1. 문의 등록
    @POST("/users/{userId}/inquiries")
    fun registerInquiry(
        @Path("userId") userId: Long,
        @Body inquiryRegisterDTO: InquiryRegisterDTO,
    ): Call<DefaultRes<String>>

    // 2. 문의 목록 조회
    @GET("/users/{userId}/inquiries")
    fun getInquiryList(
        @Path("userId") userId: Long,
    ): Call<DefaultRes<InquiryListResponseDTO>>

    // 3. 문의 상세 조회
    @GET("/users/{userId}/{inquiryId}")
    fun getInquiryDetail(
        @Path("userId") userId: Long,
        @Path("inquiryId") inquiryId: Long,
    ): Call<DefaultRes<InquiryDetailDTO>>

    // 4. 문의 삭제
    @DELETE("/inquiries/{inquiryId}")
    fun deleteInquiry(
        @Path("inquiryId") inquiryId: Long
    ): Call<DefaultRes<String>>
}
