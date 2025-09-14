package com.project.meongnyangcare.utils


import android.app.Application
import com.kakao.vectormap.KakaoMapSdk
import com.project.meongnyangcare.BuildConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val apiKey = BuildConfig.KAKAO_MAP_KEY
        KakaoMapSdk.init(this, apiKey)
    }
}
