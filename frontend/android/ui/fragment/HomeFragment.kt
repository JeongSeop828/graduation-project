package com.project.meongnyangcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.project.meongnyangcare.model.Pet
import com.project.meongnyangcare.ui.AddPetFragment
import com.project.meongnyangcare.ui.activity.MainActivity
import com.project.meongnyangcare.ui.adapter.PetAdapter
import com.project.meongnyangcare.ui.fragment.AnalysisFragment
import com.project.meongnyangcare.ui.fragment.PetDetailFragment
import com.project.meongnyangcare.utils.PetUtils

class HomeFragment : Fragment() {

    private lateinit var petAdapter: PetAdapter
    private lateinit var recyclerViewPets: RecyclerView
    private lateinit var linearLayoutAiDiagnosis: LinearLayout
    private lateinit var linearLayoutNoPets: LinearLayout // 반려동물 없음 문구와 버튼
    private lateinit var petList: List<Pet> // 반려동물 리스트

    private val BACK_PRESSED_DURATION = 2000L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setToolbarTitle("홈")

        // RecyclerView 초기화
        recyclerViewPets = view.findViewById(R.id.recyclerViewPets)
        linearLayoutAiDiagnosis = view.findViewById(R.id.linearLayoutAiDiagnosis)
        linearLayoutNoPets = view.findViewById(R.id.linearLayoutNoPets) // 반려동물 없음 문구

        petAdapter = PetAdapter(emptyList()) { pet ->
            // 반려동물 리스트 아이템 클릭 시의 처리
            openPetDetailFragment(pet)
        }
        recyclerViewPets.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewPets.adapter = petAdapter

        // 반려동물 리스트 가져오기
        loadPets()

        // AI 진단 LinearLayout 클릭 이벤트 처리
        linearLayoutAiDiagnosis.setOnClickListener {
            // AnalysisFragment로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, AnalysisFragment())
                .addToBackStack(null)
                .commit()
        }

        // 반려동물 등록 버튼 클릭 이벤트 처리
        view.findViewById<Button>(R.id.buttonAddPet).setOnClickListener {
            // AddPetFragment로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, AddPetFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadPets() {
        PetUtils.loadPetList(requireContext(), onSuccess = { petList ->
            this.petList = petList

            if (petList.isEmpty()) {
                // 반려동물 리스트가 없으면 RecyclerView를 숨기고, 버튼만 보이도록 설정
                recyclerViewPets.visibility = View.GONE
                linearLayoutNoPets.visibility = View.VISIBLE
            } else {
                // 반려동물 리스트가 있으면 RecyclerView에 표시
                recyclerViewPets.visibility = View.VISIBLE
                linearLayoutNoPets.visibility = View.GONE

                // 반려동물 리스트를 변환해서 viewType을 1 또는 2로 넘겨줌
                val updatedPetList = petList.map { pet ->
                    Pair(pet, 2)  // homeFragment에서 2를 넘겨주는 예시
                }

                petAdapter.updatePetList(updatedPetList) // (Pet, viewType) 형태로 넘겨줌
            }
        }, onError = {
            // 에러 처리
            Snackbar.make(requireView(), "반려동물 목록을 가져오는 데 실패했습니다.", Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun openPetDetailFragment(pet: Pet) {
        val fragment = PetDetailFragment()
        val bundle = Bundle()
        bundle.putLong("petId", pet.id.toLong()) // petId를 프래그먼트에 전달
        fragment.arguments = bundle

        // PetDetailFragment로 전환
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}
