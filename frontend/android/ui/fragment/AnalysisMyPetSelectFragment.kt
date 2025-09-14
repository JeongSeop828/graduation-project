package com.project.meongnyangcare.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.Pet
import com.project.meongnyangcare.ui.activity.MainActivity
import com.project.meongnyangcare.ui.adapter.PetAdapter
import com.project.meongnyangcare.utils.PetUtils

class AnalysisMyPetSelectFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PetAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바 제목 설정
        (activity as? MainActivity)?.setToolbarTitle("내 반려동물 진단")

        recyclerView = view.findViewById(R.id.recyclerViewPets)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PetAdapter(emptyList()) { pet -> openNextFragment(pet) }
        recyclerView.adapter = adapter

        loadPetList()
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analysis_my_pet_select, container, false)
    }

    private fun openNextFragment(pet: Pet) {
        val fragment = UploadPetImageFragment()
        val bundle = Bundle()

        bundle.putLong("petId", pet.id.toLong())
        bundle.putString("petName", pet.name)

        // speciesimageUrl 값에 따라 종류 문자열 설정
        val petType = when (pet.speciesimageUrl) {
            R.drawable.clean_dog -> "dog"
            R.drawable.clean_cat -> "cat"
            else -> "기타"
        }
        bundle.putString("petType", petType)

        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}
