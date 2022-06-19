package com.example.travelcultureapplicaiton

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelcultureapplicaiton.databinding.FragmentMapBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding : FragmentMapBinding

    private var latitude : Double = 0.0
    private var longitude : Double = 0.0

    private lateinit var mView: MapView
    var googleMap: GoogleMap? = null

    // 사용자 위치정보에 대한 클라이언트 변수
    lateinit var apiClient : GoogleApiClient
    lateinit var providerClient: FusedLocationProviderClient
    private lateinit var call: Call<responseInfo_locate>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView = inflater.inflate(R.layout.fragment_map, container, false)
        mView = rootView.findViewById(R.id.mapView)
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this)

        // 사용자 퍼미션 얻기
        providerClient = LocationServices.getFusedLocationProviderClient(activity as MainActivity)
        apiClient = GoogleApiClient.Builder(activity as MainActivity)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            if(it.all{permission -> permission.value == true}){
                apiClient.connect() // 정보 가져옴, onConnected와 연결
            } else{
                Toast.makeText(activity as MainActivity,"권한 거부..", Toast.LENGTH_SHORT).show()
            }
        }
        if(ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(activity as MainActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(activity as MainActivity,Manifest.permission.ACCESS_NETWORK_STATE) !== PackageManager.PERMISSION_GRANTED)
        { Log.d("mobileApp", "checkSelfPermission")
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE)
            )
        }
        else{
            apiClient.connect() // 정보 가져옴
        }

        return rootView
    }
    
    override fun onMapReady(p0: GoogleMap) { // 맵 초기화
        googleMap = p0 // 구글맵을 변수에 넣는다.
    }

    //데이터를 불러오는 함수
    private fun callData(latitude: Double, longitude: Double){
        // 데이터를 가져온다.
        val returnType = arguments?.getString("returnType")
        call = MyApplication.networkService_location.getLocationXmlList(
            "CDNRFWzcqVNIQ++7vj9QCBoCKvsk5fAEh/nT6XXO+49SR7SN2qEWcX9vTorvWC1Zsgn1VGftwEZslejzAUs/ww==",
            1,
            20,
            "ETC",
            "TravelCultureApp",
            "E",
            12,
            longitude,
            latitude,
            10000
        )
        Log.d("appTest", "callData: $latitude, $longitude")

        //서버로부터 전달받은 내용 처리
        call?.enqueue(object: Callback<responseInfo_locate> {
            override fun onResponse(call: Call<responseInfo_locate>, response: Response<responseInfo_locate>) {
                if(response.isSuccessful){
                    val locateItem = response.body()!!.body!!.items!!.item

                    Log.d("appTest", "addMarker, googleMap: $googleMap")
                    for(i in 0 until locateItem.size){
                        // 마커 위치 값 얻기
                        val markerOp = MarkerOptions()
                        Log.d("appTest", "$markerOp")
                        val lat = locateItem[i]!!.mapx!!
                        val long = locateItem[i]!!.mapy!!
                        val latLng = LatLng(long, lat)
                        Log.d("appTest", "$lat, $long")

                        // 위치, 타이틀 설정
                        markerOp.title(locateItem[i]!!.title)
                        markerOp.position(latLng)
                        markerOp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        // 마커 색깔 값 설정하기
                        Log.d("appTest", " ${locateItem[i]!!.title} ${locateItem[i]!!.cat2}")
                        when(locateItem[i]!!.cat2){
                            "A0201" -> markerOp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            "A0202" -> markerOp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            "A0203" -> markerOp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            "A0204" -> markerOp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                            "A0205" -> markerOp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                        }

                        // 마커 생성
                        googleMap?.addMarker(markerOp)
                        Log.d("appTest", "addMarker, googleMap: $googleMap")
                    }

                }
            }

            override fun onFailure(call: Call<responseInfo_locate>, t: Throwable) {
                Log.d("appTest", "onFailure")
                Log.d("appTest", "$t")
            }

        })
    }

    // 카메라를 이동시키는 함수
    private fun moveMap(latitude: Double, longitude: Double){
        val latLng = LatLng(latitude, longitude)
        val position: CameraPosition = CameraPosition.Builder() // 카메라를 움직여서 설정해놓은 위치로 지도를 이동하도록 한다.
            .target(latLng)
            .zoom(16f)
            .build()
        googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(position))

        // 마커 추가하기
        val markerOp = MarkerOptions()
        markerOp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        markerOp.position(latLng)
        markerOp.title("내 위치")
        markerOp.icon(bitmapDescriptorFromVector(activity as MainActivity, R.drawable.ic_baseline_emoji_people_24))
        Log.d("appTest", "markerOp")
        googleMap?.addMarker(markerOp)
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onStart() {
        super.onStart()
        mView.onStart()
    }
    override fun onStop() {
        super.onStop()
        mView.onStop()
    }
    override fun onResume() {
        super.onResume()
        mView.onResume()
    }
    override fun onPause() {
        super.onPause()
        mView.onPause()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mView.onLowMemory()
    }
    override fun onDestroy() {
        mView.onDestroy()
        super.onDestroy()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onConnected(p0: Bundle?) {
        // 퍼미션 다시 확인
        Log.d("mobileApp", "onConnected")
        if(ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED){
            providerClient.lastLocation.addOnSuccessListener(
                activity as MainActivity,
                object: OnSuccessListener<Location> {
                    override fun onSuccess(p0: Location?) { // 사용자 위치정보에 대한 값
                        p0?.let{
                            latitude = p0.latitude
                            longitude = p0.longitude
                            Log.d("appTest", "lat: $latitude, lng: $longitude")
                            // 사용자 위치를 카메라로
                            callData(latitude, longitude)
                            Log.d("appTest", "callData")
                            moveMap(latitude, longitude)
                            Log.d("appTest", "moveMap")

                        }
                    }
                }
            )
            apiClient.disconnect()
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


}