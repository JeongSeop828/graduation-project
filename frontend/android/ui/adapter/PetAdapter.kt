package com.project.meongnyangcare.ui.adapter

import android.util.Log
import com.project.meongnyangcare.model.Pet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.meongnyangcare.R

class PetAdapter(
    private var petList: List<Pair<Pet, Int>>,  // Pet과 viewType을 함께 저장
    private val onItemClick: (Pet) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // ViewType Constants
    companion object {
        const val TYPE_ITEM_PET_HOME = 2
        const val TYPE_ITEM_PET = 1
    }

    // getItemViewType을 사용해서 레이아웃 구분
    override fun getItemViewType(position: Int): Int {
        return petList[position].second  // viewType을 반환
    }

    // 기존의 PetViewHolder와 새로운 PetImageViewHolder를 각각 정의
    inner class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textId: TextView = itemView.findViewById(R.id.tvId)
        val petImage: ImageView = itemView.findViewById(R.id.imgPet)
        val petName: TextView = itemView.findViewById(R.id.tvPetNameValue)
        val petSpecies: ImageView = itemView.findViewById(R.id.tvPetSpeciesValue)
        val petAge: TextView = itemView.findViewById(R.id.tvPetAgeValue)
        val petGender: ImageView = itemView.findViewById(R.id.tvPetGenderValue)

        fun bind(pet: Pet, position: Int) {
            Log.d("PetAdapter", "Pet image URL: ${pet.petimageUrl}")
            Log.d("PetAdapter", "Species image URL: ${pet.speciesimageUrl}")
            Log.d("PetAdapter", "Gender image URL: ${pet.genderimageUrl}")

            textId.text = (position + 1).toString()

            Glide.with(itemView.context)
                .load(pet.petimageUrl)
                .override(1000, 1000)
                .into(petImage)
            petName.text = pet.name
            Glide.with(itemView.context)
                .load(pet.speciesimageUrl)
                .into(petSpecies)
            petAge.text = "${pet.age}살"
            Glide.with(itemView.context)
                .load(pet.genderimageUrl)
                .into(petGender)

            itemView.setOnClickListener { onItemClick(pet) }
        }
    }

    // ImageOnly의 경우 사용할 ViewHolder
    inner class PetImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val petImage: ImageView = itemView.findViewById(R.id.imgPet)

        fun bind(pet: Pet) {
            Glide.with(itemView.context)
                .load(pet.petimageUrl)
                .override(1000, 1000)
                .into(petImage)
            itemView.setOnClickListener { onItemClick(pet) }
        }
    }

    // onCreateViewHolder에서 조건에 맞는 레이아웃을 선택
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM_PET_HOME -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pet_home, parent, false)
                PetImageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pet, parent, false)
                PetViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PetViewHolder -> holder.bind(petList[position].first, position)
            is PetImageViewHolder -> holder.bind(petList[position].first)
        }
    }

    override fun getItemCount(): Int {
        return petList.size
    }

    fun updatePetList(newList: List<Pair<Pet, Int>>) {
        petList = newList
        notifyDataSetChanged()
    }
}
