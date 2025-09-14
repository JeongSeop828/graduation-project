package com.project.meongnyangcare.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.project.meongnyangcare.HomeFragment
import com.project.meongnyangcare.ui.fragment.AnalysisFragment
import com.project.meongnyangcare.ui.fragment.MyPageFragment
import com.project.meongnyangcare.ui.fragment.PastDiagnosisFragment
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.project.meongnyangcare.R
import com.project.meongnyangcare.databinding.ActivityMainBinding
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val BACK_PRESSED_DURATION = 2000L
    private val backPressEvent = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        val loginSuccess = intent.getBooleanExtra("loginSuccess", false)
        if (loginSuccess) {
            showSnackbar("로그인 성공!", R.color.snackbar_background)
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.analysis -> replaceFragment(AnalysisFragment())
                R.id.analysishistory -> replaceFragment(PastDiagnosisFragment())
                R.id.mypage -> replaceFragment(MyPageFragment())
            }
            true
        }

        // 뒤로가기 처리
        onBackPressedDispatcher.addCallback(this) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
            if (currentFragment is HomeFragment) {
                backPressEvent.tryEmit(Unit)
            } else {
                // 홈이 아닐 경우 홈으로 이동
                replaceFragment(HomeFragment())
                binding.bottomNavigationView.selectedItemId = R.id.home
            }
        }

        // 두 번 눌렀을 때 종료 처리
        lifecycleScope.launch {
            backPressEvent
                .scan(listOf(System.currentTimeMillis() - BACK_PRESSED_DURATION)) { acc, _ ->
                    acc.takeLast(1) + System.currentTimeMillis()
                }
                .drop(1)
                .collectLatest {
                    if (it.last() - it.first() < BACK_PRESSED_DURATION) {
                        finishAffinity()
                    } else {
                        showSnackbar("뒤로가기를 한 번 더 누르면 종료됩니다.", R.color.snackbar_background)
                    }
                }
        }
    }

    // 툴바 타이틀 설정
    fun setToolbarTitle(title: String) {
        findViewById<TextView>(R.id.textViewToolbarTitle).text = title
    }

    // 프래그먼트 교체
    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        // 애니메이션 설정:
        // 첫 번째 파라미터: 들어올 때 애니메이션
        // 두 번째 파라미터: 나갈 때 애니메이션
        transaction.setCustomAnimations(
            R.anim.enter_from_right,   // 들어올 때 애니메이션
            R.anim.exit_to_left,       // 나갈 때 애니메이션
            R.anim.enter_from_left,    // 다시 들어올 때 애니메이션
            R.anim.exit_to_right      // 나갈 때 애니메이션
        )

        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()
    }

    // 스낵바 표시
    private fun showSnackbar(message: String, colorResId: Int) {
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(this, colorResId))
            .show()
    }
}