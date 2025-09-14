package com.project.meongnyangcare.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.project.meongnyangcare.R
import com.project.meongnyangcare.model.DiagnosisRequestDTO
import com.project.meongnyangcare.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class UploadPetImageFragment : Fragment() {

    private lateinit var ivUploadImage: ImageView
    private var selectedImageFile: File? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_GALLERY_PICK = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_upload_pet_image, container, false)

        ivUploadImage = view.findViewById(R.id.ivUploadImage)
        val btnAnalyze = view.findViewById<Button>(R.id.btnAnalyze)

        val petId = arguments?.getLong("petId")
        val petName = arguments?.getString("petName")
        val petType = arguments?.getString("petType")
        val sharedPreferences =
            requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)

        Log.d("UploadPetImageFragment", "petId: $petId")
        Log.d("UploadPetImageFragment", "petName: $petName")
        Log.d("UploadPetImageFragment", "petType: $petType")
        Log.d("UploadPetImageFragment", "userId: $userId")

        ivUploadImage.setOnClickListener {
            showImagePickerDialog()
        }

        btnAnalyze.setOnClickListener {
            if (selectedImageFile != null) {
                analyzeImage(userId, petId, petName, petType)
            } else {
                Toast.makeText(requireContext(), "이미지를 먼저 업로드해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("사진 촬영", "갤러리에서 선택", "취소")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("이미지 업로드 방법 선택")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun openGallery() {
        val pickPhotoIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent, REQUEST_GALLERY_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    ivUploadImage.setImageBitmap(imageBitmap)
                    selectedImageFile = bitmapToFile(imageBitmap, requireContext())
                }

                REQUEST_GALLERY_PICK -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        ivUploadImage.setImageURI(it)
                        selectedImageFile = uriToFile(it, requireContext())
                    }
                }
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        val file = File(context.cacheDir, "captured_image.jpg")
        file.createNewFile()

        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()

        return file
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "selected_image.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }

    private fun analyzeImage(userId: Long?, petId: Long?, petName: String?, petType: String?) {
        val dialog = AlertDialog.Builder(requireContext())
            .setView(R.layout.activity_dialong_analysis_progress)
            .setCancelable(false)
            .create()
        dialog.show()

        val imageRequestBody = selectedImageFile!!
            .asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            "image",
            selectedImageFile!!.name,
            imageRequestBody
        )

        // 준비된 DiagnosisRequestDTO 생성
        val diagnosisRequestDTO = DiagnosisRequestDTO(
            userId = userId ?: -1L,  // userId를 Long으로 처리
            petId = petId,
            petName = petName,
            species = petType ?: ""  // petType을 species로 설정
        )

        val gson = Gson()
        val jsonRequestBody = gson.toJson(diagnosisRequestDTO)
            .toRequestBody("application/json".toMediaTypeOrNull())

// 변경된 Retrofit 인터페이스: @Part("diagnosisRequest") RequestBody 로 받도록
        val diagnosisService = RetrofitClient.getDiagnosisServiceWithAuth(requireContext())

        lifecycleScope.launch {
            try {
                val response = diagnosisService.analyzeDiagnosis(jsonRequestBody, imagePart)
                dialog.dismiss()

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!.data
                    result?.let {
                        val resultFragment = AnalysisResultFragment().apply {
                            arguments = Bundle().apply {
                                putLong("diagnosisId", it.diagnosisId)
                            }
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, resultFragment)
                            .addToBackStack(null)
                            .commit()
                    } ?: run {
                        Toast.makeText(requireContext(), "진단 결과를 불러올 수 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(requireContext(), "진단 실패: 서버 오류", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Diagnosis", "오류 발생", e)
                dialog.dismiss()
                Toast.makeText(requireContext(), "진단 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
