package com.project.meongnyangcare.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.commit
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.NicknameChangeRequestDTO
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.ui.activity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateNickNameFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setToolbarTitle("닉네임 변경")

        val btnSaveUser = view.findViewById<Button>(R.id.btnSaveUser)
        val editNickName = view.findViewById<EditText>(R.id.editNickName)
        val errorNickName = view.findViewById<TextView>(R.id.errorNickName)

        btnSaveUser.setOnClickListener {
            val newNickName = editNickName.text.toString().trim()

            if (newNickName.isEmpty()) {
                errorNickName.visibility = View.VISIBLE
            } else {
                errorNickName.visibility = View.GONE

                // Retrofit을 이용해 서버에 닉네임 변경 요청
                val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getLong("user_id", -1L)
                val token = sharedPreferences.getString("auth_token", null)

                if (userId == -1L || token == null) {
                    Toast.makeText(requireContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 서버에 닉네임 변경 요청
                val nicknameChangeRequestDTO = NicknameChangeRequestDTO(newNickName)

                RetrofitClient.getUserService().changeNickname(userId, nicknameChangeRequestDTO, "Bearer $token")
                    .enqueue(object : Callback<DefaultRes<String>> {
                        override fun onResponse(call: Call<DefaultRes<String>>, response: Response<DefaultRes<String>>) {
                            if (response.isSuccessful) {
                                // 닉네임 변경 성공
                                AlertDialog.Builder(requireContext())
                                    .setTitle("알림")
                                    .setMessage("닉네임이 변경되었습니다.")
                                    .setPositiveButton("확인") { dialog, _ ->
                                        dialog.dismiss()
                                        replaceFragment(MyPageFragment()) // MyPageFragment로 돌아가기
                                    }
                                    .show()
                            } else {
                                // 닉네임 변경 실패
                                Toast.makeText(requireContext(), "닉네임 변경 실패", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<DefaultRes<String>>, t: Throwable) {
                            Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_nick_name, container, false)
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.frame_layout, fragment)
            addToBackStack(null)
        }
    }
}
