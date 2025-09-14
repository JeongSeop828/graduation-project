package com.project.meongnyangcare.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.PetRegisterDTO
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.ui.fragment.MyPageFragment
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AddPetFragment : Fragment() {

    private lateinit var ivAddPetImage: ImageView
    private lateinit var editPetName: EditText
    private lateinit var editPetAge: EditText
    private lateinit var editPetBreed: EditText
    private lateinit var errorPetName: TextView
    private lateinit var errorPetAge: TextView
    private lateinit var errorPetBreed: TextView
    private lateinit var radioPetType: RadioGroup
    private lateinit var radioPetGender: RadioGroup

    private var imageUri: Uri? = null
    private var userId: Long? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_GALLERY_PICK = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_pet, container, false)

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

        view.findViewById<View>(R.id.btnSaveUser).setOnClickListener { loadUserIdFromSharedPreferencesAndSavePet() }

        return view
    }

    private fun loadUserIdFromSharedPreferencesAndSavePet() {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        val id = sharedPreferences.getLong("user_id", -1L)

        if (token.isNullOrEmpty() || id == -1L) {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        userId = id
        Log.d("AddPetFragment", "Loaded token: $token, userId: $userId")
        savePetDetails()
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("사진 촬영", "갤러리에서 선택", "기본 이미지 사용")
        AlertDialog.Builder(requireContext())
            .setTitle("이미지 업로드 방법 선택")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                    2 -> {
                        imageUri = null
                        ivAddPetImage.setImageResource(R.drawable.white_logo)
                    }
                }
            }.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    imageUri = saveBitmapToFile(bitmap)
                    ivAddPetImage.setImageBitmap(bitmap)
                    Log.d("AddPetFragment", "촬영한 이미지 URI: $imageUri")
                }
                REQUEST_GALLERY_PICK -> {
                    imageUri = data?.data
                    ivAddPetImage.setImageURI(imageUri)
                    Log.d("AddPetFragment", "선택한 이미지 URI: $imageUri")
                }
            }
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri {
        val file = File(requireContext().cacheDir, "pet_image.jpg")
        file.createNewFile()
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return Uri.fromFile(file)
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "selected_pet_image.jpg")
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun savePetDetails() {
        if (userId == null || userId == -1L) {
            Toast.makeText(requireContext(), "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val name = editPetName.text.toString()
        val ageText = editPetAge.text.toString()
        val breed = editPetBreed.text.toString()
        val weightText = 0.0
        val gender = when (radioPetGender.checkedRadioButtonId) {
            R.id.radioFemale -> "암컷"
            R.id.radioMale -> "수컷"
            else -> ""
        }
        val species = when (radioPetType.checkedRadioButtonId) {
            R.id.radioDog -> "강아지"
            R.id.radioCat -> "고양이"
            else -> ""
        }

        Log.d("AddPetFragment", "사용자 ID: $userId")
        Log.d("AddPetFragment", "이름: $name")
        Log.d("AddPetFragment", "나이: $ageText")
        Log.d("AddPetFragment", "종: $species")
        Log.d("AddPetFragment", "품종: $breed")
        Log.d("AddPetFragment", "몸무게: $weightText")
        Log.d("AddPetFragment", "성별: $gender")
        Log.d("AddPetFragment", "이미지 URI: $imageUri")

        if (name.isEmpty() || ageText.isEmpty() || breed.isEmpty() || gender.isEmpty() || species.isEmpty()) {
            Toast.makeText(requireContext(), "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val dto = PetRegisterDTO(name, species, breed, ageText.toInt(), weightText, gender)
        val json = Gson().toJson(dto)
        val petInfoPart = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        val imagePart = if (imageUri != null) {
            val file = getFileFromUri(imageUri!!)
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        } else {
            // 기본 이미지 사용 시 clean_logo를 Bitmap으로 변환하고 MultipartBody.Part로 생성
            val drawable = resources.getDrawable(R.drawable.white_logo, null)
            val bitmap = (drawable as BitmapDrawable).bitmap

            val file = File(requireContext().cacheDir, "default_image.jpg")
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }

            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        val service = RetrofitClient.getPetServiceWithAuth(requireContext())

        lifecycleScope.launch {
            try {
                val response: Response<*> = service.registerPet(userId!!, petInfoPart, imagePart)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "반려동물이 등록되었습니다!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, MyPageFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(requireContext(), "등록 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "에러 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddPetFragment", "등록 중 오류 발생", e)
            }
        }
    }
}
