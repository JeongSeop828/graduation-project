package com.project.meongnyangcare.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.InquiryRegisterDTO
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.ui.activity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InquiryFormFragment : Fragment() {

    private lateinit var editInquiryTitle: EditText
    private lateinit var editInquiryContent: EditText
    private lateinit var errorInquiryTitle: TextView
    private lateinit var errorInquiryContent: TextView
    private lateinit var btnSubmitInquiry: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setToolbarTitle("문의 하기")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_inquiry_form, container, false)

        editInquiryTitle = view.findViewById(R.id.editInquiryTitle)
        editInquiryContent = view.findViewById(R.id.editInquiryContent)
        errorInquiryTitle = view.findViewById(R.id.errorInquiryTitle)
        errorInquiryContent = view.findViewById(R.id.errorInquiryContent)
        btnSubmitInquiry = view.findViewById(R.id.btnSubmitInquiry)

        btnSubmitInquiry.setOnClickListener {
            submitInquiry()
        }
        return view
    }

    private fun submitInquiry() {
        var isValid = true

        if (editInquiryTitle.text.isEmpty()) {
            errorInquiryTitle.visibility = View.VISIBLE
            isValid = false
        } else {
            errorInquiryTitle.visibility = View.GONE
        }

        if (editInquiryContent.text.isEmpty()) {
            errorInquiryContent.visibility = View.VISIBLE
            isValid = false
        } else {
            errorInquiryContent.visibility = View.GONE
        }

        if (!isValid) return

        // 사용자 ID 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)
        val token = sharedPreferences.getString("auth_token", null)

        if (userId == -1L) {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val inquiryRegisterDTO = InquiryRegisterDTO(
            title = editInquiryTitle.text.toString(),
            content = editInquiryContent.text.toString()
        )

        // Retrofit을 통해 문의 등록 요청
        RetrofitClient.getInquiryServiceWithAuth(requireContext()).registerInquiry(userId, inquiryRegisterDTO)
            .enqueue(object : Callback<DefaultRes<String>> {
                override fun onResponse(
                    call: Call<DefaultRes<String>>,
                    response: Response<DefaultRes<String>>
                ) {
                    if (response.isSuccessful) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("제출 완료")
                        builder.setMessage("문의가 제출되었습니다.")
                        builder.setPositiveButton("확인") { _, _ ->
                            val fragment = InquiryFragment()
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, fragment)
                                .addToBackStack(null)
                                .commit()
                        }
                        builder.show()
                    } else {
                        // 서버 응답 실패 로그 추가
                        Log.e("InquiryFormFragment", "Response error: ${response.code()} - ${response.message()}")
                        Toast.makeText(requireContext(), "문의 등록에 실패했습니다. (${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultRes<String>>, t: Throwable) {
                    Log.e("InquiryFormFragment", "Request failed: ${t.message}")
                    Toast.makeText(requireContext(), "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
