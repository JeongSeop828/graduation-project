package com.project.meongnyangcare.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.PetDataResponseDTO
import com.project.meongnyangcare.network.RetrofitClient
import kotlinx.coroutines.launch

class PetDetailFragment : Fragment() {

    private lateinit var btnUpdatePet: Button
    private lateinit var btnDeletePet: Button
    private lateinit var petName: TextView
    private lateinit var petAge: TextView
    private lateinit var petBreed: TextView
    private lateinit var petImage: ImageView


    private lateinit var radioPetType: RadioGroup
    private lateinit var radioPetGender: RadioGroup
    private lateinit var radioDog: RadioButton
    private lateinit var radioCat: RadioButton
    private lateinit var radioMale: RadioButton
    private lateinit var radioFemale: RadioButton

    private var petId: Long = -1L
    private var userId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pet_detail, container, false)

        // UI 요소 연결
        petName = view.findViewById(R.id.editPetName)
        petAge = view.findViewById(R.id.editPetAge)
        petBreed = view.findViewById(R.id.editPetBreed)
        petImage = view.findViewById(R.id.ivPetImage)
        radioPetType = view.findViewById(R.id.radioPetType)
        radioPetGender = view.findViewById(R.id.radioPetGender)
        radioDog = view.findViewById(R.id.radioDog)
        radioCat = view.findViewById(R.id.radioCat)
        radioMale = view.findViewById(R.id.radioMale)
        radioFemale = view.findViewById(R.id.radioFemale)
        btnUpdatePet = view.findViewById(R.id.btnUpdatePet)
        btnDeletePet = view.findViewById(R.id.btnDeletePet)

        // 버튼 클릭 리스너
        btnUpdatePet.setOnClickListener {
            val fragment = UpdatePetFragment()
            replaceFragment(fragment, petId)  // petId를 함께 전달
        }

        btnDeletePet.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        loadPetDetails()

        return view
    }

    private fun loadPetDetails() {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        userId = sharedPreferences.getLong("user_id", -1L)
        petId = arguments?.getLong("petId", -1L) ?: -1L

        Log.d("PetDetailFragment", "userId: $userId, petId: $petId")

        if (userId != -1L && petId != -1L) {
            lifecycleScope.launch {
                val service = RetrofitClient.getPetServiceWithAuth(requireContext())

                try {
                    val response = service.getPetDetail(userId, petId)
                    if (response.isSuccessful) {
                        val petData = response.body()?.data
                        petData?.let { updateUI(it) }
                    } else {
                        Toast.makeText(requireContext(), "반려동물 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(requireContext(), "반려동물 정보가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(petData: PetDataResponseDTO) {
        petName.text = petData.petName
        petAge.text = "${petData.age}살"
        petBreed.text = petData.breed

        // 라디오버튼 선택 처리
        when (petData.species.uppercase()) {
            "강아지" -> radioPetType.check(radioDog.id)
            "고양이" -> radioPetType.check(radioCat.id)
        }

        when (petData.gender.uppercase()) {
            "수컷" -> radioPetGender.check(radioMale.id)
            "암컷" -> radioPetGender.check(radioFemale.id)
        }

        // 라디오버튼 비활성화
        setRadioGroupDisabled(radioPetType)
        setRadioGroupDisabled(radioPetGender)

        // 이미지 디코딩 및 표시
        if (petData.petImg.isNotEmpty()) {
            val decodedImage = Base64.decode(petData.petImg, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
            petImage.setImageBitmap(bitmap)
        }
    }

    private fun setRadioGroupDisabled(radioGroup: RadioGroup) {
        for (i in 0 until radioGroup.childCount) {
            val radioButton = radioGroup.getChildAt(i)
            radioButton.isEnabled = false
        }
    }

    // Fragment를 교체하는 함수
    private fun replaceFragment(fragment: Fragment, petId: Long) {
        val bundle = Bundle()
        bundle.putLong("petId", petId)  // 현재 반려동물 ID 전달
        fragment.arguments = bundle

        parentFragmentManager.commit {
            replace(R.id.frame_layout, fragment)
            addToBackStack(null)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("반려동물 삭제")
            .setMessage("정말로 삭제하시겠습니까?")
            .setNegativeButton("취소", null)
            .setPositiveButton("삭제") { _, _ -> deletePet() }
            .show()
    }

    private fun deletePet() {
        lifecycleScope.launch {
            try {
                val service = RetrofitClient.getPetServiceWithAuth(requireContext())
                val response = service.deletePet(petId)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "반려동물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()

                    // 삭제 후 MyPageFragment로 이동
                    navigateToMyPageFragment()
                } else {
                    Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun navigateToMyPageFragment() {
        // MyPageFragment로 이동하는 코드
        val fragment = MyPageFragment()
        parentFragmentManager.commit {
            replace(R.id.frame_layout, fragment)
            addToBackStack(null) // 백스택에 추가하여 뒤로가기 가능하게 함
        }
    }
}
