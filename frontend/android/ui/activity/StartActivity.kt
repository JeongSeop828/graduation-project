package com.project.meongnyangcare.ui.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.project.meongnyangcare.R
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

class StartActivity : AppCompatActivity() {

    private val BACK_PRESSED_DURATION = 2000L

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton = findViewById<android.widget.Button>(R.id.loginBtn)
        val registerButton = findViewById<android.widget.Button>(R.id.registerBtn)

        // 로그인 화면으로 이동
        loginButton.setOnClickListener {
            val intent = android.content.Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 회원가입 화면으로 이동
        registerButton.setOnClickListener {
            val intent = android.content.Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 로그인 성공 여부 확인
        val loginSuccess = intent.getBooleanExtra("loginSuccess", false)
        if (loginSuccess) {
            // 로그인 성공 시 Snackbar 표시
            val rootView = findViewById<android.view.View>(R.id.main)
            Snackbar.make(rootView, "로그인 성공!", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.snackbar_background)) // 색상 변경
                .show()
        }

        // 뒤로가기 이벤트 처리
        val backPressEvent = MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        onBackPressedDispatcher.addCallback(this) {
            backPressEvent.tryEmit(Unit)
        }

        // Snackbar로 뒤로가기 이벤트 처리
        lifecycleScope.launch {
            backPressEvent
                .scan(listOf(System.currentTimeMillis() - BACK_PRESSED_DURATION)) { acc, _ ->
                    acc.takeLast(1) + System.currentTimeMillis()
                }
                .drop(1)
                .collectLatest {
                    if (it.last() - it.first() < BACK_PRESSED_DURATION) {
                        finishAffinity() // 앱 종료
                    } else {
                        // Snackbar로 사용자에게 한 번 더 눌러야 종료되는 메시지 표시
                        val rootView = findViewById<android.view.View>(R.id.main)
                        Snackbar.make(rootView, "뒤로가기를 한 번 더 누르면 종료합니다.", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getColor(R.color.snackbar_background)) // 색상 변경
                            .show()
                    }
                }
        }
    }
}
