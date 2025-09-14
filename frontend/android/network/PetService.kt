package com.project.meongnyangcare.network

import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.PetDataResponseDTO
import com.project.meongnyangcare.model.PetListResponseDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PetService {

    @Multipart
    @POST("users/{userId}/pets")
    suspend fun registerPet(
        @Path("userId") userId: Long,
        @Part("petInfo") petInfo: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<DefaultRes<String>>

    @DELETE("users/pets/{petId}")
    suspend fun deletePet(
        @Path("petId") petId: Long
    ): Response<DefaultRes<String>>

    @Multipart
    @PUT("users/{userId}/pets/{petId}")
    suspend fun updatePet(
        @Path("userId") userId: Long,
        @Path("petId") petId: Long,
        @Part("petInfo") petInfo: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<DefaultRes<String>>

    @GET("users/{userId}/pets")
    suspend fun getPets(
        @Path("userId") userId: Long?
    ): Response<DefaultRes<PetListResponseDTO>>

    @GET("users/{userId}/pets/{petId}")
    suspend fun getPetDetail(
        @Path("userId") userId: Long,
        @Path("petId") petId: Long
    ): Response<DefaultRes<PetDataResponseDTO>>
}
