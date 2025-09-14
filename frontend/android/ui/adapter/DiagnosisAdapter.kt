package com.project.meongnyangcare.ui.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.DiagnosisDTO
import java.lang.Exception

class DiagnosisAdapter(
    private var diagnosisList: List<DiagnosisDTO>, // DiagnosisDTO 리스트
    private val onItemClick: (DiagnosisDTO) -> Unit
) : RecyclerView.Adapter<DiagnosisAdapter.DiagnosisViewHolder>() {

    inner class DiagnosisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgPet: ImageView = itemView.findViewById(R.id.imgPet)
        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvDisease: TextView = itemView.findViewById(R.id.tvDisease)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvRisk: TextView = itemView.findViewById(R.id.tvRisk)
        private val tvId: TextView = itemView.findViewById(R.id.tvId) // tvId 추가

        fun bind(diagnosis: DiagnosisDTO, position: Int) {
            // tvId에 position 값 설정 (1부터 시작하는 번호)
            tvId.text = (position + 1).toString()

            // 이미지가 Base64로 인코딩된 경우 디코딩하여 표시
            if (!diagnosis.diagnosisImg.isNullOrEmpty()) {
                try {
                    val decodedImage = Base64.decode(diagnosis.diagnosisImg, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
                    imgPet.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    imgPet.setImageResource(R.drawable.clean_dog)  // 기본 이미지
                }
            } else {
                imgPet.setImageResource(R.drawable.clean_dog)  // 기본 이미지
            }

            tvPetName.text = diagnosis.petName

            // "상피성잔고리", "과다색소침착" 제외한 질환 이름만 표시
            val excludedDiseases = listOf("상피성잔고리", "과다색소침착")

            val filteredDiseaseName = diagnosis.disease
                .split("/")  // 질환 이름이 "/"로 구분되어 있다고 가정
                .filter { it !in excludedDiseases }  // 제외할 질환들 필터링
                .joinToString("\n")  // 새로운 줄로 구분하여 연결

            tvDisease.text = filteredDiseaseName  // 필터링된 질환 이름 표시

            // 진행 상태 변환
            val stageKorean = when (diagnosis.stage) {
                "Early" -> "초기"
                "Middle" -> "중기"
                "Late" -> "말기"
                else -> "정보 없음" // stage가 예상 값이 아닐 경우 처리
            }
            tvStatus.text = stageKorean
            tvRisk.text = diagnosis.riskScore.toString()

            itemView.setOnClickListener {
                onItemClick(diagnosis)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diagnosis, parent, false)
        return DiagnosisViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        holder.bind(diagnosisList[position], position)
    }

    override fun getItemCount(): Int = diagnosisList.size

    fun updateDiagnosisList(newList: List<DiagnosisDTO>) {
        diagnosisList = newList
        notifyDataSetChanged()
    }
}
