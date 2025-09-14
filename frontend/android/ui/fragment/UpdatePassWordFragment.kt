package com.project.meongnyangcare.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.PasswordChangeRequestDto
import com.project.meongnyangcare.model.PasswordVerifyRequestDto
import com.project.meongnyangcare.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePassWordFragment : Fragment() {

    private lateinit var editTextCurrentPassword: EditText
    private lateinit var etUpdatePassword: EditText
    private lateinit var etUpdatePasswordConfirm: EditText
    private lateinit var tvPWCheckResult: TextView
    private lateinit var textViewPasswordError: TextView
    private lateinit var btnCheckDuplicate: Button
    private lateinit var btnSaveUser: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_pass_word, container, false)

        // 초기화
        editTextCurrentPassword = view.findViewById(R.id.editTextRegisterUserId)
        etUpdatePassword = view.findViewById(R.id.etUpdatePassword)
        etUpdatePasswordConfirm = view.findViewById(R.id.etUpdatePasswordConfirm)
        tvPWCheckResult = view.findViewById(R.id.tvPWCheckResult)
        textViewPasswordError = view.findViewById(R.id.textViewPasswordError)
        btnCheckDuplicate = view.findViewById(R.id.btnCheckDuplicate)
        btnSaveUser = view.findViewById(R.id.btnSaveUser)

        // 비밀번호 확인 버튼 클릭 이벤트
        btnCheckDuplicate.setOnClickListener {
            val enteredPassword = editTextCurrentPassword.text.toString()

            // SharedPreferences에서 token과 userId 가져오기
            val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getLong("user_id", -1L)
            val token = sharedPreferences.getString("auth_token", null)

            // 서버에서 기존 비밀번호 확인
            if (userId != -1L && token != null) {
                verifyCurrentPassword(userId, enteredPassword, token)
            } else {
                showErrorDialog("로그인 정보가 유효하지 않습니다.")
            }
        }

        // 비밀번호 변경 버튼 클릭 이벤트
        btnSaveUser.setOnClickListener {
            val newPassword = etUpdatePassword.text.toString()
            val confirmPassword = etUpdatePasswordConfirm.text.toString()

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                textViewPasswordError.text = "새 비밀번호를 입력해주세요."
                textViewPasswordError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                textViewPasswordError.text = "비밀번호가 일치하지 않습니다."
                textViewPasswordError.visibility = View.VISIBLE
            } else {
                textViewPasswordError.visibility = View.GONE

                // SharedPreferences에서 token과 userId 가져오기
                val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getLong("user_id", -1L)
                val token = sharedPreferences.getString("auth_token", null)

                // 비밀번호 변경 요청
                if (userId != -1L && token != null) {
                    changePassword(userId, newPassword, token)
                } else {
                    showErrorDialog("로그인 정보가 유효하지 않습니다.")
                }
            }
        }

        return view
    }

    private fun verifyCurrentPassword(userId: Long, currentPassword: String, token: String) {
        // Retrofit 사용하여 비밀번호 확인 요청
        RetrofitClient.getUserService().verifyPassword(userId, PasswordVerifyRequestDto(currentPassword), "Bearer $token")
            .enqueue(object : Callback<DefaultRes<String>> {
                override fun onResponse(
                    call: Call<DefaultRes<String>>,
                    response: Response<DefaultRes<String>>
                ) {
                    if (response.isSuccessful) {
                        // 비밀번호가 일치하는 경우
                        tvPWCheckResult.text = "비밀번호가 확인되었습니다."
                        tvPWCheckResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue)) // 파란색으로 설정
                        tvPWCheckResult.visibility = View.VISIBLE
                        etUpdatePassword.isEnabled = true
                        etUpdatePasswordConfirm.isEnabled = true
                    } else {
                        // 비밀번호가 일치하지 않는 경우
                        tvPWCheckResult.text = "현재 비밀번호가 올바르지 않습니다."
                        tvPWCheckResult.visibility = View.VISIBLE
                        etUpdatePassword.isEnabled = false
                        etUpdatePasswordConfirm.isEnabled = false
                    }
                }

                override fun onFailure(call: Call<DefaultRes<String>>, t: Throwable) {
                    // 서버와 연결 실패
                    showErrorDialog("서버와의 연결에 실패했습니다. 오류: ${t.message}")
                }
            })
    }


    private fun changePassword(userId: Long, newPassword: String, token: String) {
        // 새로운 비밀번호를 담은 DTO 객체 생성
        val passwordChangeRequestDto = PasswordChangeRequestDto(newPassword)

        // Retrofit을 사용하여 비밀번호 변경 요청
        RetrofitClient.getUserService().changePassword(userId, passwordChangeRequestDto, "Bearer $token")
            .enqueue(object : Callback<DefaultRes<String>> {
                override fun onResponse(
                    call: Call<DefaultRes<String>>,
                    response: Response<DefaultRes<String>>
                ) {
                    if (response.isSuccessful) {
                        // 비밀번호 변경 성공 시 처리
                        showSuccessDialog()  // 비밀번호 변경 성공 다이얼로그
                    } else {
                        // 비밀번호 변경 실패 시 처리
                        showErrorDialog("비밀번호 변경 실패")
                    }
                }

                override fun onFailure(call: Call<DefaultRes<String>>, t: Throwable) {
                    // 서버 연결 실패 시 처리
                    showErrorDialog("서버와의 연결에 실패했습니다.")
                }
            })
    }


    private fun showSuccessDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("비밀번호 변경")
            .setMessage("비밀번호가 성공적으로 변경되었습니다.")
            .setPositiveButton("확인") { _, _ -> replaceFragment(MyPageFragment()) }
            .show()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("에러")
            .setMessage(message)
            .setPositiveButton("확인", null)
            .show()
    }
}
