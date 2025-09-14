package com.project.meongnyangcare.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.widget.Toast
import com.project.meongnyangcare.model.Pet
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object PetUtils {
    fun loadPetList(
        context: Context,
        onSuccess: (List<Pet>) -> Unit,
        onError: (() -> Unit)? = null
    ) {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)

        if (userId == -1L) {
            Toast.makeText(context, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            onError?.invoke()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getPetServiceWithAuth(context).getPets(userId)
                if (response.isSuccessful) {
                    val petListDto = response.body()?.data?.petLists ?: emptyList()

                    val petList = petListDto.map { dto ->
                        val petImgByteArray = Base64.decode(dto.petImg, Base64.DEFAULT)

                        Pet(
                            id = dto.petId.toString(),
                            petimageUrl = BitmapDrawable(context.resources, BitmapFactory.decodeByteArray(petImgByteArray, 0, petImgByteArray.size)),
                            name = dto.petName,
                            speciesimageUrl = if (dto.species == "강아지") R.drawable.clean_dog else R.drawable.clean_cat,
                            age = dto.age.toString(),
                            genderimageUrl = if (dto.gender == "수컷") R.drawable.male else R.drawable.female,
                        )
                    }

                    withContext(Dispatchers.Main) {
                        onSuccess(petList)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "반려동물 목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                        onError?.invoke()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "서버 연결 오류", Toast.LENGTH_SHORT).show()
                    onError?.invoke()
                }
            }
        }
    }
}
