package com.project.meongnyangcare.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.fragment.app.DialogFragment
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.project.meongnyangcare.R
import com.project.meongnyangcare.network.RetrofitClient
import com.project.meongnyangcare.network.dto.HospitalDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KakaoMapFragment : DialogFragment() {

    private var mapView: MapView? = null
    private lateinit var locationManager: LocationManager

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_kakao_map, container, false)
        mapView = view.findViewById(R.id.mapView)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val closeButton: ImageButton = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener { dismiss() }

        mapView?.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 라이프사이클 종료 시 처리할 로직
            }

            override fun onMapError(e: Exception) {
                // 지도 초기화 또는 에러 처리 시 처리할 로직
                e.printStackTrace()
            }
        }, object : KakaoMapReadyCallback() {
            @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION])
            override fun onMapReady(kakaoMap: KakaoMap) {
                getCurrentLocationAndFetchHospitals(kakaoMap)
            }
        })

        return view
    }
    // 모바일용
//    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//    private fun getCurrentLocationAndFetchHospitals(kakaoMap: KakaoMap) {
//        try {
//            val locationListener = object : LocationListener {
//                override fun onLocationChanged(location: Location) {
//                    Log.d("Location", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
//                    fetchNearbyHospitals(location.latitude, location.longitude, kakaoMap)
//
//                    val currentLocation = LatLng.from(location.latitude, location.longitude)
//                    val cameraUpdate = CameraUpdateFactory.newCenterPosition(currentLocation)
//                    kakaoMap.moveCamera(cameraUpdate)
//
//                    // 위치를 한 번 받은 후에는 두 프로바이더 모두 위치 업데이트 중지
//                    locationManager.removeUpdates(this)
//                }
//
//                override fun onProviderEnabled(provider: String) {}
//                override fun onProviderDisabled(provider: String) {}
//                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
//            }
//
//            // GPS와 네트워크 프로바이더에 모두 요청
//            locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                10000L,
//                10f,
//                locationListener
//            )
//            locationManager.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER,
//                10000L,
//                10f,
//                locationListener
//            )
//
//            // 혹시 즉시 위치가 필요하면 마지막 알려진 위치 가져오기 (선택사항)
//            val gpsLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            val networkLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//
//            val lastLocation = when {
//                gpsLastLocation != null -> gpsLastLocation
//                networkLastLocation != null -> networkLastLocation
//                else -> null
//            }
//
//            lastLocation?.let { location ->
//                Log.d("Location", "Last known - Latitude: ${location.latitude}, Longitude: ${location.longitude}")
//                fetchNearbyHospitals(location.latitude, location.longitude, kakaoMap)
//
//                val currentLocation = LatLng.from(location.latitude, location.longitude)
//                val cameraUpdate = CameraUpdateFactory.newCenterPosition(currentLocation)
//                kakaoMap.moveCamera(cameraUpdate)
//            }
//
//        } catch (e: SecurityException) {
//            e.printStackTrace()
//            Toast.makeText(requireContext(), "위치 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
//        }
//    }

    // 안드로이드 스튜디오용
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun getCurrentLocationAndFetchHospitals(kakaoMap: KakaoMap) {
        try {
            // GPS_PROVIDER로부터 위치를 가져옵니다.
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000L,
                10f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        // 위치가 성공적으로 조회되었을 때, 병원 정보를 가져오기
                        Log.d("Location", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                        fetchNearbyHospitals(location.latitude, location.longitude, kakaoMap)

                        // 카메라를 현재 위치로 이동
                        val currentLocation = LatLng.from(location.latitude, location.longitude)
                        val cameraUpdate = CameraUpdateFactory.newCenterPosition(currentLocation)
                        kakaoMap.moveCamera(cameraUpdate)

                        // 첫 번째 위치를 받은 후 더 이상 업데이트를 받지 않도록 설정
                        locationManager.removeUpdates(this)
                    }

                    override fun onProviderEnabled(provider: String) {
                        // GPS가 활성화되었을 때의 처리
                    }

                    override fun onProviderDisabled(provider: String) {
                        // GPS가 비활성화되었을 때의 처리
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                        // 상태가 변경되었을 때 처리
                    }
                })
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "위치 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchNearbyHospitals(lat: Double, lon: Double, kakaoMap: KakaoMap) {
        val hospitalService = RetrofitClient.getHospitalServiceWithAuth(requireContext())

        hospitalService.getNearestHospitals(lat, lon, 5).enqueue(object : Callback<List<HospitalDto>> {
            override fun onResponse(call: Call<List<HospitalDto>>, response: Response<List<HospitalDto>>) {
                if (response.isSuccessful) {
                    val hospitals = response.body() ?: emptyList()
                    hospitals.forEach { hospital ->
                        Log.d("HospitalInfo", "ID: ${hospital.id}, Name: ${hospital.name}, Address: ${hospital.address}, Lat: ${hospital.latitude}, Lng: ${hospital.longitude}")
                    }
                    addAllHospitalsToMap(hospitals, kakaoMap)
                } else {
                    Log.e("HospitalService", "Failed to fetch hospitals. Response code: ${response.code()}")
                    Toast.makeText(requireContext(), "병원 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<HospitalDto>>, t: Throwable) {
                Log.e("HospitalService", "Failed to fetch hospitals: ${t.message}")
                Toast.makeText(requireContext(), "병원 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addAllHospitalsToMap(hospitals: List<HospitalDto>, kakaoMap: KakaoMap) {
        // 1. 레이블 스타일 - 병원 아이콘 (커스텀 아이콘 설정 가능)
        val styles = kakaoMap.labelManager?.addLabelStyles(
            LabelStyles.from(LabelStyle.from(R.drawable.marker))
        )

        // 2. 레이블 레이어 가져오기
        val layer = kakaoMap.labelManager?.layer

        // 3. 각 병원마다 레이블 생성
        hospitals.forEach { hospital ->
            val hospitalLocation = LatLng.from(hospital.latitude, hospital.longitude)
            val label = layer?.addLabel(
                LabelOptions.from(hospital.id.toString(), hospitalLocation)
                    .setStyles(styles)
            )
        }

        // 4. Label 클릭 이벤트 설정
        kakaoMap.setOnLabelClickListener { _, _, label ->
            val hospitalId = label.labelId.toLongOrNull()
            val selectedHospital = hospitals.find { it.id == hospitalId }
            if (selectedHospital != null) {
                showHospitalInfoDialog(selectedHospital)
            }
            true
        }
    }

    // 병원 정보 다이얼로그 표시
    private fun showHospitalInfoDialog(hospital: HospitalDto) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_hospital_info, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val hospitalNameText = dialogView.findViewById<TextView>(R.id.hospitalName)
        val hospitalAddressText = dialogView.findViewById<TextView>(R.id.hospitalAddress)

        // 병원 이름과 주소를 한 줄로 표시
        hospitalNameText.text = "${hospital.name} "
        hospitalAddressText.text = hospital.address

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        mapView?.resume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.pause()
    }
}
