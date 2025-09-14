package com.project.meongnyangcare.ui.fragment

import com.project.meongnyangcare.model.Pet
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongnyangcare.R
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.ui.AddPetFragment
import com.project.meongnyangcare.ui.activity.MainActivity
import com.project.meongnyangcare.ui.activity.StartActivity
import com.project.meongnyangcare.ui.adapter.PetAdapter
import android.util.Log
import android.widget.Toast
import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.UserResponseDto
import com.project.meongnyangcare.utils.PetUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {

    private lateinit var btnUpdateUser: Button
    private lateinit var btnAddPet: Button
    private lateinit var btnInquiry: Button
    private lateinit var btnLogOut: Button
    private lateinit var btnWithdrawMember: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNickName: TextView

    private lateinit var adapter: PetAdapter // 어댑터 전역 선언

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle("마이페이지")

        btnUpdateUser = view.findViewById(R.id.btnUpdateUser)
        btnAddPet = view.findViewById(R.id.btnAddPet)
        btnInquiry = view.findViewById(R.id.btnInquiry)
        btnLogOut = view.findViewById(R.id.btnLogOut)
        btnWithdrawMember = view.findViewById(R.id.btnWithdrawMember)
        recyclerView = view.findViewById(R.id.recyclerViewPets)
        tvNickName = view.findViewById(R.id.tvNickName)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PetAdapter(emptyList()) { pet -> openNextFragment(pet) }
        recyclerView.adapter = adapter

        btnUpdateUser.setOnClickListener { replaceFragment(UpdateUserFragment()) }
        btnAddPet.setOnClickListener { replaceFragment(AddPetFragment()) }
        btnInquiry.setOnClickListener { replaceFragment(InquiryFragment()) }
        btnLogOut.setOnClickListener { showLogOutConfirmationDialog() }
        btnWithdrawMember.setOnClickListener { showWithdrawMemberConfirmationDialog() }

        loadUserProfile()
        loadPetList()
    }

    private fun loadUserProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)
        val token = sharedPreferences.getString("auth_token", null)

        if (userId == -1L || token == null) {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("MyPageFragment", "UserID: $userId, Token: $token")

        // Retrofit 요청에 Authorization 헤더 추가
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
                        Log.e("MyPageFragment", "Error: ${response.code()}, ${response.message()}")
                        Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultRes<UserResponseDto>>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun loadPetList() {
        PetUtils.loadPetList(
            context = requireContext(),
            onSuccess = { petList ->
                Log.d("MyPageFragment", "불러온 반려동물 수: ${petList.size}")

                val updatedPetList = petList.map { pet ->
                    Pair(pet, 1)
                }

                // 어댑터에 업데이트된 리스트 전달
                adapter.updatePetList(updatedPetList)
            }
        )
    }

    private fun openNextFragment(pet: Pet) {
        val fragment = PetDetailFragment()
        val bundle = Bundle()
        bundle.putLong("petId", pet.id.toLong())
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.frame_layout, fragment)
            addToBackStack(null)
        }
    }

    private fun showLogOutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("로그아웃")
            .setMessage("로그아웃 하시겠습니까?")
            .setPositiveButton("확인") { _, _ -> logOutProcess() }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun logOutProcess() {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (!token.isNullOrEmpty()) {
            RetrofitClient.getUserService().logout("Bearer $token")
                .enqueue(object : Callback<DefaultRes<String>> {
                    override fun onResponse(
                        call: Call<DefaultRes<String>>,
                        response: Response<DefaultRes<String>>
                    ) {
                        if (response.isSuccessful) {
                            sharedPreferences.edit().remove("auth_token").apply()
                            Toast.makeText(requireContext(), "로그아웃이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(requireContext(), StartActivity::class.java))
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<DefaultRes<String>>, t: Throwable) {
                        Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showWithdrawMemberConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("회원탈퇴")
            .setMessage("회원탈퇴를 하시겠습니까?")
            .setPositiveButton("확인") { _, _ -> checkWithdrawMember() }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun checkWithdrawMember() {
        AlertDialog.Builder(requireContext())
            .setTitle("회원탈퇴")
            .setMessage("정말 회원탈퇴를 하시겠습니까?")
            .setPositiveButton("확인") { _, _ -> withdrawMemberProcess() }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun withdrawMemberProcess() {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)
        val token = sharedPreferences.getString("auth_token", null)

        if (userId == -1L || token == null) {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getUserService().deleteUser(userId, "Bearer $token")
            .enqueue(object : Callback<DefaultRes<String>> {
                override fun onResponse(
                    call: Call<DefaultRes<String>>,
                    response: Response<DefaultRes<String>>
                ) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        sharedPreferences.edit().clear().apply()
                        Toast.makeText(requireContext(), "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireContext(), StartActivity::class.java))
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "회원탈퇴 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultRes<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
