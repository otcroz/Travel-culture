package com.example.travelcultureapplicaiton

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.travelcultureapplicaiton.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding : FragmentHomeBinding
    private lateinit var call: Call<responseInfo_area>

    lateinit var categoryList : MutableList<String>

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
        // 바인딩 설정
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 지역 문화 정보
        getMyRegionCulture()
        
        // 문화 추천 정보
        getCategory()

        // 유저 정보 업로드(이메일, 추후에 닉네임으로 변경)
        //binding.username.text =

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // 사용자 값 업데이트
        updateUserInfo()
    }

    // 사용자 정보 업데이트
    private fun updateUserInfo(){
        // 사용자 지역값 가져오기
        MyApplication.db.collection("user").document(MyApplication.auth?.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                val resultItem = result.toObject(UserDataModel::class.java)
                // 이름 반영하기
                val userNickname = resultItem!!.nickname.toString()
                binding.username.text = userNickname
                // 거주지역 반영하기
                val userResidence = resultItem!!.residence.toString()
                binding.userResidence.text = userResidence

                // 카테고리 반영하기
                val userCategory = resultItem!!.category
                var temp: String? = ""
                if (userCategory != null) {
                    for(i in userCategory)
                        temp += "$i "
                }
                Log.d("appTest", "temp: $temp")
                binding.userCategory.text = temp
            }
            .addOnFailureListener{
                Toast.makeText(activity as MainActivity,"서버 데이터 획득 실패",  Toast.LENGTH_SHORT).show()
            }
    }

    // 지역 문화 리사이클러 뷰
    private fun getMyRegionCulture(){
        // 사용자 지역값 가져오기
        MyApplication.db.collection("user").document(MyApplication.auth?.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                val resultItem = result.toObject(UserDataModel::class.java)
                // 지역 값 가져오기
                var userArea = resultItem!!.residence.toString()
                // string값 가지고 locate에서 index값 찾기
                val index = resources.getStringArray(R.array.set_location).indexOf(userArea)
                Log.d("appTest", "index: $index")
                // index값을 locate_value에 넣어서 값 반환하기
                val userAreaNum = resources.getStringArray(R.array.set_location_value)[index]
                Log.d("appTest", "userAreaNum: $userAreaNum")

                // 데이터 요청
                callDataLocate(userAreaNum!!.toInt())
            }
            .addOnFailureListener{
                Toast.makeText(activity as MainActivity,"서버 데이터 획득 실패",  Toast.LENGTH_SHORT).show()
            }
    }

    // 유저 카테고리, 지역 값 가져오기
    private fun getCategory() {
        lateinit var randomCategory: String
            val random = Random
            val categoryNum = CoroutineScope(Dispatchers.Default).async {
                MyApplication.db.collection("user").document(MyApplication.auth?.currentUser?.uid.toString())
                    .get()
                    .addOnSuccessListener { result ->
                        val resultItem = result.toObject(UserDataModel::class.java)
                        // 지역 값과 카테고리 값 가져오기
                        categoryList = resultItem!!.category!!

                        // 지역 값 가져오기
                        var userArea = resultItem!!.residence.toString()
                        val indexArea = resources.getStringArray(R.array.set_location).indexOf(userArea)
                        val userAreaNum = resources.getStringArray(R.array.set_location_value)[indexArea]

                        // 랜덤 숫자 뽑기
                        val randomNum = random.nextInt(categoryList.size)
                        Log.d("appTest", "randomNum: $randomNum")
                        // 사용자 카테고리 배열에서 string 값 뽑아내기
                        val selUserCategory = categoryList[randomNum]
                        Log.d("appTest", "selUserCategory: $selUserCategory")
                        // string값 가지고 category에서 index값 찾기
                        val index = resources.getStringArray(R.array.set_category).indexOf(selUserCategory)
                        Log.d("appTest", "index: $index")
                        // index값을 category_value에 넣어서 값 반환하기
                        randomCategory = resources.getStringArray(R.array.set_category_value)[index]
                        Log.d("appTest", "randomCategory: $randomCategory")

                        // 값 전달하기
                        callDataSummary(userAreaNum!!.toInt(), randomCategory)
                    }
                    .addOnFailureListener{
                        randomCategory = null.toString()
                        Toast.makeText(activity as MainActivity,"서버 데이터 획득 실패",  Toast.LENGTH_SHORT).show()
                    }
        }
    }

    // 공공데이터 요청 함수: 사용자 추천
    private fun callDataSummary(areaNum: Int, categoryNum: String){
        val returnType = arguments?.getString("returnType")
        call = MyApplication.networkSetvice_area.getAreaXmlList(
            "CDNRFWzcqVNIQ++7vj9QCBoCKvsk5fAEh/nT6XXO+49SR7SN2qEWcX9vTorvWC1Zsgn1VGftwEZslejzAUs/ww==",
            1,
            10,
            "ETC",
            "TravelCultureApp",
            "O",
            areaNum,
            "A02",
            categoryNum,
            12,
        )
        //서버로부터 전달받은 내용 처리
        call?.enqueue(object: Callback<responseInfo_area> {
            override fun onResponse(call: Call<responseInfo_area>, response: Response<responseInfo_area>) {
                if(response.isSuccessful){
                    Log.d("appTest", "$response")
                    // 불러온 데이터 적용하기
                    val recHomeData = response.body()!!.body!!.items!!.item

                    binding.recHomeTitle.text = recHomeData[0].title
                    binding.recHomeLocate.text = recHomeData[0].addr1
                    Glide.with(binding.root)
                        .load(recHomeData[0].firstimage)
                        .override(300,200)
                        .into(binding.recHomeImage)
                }
            }

            override fun onFailure(call: Call<responseInfo_area>, t: Throwable) {
                Log.d("appTest", "onFailure")
                Log.d("appTest", "$t")
            }

        })
    }

    // 공공데이터 요청 함수: 지역별
    private fun callDataLocate(areaNum: Int){
        val returnType = arguments?.getString("returnType")
        call = MyApplication.networkSetvice_area.getAreaXmlList(
            "CDNRFWzcqVNIQ++7vj9QCBoCKvsk5fAEh/nT6XXO+49SR7SN2qEWcX9vTorvWC1Zsgn1VGftwEZslejzAUs/ww==",
            1,
            10,
            "ETC",
            "TravelCultureApp",
            "O",
            areaNum,
            "A02",
            null,
            12,
        )
        //서버로부터 전달받은 내용 처리
        call?.enqueue(object: Callback<responseInfo_area> {
            override fun onResponse(call: Call<responseInfo_area>, response: Response<responseInfo_area>) {
                Log.d("appTest", "$call / $response")
                if(response.isSuccessful){
                    Log.d("appTest", "$response")
                    val areaAdapter = AdapterHomeRecommand(activity as Context, response.body()!!.body!!.items!!.item)
                    binding.homeRecommandRecyclerView.layoutManager =  GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false);
                    binding.homeRecommandRecyclerView.adapter = areaAdapter

                    // 리사이클러뷰 이벤트 처리
                    areaAdapter.setItemClickListener(object: AdapterHomeRecommand.OnItemClickListener{
                        override fun onClick(v: View, position: Int) {
                            // 클릭 시 이벤트 작성
                            val uniqueContent = response.body()!!.body!!.items!!.item
                            val uniqueContentNum = uniqueContent[position]!!.contentid
                            val intent = Intent(activity, DetailActivity::class.java)
                            //contentID 넘기기
                            intent.putExtra("contentID", uniqueContentNum)
                            startActivity(intent)
                        }
                    })
                }
            }
            override fun onFailure(call: Call<responseInfo_area>, t: Throwable) {
                Log.d("appTest", "onFailure")
                Log.d("appTest", "$t")
            }

        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}