package com.project.meongnyangcare.network

import com.project.meongnyangcare.network.dto.HospitalDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HospitalService {

    // 병원 리스트 가져오기
    @GET("/api/hospitals")
    fun getNearestHospitals(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int
    ): Call<List<HospitalDto>>
}
