package com.project.meongnyangcare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PetList(
    val petId: Long,
    val petName: String,
    val species: String,
    val age: Int,
    val gender: String,
    val petImg: String // Base64 인코딩된 문자열
) : Parcelable