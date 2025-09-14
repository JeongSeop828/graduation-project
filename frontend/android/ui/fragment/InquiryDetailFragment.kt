package com.project.meongnyangcare.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.InquiryDetailDTO
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.ui.activity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InquiryDetailFragment : Fragment() {

    private lateinit var editInquiryTitle: TextView
    private lateinit var editInquiryContent: TextView
    private lateinit var editInquiryReply: TextView
    private lateinit var btnCheck: Button
    private lateinit var btnDeleteInquiry: Button
    private var inquiryId: Long = 0
    private var userId: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setToolbarTitle("상세 문의 내역")

        // 로그인한 사용자의 userId와 inquiryId 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        userId = sharedPreferences.getLong("user_id", 0) // 로그인한 사용자 ID 가져오기
        inquiryId = arguments?.getLong("inquiryId") ?: 0

        btnCheck = view.findViewById(R.id.btnCheck)
        btnDeleteInquiry = view.findViewById(R.id.btnDeleteInquiry)
        editInquiryTitle = view.findViewById(R.id.editInquiryTitle)
        editInquiryContent = view.findViewById(R.id.editInquiryContent)
        editInquiryReply = view.findViewById(R.id.editInquiryReply)

        btnCheck.setOnClickListener {
            replaceFragment(InquiryFragment())
        }

        btnDeleteInquiry.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // 문의 상세 내용 불러오기
        loadInquiryDetail()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inquiry_detail, container, false)
    }

    private fun loadInquiryDetail() {
        // Retrofit을 이용하여 서버에서 문의 상세 내용 불러오기
        RetrofitClient.getInquiryServiceWithAuth(requireContext()).getInquiryDetail(userId, inquiryId)
            .enqueue(object : Callback<DefaultRes<InquiryDetailDTO>> {
                override fun onResponse(
                    call: Call<DefaultRes<InquiryDetailDTO>>,
                    response: Response<DefaultRes<InquiryDetailDTO>>
                ) {
                    if (response.isSuccessful) {
                        val inquiryDetail = response.body()?.data
                        if (inquiryDetail != null) {
                            // UI에 데이터를 설정
                            editInquiryTitle.text = inquiryDetail.title
                            editInquiryContent.text = inquiryDetail.content
                            editInquiryReply.text = inquiryDetail.reply ?: "답변 없음"
                        }
                    } else {
                        Toast.makeText(requireContext(), "문의 상세 조회 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultRes<InquiryDetailDTO>>, t: Throwable) {
                    Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.frame_layout, fragment)
            addToBackStack(null)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("문의내역 삭제")
            .setNegativeButton("취소", null)
            .setMessage("삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                deleteInquiry()
            }
            .show()
    }

    private fun deleteInquiry() {
        // 서버에서 문의 내역 삭제 로직
        RetrofitClient.getInquiryServiceWithAuth(requireContext()).deleteInquiry(inquiryId)
            .enqueue(object : Callback<DefaultRes<String>> {
                override fun onResponse(call: Call<DefaultRes<String>>, response: Response<DefaultRes<String>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "문의가 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        replaceFragment(InquiryFragment())
                    } else {
                        Toast.makeText(requireContext(), "문의 삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultRes<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
