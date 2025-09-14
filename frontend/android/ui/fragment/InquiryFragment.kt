package com.project.meongnyangcare.ui.fragment

import com.project.meongnyangcare.model.Inquiry
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.InquiryListResponseDTO
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.ui.activity.MainActivity
import com.project.meongnyangcare.ui.adapter.InquiryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InquiryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnInquiry: Button
    private lateinit var adapter: InquiryAdapter
    private var inquiryList: List<Inquiry> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inquiry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setToolbarTitle("문의 하기")

        recyclerView = view.findViewById(R.id.recyclerViewInquiry)
        btnInquiry = view.findViewById(R.id.btnInquiry)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = InquiryAdapter(inquiryList) { inquiry ->
            openNextFragment(inquiry)
        }
        recyclerView.adapter = adapter

        btnInquiry.setOnClickListener {
            replaceFragment(InquiryFormFragment())
        }

        loadInquiryList()
    }

    private fun loadInquiryList() {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)
        val token = sharedPreferences.getString("auth_token", null)

        if (userId == -1L) {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getInquiryServiceWithAuth(requireContext()).getInquiryList(userId)
            .enqueue(object : Callback<DefaultRes<InquiryListResponseDTO>> {
                override fun onResponse(
                    call: Call<DefaultRes<InquiryListResponseDTO>>,
                    response: Response<DefaultRes<InquiryListResponseDTO>>
                ) {
                    if (response.isSuccessful) {
                        val inquiryDTOs = response.body()?.data?.inquiryDTOS.orEmpty()

                        // InquiryDTO -> Inquiry로 변환
                        val inquiryList = inquiryDTOs.map { dto ->
                            Inquiry(
                                id = dto.inquiryId.toString(),
                                title = dto.title,
                                isAnswered = when (dto.status) {
                                    "COMPLETED" -> "답변 완료"
                                    "PENDING" -> "답변\n진행중"
                                    else -> "상태 알 수 없음"
                                }
                            )
                        }

                        adapter = InquiryAdapter(inquiryList) { inquiry ->
                            openNextFragment(inquiry)
                        }
                        recyclerView.adapter = adapter
                    } else {
                        Log.e("InquiryList", "Response error: ${response.message()}")
                        Toast.makeText(requireContext(), "문의 내역을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultRes<InquiryListResponseDTO>>, t: Throwable) {
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

    private fun openNextFragment(inquiry: Inquiry) {
        val fragment = InquiryDetailFragment()
        val bundle = Bundle()
        bundle.putLong("inquiryId", inquiry.id.toLong()) // com.project.meongnyangcare.model.Inquiry.id를 Long으로 변환
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}
