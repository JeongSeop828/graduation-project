package com.project.meongnyangcare.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.project.meongnyangcare.R
import com.project.meongnyangcare.ui.activity.MainActivity

class AnalysisNewPetFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setToolbarTitle("새 반려동물 진단")

        // 반려견 선택 버튼 클릭 시
        view.findViewById<ImageView>(R.id.btnDog).setOnClickListener {
            openUploadPetImageFragment("dog")
        }

        // 반려묘 선택 버튼 클릭 시
        view.findViewById<ImageView>(R.id.btnCat).setOnClickListener {
            openUploadPetImageFragment("cat")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analysis_new_pet, container, false)
    }

    // 반려동물 타입을 전달하며 UploadPetImageFragment로 이동
    private fun openUploadPetImageFragment(petType: String) {
        // UploadPetImageFragment로 이동
        val uploadFragment = UploadPetImageFragment()

        // 반려동물 타입 전달 (Bundle을 사용하여 전달)
        val bundle = Bundle()
        bundle.putString("petType", petType)
        bundle.putString("petId", null)
        uploadFragment.arguments = bundle

        // FragmentTransaction을 통해 UploadPetImageFragment로 교체
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, uploadFragment)  // 'frame_layout'은 프래그먼트를 담을 컨테이너
            .addToBackStack(null)  // 뒤로 가기 스택에 추가
            .commit()  // 트랜잭션 커밋
    }
}
