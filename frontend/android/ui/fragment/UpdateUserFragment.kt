package com.project.meongnyangcare.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.project.meongnyangcare.R
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.UserResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateUserFragment : Fragment() {

    private lateinit var tvNickName: TextView
    private lateinit var btnUpdateNickName: Button
    private lateinit var btnUpdatePassword: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvNickName = view.findViewById(R.id.tvNickName)
        btnUpdateNickName = view.findViewById(R.id.btnUpdateNickName)
        btnUpdatePassword = view.findViewById(R.id.btnUpdatePassWord)

        // 사용자 프로필 로드
        loadUserProfile()

        btnUpdateNickName.setOnClickListener {
            // 닉네임 업데이트 화면으로 이동
            replaceFragment(UpdateNickNameFragment())
        }

        btnUpdatePassword.setOnClickListener {
            // 비밀번호 업데이트 화면으로 이동
            replaceFragment(UpdatePassWordFragment())
        }
    }

    private fun loadUserProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)
        val token = sharedPreferences.getString("auth_token", null)

        if (userId == -1L || token == null) {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getUserService().getUser(userId, "Bearer $token")
            .enqueue(object : Callback<DefaultRes<UserResponseDto>> {
                override fun onResponse(
                    call: Call<DefaultRes<UserResponseDto>>,
                    response: Response<DefaultRes<UserResponseDto>>
                ) {
                    if (response.isSuccessful) {
                        val user = response.body()?.data
                        if (user != null) {
                            tvNickName.text = user.nickname // 서버에서 받아온 닉네임으로 설정
                        } else {
                            Toast.makeText(requireContext(), "사용자 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultRes<UserResponseDto>>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}
