package com.project.meongnyangcare.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.PetRegisterDTO
import com.project.meongnyangcare.model.PetDataResponseDTO
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.ui.activity.MainActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class UpdatePetFragment : Fragment() {

    private lateinit var ivAddPetImage: ImageView
    private lateinit var editPetName: EditText
    private lateinit var editPetAge: EditText
    private lateinit var editPetBreed: EditText
    private lateinit var errorPetName: TextView
    private lateinit var errorPetAge: TextView
    private lateinit var errorPetBreed: TextView
    private lateinit var radioPetType: RadioGroup
    private lateinit var radioPetGender: RadioGroup

    private var petId: Long = -1L
    private var userId: Long = -1L
    private var imageUri: Uri? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_GALLERY_PICK = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_update_pet, container, false)

        ivAddPetImage = view.findViewById(R.id.ivAddPet)
        editPetName = view.findViewById(R.id.editPetName)
        editPetAge = view.findViewById(R.id.editPetAge)
        editPetBreed = view.findViewById(R.id.editPetBreed)
        errorPetName = view.findViewById(R.id.errorPetName)
        errorPetAge = view.findViewById(R.id.errorPetAge)
        errorPetBreed = view.findViewById(R.id.errorPetBreed)
        radioPetType = view.findViewById(R.id.radioPetType)
        radioPetGender = view.findViewById(R.id.radioPetGender)

        ivAddPetImage.setOnClickListener { showImagePickerDialog() }
        view.findViewById<View>(R.id.btnSaveUser).setOnClickListener { savePetDetails() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle("반려동물 수정")
        loadPetDetails()
    }

    private fun loadPetDetails() {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        userId = sharedPreferences.getLong("user_id", -1L)
        petId = arguments?.getLong("petId", -1L) ?: -1L

        if (userId != -1L && petId != -1L) {
            lifecycleScope.launch {
                val service = RetrofitClient.getPetServiceWithAuth(requireContext())
                try {
                    val response = service.getPetDetail(userId, petId)
                    if (response.isSuccessful) {
                        val petData = response.body()?.data
                        petData?.let { updateUI(it) }
                    } else {
                        showToast("반려동물 정보를 불러오는 데 실패했습니다.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast("서버 오류 발생")
                }
            }
        } else {
            showToast("반려동물 정보가 없습니다.")
        }
    }

    private fun updateUI(petData: PetDataResponseDTO) {
        editPetName.setText(petData.petName)
        editPetAge.setText(petData.age.toString())
        editPetBreed.setText(petData.breed)

        // Pet Type
        when (petData.species.uppercase()) {
            "강아지" -> view?.findViewById<RadioButton>(R.id.radioDog)?.isChecked = true
            "고양이" -> view?.findViewById<RadioButton>(R.id.radioCat)?.isChecked = true
        }

        // Pet Gender
        when (petData.gender.uppercase()) {
            "수컷" -> view?.findViewById<RadioButton>(R.id.radioMale)?.isChecked = true
            "암컷" -> view?.findViewById<RadioButton>(R.id.radioFemale)?.isChecked = true
        }

        if (petData.petImg.isNotEmpty()) {
            val decodedImage = Base64.decode(petData.petImg, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
            ivAddPetImage.setImageBitmap(bitmap)
        }
    }

    private fun savePetDetails() {
        if (!validateInputs()) return

        val petName = editPetName.text.toString()
        val petAge = editPetAge.text.toString().toInt()
        val petBreed = editPetBreed.text.toString()
        val petType = when (radioPetType.checkedRadioButtonId) {
            R.id.radioDog -> "강아지"
            R.id.radioCat -> "고양이"
            else -> ""
        }
        val petGender = when (radioPetGender.checkedRadioButtonId) {
            R.id.radioMale -> "수컷"
            R.id.radioFemale -> "암컷"
            else -> ""
        }

        val petDto = PetRegisterDTO(
            petName = petName,
            age = petAge,
            breed = petBreed,
            species = petType,
            weight = 0.0,
            gender = petGender
        )

        lifecycleScope.launch {
            val service = RetrofitClient.getPetServiceWithAuth(requireContext())
            val gson = Gson()
            val petJson = gson.toJson(petDto)
            val petRequestBody = petJson.toRequestBody("application/json".toMediaTypeOrNull())

            try {
                val imagePart = getImagePart() ?: run {
                    showToast("이미지가 필요합니다.")
                    return@launch
                }

                val response = service.updatePet(userId, petId, petRequestBody, imagePart)
                if (response.isSuccessful) {
                    showSuccessDialog()
                } else {
                    showToast("반려동물 수정 실패")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("서버 오류 발생")
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (editPetName.text.isEmpty()) {
            errorPetName.visibility = View.VISIBLE
            isValid = false
        } else {
            errorPetName.visibility = View.GONE
        }

        if (editPetAge.text.isEmpty()) {
            errorPetAge.visibility = View.VISIBLE
            isValid = false
        } else {
            errorPetAge.visibility = View.GONE
        }

        if (editPetBreed.text.isEmpty()) {
            errorPetBreed.visibility = View.VISIBLE
            isValid = false
        } else {
            errorPetBreed.visibility = View.GONE
        }

        return isValid
    }

    private fun getImagePart(): MultipartBody.Part? {
        val drawable = ivAddPetImage.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val file = bitmapToFile(bitmap)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData("image", file.name, requestFile)
        }
        return null
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("저장되었습니다.")
            .setPositiveButton("확인") { _, _ ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, MyPageFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .show()
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("사진 촬영", "갤러리에서 선택")
        AlertDialog.Builder(requireContext())
            .setTitle("이미지 업로드 방법 선택")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun openGallery() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent, REQUEST_GALLERY_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    ivAddPetImage.setImageBitmap(imageBitmap)
                    imageUri = null
                }
                REQUEST_GALLERY_PICK -> {
                    imageUri = data?.data
                    ivAddPetImage.setImageURI(imageUri)
                }
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val file = File(requireContext().cacheDir, "temp_image.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
