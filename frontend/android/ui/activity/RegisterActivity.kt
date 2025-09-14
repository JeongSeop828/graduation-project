package com.project.meongnyangcare.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.UserDTO
import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.StatusDTO
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.network.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var userService: UserService
    private var isIdChecked = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userService = RetrofitClient.getUserService()

        val editTextUserId = findViewById<EditText>(R.id.editTextRegisterUserId)
        val editTextPassword = findViewById<EditText>(R.id.editTextRegisterPassword)
        val editTextPasswordConfirm = findViewById<EditText>(R.id.editTextRegisterPasswordConfirm)
        val editTextNickname = findViewById<EditText>(R.id.editTextRegisterNickname)

        val textViewIdCheckResult = findViewById<TextView>(R.id.textViewIdCheckResult)
        val textViewPasswordError = findViewById<TextView>(R.id.textViewPasswordError)
        val textViewEmptyFieldsError = findViewById<TextView>(R.id.textViewEmptyFieldsError)

        val btnCheckDuplicate = findViewById<Button>(R.id.btnCheckDuplicate)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // 아이디 중복 확인
        btnCheckDuplicate.setOnClickListener {
            val inputId = editTextUserId.text.toString().trim()
            if (inputId.isEmpty()) {
                showAnimatedText(textViewIdCheckResult, "아이디를 입력하세요.", R.color.red)
                return@setOnClickListener
            }

            val userDTO = UserDTO(username = inputId, password = "")
            userService.checkDuplicate(userDTO).enqueue(object : Callback<DefaultRes<StatusDTO>> {
                override fun onResponse(
                    call: Call<DefaultRes<StatusDTO>>,
                    response: Response<DefaultRes<StatusDTO>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val status = response.body()?.data?.status
                        Log.d("RegisterActivity", "Status: $status")  // 상태 확인

                        if (status == 0) {
                            showAnimatedText(textViewIdCheckResult, "중복된 아이디입니다.", R.color.red)
                            isIdChecked = false
                        } else if (status == 1) {
                            showAnimatedText(textViewIdCheckResult, "가입 가능한 아이디입니다.", R.color.blue)
                            isIdChecked = true
                        } else {
                            showAnimatedText(textViewIdCheckResult, "오류 발생", R.color.red)
                            isIdChecked = false
                        }
                    } else {
                        showAnimatedText(textViewIdCheckResult, "서버 응답 오류", R.color.red)
                        isIdChecked = false
                    }
                }

                override fun onFailure(call: Call<DefaultRes<StatusDTO>>, t: Throwable) {
                    showAnimatedText(textViewIdCheckResult, "네트워크 오류", R.color.red)
                    isIdChecked = false
                }
            })
        }

        // 회원가입 버튼 클릭
        btnRegister.setOnClickListener {
            val userId = editTextUserId.text.toString().trim()
            val password = editTextPassword.text.toString()
            val passwordConfirm = editTextPasswordConfirm.text.toString()
            val nickname = editTextNickname.text.toString().trim()

            // 필수 입력 필드 확인
            if (userId.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || nickname.isEmpty()) {
                showAnimatedText(textViewEmptyFieldsError, "모든 항목을 입력해주세요.", R.color.red)
                return@setOnClickListener
            } else {
                hideTextWithAnimation(textViewEmptyFieldsError)
            }

            // 비밀번호 일치 확인
            if (password != passwordConfirm) {
                showAnimatedText(textViewPasswordError, "비밀번호가 일치하지 않습니다.", R.color.red)
                return@setOnClickListener
            } else {
                hideTextWithAnimation(textViewPasswordError)
            }

            // 중복 확인이 먼저 되어야 함
            if (!isIdChecked) {
                showAnimatedText(textViewIdCheckResult, "아이디 중복 확인을 해주세요.", R.color.red)
                return@setOnClickListener
            }

            // 회원가입 요청
            val userDTO = UserDTO(username = userId, password = password, nickname = nickname, email = "-", name = "-", phone = "-")
            userService.signUp(userDTO).enqueue(object : Callback<DefaultRes<StatusDTO>> {
                override fun onResponse(
                    call: Call<DefaultRes<StatusDTO>>,
                    response: Response<DefaultRes<StatusDTO>>
                ) {
                    Log.d("RegisterActivity", "Registration status: ${response.body()?.data?.status}")

                    if (response.isSuccessful && response.body() != null) {
                        val status = response.body()?.data?.status

                        if (status == 1) {
                            // 회원가입 성공
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            intent.putExtra("registrationSuccess", true)
                            startActivity(intent)
                            finish()
                        } else {
                            // 회원가입 실패
                            Toast.makeText(this@RegisterActivity, "회원가입 실패: ${response.body()?.data?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("RegisterActivity", "서버 오류: ${response.message()}")
                        // 서버 응답 오류
                        Toast.makeText(this@RegisterActivity, "서버 오류: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultRes<StatusDTO>>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "네트워크 오류: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showAnimatedText(textView: TextView, message: String, colorResId: Int) {
        textView.text = message
        textView.setTextColor(resources.getColor(colorResId, null))
        if (textView.visibility == View.GONE) {
            textView.alpha = 0f
            textView.visibility = View.VISIBLE
            textView.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        }
    }

    private fun hideTextWithAnimation(textView: TextView) {
        if (textView.visibility == View.VISIBLE) {
            textView.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    textView.visibility = View.GONE
                }
                .start()
        }
    }
}
