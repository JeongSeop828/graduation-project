package com.project.meongnyangcare.network

import com.project.meongnyangcare.model.UserDTO
import com.project.meongnyangcare.model.StatusDTO
import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.TokenResponseDTO
import com.project.meongnyangcare.model.UserResponseDto
import com.project.meongnyangcare.model.PasswordChangeRequestDto
import com.project.meongnyangcare.model.NicknameChangeRequestDTO
import com.project.meongnyangcare.model.PasswordVerifyRequestDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.*

interface UserService {
    @POST("/users/signup")
    fun signUp(@Body user: UserDTO): Call<DefaultRes<StatusDTO>>

    @POST("/users/DuplicateTest")
    fun checkDuplicate(@Body user: UserDTO): Call<DefaultRes<StatusDTO>>

    @POST("/users/login")
    fun login(@Body loginRequest: UserDTO): Call<DefaultRes<TokenResponseDTO>>

    @GET("/users/{id}")
    fun getUser(@Path("id") userId: Long, @Header("Authorization") token: String): Call<DefaultRes<UserResponseDto>>

    @DELETE("/users/{id}")
    fun deleteUser(@Path("id") userId: Long, @Header("Authorization") token: String): Call<DefaultRes<String>>

    @PATCH("/users/{id}/password")
    fun changePassword(@Path("id") userId: Long, @Body dto: PasswordChangeRequestDto, @Header("Authorization") token: String): Call<DefaultRes<String>>

    @POST("/users/{id}/verify-password")
    fun verifyPassword(@Path("id") userId: Long, @Body request: PasswordVerifyRequestDto, @Header("Authorization") token: String): Call<DefaultRes<String>>

    @PATCH("/users/{id}/nickName")
    fun changeNickname(@Path("id") userId: Long, @Body dto: NicknameChangeRequestDTO, @Header("Authorization") token: String): Call<DefaultRes<String>>

    @POST("/users/logout")
    fun logout(@Header("Authorization") token: String): Call<DefaultRes<String>>
}
