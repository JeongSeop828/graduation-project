package com.project.meongnyangcare.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.commit
import com.project.meongnyangcare.R
import com.project.meongnyangcare.ui.activity.MainActivity

class AnalysisFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setToolbarTitle("피부질환 진단")

        val btnMyPetAnalysis = view.findViewById<Button>(R.id.btn_my_pet_analysis)
        val btnNewPetAnalysis = view.findViewById<Button>(R.id.btn_new_pet_analysis)

        btnMyPetAnalysis.setOnClickListener {
            replaceFragment(AnalysisMyPetSelectFragment())
        }

        btnNewPetAnalysis.setOnClickListener {
            replaceFragment(AnalysisNewPetFragment())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analysis, container, false)
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.frame_layout, fragment)
            addToBackStack(null)
        }
    }
}
