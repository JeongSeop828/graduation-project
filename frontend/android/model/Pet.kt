package com.project.meongnyangcare.model

import android.graphics.drawable.BitmapDrawable
import java.io.Serializable

data class Pet(
    val id: String,
    val petimageUrl: BitmapDrawable,
    val name: String,      // 반려동물 이름
    val speciesimageUrl: Int,  // 반려동물 종류 (예: 강아지, 고양이)
    val age: String,         // 반려동물 나이
    val genderimageUrl: Int,    // 반려동물 성별 (수컷/암컷)
) : Serializable
