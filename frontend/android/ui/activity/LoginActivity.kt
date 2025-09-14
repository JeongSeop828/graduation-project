package com.project.meongnyangcare.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.model.UserDTO
import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.TokenResponseDTO
import com.project.meongnyangcare.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextUserId = findViewById<EditText>(R.id.editTextUserId)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val textViewLoginError = findViewById<TextView>(R.id.textViewLoginError)

        val btnRegister = findViewById<Button>(R.id.registerBtn)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 로그인 화면에 회원가입 성공 메시지 표시
        val registrationSuccess = intent.getBooleanExtra("registrationSuccess", false)
        if (registrationSuccess) {
            showSnackbar("회원가입 성공! 로그인 해주세요.", R.color.snackbar_background)
        }

        // 로그인 버튼 클릭
        btnLogin.setOnClickListener {
            val userId = editTextUserId.text.toString().trim()
            val password = editTextPassword.text.toString()

            // 에러 메시지 초기화
            textViewLoginError.visibility = View.GONE

            // 아이디 및 비밀번호 체크
            when {
                userId.isEmpty() && password.isEmpty() -> {
                    showAnimatedText(textViewLoginError, "아이디와 비밀번호를 입력해주세요.", R.color.red)
                }
                userId.isEmpty() -> {
                    showAnimatedText(textViewLoginError, "아이디를 입력해주세요.", R.color.red)
                }
                password.isEmpty() -> {
                    showAnimatedText(textViewLoginError, "비밀번호를 입력해주세요.", R.color.red)
                }
                else -> {
                    // 서버로 로그인 요청
                    val user = UserDTO(username = userId, password = password)
                    loginUser(user)
                }
            }
        }
    }

    // 서버와 로그인 처리
    private fun loginUser(user: UserDTO) {
        val call = RetrofitClient.getUserService().login(user)
        call.enqueue(object : Callback<DefaultRes<TokenResponseDTO>> {
            override fun onResponse(
                call: Call<DefaultRes<TokenResponseDTO>>,
                response: Response<DefaultRes<TokenResponseDTO>>
            ) {
                if (response.isSuccessful) {
                    val userResponse = response.body()?.data
                    val token = userResponse?.accessToken
                    val userId = userResponse?.userId
                    Log.d("LoginActivity", "받은 토큰: $token")
                    Log.d("LoginActivity", "받은 유저 ID: $userId")

                    // 토큰을 SharedPreferences에 저장
                    saveAuthToken(token)
                    userId?.let { saveUserId(it) }

                    // 로그인 성공 후 MainActivity로 이동
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("loginSuccess", true)
                    startActivity(intent)
                    finish() // 로그인 화면 종료
                } else {
                    val errorMessage = response.body()?.message ?: "아이디/비밀번호를 확인해주세요."
                    showAnimatedText(findViewById(R.id.textViewLoginError), errorMessage, R.color.red)
                }
            }

            override fun onFailure(call: Call<DefaultRes<TokenResponseDTO>>, t: Throwable) {
                showAnimatedText(findViewById(R.id.textViewLoginError), "네트워크 오류", R.color.red)
            }
        })
    }

    // 에러 메시지 애니메이션으로 보여주기
    private fun showAnimatedText(textView: TextView, message: String, colorResId: Int) {
        textView.text = message
        textView.setTextColor(ContextCompat.getColor(this, colorResId))
        textView.visibility = View.VISIBLE
        textView.alpha = 0f
        textView.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    // 로그인 성공 메시지 표시
    private fun showSnackbar(message: String, colorResId: Int) {
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(this, colorResId))
            .show()
    }

    // 인증 토큰 저장 (예시: SharedPreferences)
    private fun saveAuthToken(token: String?) {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        token?.let {
            editor.putString("auth_token", it)
        }
        editor.apply()
    }

    // userId 저장 (같은 SharedPreferences 사용)
    private fun saveUserId(userId: Long) {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putLong("user_id", userId)
            apply()
        }
    }

    // 인증 토큰 가져오기
    private fun getAuthToken(): String? {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }
}
