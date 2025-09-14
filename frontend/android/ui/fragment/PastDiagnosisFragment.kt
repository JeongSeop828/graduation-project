package com.project.meongnyangcare.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.DefaultRes
import com.project.meongnyangcare.model.DiagnosisListResponseDTO
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.ui.activity.MainActivity
import com.project.meongnyangcare.ui.adapter.DiagnosisAdapter
import kotlinx.coroutines.launch
import retrofit2.Response

class PastDiagnosisFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setToolbarTitle("과거 진단 기록")

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewDiagnosis)

        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)
        val token = sharedPreferences.getString("auth_token", null)

        if (userId == -1L || token == null) {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val diagnosisService = RetrofitClient.getDiagnosisServiceWithAuth(requireContext())

        // 코루틴을 사용하여 suspend 함수 호출
        lifecycleScope.launch {
            try {
                val response: Response<DefaultRes<DiagnosisListResponseDTO>> = diagnosisService.getDiagnosisResults(userId)

                if (response.isSuccessful) {
                    val diagnosisList = response.body()?.data?.diagnosisDTOS ?: emptyList()
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.adapter = DiagnosisAdapter(diagnosisList) { diagnosis ->
                        openNextFragment(diagnosis.diagnosisId)
                    }
                } else {
                    Toast.makeText(requireContext(), "진단 내역 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "서버 통신 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_past_diagnosis, container, false)
    }

    private fun openNextFragment(diagnosisId: Long) {
        val fragment = DiagnosisDetailFragment()
        val bundle = Bundle()
        bundle.putLong("diagnosisId", diagnosisId) // diagnosisId를 넘김
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}
