package com.project.meongnyangcare.network

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // 서버 주소 설정
    private const val BASE_URL = "http://ceprj.gachon.ac.kr:60014" // 서버 주소

    private fun createDefaultOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    // 토큰 필요 없는 요청용 Retrofit
    private val retrofitNoAuth: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createDefaultOkHttpClient())
            .build()
    }

    fun getHospitalServiceWithAuth(context: Context): HospitalService {
        return getInstanceWithAuth(context).create(HospitalService::class.java)
    }

    // 토큰이 필요 없는 UserService
    fun getUserService(): UserService {
        return retrofitNoAuth.create(UserService::class.java)
    }

    // 로그인 후 토큰 포함 요청용 Retrofit
    private fun provideOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) // 인증을 위한 인터셉터 추가
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    // 인증이 필요한 Retrofit 인스턴스
    fun getInstanceWithAuth(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient(context)) // 인증 정보를 포함한 OkHttpClient 사용
            .build()
    }

    // 인증이 필요한 UserService
    fun getUserServiceWithAuth(context: Context): UserService {
        return getInstanceWithAuth(context).create(UserService::class.java)
    }

    // 인증이 필요 없는 Retrofit 인스턴스
    fun getInstanceNoAuth(): Retrofit {
        return retrofitNoAuth
    }

    // 인증이 필요 없는 UserService
    fun getUserServiceNoAuth(): UserService {
        return getInstanceNoAuth().create(UserService::class.java)
    }

    // 인증이 필요한 PetService
    fun getPetServiceWithAuth(context: Context): PetService {
        return getInstanceWithAuth(context).create(PetService::class.java)
    }

    // 인증이 필요 없는 PetService
    fun getPetServiceNoAuth(): PetService {
        return getInstanceNoAuth().create(PetService::class.java)
    }

    // 인증이 필요 없는 PetService
    fun getPetService(): PetService {
        return retrofitNoAuth.create(PetService::class.java)
    }

    fun getInquiryServiceWithAuth(context: Context): InquiryService {
        return getInstanceWithAuth(context).create(InquiryService::class.java)
    }

    // 인증이 필요 없는 InquiryService
    fun getInquiryServiceNoAuth(): InquiryService {
        return getInstanceNoAuth().create(InquiryService::class.java)
    }

    // 기본 인증 없이 호출할 InquiryService (필요 시)
    fun getInquiryService(): InquiryService {
        return retrofitNoAuth.create(InquiryService::class.java)
    }

    // DiagnosisService (인증 필요)
    fun getDiagnosisServiceWithAuth(context: Context): DiagnosisService {
        Log.d("RetrofitClient", "Getting DiagnosisService with Auth")
        return getInstanceWithAuth(context).create(DiagnosisService::class.java)
    }

    // DiagnosisService (인증 필요 없음)
    fun getDiagnosisServiceNoAuth(): DiagnosisService {
        return getInstanceNoAuth().create(DiagnosisService::class.java)
    }
}

// Authorization 헤더에 JWT 토큰 자동 추가
class AuthInterceptor(private val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        val requestBuilder = chain.request().newBuilder()
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token") // 토큰을 Authorization 헤더에 추가
        }

        return chain.proceed(requestBuilder.build())
    }
}
