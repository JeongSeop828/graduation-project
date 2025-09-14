package com.project.meongnyangcare

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.project.meongnyangcare.ui.activity.StartActivity
import com.project.meongnyangcare.R
import java.security.MessageDigest


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val appNameTextView: TextView = findViewById(R.id.name)
        val subNameTextView: TextView = findViewById(R.id.subName)


        getKeyHash()
        // Bold 폰트 적용은 xml에서 설정했으므로 코드로는 별도 처리 불필요

        // 3초 후 메인화면으로 이동 (예시 MainActivity)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    private fun getKeyHash() {
        try {
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                info.signatures
            }

            if (signatures != null) {
                for (signature in signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val hashKey = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                    Log.d("getKeyHash", "key hash: $hashKey")
                }
            }
        } catch (e: Exception) {
            Log.e("getKeyHash", "Unable to get KeyHash", e)
        }
    }
}
