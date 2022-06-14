package com.example.travelcultureapplicaiton

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelcultureapplicaiton.databinding.FragmentListLocateBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.tasks.OnSuccessListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragmentLocate.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragmentLocate : Fragment(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var call: Call<responseInfo_locate>
    lateinit var binding : FragmentListLocateBinding

    // 사용자 위치정보에 대한 클라이언트 변수
    lateinit var apiClient : GoogleApiClient
    lateinit var providerClient: FusedLocationProviderClient

    var latitude : Double = 0.0
    var longitude : Double = 0.0

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
        binding = FragmentListLocateBinding.inflate(inflater, container, false)
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

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragmentLocate.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragmentLocate().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun callData(latitude: Double, longitude: Double){
        // 데이터를 가져온다.
        val returnType = arguments?.getString("returnType")
        call = MyApplication.networkService_location.getLocationXmlList(
            "CDNRFWzcqVNIQ++7vj9QCBoCKvsk5fAEh/nT6XXO+49SR7SN2qEWcX9vTorvWC1Zsgn1VGftwEZslejzAUs/ww==",
            1,
            10,
            "ETC",
            "TravelCultureApp",
            "O",
            longitude,
            latitude,
            5000
        )
        Log.d("appTest", "$latitude, $longitude")

        //서버로부터 전달받은 내용 처리
        call?.enqueue(object: Callback<responseInfo_locate> {
            override fun onResponse(call: Call<responseInfo_locate>, response: Response<responseInfo_locate>) {
                if(response.isSuccessful){
                    Log.d("appTest", "$response")
                    val locateAdapter = AdapterLocation(activity as Context, response.body()!!.body!!.items!!.item)
                    binding.listLocateRecyclerView.layoutManager = LinearLayoutManager(activity)
                    binding.listLocateRecyclerView.adapter = locateAdapter

                    locateAdapter.setItemClickListener(object: AdapterLocation.OnItemClickListener{
                        override fun onClick(v: View, position: Int) {
                            // 클릭 시 이벤트 작성
                            val intent = Intent(activity, DetailActivity::class.java)
                            val uniqueContentNum =  response.body()!!.body!!.items!!.item
                            Log.d("appTest", "${uniqueContentNum[position].contentid}")
                            //contentID 넘기기
                            intent.putExtra("contentID", uniqueContentNum[position].contentid)
                            startActivity(intent)

                        }
                    })


                }
            }

            override fun onFailure(call: Call<responseInfo_locate>, t: Throwable) {
                Log.d("appTest", "onFailure")
                Log.d("appTest", "$t")
            }

        })
    }

    override fun onConnected(p0: Bundle?) {
        // 퍼미션 다시 확인
        Log.d("appTest", "onConnected")
        if(ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED){
            providerClient.lastLocation.addOnSuccessListener(
                activity as MainActivity,
                object: OnSuccessListener<Location> {
                    override fun onSuccess(p0: Location?) { // 사용자 위치정보에 대한 값
                        p0?.let{
                            latitude = p0.latitude
                            longitude = p0.longitude
                            Log.d("appTest", "lat: $latitude, lng: $longitude")
                            callData(latitude, longitude)
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
