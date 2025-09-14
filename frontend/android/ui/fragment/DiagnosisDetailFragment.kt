package com.project.meongnyangcare.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.project.meongnyangcare.R
import com.project.meongnyangcare.databinding.FragmentDiagnosisDetailBinding
import com.project.meongnyangcare.model.DiagnosisResponseDTO
import com.project.meongnyangcare.network.DiagnosisService
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.ui.activity.MainActivity
import kotlinx.coroutines.launch

class DiagnosisDetailFragment : Fragment() {

    private lateinit var diagnosisService: DiagnosisService
    private var _binding: FragmentDiagnosisDetailBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                checkGPSPermission() // 권한 승인 시 GPS가 켜져있는지 확인
            } else {
                Toast.makeText(requireContext(), "GPS 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setToolbarTitle("진단 상세 정보")

        val diagnosisId = arguments?.getLong("diagnosisId")
        val userId = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            .getLong("user_id", -1L)

        diagnosisId?.let {
            getDiagnosisDetails(userId, it)
        }

        // 이미지 클릭 시 KakaoMapFragment로 이동
        binding.IVHospital.setOnClickListener {
            checkGPSPermission()  // GPS 권한 확인
        }

        binding.btnBackToHome.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


    }

    private fun checkGPSPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // 위치 권한이 이미 허용되었으면, GPS가 활성화되어 있는지 확인
            if (isLocationEnabled()) {
                openKakaoMapFragment() // GPS가 켜져 있으면 KakaoMapFragment로 이동
            } else {
                // GPS가 꺼져있으면 설정 화면으로 이동
                Toast.makeText(requireContext(), "GPS가 꺼져 있습니다. GPS를 켜주세요.", Toast.LENGTH_SHORT).show()
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent) // GPS 설정 화면으로 이동
            }
        } else {
            // 위치 권한이 없으면 권한 요청
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // GPS가 활성화되어 있는지 확인하는 함수
    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun openKakaoMapFragment() {
        val existingFragment = parentFragmentManager.findFragmentByTag("KakaoMapFragment")

        if (existingFragment == null) {
            // KakaoMapFragment를 직접 다이얼로그로 띄웁니다.
            val kakaoMapFragment = KakaoMapFragment()
            kakaoMapFragment.show(parentFragmentManager, "KakaoMapFragment")
        } else {
            Log.d("DiagnosisDetailFragment", "KakaoMapFragment already shown.")
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
                Log.e("DiagnosisDetailFragment", "Error fetching diagnosis details", e)
            }
        }
    }

    private fun updateUI(diagnosis: DiagnosisResponseDTO) {
        binding.TVdiseaseContent.text = diagnosis.diseasesContent
        binding.TVdiseaseTreatment.text = diagnosis.treatment

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
            else -> "정보 없음" // stage가 예상 값이 아닐 경우 처리
        }

        binding.TVdiseaseProgress.text = stageKorean
        binding.TVriskScore.text = diagnosis.riskScore.toString()
        binding.TVmedications.text = diagnosis.medications.joinToString(", ")

        // 이미지 설정
        if (!diagnosis.diseaseImg.isNullOrEmpty()) {
            val decodedImage = Base64.decode(diagnosis.diseaseImg, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
            binding.IVdiseaseImg.setImageBitmap(bitmap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiagnosisDetailBinding.inflate(inflater, container, false)
        diagnosisService = RetrofitClient.getDiagnosisServiceWithAuth(requireContext())
        return binding.root
    }


    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.frame_layout, fragment)
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
