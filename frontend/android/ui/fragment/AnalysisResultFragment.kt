package com.project.meongnyangcare.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.project.meongnyangcare.HomeFragment
import com.project.meongnyangcare.R
import com.project.meongnyangcare.databinding.FragmentAnalysisResultBinding
import com.project.meongnyangcare.model.DiagnosisResponseDTO
import com.project.meongnyangcare.network.DiagnosisService
import com.project.meongnyangcare.network.RetrofitClient
import kotlinx.coroutines.launch

class AnalysisResultFragment : Fragment() {

    private lateinit var diagnosisService: DiagnosisService
    private var _binding: FragmentAnalysisResultBinding? = null  // ViewBinding 사용
    private val binding get() = _binding!!  // 안전하게 접근

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                openKakaoMapFragment() // 권한 승인 시 지도 프래그먼트로 이동
            } else {
                Toast.makeText(requireContext(), "GPS 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DiagnosisService 인스턴스 초기화
        diagnosisService = RetrofitClient.getDiagnosisServiceWithAuth(requireContext())

        val diagnosisId = arguments?.getLong("diagnosisId")
        val userId = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            .getLong("user_id", -1L)

        Log.d("AnalysisResultFragment", "userId: $userId, diagnosisId: $diagnosisId")

        diagnosisId?.let {
            getDiagnosisDetails(userId, it)
        }

        // 병원 이미지 클릭 시 KakaoMapFragment 열기
        binding.IVHospital.setOnClickListener {
            checkGPSPermission()
        }

        // 홈으로 돌아가기 버튼 클릭 이벤트
        binding.btnBackToHome.setOnClickListener {
            navigateToHomeFragment()
        }
    }

    private fun checkGPSPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            openKakaoMapFragment()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun openKakaoMapFragment() {
        val existingFragment = parentFragmentManager.findFragmentByTag("KakaoMapFragment")

        if (existingFragment == null) {
            // KakaoMapFragment를 직접 다이얼로그로 띄웁니다.
            val kakaoMapFragment = KakaoMapFragment()
            kakaoMapFragment.show(parentFragmentManager, "KakaoMapFragment")
        } else {
            Log.d("AnalysisResultFragment", "KakaoMapFragment already shown.")
        }
    }

    private fun getDiagnosisDetails(userId: Long, diagnosisId: Long) {
        lifecycleScope.launch {
            try {
                val response = diagnosisService.getDiagnosisDetail(userId, diagnosisId)

                if (response.isSuccessful) {
                    val diagnosisDetail = response.body()?.data
                    if (diagnosisDetail != null) {
                        updateUI(diagnosisDetail)
                    } else {
                        Toast.makeText(requireContext(), "진단 상세 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "진단 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "서버 연결 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("AnalysisResultFragment", "Error fetching diagnosis details", e)
            }
        }
    }

    private fun updateUI(diagnosis: DiagnosisResponseDTO) {
        val excludedDiseases = listOf("상피성잔고리", "과다색소침착")

        val filteredDiseaseName = diagnosis.diseases
            .split("/")
            .map { disease ->
                if (disease in excludedDiseases) {
                    disease.chunked(3).joinToString("\n")
                } else {
                    disease
                }
            }
            .joinToString("\n")


        binding.TVdiseaseName.text = filteredDiseaseName
        binding.TVdiseaseContent.text = diagnosis.diseasesContent
        binding.TVdiseaseTreatment.text = diagnosis.treatment
        val stageKorean = when (diagnosis.stage) {
            "Early" -> "초기"
            "Middle" -> "중기"
            "Late" -> "말기"
            else -> "정보 없음"
        }

        binding.TVdiseaseProgress.text = stageKorean
        binding.TVriskScore.text = diagnosis.riskScore.toString()
        binding.TVmedications.text = diagnosis.medications.joinToString(", ")

        // 이미지 설정
        if (!diagnosis.diseaseImg.isNullOrEmpty()) {
            try {
                val decodedImage = Base64.decode(diagnosis.diseaseImg, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
                binding.IVdiseaseImg.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Log.e("AnalysisResultFragment", "Error decoding image: ${e.message}")
                binding.IVdiseaseImg.setImageResource(R.drawable.logo)
            }
        } else {
            binding.IVdiseaseImg.setImageResource(R.drawable.logo)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnalysisResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 홈으로 이동하는 함수
    private fun navigateToHomeFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, HomeFragment())
            .addToBackStack(null)
            .commit()
    }
}
